package com.dbz.verge.modes;

import com.dbz.verge.AssetsManager;
import com.dbz.verge.Mode;

public class TimeAttackMode extends Mode {

	// --------------
	// --- Fields ---
	// --------------
	
	// Tracks required MicroGame wins to win the TimeAttack.
	// * Initialized in Constructor. *
    public int winsRequired;
    
    // Tracks total time accumulated.
    public float totalTime;
    public int totalMinutes;
    public float totalSeconds;
	
	// -------------------
	// --- Constructor ---
	// -------------------
	public TimeAttackMode() {
		winsRequired = microGames.length * 3;
		roundsToLevelUp = microGames.length;
		roundsToSpeedUp = microGames.length;
	}
	
	// ----------------------
	// --- Update Methods ---
	// ----------------------
	
	@Override
	public void updateMicroGameWon() {
		super.updateMicroGameWon();
		
		totalTime += microGames[microGameIndex].totalRunningTime;
		
		if (currentRound > winsRequired)
			modeState = ModeState.Won;
		else
			modeState = ModeState.Transition;
	}
	
	@Override
	public void updateMicroGameLost() {
		super.updateMicroGameLost();
		
		totalTime += microGames[microGameIndex].totalMicroGameTime;
		modeState = ModeState.Transition;

		// TODO: Might want to take a better approach to this.
		//		 Currently decrementing to counter the increment that would still occur on loss.
		if ((currentRound-1) % roundsToLevelUp == 0 && currentRound != 1)
			if (level != 3)
				level--;
		if ((currentRound-1) % roundsToSpeedUp == 0 && currentRound != 1)
			if (speed != 3)
				speed--;	
	}
	
	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	@Override
	// Goes through MicroGames sequentially.
	public void loadNextMicroGame() {
		microGameIndex = (currentRound-1) % microGames.length;
		super.loadNextMicroGame();
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------

	@Override
	public void presentStatusReport() {
		totalMinutes = (int)(totalTime / 60);
		totalSeconds = totalTime % 60.0f;
		
		batcher.beginBatch(AssetsManager.vergeFont);
		AssetsManager.terminalFont.drawTextCentered(batcher, "Level: " + String.valueOf(level), 640, 450, 1.5f);
		AssetsManager.terminalFont.drawTextCentered(batcher, "Speed: " + String.valueOf(speed), 640, 400, 1.5f);
		AssetsManager.terminalFont.drawTextCentered(batcher, "Wins: " + String.valueOf(currentRound-1) + " / " + String.valueOf(winsRequired), 640, 350, 1.5f);
		AssetsManager.terminalFont.drawTextCentered(batcher, "Total Time: " + String.format("%02d:%05.2f", totalMinutes, totalSeconds), 640, 300, 1.5f);
		batcher.endBatch();
	}
	
}
