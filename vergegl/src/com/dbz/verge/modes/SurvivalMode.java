package com.dbz.verge.modes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

import android.util.Log;

import com.dbz.framework.input.FileIO;
import com.dbz.verge.AssetsManager;
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

	//high score variables
	public static int[] surivalHighScores=new int[]{0,0,0,0,0};
	public static String[] extraHighScores=new String[]{"","","","",""};
	public final static String file = ".vergehighscores";
	// -------------------
	// --- Constructor ---
	// -------------------
	public SurvivalMode() {
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
		{
			modeState = ModeState.Lost;
			validHighScore(currentRound);

		}
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
		batcher.beginBatch(AssetsManager.vergeFont);
		AssetsManager.terminalFont.drawTextCentered(batcher, "Level: " + String.valueOf(level), 640, 450, 1.5f);
		AssetsManager.terminalFont.drawTextCentered(batcher, "Speed: " + String.valueOf(speed), 640, 400, 1.5f);
		AssetsManager.terminalFont.drawTextCentered(batcher, "Lives: " + String.valueOf(lives), 640, 350, 1.5f);
		AssetsManager.terminalFont.drawTextCentered(batcher, "Rounds Survived: " + String.valueOf(currentRound-1) + " / OVER 9000", 640, 300, 1.5f);
		batcher.endBatch();
	}
	//HIGH SCORES FOR SURIVAL MODE STORED INSIDE OF .vergehighscores
	public static void validHighScore(int score)
	{
		loadHighScores(game.getFileIO());
		int min=findMinScore();
		if(score>min)
		{
			for(int i=0;i<5;i++)
			{
				if(surivalHighScores[i]==min)
				{
					surivalHighScores[i]=score;
					break;
				}
			}
			organizeScores();
		}
		saveHighScores(game.getFileIO());
	}
	public static int findMinScore()
	{
		int min=0;
		for(int i=0;i<5;i++)
		{
			if(surivalHighScores[i]<min)
			{
				min=surivalHighScores[i];
			}
		}
		return min;
	}
	public static void organizeScores()
	{
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<4;j++)
			{
				int temp;
				if(surivalHighScores[j]<surivalHighScores[j+1])
				{
					temp=surivalHighScores[j];
					surivalHighScores[j]=surivalHighScores[j+1];
					surivalHighScores[j+1]=temp;
				}
			}
		}
	}
	public static void loadHighScores(FileIO files) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(files.readFile(file)));
			in.readLine();
			for(int i = 0; i < 5; i++) {
				surivalHighScores[i] = Integer.parseInt(in.readLine());
			}
			in.readLine();
			for(int i = 0; i < 5; i++) {
			extraHighScores[i] = in.readLine();
			}
		} catch (IOException e) {
			// :( It's ok we have defaults
		} catch (NumberFormatException e) {
			// :/ It's ok, defaults save our day
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}

	public static void saveHighScores(FileIO files) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					files.writeFile(file)));
			out.write("SURIVAL HIGH SCORES");
			out.write("\n");
			for(int i = 0; i < 5; i++) {
				out.write(Integer.toString(surivalHighScores[i]));
				out.write("\n");
			}
			out.write("TIME ATTACK HIGH SCORES");
			out.write("\n");
			for(int i = 0; i < 5; i++) {
				out.write(extraHighScores[i]);
				out.write("\n");
			}

		} catch (IOException e) {
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
	}

}
