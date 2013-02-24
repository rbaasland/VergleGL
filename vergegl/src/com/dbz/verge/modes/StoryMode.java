package com.dbz.verge.modes;

import java.util.Random;

import android.util.Log;

import com.dbz.verge.AssetsManager;
import com.dbz.verge.Mode;

//TODO: Currently, this class is just a copied version of Survival...
//...so we need to actually change the implementation to be StoryMode.
// TODO: Implement better random number generation?
public class StoryMode extends Mode {

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
	public StoryMode() {
		
		indexHistory = new int[microGames.length];
		clearIndexHistory();
	}
	
	// ----------------------
	// --- Update Methods ---
	// ----------------------
	
	@Override
	public void updateMicroGameWon() {
		currentRound++;
		modeState = ModeState.Transition;
	}
	
	@Override
	public void updateMicroGameLost() {
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
	public void presentStatusReport(int startY) {		
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		AssetsManager.vergeFont.drawTextCentered(batcher, "Level: " + String.valueOf(level), 640, 450, 1.5f);
		AssetsManager.vergeFont.drawTextCentered(batcher, "Speed: " + String.valueOf(speed), 640, 400, 1.5f);
		AssetsManager.vergeFont.drawTextCentered(batcher, "Lives: " + String.valueOf(lives), 640, 350, 1.5f);
		AssetsManager.vergeFont.drawTextCentered(batcher, "Rounds Survived: " + String.valueOf(currentRound-1) + " / OVER 9000", 640, 300, 1.5f);
		batcher.endBatch();
	}
}

