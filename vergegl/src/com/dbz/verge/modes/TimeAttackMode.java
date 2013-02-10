package com.dbz.verge.modes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.dbz.framework.input.FileIO;
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

	//high score variables
	public static float[] timeHighScores=new float[]{3600,3600,3600,3600,3600};
	public static String[] extraHighScores=new String[]{"","","","",""};
	public final static String file = ".vergehighscores";
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

		if (currentRound > 3)
		{
			modeState = ModeState.Won;
			validHighScore(totalTime);
		}
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
	//HIGH SCORES FOR TIME ATTACK MODE STORED INSIDE OF .vergehighscores
	public static void validHighScore(float score)
	{
		loadHighScores(game.getFileIO());
		float max=findMaxScore();
		if(score<max)
		{
			for(int i=0;i<5;i++)
			{
				if(timeHighScores[i]==max)
				{
					timeHighScores[i]=score;
					break;
				}
			}
			organizeScores();
		}
		saveHighScores(game.getFileIO());
	}
	public static float findMaxScore()
	{
		float max=0;
		for(int i=0;i<5;i++)
		{
			if(timeHighScores[i]>max)
			{
				max=timeHighScores[i];
			}
		}
		return max;
	}
	public static void organizeScores()
	{
		for(int i=0;i<5;i++)
		{
			for(int j=0;j<4;j++)
			{
				float temp;
				if(timeHighScores[j]>timeHighScores[j+1])
				{
					temp=timeHighScores[j];
					timeHighScores[j]=timeHighScores[j+1];
					timeHighScores[j+1]=temp;
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
				extraHighScores[i] = in.readLine();
			}
			in.readLine();
			for(int i = 0; i < 5; i++) {
				timeHighScores[i] = Float.parseFloat(in.readLine());
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
				out.write(extraHighScores[i]);
				out.write("\n");
			}
			out.write("TIME ATTACK HIGH SCORES");
			out.write("\n");
			for(int i = 0; i < 5; i++) {
				out.write(Float.toString(timeHighScores[i]));
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
