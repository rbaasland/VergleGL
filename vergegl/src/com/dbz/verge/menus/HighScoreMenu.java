package com.dbz.verge.menus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import com.dbz.framework.input.FileIO;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.Menu;

public class HighScoreMenu extends Menu {

	// --------------
	// --- Fields ---
	// --------------

	// Bounding Boxes.
	private Rectangle backArrowBounds = new Rectangle(5, 5, 140, 140);
	
	// Scores.
	public final static String file = ".vergehighscores";
	public static float[] timeAttackHighScores = new float[5];
	public static int[] survivalHighScores = new int[5];

	// -------------------
	// --- Constructor ---
	// -------------------

	public HighScoreMenu() {}       

	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void update(float deltaTime) {
		// Gets all TouchEvents and stores them in a list.
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		game.getInput().getKeyEvents();

		// Cycles through and tests all touch events.
		int len = touchEvents.size();
		for(int i = 0; i < len; i++) {
			// Gets a single TouchEvent from the list.
			TouchEvent event = touchEvents.get(i);  

			// Only handle if TouchEvent is TOUCH_UP.
			if(event.type == TouchEvent.TOUCH_UP) {
				// Sets the x and y coordinates of the TouchEvent to our touchPoint vector.
				touchPoint.set(event.x, event.y);
				// Sends the vector to the OpenGL Camera for handling.
				guiCam.touchToWorld(touchPoint);

				// Back Arrow Bounds Check.
				if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
					AssetsManager.playSound(AssetsManager.clickSound);
					game.setScreen(new MainMenu());
					return;
				}

				// Non-Unique, Super Class Bounds Check.
				super.update(touchPoint);
			}
		}
	}

	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------
	
	public static void loadCurrentHighScores(FileIO files) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(files.readFile(file)));
			in.readLine();
			for(int i = 0; i < 5; i++) {
				survivalHighScores[i] = Integer.parseInt(in.readLine());
			}
			in.readLine();
			for(int i = 0; i < 5; i++) {
				timeAttackHighScores[i] = Float.parseFloat(in.readLine());
			}
		} catch (IOException e) {
		} catch (NumberFormatException e) {
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}
	
	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------
	
	public void drawBackground() {
		batcher.beginBatch(AssetsManager.background);
		batcher.drawSprite(0, 0, 1280, 800, AssetsManager.backgroundRegion);
		batcher.drawSprite(245, 165, 790, 470, AssetsManager.backgroundGreyFillRegion);
		batcher.endBatch();
	}

	public void drawObjects() {
		loadCurrentHighScores(game.getFileIO());

		// Prints all of the text.
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		
		// Survival High Scores.
		AssetsManager.vergeFont.drawTextLeft(batcher, "Survival High Scores", 180, 550);
		for (int i = 0; i < 5; i++)
			AssetsManager.vergeFont.drawTextLeft(batcher, ""+survivalHighScores[i], 180, 520-(i*30));
		
		// Time Attack High Scores. (Formatted MM:SS.MSMS).
		AssetsManager.vergeFont.drawTextLeft(batcher, "Time Attack High Scores", 600, 550);
		for (int i = 0; i < 5; i++)
		{
			int minutes = ((int)timeAttackHighScores[i]) / 60;
			float seconds = timeAttackHighScores[i] - (minutes * 60);	
			AssetsManager.vergeFont.drawTextLeft(batcher, String.format("%02d:%05.2f", minutes, seconds), 600, 520-(i*30));

		}
		batcher.endBatch();
		
		// Draws Back Arrow.
		batcher.beginBatch(AssetsManager.backArrow);
		batcher.drawSprite(backArrowBounds, AssetsManager.backArrowRegion);
		batcher.endBatch(); 

		super.drawObjects();
	}

	public void drawBounds() {
		batcher.beginBatch(AssetsManager.boundOverlay);     
		batcher.drawSprite(backArrowBounds, AssetsManager.boundOverlayRegion); 	// Back Arrow Bounding Box
		super.drawBounds();
		batcher.endBatch();
	}

	// --------------------------------
	// --- Android State Management ---
	// --------------------------------
	
	@Override
	public void onBackPressed(){
		game.setScreen(new MainMenu());
	}

}
