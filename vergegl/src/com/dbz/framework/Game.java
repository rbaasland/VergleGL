package com.dbz.framework;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
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
}
