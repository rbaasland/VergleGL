package com.dbz.framework.impl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.R;
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

import com.dbz.framework.Audio;
import com.dbz.framework.FileIO;
import com.dbz.framework.Game;
import com.dbz.framework.Input;
import com.dbz.framework.Screen;
import com.dbz.verge.Menu;
import com.dbz.verge.MicroGame;
import com.dbz.verge.Mode;
import com.dbz.verge.MicroGame.MicroGameState;
import com.dbz.verge.Mode.GameState;
import com.dbz.verge.menus.GameGridMenu;
import com.dbz.verge.menus.MainMenu;
import com.dbz.verge.menus.PlayMenu;

public abstract class GLGame extends Activity implements Game, Renderer {
	enum GLGameState {
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
	GLGameState state = GLGameState.Initialized;
	Object stateChanged = new Object();
	long startTime = System.nanoTime();
	WakeLock wakeLock;
	Mode currentModeScreen; //is null when no mode is active

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
		fileIO = new AndroidFileIO(this);
		audio = new AndroidAudio(this);
		input = new AndroidInput(this, glView, 1, 1);
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");        
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
			if(state == GLGameState.Initialized)
				screen = getStartScreen();
			state = GLGameState.Running;
			screen.resume();
			startTime = System.nanoTime();
		}        
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {        
	}

	@Override
	public void onDrawFrame(GL10 gl) {                
		GLGameState state = null;

		synchronized(stateChanged) {
			state = this.state;
		}

		if(state == GLGameState.Running) {
			float deltaTime = (System.nanoTime()-startTime) / 1000000000.0f;
			startTime = System.nanoTime();

			screen.update(deltaTime);
			screen.present(deltaTime);
		}

		if(state == GLGameState.Paused) {
			screen.pause();            
			synchronized(stateChanged) {
				this.state = GLGameState.Idle;
				stateChanged.notifyAll();
			}
		}

		if(state == GLGameState.Finished) {
			screen.pause();
			screen.dispose();
			synchronized(stateChanged) {
				this.state = GLGameState.Idle;
				stateChanged.notifyAll();
			}            
		}
	}   

	@Override 
	public void onPause() {        
		synchronized(stateChanged) {
			if(isFinishing())            
				state = GLGameState.Finished;
			else
				state = GLGameState.Paused;
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

	@Override
	public Input getInput() {
		return input;
	}

	@Override
	public FileIO getFileIO() {
		return fileIO;
	}

	@Override
	public Audio getAudio() {
		return audio;
	}

	@Override
	public void setScreen(Screen screen) {
		if (screen == null)
			throw new IllegalArgumentException("Screen must not be null");

		this.screen.pause();
		this.screen.dispose();
		screen.resume();
		screen.update(0);
		this.screen = screen;
	}

	@Override
	public Screen getCurrentScreen() {
		return screen;
	}   

	
	/**
	 *  Set current mode screen. If a non Mode Screen is passed, current mode screen will be dereferenced via null pointer
	 */
	public void setCurrentModeScreen(Screen modeScreen) {
		if(modeScreen instanceof Mode)
			currentModeScreen = (Mode)modeScreen;
		else  currentModeScreen = null; 

	}

	
	@Override  //used for hardware back button
	public void onBackPressed() {

		/*
		 * Back button for menu navigation
		 */
		if (screen instanceof Menu){
			if (screen instanceof MainMenu)
				super.onBackPressed();

			else if (screen instanceof GameGridMenu){
				setScreen(new PlayMenu(this));
			}

			else if (screen instanceof PlayMenu){
				setScreen(new MainMenu(this));
				
			}

		} else

			/*
			 * Back button for GameGrid navigation
			 */   		
			if (screen instanceof MicroGame){

				MicroGame mg = (MicroGame)screen;

				switch(mg.microGameState){

				case Running:  //cases to pause
					mg.microGameState = MicroGameState.Paused;
					break;

				case Paused:   //cases to resume			
					mg.microGameState = MicroGameState.Running;
					break;

				default:
					break;
				}

			} else

				/*
				 * Back button for mode navigation
				 */	
				if(currentModeScreen != null){ //!null, mode is assumed to be active screen

					switch (currentModeScreen.gameState){


					case Transition: //cases to pause
						currentModeScreen.gameState = GameState.Paused;
						break;
					case Running:
						currentModeScreen.gameState = GameState.Paused;
						break;

						//The if-else-if here needed because the microgames are screens w/in a screen
						//to ensure the game resumes at the correct point, we add 2 sub-cases in respect to previous gameState
						//NOTE: This isn't needed if back button doesn't resume when paused. Not sure if it would ever get confused w/ back button in lower
						//left corner which returns to menu instead of unpausing. 

					case Paused:   //cases to resume
						if(currentModeScreen.previousGameState == GameState.Running)
							currentModeScreen.gameState = GameState.Running;

						else if(currentModeScreen.previousGameState == GameState.Transition || 
														currentModeScreen.previousGameState == GameState.Ready)
								currentModeScreen.gameState = GameState.Transition;
						//else if(currentModeScreen.previousGameState == GameState.Ready)
						//	currentModeScreen.gameState = GameState.Transition;
						break;

					case Ready:
					case Won:
					case Lost:
						setScreen(new PlayMenu(this));
						break;

					default:
						break;

					} 

				} 

	}
}
