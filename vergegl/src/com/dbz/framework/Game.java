package com.dbz.framework;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.dbz.framework.audio.Audio;
import com.dbz.framework.gl.GLGraphics;
import com.dbz.framework.gl.Screen;
import com.dbz.framework.input.FileIO;
import com.dbz.framework.input.Input;
import com.dbz.verge.AssetsManager;

public abstract class Game extends Activity implements Renderer {
	enum GameState {
		Initialized,
		Running,
		Paused,
		Finished,
		Idle
	}

	GLSurfaceView glView;    
	GLGraphics glGraphics;
	Audio audio;
	Input input;
	FileIO fileIO;
	Screen screen;
	GameState state = GameState.Initialized;
	Object stateChanged = new Object();
	long startTime = System.nanoTime();
	WakeLock wakeLock;
	
	//made public to avoid getters
    public BluetoothAdapter mBtAdapter;  
  //  public Set<BluetoothDevice> mPairedDevices;
    public HashSet<BluetoothDevice> mNewDevices; //list cuz of issues with init hashset
	
	

	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		glView = new GLSurfaceView(this);
		glView.setRenderer(this);
		setContentView(glView);

		glGraphics = new GLGraphics(glView);
		fileIO = new FileIO(this);
		audio = new Audio(this);
		input = new Input(this, glView, 1, 1);
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Game");
		
		//should check if bluetooth is supported first...
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		//mPairedDevices = mBtAdapter.getBondedDevices();
		  
	}

	public void onResume() {
		super.onResume();
		glView.onResume();
		wakeLock.acquire();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glGraphics.setGL(gl);
		
		//checkSupportedTextureCompression(gl);
		//checkOpenGLPrimativeSizes(gl);
		
		synchronized(stateChanged) {
			if(state == GameState.Initialized)
				screen = getStartScreen();
			state = GameState.Running;
			screen.resume();
			startTime = System.nanoTime();
		}        
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {}

	@Override
	public void onDrawFrame(GL10 gl) {
		GameState state = null;

		synchronized(stateChanged) {
			state = this.state;
		}

		if(state == GameState.Running) {
			float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f;
			startTime = System.nanoTime();

			screen.update(deltaTime);
			screen.present(deltaTime);
		}

		if(state == GameState.Paused) {
			screen.pause();            
			synchronized(stateChanged) {
				this.state = GameState.Idle;
				stateChanged.notifyAll();
			}
		}

		if(state == GameState.Finished) {
			screen.pause();
			screen.dispose();
			synchronized(stateChanged) {
				this.state = GameState.Idle;
				stateChanged.notifyAll();
			}            
		}
	}   

	@Override 
	public void onPause() {
		synchronized(stateChanged) {
			if(isFinishing())            
				state = GameState.Finished;
			else
				state = GameState.Paused;
			while(true) {
				try {
					stateChanged.wait();
					break;
				} catch(InterruptedException e) {         
				}
			}
		}
		wakeLock.release();
		glView.onPause();  
		super.onPause();
	}    

	public GLGraphics getGLGraphics() {
		return glGraphics;
	}  

	public Input getInput() {
		return input;
	}

	public FileIO getFileIO() {
		return fileIO;
	}

	public Audio getAudio() {
		return audio;
	}

	public void setScreen(Screen screen) {
		if (screen == null)
			throw new IllegalArgumentException("Screen must not be null");

		this.screen.pause();
		this.screen.dispose();
		screen.resume();
		screen.update(0);
		this.screen = screen;
	}

	public Screen getCurrentScreen() {
		return screen;
	}
	
	public abstract Screen getStartScreen();
	
	//Logs supported texture compression for device 
	public void checkSupportedTextureCompression(GL10 gl){
		
		String s = gl.glGetString(GL10.GL_EXTENSIONS);
		
        if (s.contains("GL_IMG_texture_compression_pvrtc")){       //Use PVR compressed textures 
        	Log.d("GL_EXTENSIONS", "pvrtc");
        	
        }else if (s.contains("GL_AMD_compressed_ATC_texture") ||
                 s.contains("GL_ATI_texture_compression_atitc")){  //Load ATI Textures     
        	Log.d("GL_EXTENSIONS", "ATITC"); 
        	
        }else if (s.contains("GL_OES_texture_compression_S3TC") ||
                   s.contains("GL_EXT_texture_compression_s3tc")){  //Use DTX Textures
        	Log.d("GL_EXTENSIONS", "S3TC");
        	
        }else{														//No texture compression found. 
        	Log.d("GL_EXTENSIONS", "NONE");
        }         
	}
	
	//Logs Primatives Sizes
	public void checkOpenGLPrimativeSizes(GL10 gl){
		
		IntBuffer params = IntBuffer.allocate(2);
		
		gl.glGetIntegerv(GL10.GL_ALIASED_LINE_WIDTH_RANGE, params);
		Log.d("GL_PRIMATIVE_SIZE", "ALIASED_LINE_WIDTH_RANGE = " + params.get(0) + " to " + params.get(1));
			
		gl.glGetIntegerv(GL10.GL_SMOOTH_LINE_WIDTH_RANGE, params);
		Log.d("GL_PRIMATIVE_SIZE", "SMOOTH_LINE_WIDTH_RANGE = " + params.get(0) + " to " + params.get(1));
			
		gl.glGetIntegerv(GL10.GL_ALIASED_POINT_SIZE_RANGE, params);
		Log.d("GL_PRIMATIVE_SIZE", "ALIASED_POINT_SIZE_RANGE = " + params.get(0) + " to " + params.get(1));
			
		gl.glGetIntegerv(GL10.GL_SMOOTH_POINT_SIZE_RANGE, params);
		Log.d("GL_PRIMATIVE_SIZE", "SMOOTH_POINT_SIZE_RANGE = " + params.get(0) + " to " + params.get(1));
		
	}


	@Override  //used for hardware back button
	public void onBackPressed() {
    	AssetsManager.playSound(AssetsManager.clickSound);
		screen.onBackPressed(); //lil strategy pattern-esk - each instance of screen implements its own onBackPressed to define behavior.
		
	}
	
	// ---------------
	// -- Bluetooth --
	// ---------------
	
	
    public void ensureDiscoverable() {
       // if(D) Log.d(TAG, "ensure discoverable");
        if (mBtAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
            startActivity(discoverableIntent);
        }
    }
	
    /**
     * Start device discover with the BluetoothAdapter
     */
    public void startDiscovery() {
        Log.d("Bluetooth", "startDiscovery()");
        
        //Might not need all of these, but may come in handy when troubleshooting
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);  
        // Register for broadcasts when device has connected
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(mReceiver, filter);
        // Register for broadcasts when disconnect requested
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        this.registerReceiver(mReceiver, filter);
        // Register for broadcasts when disconnected
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);

        
        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }
    
    public void endDiscovery() {
    	Log.d("Bluetooth", "stopDiscovery()");
    	 if (mBtAdapter != null) {
             mBtAdapter.cancelDiscovery();
         }
    	 //TODO LATER, PROBABLY SHOULD MOVE THIS SOMEWHERE ELSE... FOR NOW OK, BUT NOT OK IF WE BACK OUT OF BLUETOOTH SCREEN
    	 // AND EXPECT TO MAINTAIN CONNECTION
    	this.unregisterReceiver(mReceiver);
    }
    
    // The BroadcastReceiver that listens for discovered devices and add the device to the new devices array
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if (intent == null) 
        		return;
        	
            String action = intent.getAction();
            
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
               // if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                if (true){
                	if(mNewDevices == null){
                		Log.d("Bluetooth", "Device Found");
                		mNewDevices = new HashSet<BluetoothDevice>();
                	}
                	mNewDevices.add(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	//done searching
            }
            else if (BluetoothDevice .ACTION_ACL_CONNECTED.equals(action)) {
                 //Device is now connected
            	Log.d("Bluetooth", "connected");
             }
             else if (BluetoothDevice .ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                 //Device is about to disconnect
            	 Log.d("Bluetooth", "connecting");
             }
             else if (BluetoothDevice .ACTION_ACL_DISCONNECTED.equals(action)) {
                 //Device has disconnected
            	 Log.d("Bluetooth", "disconnected");
             } 
        }
    };
    
}
