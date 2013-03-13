package com.dbz.verge.menus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
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
    
  //  private AcceptThread mAcceptThread;
   // private ConnectThread mConnectThread;
   // private ConnectedThread mConnectedThread;
  //  private int mState;
    
    private final BluetoothAdapter mAdapter = btAdapter;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    public ControlThread mControlThread;
    public static int mState;
    public String mConnectedDevice = "";

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_READY = 2;	// discovery process complete, device list built
    public static final int STATE_CONNECTING = 3; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 4;  // now connected to a remote device
    
	// Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    // Debugging
    private static final String TAG = "BluetoothScreen";
    private static final boolean D = true;
   
    public Integer testMessage = 0;
    // -------------------
 	// --- Constructor ---
    // -------------------
    public BluetoothScreen() {
    	//BluetoothAdapter btAdapter = game.mBtAdapter; //btAdapter is null for some reason
		if(!btAdapter.isEnabled()){
			btAdapter.enable();
		}
		game.testMessageRead = "";
		mControlThread = new ControlThread();
		mControlThread.start();
    }       
    
    
    // TODO
    // Figure out why dinc1 cant connect ot dinc2, but works vice versa (one way pairing?)
    // look into why detecting new devices doesn't always work (must hit help button a few times)
    // why pairing prompt appears even when using insecure. though i tried to disable using reflection methods.. still no go. 

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
                
                if(mState == STATE_CONNECTED){
                	testMessage += 1;
                	mConnectedThread.write(testMessage.toString().getBytes());
                }
    	        super.update(touchPoint);
            }
        }
    }
    
    /////////////////////////////////////////////////////////////
    //Bluetooth connection threads
    ////////////////////////////////////////////////////////////
    

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    public static synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
       // mHandler.obtainMessage(BluetoothChat.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public static synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void begin() {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        setState(STATE_LISTEN);
        
        

        // Start the thread to listen on a BluetoothServerSocket
      //  if (mSecureAcceptThread == null) {
      //      mSecureAcceptThread = new AcceptThread(true); //commenting out, we only want to connect insecure for now
      //      mSecureAcceptThread.start();
     //  }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        mConnectedDevice = device.getName();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        if (D) Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null; } 

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
       // Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_DEVICE_NAME);
      //  Bundle bundle = new Bundle();
      //  bundle.putString(BluetoothChat.DEVICE_NAME, device.getName());
      //  msg.setData(bundle);
      //  mHandler.sendMessage(msg);
        mConnectedDevice = device.getName();
        setState(STATE_CONNECTED);
        synchronized(mControlThread){
        	mControlThread.notify();
        }
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
//        if (D) Log.d(TAG, "stop");
    	//mControlThread.notify();
//        if (mConnectThread != null) {
//            mConnectThread.cancel();
//            mConnectThread = null;
//        }

//        if (mConnectedThread != null) {
//            mConnectedThread.cancel();
//            mConnectedThread = null;
//        }
//
//        if (mSecureAcceptThread != null) {
//            mSecureAcceptThread.cancel();
//            mSecureAcceptThread = null;
//        }
//
//        if (mInsecureAcceptThread != null) {
//            mInsecureAcceptThread.cancel();
//            mInsecureAcceptThread = null;
//        }
//        if (mControlThread != null) {
//        	mControlThread = null;
//        }
        setState(STATE_NONE);
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
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
/*
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
 /*
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BluetoothChat.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothChat.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }
*/
    public class ControlThread extends Thread {
    	public ControlThread() {
    		while(true) { //must wait BT enable() to finish before ensureDiscoverable(). Else we get a force close.
    			if(btAdapter.getState() == BluetoothAdapter.STATE_ON){
    				synchronized(game.mNewDevices) {
    					game.mNewDevices.clear(); //clear previously found devices before launch
    				}
    				game.ensureDiscoverable(); //puts device in discoverable mode (user prompt)
    				break;
    			}
    		}
    	}
    	public synchronized void run() {
    		while(true){
	    		begin(); // accept thread
	    		game.mNewDevices.clear();
	    		game.startDiscovery(); // right now ending on back press (hardware)
	    		while(BluetoothScreen.this.getState() != STATE_READY){}
    			for (BluetoothDevice btd : game.mNewDevices) {
	        		Log.d("Bluetooth", btd.getName());
	            	connect(btd, false);  //try connecting to the device, if fails. restart accept thread
	            	try {
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	if (BluetoothScreen.this.getState() == STATE_CONNECTED)
	            		return;
    			}
    		}
		}
    }
    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure) {
        	
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure":"Insecure";
            // Create a new listening server socket
            try {
                if (secure) {
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                } else { tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "Socket Type: " + mSocketType + "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + " accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothScreen.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice(),
                                    mSocketType);
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
            if (D) Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);
            
            
        }

        public void cancel() {
            if (D) Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
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
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
            	if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                } else { tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);}
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + " create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread" + mSocketType);


            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.e(TAG, "Closed Socket()");
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType +
                            " socket during connection failure", e2);
                }
                synchronized(mControlThread){
                	mControlThread.notify();
                }
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothScreen.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
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
     
        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
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
                    game.mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                  //  connectionLost();
//                    synchronized(mControlThread) {
//	                    if (mControlThread.getState() != Thread.State.WAITING) {
//	                    	mControlThread = new ControlThread();
//	                    	mControlThread.start();
//	                    }
//                    	mControlThread.notify();
//                    }
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
                game.mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
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
		AssetsManager.vergeFont.drawTextCentered(batcher, "Device List", 640, 700-lineSpacer, 1.7f);
		lineSpacer += 40;
		// list of new devices
		synchronized(game.mNewDevices) {
		if(!game.mNewDevices.isEmpty()) {
				for(BluetoothDevice nd : game.mNewDevices){
					AssetsManager.vergeFont.drawTextCentered(batcher, nd.getName() + " : " + nd.getAddress() , 640, 700-lineSpacer, 1.5f);
					lineSpacer += 40;
				}
			}
		}
		
		//Connection Status
		lineSpacer += 240;
		if (mState == STATE_LISTEN) {
			AssetsManager.vergeFont.drawTextCentered(batcher, "Searching..." , 640, 700-lineSpacer, 2f);
			lineSpacer += 40;
		} else if (mState == STATE_CONNECTING) {
			AssetsManager.vergeFont.drawTextCentered(batcher, "Connecting to " + mConnectedDevice , 640, 700-lineSpacer, 2f);
			lineSpacer += 40;
		} else if (mState == STATE_CONNECTED) {
			AssetsManager.vergeFont.drawTextCentered(batcher, "Connected to " + mConnectedDevice , 640, 700-lineSpacer, 2f);
			lineSpacer += 80;
			if (game.testMessageRead != "") {
				AssetsManager.vergeFont.drawTextCentered(batcher, mConnectedDevice + " sent " + game.testMessageRead + " message" , 640, 700-lineSpacer, 1.8f);
				lineSpacer += 40;
			}
		}
		
		//AssetsManager.vergeFont.drawTextCentered(batcher, string, 640, 700, 1.5f);
    	batcher.endBatch();
		
        super.drawObjects(); //pause button and stuff
    }

    
    @Override
    public void drawBounds() {}
   
    
    @Override
    public void onBackPressed(){
		game.endDiscovery();
		this.stop(); //stop all threads
		if(game.mBtAdapter.isEnabled()){
			game.mBtAdapter.disable(); //uncomment - leaving enable for faster debugging
		}
		game.setScreen(new MainMenu());
    }

}
