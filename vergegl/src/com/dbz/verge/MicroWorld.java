package com.dbz.verge;

public class MicroWorld extends World {
	private enum MicroWorldState {
		Running,
		Paused,
		Won,
		Lost
	}
	
	MicroWorldState microWorldState;
	
	public MicroWorld() {
		this.microWorldState = MicroWorldState.Running;
	}
	
	@Override
	public void generateLevel() {}
	
	@Override
	public boolean checkWon() {
		// Check for MicroGame win conditions, returns boolean.
		return true;
	}
	
	@Override
	public boolean checkLost() {
		// Check for MicroGame lose conditions, returns boolean.
		return true;
	}
}
