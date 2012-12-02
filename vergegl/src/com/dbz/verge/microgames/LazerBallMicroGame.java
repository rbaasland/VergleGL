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
// TODO: Needs to reset when called from GameGrid and Survival
// TODO: Currently the fire button starts red in Time Attack.

public class LazerBallMicroGame extends MicroGame  {

	// --------------
	// --- Fields ---
	// --------------

	// Array used to store the different required counts of the 3 difficulty levels.
	private int requiredLazerChargeCount[] = { 10, 20, 30 };
	private float lazerChargeRate[] = {53.6f, 26.8f, 13.4f}; //rate at which lazer will increase.. (maxsize - minsize) / requiredLazerChargeCount
	private int chargeCount = 0;
	
	//height and width constands
	private int lazerMinSize = 64;   
	private int lazerMaxSize = 600;

	//growth rates for each level (one rate per animation)
	private int growthStage = 0;
	//int[] level1GrowthRate = {2, 4, 6, 8 ,10};  //touches will be used by requiredLazerChargeCount
	//int[] level2GrowthRate = {4, 8, 12, 16, 20};
	//int[] level3GrowthRate = {6, 12, 18, 24, 30};
	
	// Animation scalar based on speed variable.
	private float animationScalar[] = {1.0f, 1.5f, 2.0f};

	//used to store appropriate growth rate based on level. uses isFirstRun bool in updateRunning()
	int[] currentLevelGrowthRate;
	boolean isFirstRun = true; //TODO remove this garbage

	//used to track state of laser
	private boolean readyToFire = false; //TODO might not need anymore 
	private boolean lazerFired = false;

	// Bounds for touch detection.
	private Rectangle lazerBallBounds = new Rectangle(450, 340, lazerMinSize, lazerMinSize);//new Rectangle(150, 40, 600, 600);
	//private Rectangle originalBallBounds = new Rectangle(150, 40, 600, 600);
	
	private Rectangle fireButtonBounds = new Rectangle(0, 240, 100, 250);  //TODO: will remove
	private Rectangle targetBounds = new Rectangle(1100, 240, 250, 200);   //will keep, but remove bob texture

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
			resetLazerSize();
			return;
		}

		//if lazer has been fired, keep lazer moving & check for target collision
		if(lazerFired){
			movelazer();

			if(collision(lazerBallBounds, targetBounds)){
				Assets.playSound(Assets.highJumpSound);
				microGameState = MicroGameState.Won;
				resetLazerSize();
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
			if (targetTouchDownCenterCoords(event, touchPoint, lazerBallBounds)) {

				if(!readyToFire){ //can probably remove this. because it will trigger autofire sequence instead. 
					chargeCount++;
					increaseGrowthStage();
				}

				if (chargeCount == requiredLazerChargeCount[level-1]) {
					readyToFire = true;
					firelazer();
					//fireButtonReady(); //removing fire button

				}
				else if (chargeCount < requiredLazerChargeCount[level-1])
					Assets.playSound(Assets.coinSound); //need charging sound asset here
				return;
			}

			//Tests if fire button is touched
			//if(targetTouchDown(event, touchPoint, fireButtonBounds))
				//if(readyToFire){
					//firelazer();
					//return;
				//}	


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
		//reset members
		isFirstRun = true;
		chargeCount = 0;
		readyToFire = false;
		lazerFired = false;
		lazerBallBounds.lowerLeft.set(150, 40); //reset lazer to original bounds
		growthStage = 0;
		Assets.lazerState1Region = Assets.lazerChargingAnim.getKeyFrame(growthStage);
		// * Won't need to reset width/height if we never change the bounds. *
		//lazerBallBounds.width = 600;
		//lazerBallBounds.height = 600;
	}

	//Increments lazer's growth stage, changes lazer animation
	private void increaseGrowthStage() {
		//TODO implement size of lazer
		lazerBallBounds.width +=  lazerChargeRate[level-1];
		lazerBallBounds.height +=  lazerChargeRate[level-1];
		//growthStage++;
		//Assets.lazerState1Region = Assets.lazerChargingAnim.getKeyFrame(growthStage); //overrides first texture w/ next anim
	}

	private void fireButtonReady() {
		Assets.lazerFireButtonInitialRegion = Assets.lazerFireButtonAnim.getKeyFrame(1);
	}

	private void resetFireButton() {
		Assets.lazerFireButtonInitialRegion = Assets.lazerFireButtonAnim.getKeyFrame(0);
	}
	
		//resets growth stage and lazer animation.
	
	private void resetLazerSize() {
		Assets.lazerState1Region = Assets.lazerChargingAnim.getKeyFrame(0);
	}

	private void firelazer() {
		movelazer();
		lazerFired = true;
		readyToFire = false;
		resetFireButton();
	}

	private void movelazer() {
		lazerBallBounds.lowerLeft.x += 128 * animationScalar[speed-1];
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
		 //drawRunningBounds();

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
		batcher.drawSpriteCenterCoords(lazerBallBounds, Assets.lazerState5Region);
		//batcher.drawSprite(fireButtonBounds, Assets.lazerFireButtonInitialRegion);
		//batcher.drawSprite(targetBounds, Assets.lazerTargetRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(Assets.boundOverlay);
		batcher.drawSpriteCenterCoords(lazerBallBounds, Assets.boundOverlayRegion);  // LazerBall Bounding Box
		//batcher.drawSprite(originalBallBounds, Assets.boundOverlayRegion);
		//batcher.drawSprite(fireButtonBounds, Assets.boundOverlayRegion); // FireButton Bounding Box 
		//batcher.drawSprite(targetBounds, Assets.boundOverlayRegion);	 // Target Bounding Box
		batcher.endBatch();
	}

}
