package com.dbz.framework.impl;

import com.dbz.framework.Game;

// Will use this class to implement the extra features of MicroGame
// That aren't shared with the Screen subclass.
public abstract class MicroGame extends GLScreen {
	
	public enum MicroGameState {
		Ready,
		Running,
		Paused,
		Won,
		Lost
	}
	
	public MicroGameState microGameState;
	
	// Constructor
	public MicroGame(Game game) {
		super(game);
		microGameState = MicroGameState.Ready;
	}

	// Update Methods
	
	@Override
	public abstract void update(float deltaTime);
	
	public abstract void updateReady();
	
	public abstract void updateRunning(float deltaTime);
	
	public abstract void updatePaused(float deltaTime);
	
	public abstract void updateWon();
	
	public abstract void updateLost();

	// Draw Methods
	@Override
	public abstract void present(float deltaTime);
	
	public abstract void presentReady();
	
	public abstract void presentRunning();
	
	public abstract void presentPaused();
	
	public abstract void presentWon();
	
	public abstract void presentLost();
	
	// Utility Draw Methods
	
	// Needs implementation. (Draws instruction for the microgame to the screen.)
	public void drawInstruction() {}
	
	// ***NEED TO CHANGE IMPLEMENTATION TO SUPPORT OPENGL***
	// Draws numbers to the screen in pixmap format.
//	public void drawNumbers(Graphics g, String line, int x, int y) {
//		int len = line.length();
//		for (int i = 0; i < len; i++) {
//			char character = line.charAt(i);
//
//			if (character == ' ') {
//				x += 20;
//				continue;
//			}
//
//			int srcX = 0;
//			int srcWidth = 0;
//			if (character == '.') {
//				srcX = 200;
//				srcWidth = 10;
//			} else {
//				srcX = (character - '0') * 20;
//				srcWidth = 20;
//			}
//
//			g.drawPixmap(Assets.numbers, x, y, srcX, 0, srcWidth, 32);
//			x += srcWidth;
//		}
//	}
	
	// Android State Management
	
	@Override
	public abstract void pause();

	@Override
	public abstract void resume();

	@Override
	public abstract void dispose();
}
