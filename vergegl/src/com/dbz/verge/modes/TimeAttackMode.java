package com.dbz.verge.modes;

import com.dbz.framework.Game;
import com.dbz.verge.Assets;
import com.dbz.verge.Mode;

// TODO: Display total time accumulated on status report.
public class TimeAttackMode extends Mode {

	// --------------
	// --- Fields ---
	// --------------
	
	// Tracks win count, history, and required MicroGame wins to win the TimeAttack.
    public int winCount = 0;
    public int winCountHistory = 0;
    public int winsRequired; // * Initialized in Constructor. *
	
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
	// TODO: Insert MicroGame Win handling.
	public void updateMicroGameWon() {
		winCount++;
		if (winCount > winsRequired)
			gameState = GameState.Won;
		else
			gameState = GameState.Transition;
	}
	
	@Override
	// TODO: Insert MicroGame Loss handling.
	public void updateMicroGameLost() {
		gameState = GameState.Transition;
	}
	
	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	@Override
	// TODO: Insert Time Attack setup implementation, unique code.
	// Goes through MicroGames sequentially.
	public void setupNextMicroGame() {
		super.setupNextMicroGame();
		
		// If player won the last MicroGame, then procede to next MicroGame in set.
		if (winCountHistory < winCount) {
			winCountHistory = winCount;
			if (microGameIndex < microGames.length)
				microGameIndex++;
			else
				microGameIndex = 0;
		}
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------

	@Override
	public void presentStatusReport() {
		batcher.beginBatch(Assets.items);
	    Assets.font.drawText(batcher, "Level: " + String.valueOf(level), 600, 550);
	    Assets.font.drawText(batcher, "Speed: " + String.valueOf(speed), 600, 500);
	    Assets.font.drawText(batcher, "Win Count: " + String.valueOf(winCount) + " / " + String.valueOf(winsRequired), 600, 400);
		batcher.endBatch();
	}
	
}
