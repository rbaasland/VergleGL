package com.dbz.framework;

import java.nio.IntBuffer;
import java.util.Collections;
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
import android.os.Handler;
import android.os.Message;
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
	Screen prevScreen; //used to hold previous screen (needed for Help Menu back buttons from any non-microgame screen)
	GameState state = GameState.Initialized;
	Object stateChanged = new Object();
	long startTime = System.nanoTime();
	WakeLock wakeLock;
	
	// Bluetooth Message Passing
    public String messageRead = "";
    public String messageWrite = "";
    
	// Message types
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    
    public BluetoothAdapter mBtAdapter;  
    
    // Debugging
    private static final boolean D = false;
	

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
		mBtAdapter = BluetoothAdapter.getDefaultAdapter(); // TODO should check if device is bluetooth capable and propagate result from here (not assume all devices have bluetooth)
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
		
		//checkSupportedTextureCompression(gl); //make sure debug (D) is true before using
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
		this.screen.dispose(); //may cause problems with setPrevScreen if assets are destroyed here
		this.prevScreen = this.screen;
		screen.resume();
		screen.update(0);
		this.screen = screen;
	}
	
	/** Sets the current screen back to the previous screen 
	 * Problems may occur if needed resources are destroyed in the dispose() method*/
	public void returnToPreviousScreen() {
		if (prevScreen == null)
			throw new NullPointerException ("Previous Screen must not be null");

		Screen swapScreen;
		this.screen.pause();
		this.screen.dispose();
		swapScreen = this.prevScreen; //swap screens
		this.prevScreen = this.screen;
		this.screen = swapScreen;
		
		this.screen.resume();
		this.screen.update(0);
	}

	public Screen getCurrentScreen() {
		return screen;
	}
	
	public abstract Screen getStartScreen();
	
	//Logs supported texture compression for device 
	public void checkSupportedTextureCompression(GL10 gl){
		
		String s = gl.glGetString(GL10.GL_EXTENSIONS);
		
        if (s.contains("GL_IMG_texture_compression_pvrtc")){       //Use PVR compressed textures 
        	if(D) Log.d("GL_EXTENSIONS", "pvrtc");
        	
        }else if (s.contains("GL_AMD_compressed_ATC_texture") ||
                 s.contains("GL_ATI_texture_compression_atitc")){  //Load ATI Textures     
        	if(D) Log.d("GL_EXTENSIONS", "ATITC"); 
        	
        }else if (s.contains("GL_OES_texture_compression_S3TC") ||
                   s.contains("GL_EXT_texture_compression_s3tc")){  //Use DTX Textures
        	if(D) Log.d("GL_EXTENSIONS", "S3TC");
        	
        }else{														//No texture compression found. 
        	if(D) Log.d("GL_EXTENSIONS", "NONE");
        }         
	}
	
	//Logs Primatives Sizes
	public void checkOpenGLPrimativeSizes(GL10 gl){
		
		IntBuffer params = IntBuffer.allocate(2);
		
		gl.glGetIntegerv(GL10.GL_ALIASED_LINE_WIDTH_RANGE, params);
		if(D) Log.d("GL_PRIMATIVE_SIZE", "ALIASED_LINE_WIDTH_RANGE = " + params.get(0) + " to " + params.get(1));
			
		gl.glGetIntegerv(GL10.GL_SMOOTH_LINE_WIDTH_RANGE, params);
		if(D) Log.d("GL_PRIMATIVE_SIZE", "SMOOTH_LINE_WIDTH_RANGE = " + params.get(0) + " to " + params.get(1));
			
		gl.glGetIntegerv(GL10.GL_ALIASED_POINT_SIZE_RANGE, params);
		if(D) Log.d("GL_PRIMATIVE_SIZE", "ALIASED_POINT_SIZE_RANGE = " + params.get(0) + " to " + params.get(1));
			
		gl.glGetIntegerv(GL10.GL_SMOOTH_POINT_SIZE_RANGE, params);
		if(D) Log.d("GL_PRIMATIVE_SIZE", "SMOOTH_POINT_SIZE_RANGE = " + params.get(0) + " to " + params.get(1));
		
	}

	@Override  //used for hardware back button
	public void onBackPressed() {
    	AssetsManager.playSound(AssetsManager.clickSound);
		screen.onBackPressed(); //lil strategy pattern-esk - each instance of screen implements its own onBackPressed to define behavior.
	}
	
	// ---------------
	// -- Bluetooth --
	// ---------------

    // The Handler that gets information back from BluetoothManager to pass messages between devices
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                messageWrite = new String(writeBuf);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                messageRead = new String(readBuf, 0, msg.arg1);
                break;
            case MESSAGE_DEVICE_NAME:
                break;
            }
        }
    };
    
    // The BroadcastReceiver that listens for discovered devices and add the device to the new devices array
    public BroadcastReceiver mReceiver = null; // initialized in BluetoothManager's constructor.
}
