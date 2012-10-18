package com.dbz.verge;

//*** Work in Progress! Not currently being used, only the subclass, MicroWorld. ***
public class World {

	public enum WorldState {
		//Ready, // *Will use screen instance to handle?
		Running,
		//Paused, // *Will use screen instance to handle?
		GameOverWon,
		GameOverLost,
		Transition // *Will use screen instance to handle?
	}

	public static WorldState worldState;
	
//	public static MicroWorld currentMicroWorld;
	
	public int score;

	public World() {
		generateLevel();

		worldState = WorldState.Running;
		
//		currentMicroWorld = new MicroWorld(); // *Need to make a getRandomMicroWorld()*
		
		this.score = 0;
	}

	// Refactor this to prepareLevel?
	public void generateLevel() {
		// Logical preparation for level.
	}

	public void update(float deltaTime) {
		if (checkWon())
			checkGameOverWon();
		else if (checkLost())
			checkGameOverLost();
	}

	// updateRunning
	// updateTransition
	// 
	
	// *** Note on Below Methods: We might be able to get rid of checkGameOverWon/Lost()
	// and just use the checkWon/Lost in this class to assume that the game mode has been won.
	// In other words, The checks will see if the game set has been won or lost, instead of 
	// figuring out if the microgame has been won or lost. ***
	
	// Checks if MicroGame has been won. (Calls specific MicroWorld's checkWon())
	public boolean checkWon() {
		// currentMicroWorld.checkWon();
		// if (currentMicroWorld.microWorldState = MicroWorldState.Won)
		return true; //(currentMicroWorld.checkWon(touchPoint));
	}
	
	// Checks if MicroGame has been lost. (Calls specific MicroWorld's checkLost())
	public boolean checkLost() {
		// currentMicroWorld.checkLost();
		// if (currentMicroWorld.microWorldState = MicroWorldState.Lost)
		
		// if (currentMicroWorld.checkLost()) *Make checkLost return boolean*
		return true;
	}
	
	private void checkGameOverWon() {
		// Check for gameover, and then type (win or losing).
		// if (lives == 0)
		// else if (
	}
	
	private void checkGameOverLost() {
		
	}
}