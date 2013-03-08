package com.dbz.verge.menus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.Menu;

public class BluetoothScreen extends Menu {
	
	//TODO need to enable message passing
	
	// --------------
	// --- Fields ---
	// --------------
	
    BluetoothAdapter btAdapter = game.mBtAdapter;
    private static final String NAME = "VergeBluetoothConnect";
    private static final UUID MY_UUID = UUID.fromString("7d2b2c7a-e370-4500-a82a-47e1a76287be");
    
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    
    
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
    
    // Debugging
    private static final String TAG = "BluetoothScreen";
    private static final boolean D = true;

    // -------------------
 	// --- Constructor ---
    // -------------------
    public BluetoothScreen() {
    	
     //BluetoothAdapter btAdapter = game.mBtAdapter; //btAdapter is null for some reason
		if(!btAdapter.isEnabled()){
			btAdapter.enable();
		}
		
		//game.mPairedDevices = btAdapter.getBondedDevices(); //note, doesn't work right away.
		//bluetooth takes a few seconds to enable, thus paired devices will return null until enabled. 
		
    	//Logic: constructor automatically starts "make discoverable" then start the accept thread
    	//if a new device is found while running, stop the accept thread, start the connect thread
    	//right now, this is done in the update() function, when the screen is pressed
		
		while(true){ //must wait BT enable() to finish before ensureDiscoverable(). Else we get a force close.
			if(btAdapter.getState() == BluetoothAdapter.STATE_ON){ 
				game.mNewDevices = null; //clear previously found devices before launch
			game.ensureDiscoverable(); //puts device in discoverable mode
			game.startDiscovery(); // right now ending on back press (hardware)
			start(); //start accept thread
			break;
			}
		}
		
    }       

    // ---------------------
 	// --- Update Method ---
 	// ---------------------
    @Override
    public void update(float deltaTime) {
  
    	//game.mPairedDevices = btAdapter.getBondedDevices();
    	
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);    

            if(event.type == TouchEvent.TOUCH_UP) {
                touchPoint.set(event.x, event.y);
                guiCam.touchToWorld(touchPoint);
                
                //if currently listening & device found, then start connection
                if(mState == STATE_LISTEN && game.mNewDevices != null){              	    	
                	BluetoothDevice target = game.mNewDevices.iterator().next(); //get first bluetooth device
                	Log.d("Bluetooth", target.getName());
                	connect(target);  //try connecting to the device, if fails. restart accept thread
                }
    	        super.update(touchPoint);
            }

        }
    }
    
    /////////////////////////////////////////////////////////////
    //Bluetooth connection threads
    ////////////////////////////////////////////////////////////
    
    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }
    
    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
        setState(STATE_NONE);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);
      //  pairDevice(device); //auto pair the devices
        
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
    
    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
       // Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
      //  Bundle bundle = new Bundle();
       // bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
       // msg.setData(bundle);
      //  mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }


 /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket socket = null;

            // Create a new listening server socket
            try {
            	socket = com.dbz.verge.menus.InsecureBluetooth.listenUsingRfcommWithServiceRecord(btAdapter, NAME, MY_UUID, false); //disable encryption
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = socket;
        }

        public void run() {
            if (D) Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothScreen.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread");
        }

        public void cancel() {
            if (D) Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            BluetoothSocket socket = null;
            try {
            	socket = InsecureBluetooth.createRfcommSocketToServiceRecord(device, MY_UUID, false); //disable encryption
            } catch (IOException e) {
            	Log.e(TAG, "create() failed", e);
            }
        	mmSocket = socket;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            btAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                Log.e(TAG, "connection failed", e);
                //connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                // Start the service over to restart listening mode
                BluetoothScreen.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothScreen.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    
    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                 //   mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1, buffer)
                   //         .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    //connectionLost();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
              //  mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
                       // .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    
    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }
    
    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

       // Give the new state to the Handler so the UI Activity can update
      //  mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /////Use this stuff to communicate from phone to phone, after connection. 
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    /*
    private void connectionFailed() {
        setState(STATE_LISTEN);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
*/
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    /*
    private void connectionLost() {
        setState(STATE_LISTEN);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
*/
    
    ///////////////////////////////////////////////////////////////
    
    //////////////////////////////////////////////////////////////
    
    // ----------------------------
 	// --- Utility Draw Methods ---
 	// ----------------------------
    
    @Override
    public void drawBackground() {
       // batcher.beginBatch(AssetsManager.background);
       // batcher.drawSprite(0, 0, 1280, 800, AssetsManager.backgroundRegion);
       // batcher.endBatch();
    }
    
    @Override
    public void drawObjects() {
    	
    	int lineSpacer = 40;
    	
    	//here we want to display all available adapters
		batcher.beginBatch(AssetsManager.vergeFontTexture);

		AssetsManager.vergeFont.drawTextCentered(batcher, "Tap Screen to connect to new device", 640, 700-lineSpacer, 1.8f);
		
		//New Devices title
		lineSpacer += 80;
		AssetsManager.vergeFont.drawTextCentered(batcher, "New Devices", 640, 700-lineSpacer, 1.7f);
		lineSpacer += 40;
		// list of new devices
		if(game.mNewDevices != null){
		for(BluetoothDevice nd : game.mNewDevices){
			AssetsManager.vergeFont.drawTextCentered(batcher, nd.getName() + " : " + nd.getAddress() , 640, 700-lineSpacer, 1.5f);
			lineSpacer += 40;
		}
		}
		
		//Connection Status
		lineSpacer += 240;
		if (mState == STATE_CONNECTED){
			BluetoothDevice connectedDev = game.mNewDevices.iterator().next();
			AssetsManager.vergeFont.drawTextCentered(batcher, "Connected to " + connectedDev.getName() , 640, 700-lineSpacer, 2f);
		} else AssetsManager.vergeFont.drawTextCentered(batcher, "Not Connected" , 640, 700-lineSpacer, 2f);
		
		//AssetsManager.vergeFont.drawTextCentered(batcher, string, 640, 700, 1.5f);
		batcher.endBatch();
		
        super.drawObjects(); //pause button and stuff
    }

    
    @Override
    public void drawBounds() {}
    
    @Override
    public void onBackPressed(){
		if(game.mBtAdapter.isEnabled()){
			//game.mBtAdapter.disable(); //uncomment - leaving enable for faster debugging
		}
		game.endDiscovery();
		stop(); //stop all threads
		game.setScreen(new MainMenu());
    }

}
