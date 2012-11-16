package com.dbz.verge.microgames;


import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;


public class AquariumMicroGame extends MicroGame{

	// Speed variation based on speed
	private float animationScalar[] = new float[]{1.0f, 1.5f, 2.0f};
	
	float waterLevel=800;
	boolean isTankEmpty=false;
	crack[] CrackList ={new crack(new Rectangle(0,0,128,128)), new crack(new Rectangle(600,600,128,128)), new crack(new Rectangle(250,450,128,128)), new crack(new Rectangle(400,100,128,128))};
	float[] levelCrackTimes={4,2.5f,1};
	
	private class crack
	{
		public Rectangle bounds;
		public boolean isLeaking=true;
		public int leakRate=4;
		public boolean onScreen=false;
		
		public crack(Rectangle r)
		{
			bounds=r;
		}
	}
	
	public void showNewCracks()
	{
		CrackList[0].onScreen=true;
		if(level==1)
		{
			if(totalRunningTime>levelCrackTimes[0])
			{
				CrackList[1].onScreen=true;
			}
		}
		else if(level==2)
		{
			if(totalRunningTime>levelCrackTimes[1]&&totalRunningTime<(levelCrackTimes[1]*2))
			{
				CrackList[1].onScreen=true;
			}
			else if(totalRunningTime>levelCrackTimes[1]&&totalRunningTime<(levelCrackTimes[1]*3))
			{
				CrackList[2].onScreen=true;
			}
		}
		else
		{
			if(totalRunningTime>levelCrackTimes[2]&&totalRunningTime<(levelCrackTimes[2]*2))
			{
				CrackList[1].onScreen=true;
			}
			else if(totalRunningTime>levelCrackTimes[2]&&totalRunningTime<(levelCrackTimes[2]*3))
			{
				CrackList[1].onScreen=true;
				CrackList[2].onScreen=true;
			}
			else if(totalRunningTime>levelCrackTimes[2]&&totalRunningTime<(levelCrackTimes[2]*4))
			{
				CrackList[1].onScreen=true;
				CrackList[2].onScreen=true;
				CrackList[3].onScreen=true;
			}
			
		}
	}
	
	public void decreaseWaterLevel(crack c)
	{
		waterLevel=waterLevel-c.leakRate * animationScalar[speed-1];
	}
	
	public AquariumMicroGame(Game game) {
		super(game);

		// Extend allowed time for testing.
		totalMicroGameTime = new float []{10.0f, 8.5f, 7.0f};
	}

	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {
		// Checks for time-based loss.
		if (wonTimeBased(deltaTime)) {
			Assets.playSound(Assets.highJumpSound);
			return;
		}
		if(waterLevel<=0)
		{
			Assets.playSound(Assets.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}
		showNewCracks();//places cracks at certain time based intervals
		//get touches from screen
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		
		//iterate though all touches, check if any of the touches are in the gaps of the circuit
		for(TouchEvent touchEvent : touchEvents) {
			touchPoint.set(touchEvent.x, touchEvent.y);
			guiCam.touchToWorld(touchPoint);

			//Sets isClosed to true if gap is touched. 
			for (int i=0;i<(level+1);i++){
				if(CrackList[i].onScreen==true){
					
					if(targetTouchDragged(touchEvent, touchPoint, CrackList[i].bounds)){
						CrackList[i].isLeaking = false;
						Assets.playSound(Assets.hitSound);
					}
					
					if(touchEvent.type == TouchEvent.TOUCH_UP)
						CrackList[i].isLeaking = true;
					
				}
					
			}

			//Tests for non-unique touch events, which is currently pause only.
			if (touchEvents.get(0).type == TouchEvent.TOUCH_UP)
				super.updateRunning(touchPoint);
		}
		
		for(crack c : CrackList)
			if(c.isLeaking && c.onScreen)
				decreaseWaterLevel(c);

	}

	// -----------------------------
	// --- Utility Update Method ---
	// -----------------------------

	@Override
	public void reset() {
		super.reset();
	}

	/*
	 * Used to initialize the active gaps based on current level. Could probably use some refactoring. 
	 */
	
	//due to nature of the beast... and to save time, 
	//handle the collision detection based on sparks direction
	
	// -------------------
	// --- Draw Method ---
	// -------------------

	@Override
	public void presentRunning() {

		drawRunningBackground();
		drawRunningObjects();
		//drawRunningBounds();
		drawInstruction("Plug The Cracks!");
		super.presentRunning();
	}


	// ---------------------------
	// --- Utility Draw Method ---
	// ---------------------------

	@Override
	public void drawRunningBackground() {
		// Draw background.
		batcher.beginBatch(Assets.aquariumBackround);
		batcher.drawSprite(0, 0, 1280, 800, Assets.aquariumBackroundRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningObjects() {
		// Draw circuits
		batcher.beginBatch(Assets.aquariumTank);
		batcher.drawSprite(0, 0, 1280, waterLevel, Assets.aquariumTankRegion);
		//draw the appropriate connector when gap is closed
		for (int i=0;i<(level+1);i++){
			if(CrackList[i].onScreen==true)
				batcher.drawSprite(CrackList[i].bounds,Assets.aquariumCrack);
		}
		batcher.endBatch();
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
	}
	
	
	
	
	
}
