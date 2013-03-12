package com.dbz.verge.menus;

import java.util.List;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.Menu;

public class HelpMenu extends Menu {

	// --------------
	// --- Fields ---
	// --------------

	// Page Variables.
    private static int currentPage = 1;
    private static final int NUM_OF_PAGES = 5;
    
    // Help Text.
	private final int maxLines = 12, lineSpacing = 35;

									//   Line Width
	   								//   "TEXT TEXT TEXT TEXT TEXT TEXT!"
	private final String introText[] = { "How to Play!",
										 "",
										 "Verge is a fast-paced game",
										 "composed of many \"MicroGames\".",
										 "",
										 "Each \"MicroGame\" has a goal,",
										 "a time limit, and a type.",
										 "",
										 "The goal is shown at the top",
										 "and the timer at the bottom.",
										 "",
										 "The type is the way you play." };
																
	private final String typesText[][] = { { "Single Touch!", "", "Uses:", "Tap!", "Rapid Touch!", },
										   { "Multi Touch!", "", "Uses:", "Connect!", "Hold!", },
										   { "Accelerometer!", "", "Uses:", "Tilt!", "Turn!" },
										   { "Gestures!", "", "Uses:", "Flick!", "Wipe!" } };
	
	private final String survivalModeText[] = { "Survival!",
												 "",
												 "Endless MicroGames are thrown",
												 "at you in a random order.",
												 "",
												 "You are given 3 lives and",
												 "will lose a life for each",
												 "MicroGame you fail until you",
												 "lose them all.",
												 "",
												 "Difficulty and speed increase", 
												 "as you play." };
	
	private final String timeAttackModeText[] = {  "Time Attack!",
													"",
													"MicroGames come at you in",
													"a set order.",
													"",
													"Beat all MicroGames as fast as",
													"you can to finish the mode and", 
													"set your time. Losing a",
													"MicroGame has a time penalty.",
													"",
													"Difficulty and speed increase", 
													 "as you play."};
	
	private final String gameGridModeText[] = {  "Game Grid!",
												 "",
												 "Play any of the MicroGames",
												 "by themselves. Here you are",
												 "even allowed to change set",
												 "the level and speed manually.",
												 "",
												 "Multiplayer!",
												 "",
												 "Coming Soon.",
												 "", 
												 "" };
										
	// Bounding Boxes.
	private Rectangle backArrowBounds = new Rectangle(5, 5, 140, 140);
    private Rectangle prevPageBounds = new Rectangle(340, 20, 80, 120);
    private Rectangle nextPageBounds = new Rectangle(860, 20, 80, 120);

	// -------------------
	// --- Constructor ---
	// -------------------

	public HelpMenu() {}       

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
				
                // Previous Page Bounds Check.
		        if (currentPage != 1) {
	                if (OverlapTester.pointInRectangle(prevPageBounds, touchPoint)) {
	                	AssetsManager.playSound(AssetsManager.clickSound);
	                	// Decrement Page.
	                	if (currentPage-1 < 1)
	                		currentPage = NUM_OF_PAGES;
	                	else
	                		currentPage--;
	                	return;
	                }
		        }
                
                // Next Page Bounds Check.
		        if (currentPage != NUM_OF_PAGES) {
	                if (OverlapTester.pointInRectangle(nextPageBounds, touchPoint)) {
	                	AssetsManager.playSound(AssetsManager.clickSound);
	                	// Increment Page.
	                	if (currentPage+1 > NUM_OF_PAGES)
	                		currentPage = 1;
	                	else
	                		currentPage++;
	                	return;
	                }
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
		batcher.beginBatch(AssetsManager.background);
		batcher.drawSprite(0, 0, 1280, 800, AssetsManager.backgroundRegion);
		batcher.drawSprite(245, 165, 790, 470, AssetsManager.backgroundGreyFillRegion);
		batcher.endBatch();
	}

	public void drawObjects() {

		switch(currentPage) {
			case 1:
				drawIntroPage();
				break;
				
			case 2:
				drawMicroGameTypesPage();
				break;
				
			case 3:
				drawSurvivalModePage();
				break;
				
			case 4:
				drawTimeAttackModePage();
				break;
				
			case 5:
				drawGameGridModePage();
				break;
				
			default:
				break;
		}
		
    	// Draw Page Arrows.
        batcher.beginBatch(AssetsManager.gameGrid);
        
        if (currentPage != 1)
        	batcher.drawSprite(prevPageBounds, AssetsManager.leftArrowRegion);	// Previous Page Arrow
        if (currentPage != NUM_OF_PAGES)
        	batcher.drawSprite(nextPageBounds, AssetsManager.rightArrowRegion);	// Next Page Arrow
        batcher.endBatch();
		
		// Draws Back Arrow.
		batcher.beginBatch(AssetsManager.backArrow);
		batcher.drawSprite(backArrowBounds, AssetsManager.backArrowRegion);
		batcher.endBatch(); 

		super.drawObjects();
	}
	
	public void drawIntroPage() {	
		// Prints all of the text.
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		
		for (int i = 0; i < maxLines; i++)
			AssetsManager.vergeFont.drawTextCentered(batcher, introText[i], 640, 545-(i*lineSpacing), 1.5F);

		AssetsManager.vergeFont.drawTextCentered(batcher, "Intro", 640, 10, 2.5f);
		batcher.endBatch();
	}
	
	public void drawMicroGameTypesPage() {
		// Displays the MicroGame Indicators Icons.
		batcher.beginBatch(AssetsManager.transition);
		batcher.drawSprite(260, 535, 85, 85, AssetsManager.singleTouchOnIndicatorRegion);
		batcher.drawSprite(660, 535, 85, 85, AssetsManager.multiTouchOnIndicatorRegion);
		batcher.drawSprite(260, 295, 85, 85, AssetsManager.accelerometerOnIndicatorRegion);
		batcher.drawSprite(660, 295, 85, 85, AssetsManager.gesturesOnIndicatorRegion);
		batcher.endBatch();
		
		// Prints all of the text.
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		
		for (int i = 0; i < 5; i++)
			AssetsManager.vergeFont.drawTextCentered(batcher, typesText[0][i], 500, 545-(i*lineSpacing), 1.30F);
		for (int i = 0; i < 5; i++)
			AssetsManager.vergeFont.drawTextCentered(batcher, typesText[1][i], 900, 545-(i*lineSpacing), 1.30F);
		for (int i = 0; i < 5; i++)
			AssetsManager.vergeFont.drawTextCentered(batcher, typesText[2][i], 505, 305-(i*lineSpacing), 1.30F);
		for (int i = 0; i < 5; i++)
			AssetsManager.vergeFont.drawTextCentered(batcher, typesText[3][i], 900, 305-(i*lineSpacing), 1.30F);
		
		AssetsManager.vergeFont.drawTextCentered(batcher, "Types", 640, 10, 2.5f);
		batcher.endBatch();		
	}
	
	public void drawSurvivalModePage() {
		// Prints all of the text.
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		
		for (int i = 0; i < maxLines; i++)
			AssetsManager.vergeFont.drawTextCentered(batcher, survivalModeText[i], 640, 545-(i*lineSpacing), 1.5F);
		
		AssetsManager.vergeFont.drawTextCentered(batcher, "Modes", 640, 10, 2.5f);
		batcher.endBatch();		
	}
	
	public void drawTimeAttackModePage() {
		// Prints all of the text.
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		
		for (int i = 0; i < maxLines; i++)
			AssetsManager.vergeFont.drawTextCentered(batcher, timeAttackModeText[i], 640, 545-(i*lineSpacing), 1.5F);
		
		AssetsManager.vergeFont.drawTextCentered(batcher, "Modes", 640, 10, 2.5f);
		batcher.endBatch();		
	}
	
	public void drawGameGridModePage() {
		// Prints all of the text.
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		
		for (int i = 0; i < maxLines; i++)
			AssetsManager.vergeFont.drawTextCentered(batcher, gameGridModeText[i], 640, 545-(i*lineSpacing), 1.5F);
		
		AssetsManager.vergeFont.drawTextCentered(batcher, "Modes", 640, 10, 2.5f);
		batcher.endBatch();		
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
