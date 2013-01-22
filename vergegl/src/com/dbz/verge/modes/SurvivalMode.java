package com.dbz.verge.modes;

import java.util.Random;

import android.util.Log;

import com.dbz.framework.Game;
import com.dbz.verge.Assets;
import com.dbz.verge.Mode;

// TODO: Implement better random number generation?
//		...Add all MicroGames to this and TimeAttack
public class SurvivalMode extends Mode {

	// --------------
	// --- Fields ---
	// --------------
	
	// Tracks the amount of failed MicroGames that the player is allowed.
	public int lives = 3;
	
    // Random number generator used for randomizing games.
    public Random random = new Random();
	
    // Keeps track of randomized indexes.
    public int indexHistory[];
	
	// -------------------
	// --- Constructor ---
	// -------------------
	public SurvivalMode(Game game) {
		super(game);
		
		indexHistory = new int[microGames.length];
		clearIndexHistory();
	}
	
	// ----------------------
	// --- Update Methods ---
	// ----------------------
	
	@Override
	public void updateMicroGameWon() {
		super.updateMicroGameWon();
		
		modeState = ModeState.Transition;
	}
	
	@Override
	public void updateMicroGameLost() {
		super.updateMicroGameLost();
		
		lives--;
		if (lives <= 0)
			modeState = ModeState.Lost;
		else {
			currentRound++;
			modeState = ModeState.Transition;
		}
	}
	
	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	@Override
	// Randomly generates next MicroGame, Shuffle-style (Dependent).
	public void loadNextMicroGame() {
		// Checks the indexHistory for fullness
		if (!checkIndex(-1))
			clearIndexHistory();

		// Randomizes the microGameIndex (Dependent)
		do
		{
			microGameIndex = random.nextInt(microGames.length);
		} while(checkIndex(microGameIndex));
		
		// Log out for testing purposes.
		for(int i = 0; i < indexHistory.length; i++)
			Log.d("indexHistory", "Index History = " + indexHistory[i]);
		
		super.loadNextMicroGame();
	}
	
	// * Used for Shuffle-style random implementation. *
	// Returns false if the index wasn't found in the array.
	public boolean checkIndex(int index)
	{
		for(int i = 0; i < indexHistory.length; i++)
		{
			if(indexHistory[i] == index)
				return true;
			else if (indexHistory[i] == -1) {
				indexHistory[i] = microGameIndex;
				break;
			}
		}
		return false;
	}
	
	// * Used for Shuffle-style random implementation. *
	// Clears indexHistory.
	public void clearIndexHistory()
	{
		for (int i = 0; i < indexHistory.length; i++)
			indexHistory[i] = -1;
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------

	@Override
	public void presentStatusReport() {		
		batcher.beginBatch(Assets.vergeFont);
		Assets.terminalFont.drawTextCentered(batcher, "Level: " + String.valueOf(level), 640, 450, 1.5f);
		Assets.terminalFont.drawTextCentered(batcher, "Speed: " + String.valueOf(speed), 640, 400, 1.5f);
		Assets.terminalFont.drawTextCentered(batcher, "Lives: " + String.valueOf(lives), 640, 350, 1.5f);
		Assets.terminalFont.drawTextCentered(batcher, "Rounds Survived: " + String.valueOf(currentRound-1) + " / OVER 9000", 640, 300, 1.5f);
		batcher.endBatch();
	}
	
}
