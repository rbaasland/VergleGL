package com.dbz.verge.menus;

import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.OverlapTester;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.Menu;
import com.dbz.verge.MicroGame;
import com.dbz.verge.microgames.BroFistMicroGame;
import com.dbz.verge.microgames.CircuitMicroGame;
import com.dbz.verge.microgames.FireMicroGame;
import com.dbz.verge.microgames.FlyMicroGame;
import com.dbz.verge.microgames.LazerBallMicroGame;
import com.dbz.verge.microgames.TrafficMicroGame;
import com.dbz.verge.microgames.AquariumMicroGame;

// TODO: Keep naming conventions standard between Bounds and Asset Regions.
//		 Fix the issue where the LVL text will shift over by a few pixels when you switch through them.
//		 *** I played through the games and I think I might have gotten the same game in the same set.
//		 I think it happened on the 5th-6th game, so in other words one game earlier than it should have.
//		 We should test this more throughly to make sure I didn't just imagine things. ***
public class GameGridMenu extends Menu {
   
	// --------------
	// --- Fields ---
	// --------------
	
	// Page Variables.
    private int currentPage = 1;
    private int numOfPages = 2;
    
    // Bounding Boxes.
    private Rectangle firstMicroGameBounds = new Rectangle(315, 435, 170, 170);
    private Rectangle secondMicroGameBounds = new Rectangle(555, 435, 170, 170);
    private Rectangle thirdMicroGameBounds = new Rectangle(795, 435, 170, 170);
    private Rectangle fourthMicroGameBounds = new Rectangle(315, 200, 170, 170);
    private Rectangle fifthMicroGameBounds = new Rectangle(555, 200, 170, 170);
    private Rectangle sixthMicroGameBounds = new Rectangle(795, 200, 170, 170);
    private Rectangle backArrowBounds = new Rectangle(0, 0, 160, 160);
    private Rectangle prevPageBounds = new Rectangle(340, 20, 80, 120);
    private Rectangle nextPageBounds = new Rectangle(860, 20, 80, 120);
    
    // Fields for Level/Speed Overlay
    private boolean overlayPresent = false;
    private MicroGame selectedMicroGame;
    
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
    // private Rectangle selectedIconBounds = new Rectangle (0, 0, 0, 0);
    
    private Rectangle checkMarkAreaBounds = new Rectangle(900, 300, 200, 200);
    private Rectangle checkMarkBounds = new Rectangle(920, 320, 160, 160);
    
    
    // -------------------
 	// --- Constructor ---
    // -------------------
    public GameGridMenu(Game game) {
        super(game); 
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
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new BroFistMicroGame(game);
                	else if (currentPage == 2)
                		selectedMicroGame = new AquariumMicroGame(game); // Replace with new MicroGame.
                	overlayPresent = true;
                	return;
                }
                
                // Second MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(secondMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new FlyMicroGame(game);
                	overlayPresent = true;
                	return;
                }
                
                // Third MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(thirdMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new FireMicroGame(game);
                	overlayPresent = true;
                	return;
                }
                
                // Fourth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(fourthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new TrafficMicroGame(game);
                	overlayPresent = true;
                	return;
                }
                
                // Fifth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(fifthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new CircuitMicroGame(game);
                	overlayPresent = true;
                	return;
                }
                
                // Sixth MicroGame Bounds Check.
                if (OverlapTester.pointInRectangle(sixthMicroGameBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (currentPage == 1)
                		selectedMicroGame = new LazerBallMicroGame(game);
                	overlayPresent = true;
                	return;
                }
                
                // Back Arrow Bounds Check.
                if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(new PlayMenu(game));
                    return;
                }
                
                // Previous Page Bounds Check.
                if (OverlapTester.pointInRectangle(prevPageBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// Decrement Page.
                	if (currentPage-1 < 1)
                		currentPage = numOfPages;
                	else
                		currentPage--;
                	return;
                }
                
                // Next Page Bounds Check.
                if (OverlapTester.pointInRectangle(nextPageBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	// Increment Page.
                	if (currentPage+1 > numOfPages)
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
                    Assets.playSound(Assets.clickSound);
                    game.setScreen(selectedMicroGame);
                    return;
                }
                
                // Decrement Level Bounds Check.
                if (OverlapTester.pointInRectangle(decrementLevelBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (selectedMicroGame.level-1 < 1)
                		selectedMicroGame.level = 3;
                	else
                		selectedMicroGame.level--;
                	return;
                }   
                
                // Increment Level Bounds Check.
                if (OverlapTester.pointInRectangle(incrementLevelBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (selectedMicroGame.level+1 > 3)
                		selectedMicroGame.level = 1;
                	else
                		selectedMicroGame.level++;
                	return;
                }
                
                // Decrement Speed Bounds Check.
                if (OverlapTester.pointInRectangle(decrementSpeedBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (selectedMicroGame.speed-1 < 1)
                		selectedMicroGame.speed = 3;
                	else
                		selectedMicroGame.speed--;
                	return;
                }   
                
                // Increment Speed Bounds Check.
                if (OverlapTester.pointInRectangle(incrementSpeedBounds, touchPoint)) {
                	Assets.playSound(Assets.clickSound);
                	if (selectedMicroGame.speed+1 > 3)
                		selectedMicroGame.speed = 1;
                	else
                		selectedMicroGame.speed++;
                	return;
                }
        
                // Back Arrow Bounds Check.
                if(OverlapTester.pointInRectangle(backArrowBounds, touchPoint)) {
                    Assets.playSound(Assets.clickSound);
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
    	batcher.beginBatch(Assets.gameGrid);
        batcher.drawSprite(0, 0, 1280, 800, Assets.gameGridBackgroundRegion);
        batcher.endBatch();
    }
    
    public void drawObjects() {
		if(overlayPresent) 
			drawOverlayObjects();
		else {
	        // Draws MicroGame Icons.
	    	if (currentPage == 1) {
	    		batcher.beginBatch(Assets.gameGridIcons);
	        	batcher.drawSprite(0, 0, 1024, 800, Assets.gameGridIconsRegion);
	        	batcher.endBatch();
	    	}
	        
	    	// Draw Page Arrows.
	        batcher.beginBatch(Assets.gameGrid);
	        batcher.drawSprite(prevPageBounds, Assets.leftArrowRegion);		// Previous Page Arrow
	        batcher.drawSprite(nextPageBounds, Assets.rightArrowRegion);	// Next Page Arrow
	        batcher.endBatch();
	        
	        // Draws Page Number.
			batcher.beginBatch(Assets.items);
			Assets.font.drawText(batcher, String.valueOf(currentPage), 600, 50);
			batcher.endBatch();
		}
		
        // Draws Back Arrow.
        batcher.beginBatch(Assets.backArrow);
        batcher.drawSprite(backArrowBounds, Assets.backArrowRegion);
        batcher.endBatch(); 
		
        super.drawObjects();
    }
    
    public void drawOverlayObjects() {
    	batcher.beginBatch(Assets.gameGrid);
		
		// Draws Overlay Background.
		batcher.drawSprite(overlayAreaBounds, Assets.overlayRegion);
		
		// Level Selection Area.
		batcher.drawSprite(levelSelectAreaBounds, Assets.selectionRegion);
		batcher.drawSprite(decrementLevelBounds, Assets.leftArrowRegion);
		
		if (selectedMicroGame.level == 1)
			batcher.drawSprite(levelTextBounds, Assets.levelOneRegion);
		else if (selectedMicroGame.level == 2)
			batcher.drawSprite(levelTextBounds, Assets.levelTwoRegion);
		else
			batcher.drawSprite(levelTextBounds, Assets.levelThreeRegion);
		
		batcher.drawSprite(incrementLevelBounds, Assets.rightArrowRegion);
		
		// Speed Selection Area.
		batcher.drawSprite(speedSelectAreaBounds, Assets.selectionRegion);
		batcher.drawSprite(decrementSpeedBounds, Assets.leftArrowRegion);
		
		if (selectedMicroGame.speed == 1)
			batcher.drawSprite(speedTextBounds, Assets.speedOneRegion);
		else if (selectedMicroGame.speed == 2)
			batcher.drawSprite(speedTextBounds, Assets.speedTwoRegion);
		else
			batcher.drawSprite(speedTextBounds, Assets.speedThreeRegion);
		
		batcher.drawSprite(incrementSpeedBounds, Assets.rightArrowRegion);
		
		// Selected MicroGame Icon Area.
		batcher.drawSprite(selectedIconAreaBounds, Assets.overlayIconRegion);
		
		// Check Mark Area. 
		batcher.drawSprite(checkMarkAreaBounds, Assets.overlayIconRegion);
		batcher.drawSprite(checkMarkBounds, Assets.checkMarkRegion);
		
		batcher.endBatch();
    }
    
    public void drawBounds() {
        batcher.beginBatch(Assets.boundOverlay);
        
        if (overlayPresent)
        	drawOverlayBounds();
        else {
	        batcher.drawSprite(firstMicroGameBounds, Assets.boundOverlayRegion);	// 1st MicroGame Bounding Box
	        batcher.drawSprite(secondMicroGameBounds, Assets.boundOverlayRegion); 	// 2nd MicroGame Bounding Box
	        batcher.drawSprite(thirdMicroGameBounds, Assets.boundOverlayRegion); 	// 3rd MicroGame Bounding Box
	        batcher.drawSprite(fourthMicroGameBounds, Assets.boundOverlayRegion); 	// 4th MicroGame Bounding Box
	        batcher.drawSprite(fifthMicroGameBounds, Assets.boundOverlayRegion); 	// 5th MicroGame Bounding Box
	        batcher.drawSprite(sixthMicroGameBounds, Assets.boundOverlayRegion); 	// 6th MicroGame Bounding Box
	        batcher.drawSprite(nextPageBounds, Assets.boundOverlayRegion); 		// Next Page Bounding Box
	        batcher.drawSprite(prevPageBounds, Assets.boundOverlayRegion); 		// Previous Page Bounding Box
        }
        
        batcher.drawSprite(backArrowBounds, Assets.boundOverlayRegion); 	// Back Arrow Bounding Box
        super.drawBounds();
        
        batcher.endBatch();
    }
    
    // TODO: Update to draw all new assets from Overlay implementation.
    public void drawOverlayBounds() {}
}
