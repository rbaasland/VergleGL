package com.dbz.verge.menus;

import java.util.List;

import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.Menu;
import com.dbz.verge.MicroGame;
import com.dbz.verge.microgames.BroFistMicroGame;
import com.dbz.verge.microgames.CircuitMicroGame;
import com.dbz.verge.microgames.DirtBikeMicroGame;
import com.dbz.verge.microgames.FireMicroGame;
import com.dbz.verge.microgames.FishingMicroGame;
import com.dbz.verge.microgames.FlyMicroGame;
import com.dbz.verge.microgames.LazerBallMicroGame;
import com.dbz.verge.microgames.TossMicroGame;
import com.dbz.verge.microgames.InvasionMicroGame;
import com.dbz.verge.microgames.AquariumMicroGame;
import com.dbz.verge.microgames.TrafficMicroGame;

// TODO: Keep naming conventions standard between Bounds and Asset Regions.
//		 Fix the issue where the LVL text will shift over by a few pixels when you switch through them.
//		 *** I played through the games and I think I might have gotten the same game in the same set.
//		 I think it happened on the 5th-6th game, so in other words one game earlier than it should have.
//		 We should test this more throughly to make sure I didn't just imagine things. ***
//		 Test all empty micro game selection areas. Currently empty produces null

public class GameGridMenu extends Menu {
   
	// --------------
	// --- Fields ---
	// --------------
	
	// Page Variables.
    private static int currentPage = 1;
    private static final int NUM_OF_PAGES = 2;
    
    // Bounding Boxes.
    private Rectangle firstMicroGameBounds = new Rectangle(315, 435, 170, 170);
    private Rectangle secondMicroGameBounds = new Rectangle(555, 435, 170, 170);
    private Rectangle thirdMicroGameBounds = new Rectangle(795, 435, 170, 170);
    private Rectangle fourthMicroGameBounds = new Rectangle(315, 200, 170, 170);
    private Rectangle fifthMicroGameBounds = new Rectangle(555, 200, 170, 170);
    private Rectangle sixthMicroGameBounds = new Rectangle(795, 200, 170, 170);
    private Rectangle backArrowBounds = new Rectangle(5, 5, 140, 140);
    private Rectangle prevPageBounds = new Rectangle(340, 20, 80, 120);
    private Rectangle nextPageBounds = new Rectangle(860, 20, 80, 120);
    
    // Fields for Level/Speed Overlay
    private boolean overlayPresent = false;
    private MicroGame selectedMicroGame;
    private TextureRegion selectedMicroGameIcon;
    
    // Bounding Boxes for Level/Speed Overlay.
    private Rectangle overlayAreaBounds = new Rectangle(163, 163, 954, 474);
    
    private Rectangle levelSelectAreaBounds = new Rectangle(380, 420, 520, 200);
    private Rectangle decrementLevelBounds = new Rectangle(395, 460, 80, 120);
    private Rectangle levelTextBounds = new Rectangle(480, 440, 320, 160);
    private Rectangle incrementLevelBounds = new Rectangle(805, 460, 80, 120);
    
    private Rectangle speedSelectAreaBounds = new Rectangle(380, 180, 520, 200);
    private Rectangle decrementSpeedBounds = new Rectangle(395, 220, 80, 120);
    private Rectangle speedTextBounds = new Rectangle(480, 200, 320, 160);
    private Rectangle incrementSpeedBounds = new Rectangle(805, 220, 80, 120);
    
    private Rectangle selectedIconAreaBounds = new Rectangle(180, 300, 200, 200);
    private Rectangle selectedIconBounds = new Rectangle (160, 280, 240, 220);
    
    private Rectangle checkMarkAreaBounds = new Rectangle(900, 300, 200, 200);
    private Rectangle checkMarkBounds = new Rectangle(920, 320, 160, 160);
    
    
    // -------------------
 	// --- Constructor ---
    // -------------------
    public GameGridMenu() {
    }       

    // ---------------------
 	// --- Update Method ---
 	// ---------------------
    @Override
    public void update(float deltaTime) {
    	// Redirect to level overlay.
    	if (overlayPresent)
    		updateLevelOverlay(deltaTime);
    	
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
                
                // First MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(firstMicroGameBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	if (currentPage == 1) {
                		selectedMicroGame = new BroFistMicroGame();
                		selectedMicroGameIcon = AssetsManager.broFistIconRegion;
                	}
                	else if (currentPage == 2) {
                		selectedMicroGame = new AquariumMicroGame();
                		selectedMicroGameIcon = AssetsManager.aquariumIconRegion;
                	}
                	overlayPresent = true;
                	return;
                }
                
                // Second MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(secondMicroGameBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	if (currentPage == 1) {
                		selectedMicroGame = new FlyMicroGame();
                		selectedMicroGameIcon = AssetsManager.flyIconRegion;
                	}
                	else if (currentPage == 2) {
                		selectedMicroGame = new DirtBikeMicroGame();
                		selectedMicroGameIcon = AssetsManager.dirtBikeIconRegion;
                		selectedMicroGame.version = 0;
                	}
                	overlayPresent = true;
                	return;
                }
                
                // Third MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(thirdMicroGameBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	if (currentPage == 1) {
                		selectedMicroGame = new FireMicroGame();
                		selectedMicroGameIcon = AssetsManager.fireIconRegion;
                	}
                	else if (currentPage == 2) {
                		selectedMicroGame = new TossMicroGame();
                		selectedMicroGameIcon = AssetsManager.tossIconRegion;
                	}
                	overlayPresent = true;
                	return;
                }
                
                // Fourth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(fourthMicroGameBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	if (currentPage == 1){
                		selectedMicroGame = new TrafficMicroGame();
                		selectedMicroGameIcon = AssetsManager.trafficIconRegion;
                	}
                	else if (currentPage == 2) {
                		selectedMicroGame = new TrafficMicroGame();
                		selectedMicroGameIcon = AssetsManager.trafficIconRegion;
                		selectedMicroGame.version = 1;
                	}
                	overlayPresent = true;
                	return;
                }
                
                // Fifth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(fifthMicroGameBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	if (currentPage == 1){
                		selectedMicroGame = new CircuitMicroGame();
                		selectedMicroGameIcon = AssetsManager.circuitIconRegion;
                	} else if (currentPage == 2) {
                		selectedMicroGame = new DirtBikeMicroGame();
                		selectedMicroGameIcon = AssetsManager.dirtBikeIconRegion;
                		selectedMicroGame.version = 1;
                	}
                	overlayPresent = true;
                	return;
                }
                
                // Sixth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(sixthMicroGameBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	if (currentPage == 1){
                		selectedMicroGame = new LazerBallMicroGame();
                		selectedMicroGameIcon = AssetsManager.lazerBallIconRegion;
                		overlayPresent = true;
                	}
                	return;
                }
                
                
                // Back Arrow Bounds Check.
                if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
                    AssetsManager.playSound(AssetsManager.clickSound);
                    currentPage = 1;
                    game.setScreen(new SinglePlayerMenu());
                    return;
                }
                
                // Previous Page Bounds Check.
                if (OverlapTester.pointInRectangle(prevPageBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	// Decrement Page.
                	if (currentPage-1 < 1)
                		currentPage = NUM_OF_PAGES;
                	else
                		currentPage--;
                	return;
                }
                
                // Next Page Bounds Check.
                if (OverlapTester.pointInRectangle(nextPageBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	// Increment Page.
                	if (currentPage+1 > NUM_OF_PAGES)
                		currentPage = 1;
                	else
                		currentPage++;
                	return;
                }
                               
                // Non-Unique, Super Class Bounds Check.
    	        super.update(touchPoint);
            }
        }
    }
    
    public void updateLevelOverlay(float deltaTime)
    {
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
                
                // Ready Bounds Check.
                if(OverlapTester.pointInRectangle(checkMarkBounds, touchPoint)) {
                    AssetsManager.playSound(AssetsManager.clickSound);
                    AssetsManager.loadMicrogame(selectedMicroGame);
                    game.setScreen(selectedMicroGame);
                    return;
                }
                
                // Decrement Level Bounds Check.
                if (OverlapTester.pointInRectangle(decrementLevelBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	if (selectedMicroGame.level-1 < 1)
                		selectedMicroGame.level = 3;
                	else
                		selectedMicroGame.level--;
                	return;
                }   
                
                // Increment Level Bounds Check.
                if (OverlapTester.pointInRectangle(incrementLevelBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	if (selectedMicroGame.level+1 > 3)
                		selectedMicroGame.level = 1;
                	else
                		selectedMicroGame.level++;
                	return;
                }
                
                // Decrement Speed Bounds Check.
                if (OverlapTester.pointInRectangle(decrementSpeedBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	if (selectedMicroGame.speed-1 < 1)
                		selectedMicroGame.speed = 3;
                	else
                		selectedMicroGame.speed--;
                	return;
                }   
                
                // Increment Speed Bounds Check.
                if (OverlapTester.pointInRectangle(incrementSpeedBounds, touchPoint)) {
                	AssetsManager.playSound(AssetsManager.clickSound);
                	if (selectedMicroGame.speed+1 > 3)
                		selectedMicroGame.speed = 1;
                	else
                		selectedMicroGame.speed++;
                	return;
                }
        
                // Back Arrow Bounds Check.
                if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
                    AssetsManager.playSound(AssetsManager.clickSound);
                    overlayPresent = false;
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
    	batcher.beginBatch(AssetsManager.gameGrid);
        batcher.drawSprite(0, 0, 1280, 800, AssetsManager.gameGridBackgroundRegion);
        batcher.endBatch();
    }
    
    public void drawObjects() {
		if(overlayPresent) 
			drawOverlayObjects();
		else {
	        // Draws MicroGame Icons.
	    	if (currentPage == 1) {
	    		batcher.beginBatch(AssetsManager.gameGridIconsPageOne);
	        	batcher.drawSprite(0, 0, 1024, 800, AssetsManager.gameGridIconsPageOneRegion);
	        	batcher.endBatch();
	    	} else if (currentPage == 2) {
	    		batcher.beginBatch(AssetsManager.gameGridIconsPageTwo);
	    		batcher.drawSprite(0, 0, 1024, 800, AssetsManager.gameGridIconsPageTwoRegion);
	    		batcher.endBatch();
	    	}
	        
	    	// Draw Page Arrows.
	        batcher.beginBatch(AssetsManager.gameGrid);
	        batcher.drawSprite(prevPageBounds, AssetsManager.leftArrowRegion);	// Previous Page Arrow
	        batcher.drawSprite(nextPageBounds, AssetsManager.rightArrowRegion);	// Next Page Arrow
	        batcher.endBatch();
	        
	        // Draws Page Number.
			batcher.beginBatch(AssetsManager.vergeFontTexture);
			AssetsManager.vergeFont.drawTextCentered(batcher, String.valueOf(currentPage), 640, 10, 2.5f);
			batcher.endBatch();
		}
		
        // Draws Back Arrow.
        batcher.beginBatch(AssetsManager.backArrow);
        batcher.drawSprite(backArrowBounds, AssetsManager.backArrowRegion);
        batcher.endBatch(); 
		
        super.drawObjects();
    }
    
    public void drawOverlayObjects() {
    	batcher.beginBatch(AssetsManager.gameGrid);
		
		// Draws Overlay Background.
		batcher.drawSprite(overlayAreaBounds, AssetsManager.overlayRegion);
		
		// Level Selection Area.
		batcher.drawSprite(levelSelectAreaBounds, AssetsManager.selectionRegion);
		batcher.drawSprite(decrementLevelBounds, AssetsManager.leftArrowRegion);
		
		if (selectedMicroGame.level == 1)
			batcher.drawSprite(levelTextBounds, AssetsManager.levelOneRegion);
		else if (selectedMicroGame.level == 2)
			batcher.drawSprite(levelTextBounds, AssetsManager.levelTwoRegion);
		else
			batcher.drawSprite(levelTextBounds, AssetsManager.levelThreeRegion);
		
		batcher.drawSprite(incrementLevelBounds, AssetsManager.rightArrowRegion);
		
		// Speed Selection Area.
		batcher.drawSprite(speedSelectAreaBounds, AssetsManager.selectionRegion);
		batcher.drawSprite(decrementSpeedBounds, AssetsManager.leftArrowRegion);
		
		if (selectedMicroGame.speed == 1)
			batcher.drawSprite(speedTextBounds, AssetsManager.speedOneRegion);
		else if (selectedMicroGame.speed == 2)
			batcher.drawSprite(speedTextBounds, AssetsManager.speedTwoRegion);
		else
			batcher.drawSprite(speedTextBounds, AssetsManager.speedThreeRegion);
		
		batcher.drawSprite(incrementSpeedBounds, AssetsManager.rightArrowRegion);
		
		// Selected MicroGame Icon Area.
		batcher.drawSprite(selectedIconAreaBounds, AssetsManager.overlayIconRegion);
		
		// Check Mark Area. 
		batcher.drawSprite(checkMarkAreaBounds, AssetsManager.overlayIconRegion);
		batcher.drawSprite(checkMarkBounds, AssetsManager.checkMarkRegion);
		
		batcher.endBatch();
		
		if (currentPage == 1)
			batcher.beginBatch(AssetsManager.gameGridIconsPageOne);
		else
			batcher.beginBatch(AssetsManager.gameGridIconsPageTwo);
		batcher.drawSprite(selectedIconBounds, selectedMicroGameIcon);
		batcher.endBatch();
    }
    
    public void drawBounds() {
        batcher.beginBatch(AssetsManager.boundOverlay);
        
        if (overlayPresent)
        	drawOverlayBounds();
        else {
	        batcher.drawSprite(firstMicroGameBounds, AssetsManager.boundOverlayRegion);	// 1st MicroGame Bounding Box
	        batcher.drawSprite(secondMicroGameBounds, AssetsManager.boundOverlayRegion); 	// 2nd MicroGame Bounding Box
	        batcher.drawSprite(thirdMicroGameBounds, AssetsManager.boundOverlayRegion); 	// 3rd MicroGame Bounding Box
	        batcher.drawSprite(fourthMicroGameBounds, AssetsManager.boundOverlayRegion); 	// 4th MicroGame Bounding Box
	        batcher.drawSprite(fifthMicroGameBounds, AssetsManager.boundOverlayRegion); 	// 5th MicroGame Bounding Box
	        batcher.drawSprite(sixthMicroGameBounds, AssetsManager.boundOverlayRegion); 	// 6th MicroGame Bounding Box
	        batcher.drawSprite(nextPageBounds, AssetsManager.boundOverlayRegion); 		// Next Page Bounding Box
	        batcher.drawSprite(prevPageBounds, AssetsManager.boundOverlayRegion); 		// Previous Page Bounding Box
        }
        
        batcher.drawSprite(backArrowBounds, AssetsManager.boundOverlayRegion); 	// Back Arrow Bounding Box
        super.drawBounds();
        
        batcher.endBatch();
    }
    
    // TODO: Update to draw all new assets from Overlay implementation.
    public void drawOverlayBounds() {}
    
    @Override
    // TODO: Fix graphical glitch that occurs on page 2 with hardware back button.
    //		 The first page is displayed really quickly before the screen is switched to the PlayMenu.
    public void onBackPressed(){
    	if (overlayPresent)
    		overlayPresent = false;
    	else {
    		currentPage = 1;
    		game.setScreen(new SinglePlayerMenu());	
    	}
    }
    
}
