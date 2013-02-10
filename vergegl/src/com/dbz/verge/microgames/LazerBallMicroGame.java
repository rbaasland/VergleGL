package com.dbz.verge.microgames;

import java.util.List;

import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.framework.math.Rectangle;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.MicroGame;

// TODO: Explosion Art, better laser art, something better than bob as target. (See todo's below)

public class LazerBallMicroGame extends MicroGame  {

	// --------------
	// --- Fields ---
	// --------------

	// Assets
	public static Texture lazerBackground;
    public static TextureRegion lazerBackgroundRegion;
    public static Texture lazer;
    public static TextureRegion lazerBall;
    public static TextureRegion lazerFace;
	
	// Array used to store the different required counts of the 3 difficulty levels.
	private int requiredLazerChargeCount[] = { 10, 20, 30 };
	// Charge rate = (max size - min size) / requiredLazerChargeCount[x]
	private float lazerChargeRate[] = {40.4f, 20.2f, 13.467f};
	private int chargeCount = 0;

	//Handle game animation pause (sleep time) based on firinMahLazer.ogg length
	private int animationPauseTime[] = {2650, 1767, 1325}; //2.65 / 1.5 * 1000 = 1767 ms
	
	// height and width constants
	private int lazerMinSize = 96;   
	// private int lazerMaxSize = 500; //usually used in non-programatic calculations -- don't delete this line
	
	// Used to track state of laser
	private boolean lazerCharged = false;
	boolean lazerSoundPlayed = false;

	// Bounds for touch detection. 					//450, 340
	private Rectangle lazerBallBounds = new Rectangle(325, 340, lazerMinSize, lazerMinSize);															
	private Rectangle lazerFaceBounds = new Rectangle(325, 400, 632, 728); //width = max + 32, height = max + 128
	private Rectangle targetBounds = new Rectangle(1100, 240, 250, 200); //TODO Need to replace with... something. Maybe Random asset from game?

	// -------------------
	// --- Constructor ---
	// -------------------   
	public LazerBallMicroGame() {
		//totalMicroGameTime = new float[]{10.0f, 8.5f, 7.0f}; //leaving at default time for now
	}
	
	@Override
	public void load() {
		lazerBackground = new Texture("lazerBackground.png");
        lazerBackgroundRegion = new TextureRegion(lazerBackground, 0, 0, 1280, 800);
        lazer = new Texture("lazerItems.png");
        lazerBall= new TextureRegion(lazer, 0, 0, 192, 192);
        lazerFace = new TextureRegion(lazer, 256, 0, 224, 320);
        
	}
	
	@Override
	public void unload() {
		lazerBackground.dispose();
		lazer.dispose();
	}

	@Override
	public void reload() {
		lazerBackground.dispose();
		lazer.dispose();
		
	}
	
	// ---------------------
	// --- Update Method ---
	// ---------------------   
	@Override
	public void updateRunning(float deltaTime) {

		// stop game clock when lazer is charged
		if(!lazerCharged){ 
			if (lostTimeBased(deltaTime)) {
				AssetsManager.playSound(AssetsManager.hitSound);
				return;
			} 
		} else { // lazer fired
			
			if(!lazerSoundPlayed){ // if win sound hasn't played, pause bg music, start sound prep for animation.
				AssetsManager.music.pause();
				AssetsManager.playSound(AssetsManager.firinMahLazer, speedScalar[speed-1]); //playspeed based on anim scalar
						
				try { // sleep for sound to play
					Thread.sleep(animationPauseTime[speed-1]);
				} catch (InterruptedException e) {e.printStackTrace();}
				
				lazerSoundPlayed = true;
				return;
					
				}else {
					movelazer(); // fire lazer
				
					if(collision(lazerBallBounds, targetBounds)){
						if(com.dbz.verge.Settings.soundEnabled) //TODO add implementation in Assets.java??
							AssetsManager.music.play();
						
						AssetsManager.playSound(AssetsManager.highJumpSound);
						microGameState = MicroGameState.Won;
				}
					return;
				}
			}
	
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
		for(TouchEvent touchEvent : touchEvents) {
			touchPoint.set(touchEvent.x, touchEvent.y);
			guiCam.touchToWorld(touchPoint);

			
			if (targetTouchDownCenterCoords(touchEvent, touchPoint, lazerBallBounds)) {

					chargeCount++;
					increaseLazerSize();

				if (chargeCount == requiredLazerChargeCount[level-1]) {
					lazerCharged = true;
				}
				else if (chargeCount < requiredLazerChargeCount[level-1])
					AssetsManager.playSound(AssetsManager.coinSound); //TODO need charging sound asset here
				return;
			}

			// Tests for non-unique touch events, which is currently pause only.
			if (touchEvent.type == TouchEvent.TOUCH_UP)
				super.updateRunning(touchPoint);
		}   
	}

	// ------------------------------
	// --- Utility Update Methods ---
	// ------------------------------

	@Override
	public void reset() {
		super.reset();
		// reset members
		chargeCount = 0;
		lazerCharged = false;
		lazerSoundPlayed = false;
		lazerBallBounds.lowerLeft.set(325, 340); // reset lazer to original bounds
		lazerBallBounds.width = lazerMinSize;
		lazerBallBounds.height = lazerMinSize;
	}

	private void increaseLazerSize() {
		lazerBallBounds.width +=  lazerChargeRate[level-1];
		lazerBallBounds.height +=  lazerChargeRate[level-1];
	}

	private void movelazer() {
		lazerBallBounds.lowerLeft.x += 64 * speedScalar[speed-1];
		lazerBallBounds.width += 128 * speedScalar[speed-1];	
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

		if (lazerCharged)
			drawInstruction("FIRIN' MAH LAZER!!!!");
		else drawInstruction("Tap the lazer ball!");
		super.presentRunning();
	}

	// ----------------------------
	// --- Utility Draw Methods ---
	// ----------------------------

	@Override
	public void drawRunningBackground() {
		// Draw background.
		batcher.beginBatch(lazerBackground);
		batcher.drawSprite(0, 0, 1280, 800, lazerBackgroundRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningObjects() {
		// Draw Lazer Assets
		batcher.beginBatch(lazer);
		
		if(lazerCharged)
			batcher.drawSpriteCenterCoords(lazerFaceBounds, lazerFace);
		batcher.drawSpriteCenterCoords(lazerBallBounds, lazerBall);
		//batcher.drawSprite(targetBounds, Assets.lazerTargetRegion);
		batcher.endBatch();
	}

	@Override
	public void drawRunningBounds() {
		// Bounding Boxes
		batcher.beginBatch(AssetsManager.boundOverlay);
		batcher.drawSpriteCenterCoords(lazerBallBounds, AssetsManager.boundOverlayRegion);  // LazerBall Bounding Box
		batcher.drawSprite(targetBounds, AssetsManager.boundOverlayRegion);	 // Target Bounding Box
		batcher.endBatch();
	}

}
