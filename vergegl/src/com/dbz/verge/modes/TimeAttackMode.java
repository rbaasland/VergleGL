package com.dbz.verge.modes;

import com.dbz.framework.Game;
import com.dbz.verge.Assets;
import com.dbz.verge.Mode;

// TODO: Display total time accumulated on status report.
// TODO: Currently, if you lose round on the round before level/speed up will occur...
//		 ...you will be forced to replay the game you lost at a higher speed and level than originally.

public class TimeAttackMode extends Mode {

	// --------------
	// --- Fields ---
	// --------------
	
	// Tracks required MicroGame wins to win the TimeAttack.
	// * Initialized in Constructor. *
    public int winsRequired;
	
	// -------------------
	// --- Constructor ---
	// -------------------
	public TimeAttackMode(Game game) {
		super(game);
		
		winsRequired = microGames.length * 3;
		roundsToLevelUp = microGames.length;
		roundsToSpeedUp = microGames.length;
	}
	
	// ----------------------
	// --- Update Methods ---
	// ----------------------
	
	@Override
	public void updateMicroGameWon() {
		currentRound++;
		if (currentRound > winsRequired)
			modeState = ModeState.Won;
		else
			modeState = ModeState.Transition;
	}
	
	@Override
	public void updateMicroGameLost() {
		modeState = ModeState.Transition;
	}
	
	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	@Override
	// Goes through MicroGames sequentially.
	public void setupNextMicroGame() {
		microGameIndex = (currentRound-1) % microGames.length;
		super.setupNextMicroGame();		
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------

	@Override
	public void presentStatusReport() {
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, "Level: " + String.valueOf(level), 600, 550);
	    Assets.font.drawText(batcher, "Speed: " + String.valueOf(speed), 600, 500);
	    Assets.font.drawText(batcher, "Wins: " + String.valueOf(currentRound-1) + " / " + String.valueOf(winsRequired), 600, 400);
		batcher.endBatch();
	}
	
}
