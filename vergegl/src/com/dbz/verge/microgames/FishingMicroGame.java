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
	public int numOfFish=3;
	public Fish fish[]=new Fish[numOfFish];
	public Hook hook;
	public Junk junk[]=new Junk[2];
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
	//public static TextureRegion fishHook;
	public static TextureRegion specialFish;

	public int fishCaught=0;

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
		hook=new Hook();
		hook.bounds=new Rectangle(hook.xLocation,hook.yLocation,100,800);
		hook.fishHook=new TextureRegion(fishingTank,1560,0,100,800);
		for(int i=0;i<2;i++)
		{
			junk[i]=new Junk();
		}
		for(int i=0;i<3;i++)
		{
			fish[i]=new Fish();
		}
		//fish.bounds=new Rectangle(100,50,128,128);
		//fish[0].bounds=new Rectangle(100,50,128,128);
		junk[0].bounds=new Rectangle(100,400,100,100);
		junk[0].image=new TextureRegion(fishingTank,1315,540,67,66);
		junk[0].weight=2;
		junk[1].bounds=new Rectangle(600,5,100,100);
		junk[1].image=new TextureRegion(fishingTank,1455,540,55,66);
		junk[1].weight=4;
		if(level==1)
		{
			junk[0].visible=false;
			junk[1].visible=false;
		}
		if(level==2)
		{
			junk[1].visible=false;
		}
		fish[0].bounds= new Rectangle(100,50,120,75);
		fish[2].bounds=new Rectangle(1000,600,72,50);
		fish[1].bounds=new Rectangle(500,500,96,60);
		fish[0].rightSide=new TextureRegion(fishingTank,1288,140,120,75);
		fish[0].leftSide=new TextureRegion(fishingTank,1425,140,120,75);
		fish[2].rightSide=new TextureRegion(fishingTank,1308,380,72,50);
		fish[2].leftSide=new TextureRegion(fishingTank,1440,380,72,50);
		fish[1].rightSide=new TextureRegion(fishingTank,1296,468,96,60);
		fish[1].leftSide=new TextureRegion(fishingTank,1442,468,96,60);


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
		if (fishCaught>=3) {
			AssetsManager.reel.stop();
			AssetsManager.playSound(AssetsManager.highJumpSound);
			microGameState=MicroGameState.Won;
			return;
		}

		// Checks for water level based loss.
		if(lostTimeBased(deltaTime)) {
			AssetsManager.reel.stop();
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
		int speedUp=8;
		for(int i=0;i<2;i++)
		{
			if(junk[i].caught==true&&junk[i].visible==true&&junk[i].bounds.lowerLeft.y<800)
			{
				float tempY=junk[i].bounds.lowerLeft.y;
				speedUp=speedUp/((i+1)*2);
				tempY=tempY+speedUp;
				junk[i].bounds.lowerLeft.set(junk[i].bounds.lowerLeft.x,tempY);
				if(junk[i].bounds.lowerLeft.y>790)
				{
					junk[i].visible=false;
					hook.fishOnLine=false;
				}
			}
			else
			{
				if(junk[0].caught==false)
				moveBoot();
			}
		}
		for(int i=0;i<3;i++)
		{
			if(fish[i].caught==true&&fish[i].visible==true&&fish[i].bounds.lowerLeft.y<800)
			{
				float tempY=fish[i].bounds.lowerLeft.y;
				tempY=tempY+speedUp;
				fish[i].bounds.lowerLeft.set(fish[i].bounds.lowerLeft.x,tempY);
				if(fish[i].bounds.lowerLeft.y>800)
				{
					fishCaught++;
					fish[i].visible=false;
					hook.fishOnLine=false;
				}
			}
			else
			{
			moveFish(i);
			}
		}
		if(hook.fishOnLine==false)
		{
		moveHook();
		AssetsManager.reel.stop();
		}
		else
		{


			float tempY=hook.bounds.lowerLeft.y;
			tempY=tempY+speedUp;
			hook.bounds.lowerLeft.set(hook.bounds.lowerLeft.x, tempY);
		}
		catchFish();
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
		drawInstruction("Catch the fish!");
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
		batcher.drawSprite(hook.bounds,hook.fishHook);
		for(int i=2;i>-1;i--)
		{
			if(fish[i].visible==true)
			{
				if(fish[i].xDirection==-1)
					batcher.drawSprite(fish[i].bounds, fish[i].rightSide);
				else
					batcher.drawSprite(fish[i].bounds, fish[i].leftSide);
			}
		}
		for(int i=1;i>-1;i--)
		{
			if(junk[i].visible==true)
			{
				batcher.drawSprite(junk[i].bounds,junk[i].image);
			}
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
	public void moveHook()
	{
		int moveX=(int) game.getInput().getAccelY();
		int moveY=(int) game.getInput().getAccelX()-4;
		if(hook.xLocation+moveX>1195)
		{
			hook.xLocation=1195;
		}
		else if(hook.xLocation+moveX<5)
		{
			hook.xLocation=5;
		}
		else
		{
			hook.xLocation=(int) (hook.xLocation+moveX*1.5);
		}
		if(hook.yLocation+moveY>795)
		{
			hook.yLocation=790;
		}
		else if(hook.yLocation+moveY<5)
		{
			hook.yLocation=10;
		}
		else
		{
			hook.yLocation=(int) (hook.yLocation-moveY*1.5);
		}
		hook.bounds.lowerLeft.set(hook.xLocation, hook.yLocation);

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
		x=(float) (x+(fish[f].xDirection*2*Math.pow(speedScalar[speed-1],2)));
		y=(float) (y+(fish[f].yDirection*2*Math.pow(speedScalar[speed-1],2)));
		fish[f].bounds.lowerLeft.set(x,y);
	}
	public void catchFish()
	{
		float tempX=0;
		float tempY=0;
		for(int i=0;i<2;i++)
		{
			if(junk[i].caught==false&&junk[i].visible==true&&hook.fishOnLine==false)
			{
				if((hook.bounds.lowerLeft.x+23<=(junk[i].bounds.lowerLeft.x+junk[i].bounds.width))&&(hook.bounds.lowerLeft.x+23>=junk[i].bounds.lowerLeft.x))
				{
					if((hook.bounds.lowerLeft.y+54<=(junk[i].bounds.lowerLeft.y+junk[i].bounds.height))&&(hook.bounds.lowerLeft.y+54>=junk[i].bounds.lowerLeft.y))
					{
						AssetsManager.reel.setPlayBehavior(1);
						AssetsManager.reel.loop();
						AssetsManager.playSound(AssetsManager.reel);
						hook.fishOnLine=true;
						junk[i].caught=true;

					}
				}
			}
		}
		for(int i=0;i<numOfFish;i++)
		{
			if(fish[i].caught==false&&fish[i].visible==true&&hook.fishOnLine==false)
			{
				if((hook.bounds.lowerLeft.x+23<=(fish[i].bounds.lowerLeft.x+fish[i].bounds.width))&&(hook.bounds.lowerLeft.x+23>=fish[i].bounds.lowerLeft.x))
				{
					if((hook.bounds.lowerLeft.y+54<=(fish[i].bounds.lowerLeft.y+fish[i].bounds.height))&&(hook.bounds.lowerLeft.y+54>=fish[i].bounds.lowerLeft.y))
					{
						AssetsManager.reel.setPlayBehavior(1);
						AssetsManager.reel.loop();
						AssetsManager.playSound(AssetsManager.reel);
						hook.fishOnLine=true;
						fish[i].caught=true;

					}
				}
			}
		}
	}
public void moveBoot()
{
	float x=junk[0].bounds.lowerLeft.x;
	float y=junk[0].bounds.lowerLeft.y;
	if(x>1150)
	{
		junk[0].xDirection=-1;
	}
	if(x<5)
	{
		junk[0].xDirection=1;
	}
	x=x+2*junk[0].xDirection;
	junk[0].bounds.lowerLeft.set(x,y);
}
	public class Fish
	{
		public Rectangle bounds;
		public int xDirection=1;
		public int yDirection=1;
		public TextureRegion leftSide;
		public TextureRegion rightSide;
		public boolean visible=true;
		public boolean caught=false;
		public Fish()
		{
			//bounds=new Rectangle(1,1,1,1);
		}
	}
	public class Hook
	{
		public Rectangle bounds;
		int xLocation=50;
		int yLocation=600;
		public TextureRegion fishHook;
		public boolean fishOnLine=false;
		public Hook()
		{

		}

	}
	public class Junk
	{
		public Rectangle bounds;
		public boolean visible=true;
		public boolean caught=false;
		public TextureRegion image;
		public int xDirection=1;
		public int yDirection=1;
		public int weight;
		public Junk()
		{
		}
	}


}
