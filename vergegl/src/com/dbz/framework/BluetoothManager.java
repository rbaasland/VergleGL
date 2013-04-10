package com.dbz.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.dbz.framework.math.Rectangle;
import com.dbz.framework.gl.Screen;


public class BluetoothManager {
		// --------------
		// --- Fields ---
		// --------------

		public final BluetoothAdapter btAdapter = Screen.game.mBtAdapter;
		//public Set<BluetoothDevice> mPairedDevices; // Used to first connect to paired devices
	    public Set<BluetoothDevice> mNewDevices = Collections.synchronizedSet(new HashSet<BluetoothDevice>()); // Contains list devices found per discovery iteration
	    public static boolean isReceiverRunning = false; // Used to determine if broadcast receiver is enabled
		private static final String NAME = "VergeBluetoothConnect";
		private static final UUID MY_UUID = UUID.fromString("7d2b2c7a-e370-4500-a82a-47e1a76287be");
		public boolean isSecureConnection = false; //Used to determine the type of connection to establish
		
		// Constants that indicate the current connection state
		public static final int STATE_NONE = 0;       	// we're doing nothing
		public static final int STATE_LISTEN = 1;     	// now listening for incoming connections
		public static final int STATE_READY = 2;		// discovery process complete, device list built
		public static final int STATE_CONNECTING = 3; 	// now initiating an outgoing connection
		public static final int STATE_CONNECTED = 4;  	// now connected to a remote device

		// Message States
		public static final int MESSAGE_STATE_CHANGE = 1;
		public static final int MESSAGE_READ = 2;
		public static final int MESSAGE_WRITE = 3;
		public static final int MESSAGE_DEVICE_NAME = 4;
		public static final int MESSAGE_TOAST = 5;

		// Connection Threads
		public AcceptThread mSecureAcceptThread;		// for secure connections
		public AcceptThread mInsecureAcceptThread;		// for insecure connections 
		public ConnectThread mConnectThread;			// for connection to accept thread's open socket
		public ConnectedThread mConnectedThread;		// handles message passing between devices post connection 
		public ControlThread mControlThread;			// controls the flow of connections between devices in range of the adapter

		public static int mState;				//current state of bluetooth connection
		public String mConnectedDevice = ""; 	//reference to current device name for printing


		// Debugging
		private static final String TAG = "BluetoothManager";
		private static final boolean D = false;
		
		public Rectangle multiplayerBounds = new Rectangle(0,0,100,100);
	    public static String connectionStatus = "Searching";
	    
	    public static final float defaultDiscoveryTime = 12; //in general, startDiscovery() takes 12 seconds to complete
	    public static final float estimatedConnectionTimePerDevice = 1; //assuming time to attempt connection with a device is 1 second
	    
		// -------------------
		// --- Constructor ---
		// -------------------
		
		public BluetoothManager() {
			Screen.game.messageRead = "";
			connectionStatus = "Searching";
			// The BroadcastReceiver that listens for discovered devices and add the device to the new devices array
			Screen.game.mReceiver = new BroadcastReceiver() {
			        @Override
			        public void onReceive(Context context, Intent intent) {
			        	if (intent == null) 
			        		return;
			            String action = intent.getAction();
			            handleBroadcastReceiver(intent, action);
			        }
			    };
		}  

		// ----------------------------
		// -----Thread Wrappers--------
		// ---------------------------

		/** Auto-enables bluetooth and starts threads*/
		public void startThreads(){
			
			if(!btAdapter.isEnabled()){
				btAdapter.enable();
			}
			
			Screen.game.messageRead = "";
			mControlThread = new ControlThread();
			mControlThread.start();
		} 
		
		
		/** Ends discovery and stops all running threads*/
		public void endThreads() {
			
			endDiscovery();
			
			if ( mConnectedThread != null )
				mConnectedThread.write("NO".toString().getBytes());
			this.stop(); //stop all threads
//			if(game.mBtAdapter.isEnabled()){ TODO disable adapter when done when testing AND leave on if user already had bluetooth enabled
//				game.mBtAdapter.disable();
//			}
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

			if (isSecureConnection){ //secure or insecure connection (default insecure)
		        if (mSecureAcceptThread == null) {
		            mSecureAcceptThread = new AcceptThread(true);
		            mSecureAcceptThread.start();
		        }
			} 
			else if (mInsecureAcceptThread == null) {
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
			if (BluetoothManager.getState() == STATE_CONNECTING) {
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

			mConnectedDevice = device.getName();
			setState(STATE_CONNECTED);
			if(BluetoothManager.this.mControlThread != null){
				synchronized(BluetoothManager.this.mControlThread){
					BluetoothManager.this.mControlThread.notifyAll();
				}
			}
		}

		/**
		 * Stop all threads
		 */
		public synchronized void stop() {
			if (D) Log.d(TAG, "stop");
			if(BluetoothManager.this.mControlThread != null){
				synchronized(BluetoothManager.this.mControlThread) {
					BluetoothManager.this.mControlThread.notifyAll();
				}
			}

			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}

			if (mConnectedThread != null) {
				mConnectedThread.cancel();
				mConnectedThread = null;
			}

			if (mSecureAcceptThread != null) {
				mSecureAcceptThread.cancel();
				mSecureAcceptThread = null;
			}

			if (mInsecureAcceptThread != null) {
				mInsecureAcceptThread.cancel();
				mInsecureAcceptThread = null;
			}

			if (mControlThread != null) {
				mControlThread = null;
			}
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
				if (BluetoothManager.getState() != STATE_CONNECTED) return;
				r = mConnectedThread;
			}
			// Perform the write unsynchronized
			r.write(out);
		}

		/**
		 * Set the current state of the chat connection
		 * @param state  An integer defining the current connection state
		 */
		public static synchronized void setState(int state) {
			if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
			mState = state;
		}

		/**
		 * Return the current connection state. */
		public static synchronized int getState() {
			return mState;
		}

		// --------------------------
		// ----Connection Threads----
		// --------------------------

		/** 
		 * This thread controls the flow of connections between devices.
		 * It relies on the BluetoothAdapter callback from the BroadcastReceiver() 
		 * located in Game.java
		 */
		public class ControlThread extends Thread {
			public ControlThread() {
				while(true) { //must wait BT enable() to finish before ensureDiscoverable(). Else we get a force close.
					if(btAdapter.getState() == BluetoothAdapter.STATE_ON){
						BluetoothManager.connectionStatus = "Searching";
						mNewDevices.clear(); //clear previously found devices before launch
						ensureDiscoverable(); //puts device in discoverable mode (user prompt)
						break;
					}
				}
			}
			public void run() {
				while(true){
					begin(); // accept thread
					BluetoothManager.connectionStatus = "Searching";
					mNewDevices.clear();
					startDiscovery(); // right now ending on back press (hardware)
					while(BluetoothManager.getState() != STATE_READY){}
					for (BluetoothDevice btd : mNewDevices) {
						if(D) Log.d("Bluetooth", btd.getName());
						connect(btd, false);  // try connecting to the device, if fails. restart accept thread and continue this loop
						// if connection successful, state_connected is active and proceeds to connectedThread (message passing)
						try {
							synchronized(this) {
								wait();		// wait for connect thread to finish
							}
						} catch (InterruptedException e) {e.printStackTrace();}

						if (BluetoothManager.getState() == STATE_CONNECTED) {//if our state is connected when control is returned to this thread, return. 
							return;
						}
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
						tmp = btAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
					} else { tmp = btAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
					}
				} catch (IOException e) { Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);}

				mmServerSocket = tmp;      
			}

			public void run() {
				if (D) Log.d(TAG, "Socket Type: " + mSocketType + "BEGIN mAcceptThread" + this);
				setName("AcceptThread" + mSocketType);

				BluetoothSocket socket = null;

				// Listen to the server socket if we're not connected
				while (BluetoothManager.getState() != STATE_CONNECTED) {
					try {
						// This is a blocking call and will only return on a
						// successful connection or an exception
						socket = mmServerSocket.accept();
					} catch (IOException e) { Log.e(TAG, "Socket Type: " + mSocketType + " accept() failed", e); break;}  //break if accept fails

					// If a connection was accepted
					if (socket != null) {
						synchronized (BluetoothManager.this) {
							switch (BluetoothManager.getState()) {
							case STATE_LISTEN:
							case STATE_CONNECTING:
								// Situation normal. Start the connected thread.
								//Screen.game.cancelDiscovery();
								connected(socket, socket.getRemoteDevice(), mSocketType);
								break;
							case STATE_NONE:
							case STATE_CONNECTED:
								// Either not ready or already connected. Terminate new socket.
								try {
									socket.close();
								} catch (IOException e) { Log.e(TAG, "Could not close unwanted socket", e);}
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
					if(mmServerSocket != null)
						mmServerSocket.close();
				} catch (IOException e) {Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);}
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
				} catch (IOException e) {Log.e(TAG, "Socket Type: " + mSocketType + " create() failed", e);}

				mmSocket = tmp;
			}

			public void run() {
				Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
				setName("ConnectThread" + mSocketType);

				// Make a connection to the BluetoothSocket
				try {
					// This is a blocking call and will only return on a successful connection or an exception
					//Screen.game.cancelDiscovery();
					mmSocket.connect();
				} catch (IOException e) {
					// Close the socket
					try {
						mmSocket.close();
						Log.e(TAG, "Closed Socket()");
					} catch (IOException e2) {Log.e(TAG, "unable to close() " + mSocketType +
							" socket during connection failure", e2);}

					// TODO figure out why we must use null checking here. flow should prevent this?
					if (BluetoothManager.this.mControlThread != null) {
						synchronized(BluetoothManager.this.mControlThread){
							BluetoothManager.this.mControlThread.notifyAll();
						}
					}
					return;
				}

				// Reset the ConnectThread because we're done
				synchronized (BluetoothManager.this) {
					mConnectThread = null;
				}

				// Start the connected thread
				connected(mmSocket, mmDevice, mSocketType);
			}

			public void cancel() {
				try {
					if(mmSocket != null)
						mmSocket.close();
				} catch (IOException e) {Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);}
			}
		}

		/**
		 * This thread runs during a connection with a remote device.
		 * It handles all incoming and outgoing transmissions.
		 */
		public class ConnectedThread extends Thread {
			private final BluetoothSocket mmSocket;
			private final InputStream mmInStream;
			private final OutputStream mmOutStream;

			public ConnectedThread(BluetoothSocket socket, String socketType) {
				if(D) Log.d(TAG, "create ConnectedThread: " + socketType);
				BluetoothManager.connectionStatus = "Connected";
				mmSocket = socket;
				InputStream tmpIn = null;
				OutputStream tmpOut = null;
				// Get the BluetoothSocket input and output streams
				try {
					tmpIn = socket.getInputStream();
					tmpOut = socket.getOutputStream();
				} catch (IOException e) {Log.e(TAG, "temp sockets not created", e);}

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
						Screen.game.mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
					} catch (IOException e) {Log.e(TAG, "disconnected", e);
						//TODO Possible to synchronize device events here when the thread ends. i.e. both go back to some screen.
							//ideally: when one player pauses and exits while other player is waiting for them to respond to continue game
						BluetoothManager.connectionStatus = "Disconnected - Game Over!";
						break;
					
					} //break out of loop on disconnect	
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
					Screen.game.mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
				} catch (IOException e) { Log.e(TAG, "Exception during write", e);}
			}

			public void cancel() {
				try {
					if(mmSocket != null)
						mmSocket.close();
				} catch (IOException e) {Log.e(TAG, "close() of connect socket failed", e);}
			}
		}
		
	    public void ensureDiscoverable() {
	        if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
	            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
	            Screen.game.startActivity(discoverableIntent);
	        }
	    }
	    
	    /**
	     * Start device discover with the BluetoothAdapter
	     */
	    public void startDiscovery() {
	        if(D) Log.d("Bluetooth", "startDiscovery()");
	        isReceiverRunning = true;
	        
	        //Might not need all of these, but may come in handy when troubleshooting
	        
	        // Register for broadcasts when a device is discovered
	        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	        Screen.game.registerReceiver(Screen.game.mReceiver, filter);
	        // Register for broadcasts when discovery has finished
	        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	        Screen.game.registerReceiver(Screen.game.mReceiver, filter);  
	        // Register for broadcasts when device has connected
	        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
	        Screen.game.registerReceiver(Screen.game.mReceiver, filter);
	        // Register for broadcasts when disconnect requested
	        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
	        Screen.game.registerReceiver(Screen.game.mReceiver, filter);
	        // Register for broadcasts when disconnected
	        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
	        Screen.game.registerReceiver(Screen.game.mReceiver, filter);

	        // If we're already discovering, stop it
	        if (btAdapter.isDiscovering()) {
	            btAdapter.cancelDiscovery();
	        }

	        // Request discover from BluetoothAdapter
	        btAdapter.startDiscovery();
	    }
	    
	    public void endDiscovery() {
	    	
	    	if(D) Log.d("Bluetooth", "stopDiscovery()");
	    	 if (btAdapter != null) {
	             btAdapter.cancelDiscovery();
	         }
	    	 if(isReceiverRunning) {
	    		 Screen.game.unregisterReceiver(Screen.game.mReceiver);
	    		 isReceiverRunning = false;
	    	 }
	    }
	    
	    public void cancelDiscovery(){
	    	if(D) Log.d("Bluetooth", "cancelDiscovery()");
		   	 if (btAdapter != null) {
		            btAdapter.cancelDiscovery();
		      }
	    }
		
		/** Handles the call backs of bluetooth devices from Game's Broadcast Reciever.
		 * The if-else statements are in a direct correlation to the intent filters created in Game.startDiscovery() */
	    public final void handleBroadcastReceiver (Intent intent, String action){

	    	if (BluetoothDevice.ACTION_FOUND.equals(action)) { // When discovery finds a device

	    		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  // Get the BluetoothDevice object from the Intent

	    		if(D) Log.d("BluetoothDiscovery", "Device Found");

	    		if (device != null && device.getName() != null) {
	    			mNewDevices.add(device);
	    			if(getState() != BluetoothManager.STATE_CONNECTED)
	    				BluetoothManager.connectionStatus = "Found " + device.getName();
	    		}

	    	} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))  // done searching

	    		if (getState() == BluetoothManager.STATE_LISTEN)
	    			BluetoothManager.setState(BluetoothManager.STATE_READY);

	    		else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))  // Device is now connected
	    			if(D) Log.d("BluetoothDiscovery", "connected");

	    			else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action))  // Device is about to disconnect
	    				if(D) Log.d("BluetoothDiscovery", "connecting");

	    				else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)){ // Device has disconnected
	    					if(D) Log.d("BluetoothDiscovery", "disconnected");		
	    				}

	    }

}

