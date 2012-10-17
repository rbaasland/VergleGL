package com.dbz.verge;

public class World {

	public enum WorldState {
		Ready,
		Running,
		Paused,
		Transition,
		GameOverWon,
		GameOverLost
	}

	public WorldState worldState;
	
	public int score;

	public World() {
		generateLevel();

		this.score = 0;
		worldState = WorldState.Running;
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

	// Checks if MicroGame has been won. (Calls specific MicroWorld's checkWon())
	public boolean checkWon() {
		// currentMicroWorld.checkWon();
		// if (currentMicroWorld.microWorldState = MicroWorldState.Won)
		return true;
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