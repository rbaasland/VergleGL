package com.dbz.verge.microgames;

import java.util.List;
import java.util.Random;

import com.dbz.framework.DynamicGameObject;
import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.MicroGame;

// TODO: Stop sound from playing on all touched..
public class AquariumMicroGame extends MicroGame{

	// Assets
	public Fish fish[]=new Fish[3];
	public static Texture aquariumBackround;
	public static Texture aquariumTank;
	public static TextureRegion aquariumBackroundRegion;
	public static TextureRegion aquariumTankRegion;
	public static TextureRegion aquariumCrack;
	public static TextureRegion fish1_1;
	public static TextureRegion fish1_2;
	public static TextureRegion fish2_1;
	public static TextureRegion fish2_2;
	public static TextureRegion fish3;
	public static TextureRegion specialFish;


	// Array used to store the crack appearance times for the 3 difficulty levels.
	public float crackTimes[] = { 4.0f, 2.5f, 1.0f };

	// Original aquarium water level.
	Random generator=new Random();
	float waterLevel = 0;
	float previousDive=15;
	// Array of all possible cracks.
	Crack[] crackList = { new Crack(new Rectangle(75,200,128,128)), 
			new Crack(new Rectangle(600,600,128,128)), 
			new Crack(new Rectangle(250,450,128,128)), 
			new Crack(new Rectangle(400,100,128,128)) };

	// -------------------
	// --- Constructor ---
	// ------------------- 	
	private Rectangle[] fishBounds={};
	public AquariumMicroGame() {
		// Extend allowed time.
		baseMicroGameTime = 10.0f;
		multiTouchEnabled = true;
	}

	@Override
	public void load() {
		aquariumBackround=new Texture("aquariumBackground.png");
		aquariumBackroundRegion=new TextureRegion(aquariumBackround,0,0,1280,800);
		aquariumTank=new Texture("aquariumTank.png");
		aquariumTankRegion=new TextureRegion(aquariumTank,0,0,1280,800);
		aquariumCrack=new TextureRegion(aquariumTank,1285,0,128,128);
		for(int i=0;i<3;i++)
		{
			fish[i]=new Fish();
		}
		//fish.bounds=new Rectangle(100,50,128,128);
		//fish[0].bounds=new Rectangle(100,50,128,128);
		fish[0].bounds= new Rectangle(100,50,128,128);
		fish[1].bounds=new Rectangle(1000,600,128,128);
		fish[2].bounds=new Rectangle(500,500,128,128);
		fish[0].rightSide=new TextureRegion(aquariumTank,1285,130,128,100);
		fish[0].leftSide=new TextureRegion(aquariumTank,1415,130,128,100);
		fish[2].rightSide=new TextureRegion(aquariumTank,1285,360,128,90);
		fish[2].leftSide=new TextureRegion(aquariumTank,1415,360,128,90);
		fish[1].rightSide=new TextureRegion(aquariumTank,1285,460,128,80);
		fish[1].leftSide=new TextureRegion(aquariumTank,1415,460,128,80);

	}

	@Override
	public void unload() {
		aquariumBackround.dispose();
		aquariumTank.dispose();

	}

	@Override
	public void reload() {
		aquariumBackround.dispose();
		aquariumTank.dispose();

	}


	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {
		// Checks for time-based win.
		if (wonTimeBased(deltaTime)) {
			AssetsManager.playSound(AssetsManager.highJumpSound);
			return;
		}

		// Checks for water level based loss.
		if(waterLevel >= 800) {
			AssetsManager.playSound(AssetsManager.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}

		// Places cracks on the screen at timed intervals, based on level.
		showNewCracks();
		//updateFishPlacement();

		// Gets all TouchEvents and stores them in a list.
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

		for(TouchEvent touchEvent : touchEvents) {
			touchPoint.set(touchEvent.x, touchEvent.y);
			guiCam.touchToWorld(touchPoint);

			// Sets isClosed to true if gap is touched. 
			for (int i = 0; i < level+1; i++){
				if(crackList[i].onScreen == true) {

					if(targetTouchDragged(touchEvent, touchPoint, crackList[i].bounds))
					{
						crackList[i].isLeaking = false;
						if(touchEvent.type == TouchEvent.TOUCH_DOWN)
						{
							AssetsManager.playSound(AssetsManager.pop);
						}
						if(touchEvent.type == TouchEvent.TOUCH_UP)
						{
							AssetsManager.playSound(AssetsManager.pop);
							crackList[i].isLeaking = true;
						}
					}
					if(touchEvent.type == TouchEvent.TOUCH_UP)
					{
						crackList[i].isLeaking = true;
					}
					

				}
			}

			//Tests for non-unique touch events, which is currently pause only.
			if (touchEvents.get(0).type == TouchEvent.TOUCH_UP)
				super.updateRunning(touchPoint);
		}

		for(Crack c : crackList)
			if(c.isLeaking && c.onScreen)
				decreaseWaterLevel(c);
		for(int i=0;i<3;i++)
		{
			moveFish(i);
		}
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
		waterLevel += (crack.leakRate * speedScalar[speed-1]);
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
		batcher.beginBatch(aquariumBackround);
		batcher.drawSprite(0, 0, 1280, 800, aquariumBackroundRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningObjects() {
		batcher.beginBatch(aquariumTank);
		
		for(int i=2;i>-1;i--)
		{
			if(fish[i].xDirection==-1)
				batcher.drawSprite(fish[i].bounds, fish[i].rightSide);
			else
				batcher.drawSprite(fish[i].bounds, fish[i].leftSide);
		}


		batcher.drawSprite(0, 0, 1280, waterLevel, aquariumTankRegion);
		for (int i = 0; i < level+1; i++)
			if(crackList[i].onScreen == true)
				batcher.drawSprite(crackList[i].bounds,aquariumCrack);
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
	public void moveFish(int f)
	{

		int changeUp=generator.nextInt(500)+1;
		float x = fish[f].bounds.lowerLeft.x;
		float y = fish[f].bounds.lowerLeft.y;
		if(changeUp>499)
		{
			fish[f].xDirection=fish[f].xDirection*-1;
			fish[f].yDirection=fish[f].yDirection*-1;
		}
		else if(changeUp>498)
		{
			fish[f].xDirection=fish[f].xDirection*-1;
		}
		else if(changeUp>497)
		{
			fish[f].yDirection=fish[f].yDirection*-1;
		}

		if(x>1100)
		{
			fish[f].xDirection=-1;
		}
		if(x<10)
		{
			fish[f].xDirection=1;
		}
		if(y>600)
		{
			fish[f].yDirection=-1;
		}
		if(y<50)
		{
			fish[f].yDirection=1;
		}
		x=x+(fish[f].xDirection*2);
		y=y+(fish[f].yDirection*2);
		fish[f].bounds.lowerLeft.set(x,y);
	}
	
	public class Fish
	{
		public Rectangle bounds;
		public int xDirection=1;
		public int yDirection=1;
		public TextureRegion leftSide;
		public TextureRegion rightSide;
		public Fish()
		{
			//bounds=new Rectangle(1,1,1,1);
		}
	}


}
