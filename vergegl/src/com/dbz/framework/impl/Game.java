package com.dbz.framework.impl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

import com.dbz.framework.Input;
import com.dbz.verge.Assets;

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
		input = new AndroidInput(this, glView, 1, 1);
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

	@Override  //used for hardware back button
	public void onBackPressed() {
    	Assets.playSound(Assets.clickSound);
		screen.onBackPressed(); //lil strategy pattern-esk - each instance of screen implements its own onBackPressed to define behavior.
		
	}
}
