package com.dbz.verge;

import com.dbz.framework.Game;
import com.dbz.framework.impl.GLScreen;

// Will use this class to implement the extra features of MicroGame
// That aren't shared with the Screen subclass.
// *Might make this inherit off of a GameScreen instance?*
public abstract class MicroGameScreen extends GLScreen {
	
	public enum MicroGameState {
		Ready,
		Running,
		Paused,
		Won,
		Lost,
		// Transition // ***???***
	}
	
	public MicroGameState microGameState;
	
	// Constructor
	public MicroGameScreen(Game game) {
		super(game);
		microGameState = MicroGameState.Ready;
	}

	// *** Update Methods ***
	
	@Override
	public abstract void update(float deltaTime);
	
	public abstract void updateReady();
	
	public abstract void updateRunning(float deltaTime);
	
	public abstract void updatePaused();
	
	public abstract void updateWon();
	
	public abstract void updateLost();

	// *** Draw Methods ***
	@Override
	public abstract void present(float deltaTime);
	
	public abstract void presentReady();
	
	public abstract void presentRunning();
	
	public abstract void presentPaused();
	
	public abstract void presentWon();
	
	public abstract void presentLost();
	
	// *** Android State Management ***
	
	@Override
	public abstract void pause();

	@Override
	public abstract void resume();

	@Override
	public abstract void dispose();
}
