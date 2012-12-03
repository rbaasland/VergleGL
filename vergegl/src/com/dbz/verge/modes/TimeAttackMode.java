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
    
    // Tracks total time accumulated.
    public float totalTime;
    public int totalMinutes;
    public float totalSeconds;
	
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
		totalTime += microGames[microGameIndex].totalRunningTime;
		currentRound++;
		
		if (currentRound > winsRequired)
			modeState = ModeState.Won;
		else
			modeState = ModeState.Transition;
	}
	
	@Override
	public void updateMicroGameLost() {
		totalTime += microGames[microGameIndex].totalMicroGameTime[speed-1];
		modeState = ModeState.Transition;

		// TODO: Might want to take a better approach to this.
		//		 Currently decrementing to counter the increment that would still occur on loss.
		if (currentRound % roundsToLevelUp == 0)
			level--;
		if (currentRound % roundsToSpeedUp == 0)
			speed--;	
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
		totalMinutes = (int)(totalTime / 60);
		totalSeconds = totalTime % 60.0f;
		
		batcher.beginBatch(Assets.vergeFont);
		Assets.terminalFont.drawTextCentered(batcher, "Level: " + String.valueOf(level), 640, 450, 1.5f);
		Assets.terminalFont.drawTextCentered(batcher, "Speed: " + String.valueOf(speed), 640, 400, 1.5f);
		Assets.terminalFont.drawTextCentered(batcher, "Wins: " + String.valueOf(currentRound-1) + " / " + String.valueOf(winsRequired), 640, 350, 1.5f);
		Assets.terminalFont.drawTextCentered(batcher, "Total Time: " + String.format("%02d:%05.2f", totalMinutes, totalSeconds), 640, 300, 1.5f);
		batcher.endBatch();
	}
	
}
