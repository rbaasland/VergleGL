package com.dbz.verge.modes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Random;

import android.util.Log;

import com.dbz.framework.BluetoothManager;
import com.dbz.framework.BluetoothManager.*;
import com.dbz.framework.gl.Screen;
import com.dbz.framework.input.FileIO;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.Mode;
import com.dbz.verge.Mode.ModeState;

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

	// High Score Variables
	public static int[] surivalHighScores=new int[]{0,0,0,0,0};
	public static String[] extraHighScores=new String[]{"","","","",""};
	public final static String file = ".vergehighscores";
	
	public String otherPlayerIsReady = "NO";
	
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
		if (lives <= 0) {
			if (Mode.isMultiplayer)
				bluetoothManager.mConnectedThread.write("LOST".toString().getBytes());
			
			modeState = ModeState.Lost;
			validHighScore(currentRound-1);
		}
		else {
			currentRound++;
			modeState = ModeState.Transition;
		}
	}
	
	// Prepares the next MicroGame for launching.
	public void updateTransition(float deltaTime) {
		// Collects total time spent in Transition state.
		totalTransitionTime += deltaTime;
		
		if (Mode.isMultiplayer) {
			
			if (BluetoothManager.getState() != BluetoothManager.STATE_CONNECTED){
				bluetoothCurrentTime += deltaTime; //original
//				if(BluetoothManager.getState() == BluetoothManager.STATE_LISTEN)
//					bluetoothCurrentTime += deltaTime / (BluetoothManager.getEstimatedConnectionTime() / BluetoothManager.defaultDiscoveryTime);
//				else 
//					bluetoothCurrentTime = 0;
			}
			
			if(BluetoothManager.mState == BluetoothManager.STATE_CONNECTED) {
				otherPlayerIsReady = game.messageRead;
				Log.d("SurvivalModeMultiplayer", otherPlayerIsReady);
				if (otherPlayerIsReady.equals("LOST")) {
					modeState = ModeState.Won;
					validHighScore(currentRound-1);
				}
				
				// TODO Redundant to else if on line 112?
				if (!otherPlayerIsReady.equals("YES"))
					totalTransitionTime = 0;
				
				if (!loadComplete) {
					loadNextMicroGame();
					bluetoothManager.mConnectedThread.write("YES".toString().getBytes());
				}
				// After the time limit has past and load has completed, switch to running state.
				else if (totalTransitionTime >= transitionTimeLimit && otherPlayerIsReady.equals("YES")) {
					game.messageRead = "NO";
					totalTransitionTime = 0;
					modeState = ModeState.Running;
					previousModeState = modeState; // TODO: seems counter intuitive, but it tells game how to handle pause
					return;
				}
				
			}
		} else {
			if (!loadComplete)
				loadNextMicroGame();
			// After the time limit has past and load has completed, switch to running state.
			else if (totalTransitionTime >= transitionTimeLimit) {
				totalTransitionTime = 0;
				modeState = ModeState.Running;
				previousModeState = modeState; // TODO: seems counter intuitive, but it tells game how to handle pause
				return;
			}
		}
		
		// Gets all TouchEvents and stores them in a list.
	    List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
	    
	    // Cycles through and tests all touch events.
	    int len = touchEvents.size();
	    for(int i = 0; i < len; i++) {
	    	// Gets a single TouchEvent from the list.
	        TouchEvent event = touchEvents.get(i);
	        
	        // Skip handling if the TouchEvent isn't TOUCH_UP.
	        if(event.type != TouchEvent.TOUCH_UP)
	            continue;

	        // Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
	        touchPoint.set(event.x, event.y);
	        // Sends the vector to the OpenGL Camera for handling.
	        guiCam.touchToWorld(touchPoint);
	        
	        // Pause Toggle Bounds Check.
	        if(OverlapTester.pointInRectangle(pauseToggleBounds, touchPoint)) {
	            AssetsManager.playSound(AssetsManager.clickSound);
	            totalTransitionTime = 0;
	            previousModeState = modeState; //transition
	            modeState = ModeState.Paused;
	            return;
	        }  
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
		AssetsManager.vergeFont.drawTextLeft(batcher, "Level: " + String.valueOf(level), 315, startY);
		AssetsManager.vergeFont.drawTextLeft(batcher, "Speed: " + String.valueOf(speed), 315, startY-30);
		AssetsManager.vergeFont.drawTextLeft(batcher, "Lives: " + String.valueOf(lives), 315, startY-60);
		AssetsManager.vergeFont.drawTextLeft(batcher, "Rounds Survived: " + String.valueOf(currentRound-1) + " / OVER 9000", 315, startY-90);
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
