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

public class HighScoreScreen extends Menu {

	// --------------
	// --- Fields ---
	// --------------

	// Bounding Boxes.
	private Rectangle backArrowBounds = new Rectangle(5, 5, 140, 140);
	public static float[] timeHighScores=new float[5];
	public static int[] surivalHighScores=new int[5];
	public final static String file = ".vergehighscores";
	public static Rectangle highScoreBackground=new Rectangle(0, 200, 80, 170);

	// -------------------
	// --- Constructor ---
	// -------------------

	public HighScoreScreen() {}       

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

	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------

	public void drawBackground() {
		//      batcher.beginBatch(AssetsManager.background);
		//     batcher.drawSprite(0, 0, 1280, 800, AssetsManager.backgroundRegion);
		//    batcher.endBatch();
	}

	public void drawObjects() {

		//prints out the high scores for survival and time attack;
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		AssetsManager.vergeFont.drawTextLeft(batcher, "Survival High Scores", 180, 550);
		loadCurrentHighScores(game.getFileIO());
		for(int i=0;i<5;i++)
		{
			AssetsManager.vergeFont.drawTextLeft(batcher, ""+surivalHighScores[i], 180, 520-(i*30));
		}
		AssetsManager.vergeFont.drawTextLeft(batcher, "Time Attack High Scores", 600, 550);
		//formats the time attack scores to hours:minutes:seconds
		for(int i=0;i<5;i++)
		{
			int hrs=0;
			int mins;
			int sec;
			int rem=0;
			hrs=((int)timeHighScores[i])/3600;
			rem=((int)timeHighScores[i])%3600;
			mins=rem/60;
			rem=rem%60;
			sec=rem;

			AssetsManager.vergeFont.drawTextLeft(batcher, ""+(hrs/10)%10+hrs%10+" :"+(mins/10)%10+mins%10+" :"+(sec/10)%10+sec%10, 600, 520-(i*30));
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

	@Override
	public void onBackPressed(){
		game.setScreen(new MainMenu());
	}
	public static void loadCurrentHighScores(FileIO files) {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(files.readFile(file)));
			in.readLine();
			for(int i = 0; i < 5; i++) {
				surivalHighScores[i] = Integer.parseInt(in.readLine());
			}
			in.readLine();
			for(int i = 0; i < 5; i++) {
				timeHighScores[i] = Float.parseFloat(in.readLine());
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

}
