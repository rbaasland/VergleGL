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
public class FishingMicroGame extends MicroGame{

	// Assets
	public Fish fish[]=new Fish[3];
	public static Texture fishingBackround;
	public static Texture fishingTank;
	public static TextureRegion fishingBackroundRegion;
	public static TextureRegion fishingTankRegion;
	public static TextureRegion fishingCrack;
	public static TextureRegion fish1_1;
	public static TextureRegion fish1_2;
	public static TextureRegion fish2_1;
	public static TextureRegion fish2_2;
	public static TextureRegion fish3;
	public static TextureRegion fishHook;
	public static TextureRegion specialFish;

	public int fishCaught=0;
	public Rectangle hookBounds;

	// Original aquarium water level.
	Random generator=new Random();
	float previousDive=15;

	// -------------------
	// --- Constructor ---
	// ------------------- 	
	private Rectangle[] fishBounds={};
	public FishingMicroGame() {
		// Extend allowed time.
		baseMicroGameTime = 10.0f;
		multiTouchEnabled = true;
	}

	@Override
	public void load() {
		fishingBackround=new Texture("aquariumBackground.png");
		fishingBackroundRegion=new TextureRegion(fishingBackround,0,0,1280,800);
		fishingTank=new Texture("aquariumTank.png");
		fishingTankRegion=new TextureRegion(fishingTank,0,0,1280,800);
		fishingCrack=new TextureRegion(fishingTank,1285,0,128,128);
		hookBounds=new Rectangle(50,600,100,800);
		fishHook=new TextureRegion(fishingTank,1560,0,100,800);
		for(int i=0;i<3;i++)
		{
			fish[i]=new Fish();
		}
		//fish.bounds=new Rectangle(100,50,128,128);
		//fish[0].bounds=new Rectangle(100,50,128,128);
		fish[0].bounds= new Rectangle(100,50,128,128);
		fish[1].bounds=new Rectangle(1000,600,128,128);
		fish[2].bounds=new Rectangle(500,500,128,128);
		fish[0].rightSide=new TextureRegion(fishingTank,1285,130,128,100);
		fish[0].leftSide=new TextureRegion(fishingTank,1415,130,128,100);
		fish[2].rightSide=new TextureRegion(fishingTank,1285,360,128,90);
		fish[2].leftSide=new TextureRegion(fishingTank,1415,360,128,90);
		fish[1].rightSide=new TextureRegion(fishingTank,1285,460,128,90);
		fish[1].leftSide=new TextureRegion(fishingTank,1415,460,128,90);
		

	}

	@Override
	public void unload() {
		fishingBackround.dispose();
		fishingTank.dispose();

	}

	@Override
	public void reload() {
		fishingBackround.dispose();
		fishingTank.dispose();

	}


	// ---------------------
	// --- Update Method ---
	// ---------------------

	@Override
	public void updateRunning(float deltaTime) {
		// Checks for time-based win.
		if (fishCaught>3) {
			AssetsManager.playSound(AssetsManager.highJumpSound);
			return;
		}

		// Checks for water level based loss.
		if(lostTimeBased(deltaTime)) {
			AssetsManager.playSound(AssetsManager.hitSound);
			microGameState = MicroGameState.Lost;
			return;
		}

		//updateFishPlacement();

		// Gets all TouchEvents and stores them in a list.
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

		//iterate though all touches, check if any of the touches are in the gaps of the circuit
		for(TouchEvent touchEvent : touchEvents) {
			touchPoint.set(touchEvent.x, touchEvent.y);
			guiCam.touchToWorld(touchPoint);

			// Sets isClosed to true if gap is touched. 
			for (int i = 0; i < level+1; i++){
			}

			//Tests for non-unique touch events, which is currently pause only.
			if (touchEvents.get(0).type == TouchEvent.TOUCH_UP)
				super.updateRunning(touchPoint);
		}

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
		batcher.beginBatch(fishingBackround);
		batcher.drawSprite(0, 0, 1280, 800, fishingBackroundRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningObjects() {
		batcher.beginBatch(fishingTank);
		batcher.drawSprite(hookBounds,fishHook);
		for(int i=2;i>-1;i--)
		{
			if(fish[i].xDirection==-1)
				batcher.drawSprite(fish[i].bounds, fish[i].rightSide);
			else
				batcher.drawSprite(fish[i].bounds, fish[i].leftSide);
		}

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
