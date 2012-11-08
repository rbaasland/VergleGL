package com.dbz.verge.microgames;

import java.util.List;
import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;

// TODO: Comment code. Try to match the standard that is created with other MicroGame comments.
// TODO: The lazerBallBounds need to accurately reflect the lazer ball's size.
// TODO: Explosion Art, better laser art, something better than bob as target, Good "Firin Mah Lazer" sound byte

public class LazerBallMicroGame extends MicroGame  {

	// --------------
	// --- Fields ---
	// --------------

	// Array used to store the different required counts of the 3 difficulty levels.
	private int requiredlazerChargeCount[] = { 10, 20, 30 };
	private int chargeCount = 0;

	//growth rates for each level (one rate per animation)
	private int growthStage = 0;
	int[] level1GrowthRate = {2, 4, 6, 8 ,10};
	int[] level2GrowthRate = {4, 8, 12, 16, 20};
	int[] level3GrowthRate = {6, 12, 18, 24, 30};

	//used to store appropriate growthrate based on level. uses isFirstRun bool in updateRunning()
	int[] currentLevelGrowthRate;
	boolean isFirstRun = true;

	//used to track state of laser
	private boolean readyToFire = false;
	private boolean lazerFired = false;

	// Bounds for touch detection.
	private Rectangle lazerBallBounds = new Rectangle(150, 40, 600, 600);
	private Rectangle fireButtonBounds = new Rectangle(0, 240, 100, 250);
	private Rectangle targetBounds = new Rectangle(1100, 240, 250, 200); 

	// -------------------
	// --- Constructor ---
	// -------------------   
	public LazerBallMicroGame(Game game) {
		super(game);
	}

	// ---------------------
	// --- Update Method ---
	// ---------------------   
	@Override
	public void updateRunning(float deltaTime) {
		// Checks for time-based loss.
		if (lostTimeBased(deltaTime)) {
			Assets.playSound(Assets.hitSound);
			resetGrowthStage();
			return;
		}

		if(isFirstRun){

			switch(level){
			case 1:
				currentLevelGrowthRate = level1GrowthRate;
				break;
			case 2:
				currentLevelGrowthRate = level2GrowthRate;
				break;
			case 3:
				currentLevelGrowthRate = level3GrowthRate;
				break;
			}
			isFirstRun = false;
		}

		//if lazer has been fired, keep lazer moving & check for target collision
		if(lazerFired){
			movelazer();

			if(collision(lazerBallBounds, targetBounds)){
				Assets.playSound(Assets.highJumpSound);
				microGameState = MicroGameState.Won;
				resetGrowthStage();
			}
			return;
		}

		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		int len = touchEvents.size();
		for(int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			touchPoint.set(event.x, event.y);
			guiCam.touchToWorld(touchPoint);

			// Tests if lazer is touched.
			if (targetTouchDown(event, touchPoint, lazerBallBounds)) {

				if(!readyToFire){
					chargeCount++;
					if(chargeCount == currentLevelGrowthRate[growthStage])
						increaseGrowthStage();
				}

				if (chargeCount == requiredlazerChargeCount[level-1]) {
					readyToFire = true;
					fireButtonReady();

				}
				else if (chargeCount < requiredlazerChargeCount[level-1])
					Assets.playSound(Assets.coinSound);
				return;
			}

			//Tests if fire button is touched
			if(targetTouchDown(event, touchPoint, fireButtonBounds))
				if(readyToFire){
					firelazer();
					return;
				}	


			// Tests for non-unique touch events, which is currently pause only.
			if (event.type == TouchEvent.TOUCH_UP)
				super.updateRunning(touchPoint);
		}   
	}

	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------

	@Override
	public void reset() {
		super.reset();
		chargeCount = 0;
		readyToFire = false;
		lazerFired = false;
		lazerBallBounds.lowerLeft.set(150, 40);

		// * Won't need to reset width/height if we never change the bounds. *
		lazerBallBounds.width = 600;
		lazerBallBounds.height = 600;
	}

	//Increments lazer's growth stage, changes lazer animation
	private void increaseGrowthStage() {
		growthStage++;
		Assets.lazerState1Region = Assets.lazerChargingAnim.getKeyFrame(growthStage); //overrides first texture w/ next anim
	}

	//resets growth stage and lazer animation.
	private void resetGrowthStage() {
		growthStage = 0;
		readyToFire = false;
		lazerFired = false;
		Assets.lazerState1Region = Assets.lazerChargingAnim.getKeyFrame(growthStage);
	}

	private void fireButtonReady() {
		Assets.lazerFireButtonInitialRegion = Assets.lazerFireButtonAnim.getKeyFrame(1);
	}

	private void resetFireButton() {
		Assets.lazerFireButtonInitialRegion = Assets.lazerFireButtonAnim.getKeyFrame(0);
	}

	private void firelazer() {
		movelazer();
		lazerFired = true;
		readyToFire = false;
		resetFireButton();
	}

	private void movelazer() {
		lazerBallBounds.lowerLeft.x += 128;
	}

	public boolean collision(Rectangle lazer, Rectangle target) {
		if(target.lowerLeft.x <= lazer.lowerLeft.x)
			return true;

		return false;
	}		

	// -------------------
	// --- Draw Method ---
	// -------------------	
	@Override
	public void presentRunning() {
		drawRunningBackground();
		drawRunningObjects();
		// drawRunningBounds();

		if (readyToFire || lazerFired)
			drawInstruction("FIRE YA LAZERRR!!!!");
		else drawInstruction("Tap the lazer ball!");
		super.presentRunning();
	}

	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------

	@Override
	public void drawRunningBackground() {
		// Draw background.
		batcher.beginBatch(Assets.lazerBackground);
		batcher.drawSprite(0, 0, 1280, 800, Assets.lazerBackgroundRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningObjects() {
		// Draw Brofist.
		batcher.beginBatch(Assets.lazer);
		batcher.drawSprite(lazerBallBounds, Assets.lazerState1Region);
		batcher.drawSprite(fireButtonBounds, Assets.lazerFireButtonInitialRegion);
		batcher.drawSprite(targetBounds, Assets.lazerTargetRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
		batcher.drawSprite(lazerBallBounds, Assets.boundOverlayRegion);  // LazerBall Bounding Box
		batcher.drawSprite(fireButtonBounds, Assets.boundOverlayRegion); // FireButton Bounding Box 
		batcher.drawSprite(targetBounds, Assets.boundOverlayRegion);	 // Target Bounding Box
		batcher.endBatch();
	}

}
