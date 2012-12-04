package com.dbz.verge.microgames;


import java.util.List;

import com.dbz.framework.Game;
import com.dbz.framework.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.Assets;
import com.dbz.verge.MicroGame;


public class AquariumMicroGame extends MicroGame{
	
	// Array used to store the crack appearance times for the 3 difficulty levels.
	public float crackTimes[] = { 4.0f, 2.5f, 1.0f };
	
	// Original aquarium water level.
	float waterLevel = 800;
	
	// Array of all possible cracks.
	Crack[] crackList = { new Crack(new Rectangle(0,0,128,128)), 
						  new Crack(new Rectangle(600,600,128,128)), 
						  new Crack(new Rectangle(250,450,128,128)), 
						  new Crack(new Rectangle(400,100,128,128)) };
	
	// -------------------
	// --- Constructor ---
	// ------------------- 	
	
	public AquariumMicroGame(Game game) {
		super(game);

		// Extend allowed time.
		baseMicroGameTime = 10.0f;
	}

	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {
		// Checks for time-based win.
		if (wonTimeBased(deltaTime)) {
			Assets.playSound(Assets.highJumpSound);
			return;
		}
		
		// Checks for water level based loss.
		if(waterLevel <= 0) {
			Assets.playSound(Assets.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}
		
		// Places cracks on the screen at timed intervals, based on level.
		showNewCracks();
		
		// Gets all TouchEvents and stores them in a list.
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		
		//iterate though all touches, check if any of the touches are in the gaps of the circuit
		for(TouchEvent touchEvent : touchEvents) {
			touchPoint.set(touchEvent.x, touchEvent.y);
			guiCam.touchToWorld(touchPoint);

			// Sets isClosed to true if gap is touched. 
			for (int i = 0; i < level+1; i++){
				if(crackList[i].onScreen == true) {
					
					if(targetTouchDragged(touchEvent, touchPoint, crackList[i].bounds))
						crackList[i].isLeaking = false;
					
					if(touchEvent.type == TouchEvent.TOUCH_UP)
						crackList[i].isLeaking = true;
				}
			}

			//Tests for non-unique touch events, which is currently pause only.
			if (touchEvents.get(0).type == TouchEvent.TOUCH_UP)
				super.updateRunning(touchPoint);
		}
		
		for(Crack c : crackList)
			if(c.isLeaking && c.onScreen)
				decreaseWaterLevel(c);
	}

	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------

	@Override
	// TODO: Write necessary reset code.
	public void reset() {
		super.reset();
	}

	// TODO: Redesign and clean up this code.
	// Places cracks on the screen at timed intervals, based on level.
	public void showNewCracks() {
		crackList[0].onScreen=true;
		
		if (level == 1) {
			if (totalRunningTime>crackTimes[0])
				crackList[1].onScreen=true;
		}
		
		else if (level == 2) {
			if (totalRunningTime>crackTimes[1]&&totalRunningTime<(crackTimes[1]*2))
				crackList[1].onScreen=true;
			else if (totalRunningTime>crackTimes[1]&&totalRunningTime<(crackTimes[1]*3))
				crackList[2].onScreen=true;
		}
		
		else {
			if(totalRunningTime>crackTimes[2]&&totalRunningTime<(crackTimes[2]*2))	
				crackList[1].onScreen=true;
			else if(totalRunningTime>crackTimes[2]&&totalRunningTime<(crackTimes[2]*3)) {
				crackList[1].onScreen=true;
				crackList[2].onScreen=true;
			}
			else if(totalRunningTime>crackTimes[2]&&totalRunningTime<(crackTimes[2]*4)) {
				crackList[1].onScreen=true;
				crackList[2].onScreen=true;
				crackList[3].onScreen=true;
			}	
		}
	}
	
	public void decreaseWaterLevel(Crack crack) {
		waterLevel -= (crack.leakRate * speedScalar[speed-1]);
	}
	
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

	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------

	@Override
	public void drawRunningBackground() {
		// Draw background.
		batcher.beginBatch(Assets.aquariumBackround);
		batcher.drawSprite(0, 0, 1280, 800, Assets.aquariumBackroundRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningObjects() {
		batcher.beginBatch(Assets.aquariumTank);
		
		batcher.drawSprite(0, 0, 1280, waterLevel, Assets.aquariumTankRegion);
		for (int i = 0; i < level+1; i++)
			if(crackList[i].onScreen == true)
				batcher.drawSprite(crackList[i].bounds,Assets.aquariumCrack);
		
		batcher.endBatch();
	}

	@Override
	// TODO: Draw bounding boxes.
	public void drawRunningBounds() {
		// Bounding Boxes
	}
	
	
	// -------------------
	// --- Game Object ---
	// ------------------- 
	
	private class Crack
	{
		// Crack's leak speed.
		public int leakRate = 4;
		
		// Booleans to track Crack states.
		public boolean isLeaking = true;
		public boolean onScreen = false;
		
		// Bounds for touch detection.
		public Rectangle bounds;
			
		// Constructor.
		public Crack(Rectangle rectangle) {
			bounds = rectangle;
		}
	}
}
