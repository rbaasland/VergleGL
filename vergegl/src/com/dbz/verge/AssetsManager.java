package com.dbz.verge;

import com.dbz.framework.Game;
import com.dbz.framework.audio.Music;
import com.dbz.framework.audio.Sound;
import com.dbz.framework.audio.SoundManager;
import com.dbz.framework.gl.Font;
import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;

public class AssetsManager {

	public static MicroGame currentMicroGame;
	
	public static void loadMicrogame(MicroGame mg){
		
		if(currentMicroGame == null){
			currentMicroGame = mg;
			mg.load();
			return;
		} 
		else {
			currentMicroGame.unload();
			currentMicroGame = mg;
			mg.load();	
		}
	}
	
	public static void reloadCurrentMicroGame(){
		if(currentMicroGame == null)
			return;
		else currentMicroGame.reload();
	}
	
	// -------------------
	// --- Menu Assets ---
	// -------------------
	
    public static Texture background;
    public static TextureRegion backgroundRegion;
    public static TextureRegion backgroundGreyFillRegion;
    
    public static Texture mainMenuButtons;
    public static TextureRegion mainMenuButtonsRegion;
    
    public static Texture playMenuButtons;
    public static TextureRegion playMenuButtonsRegion;
    
    public static Texture transition;
    public static TextureRegion transitionBackgroundRegion;
    public static TextureRegion meterBarOutlineRegion;
    public static TextureRegion meterGreenBarEmptyRegion;
    public static TextureRegion meterGreenBarFillRegion;
    public static TextureRegion meterYellowBarEmptyRegion;
    public static TextureRegion meterYellowBarFillRegion;
    public static TextureRegion meterRedBarEmptyRegion;
    public static TextureRegion meterRedBarFillRegion;
    public static TextureRegion singleTouchOnIndicatorRegion;
    public static TextureRegion multiTouchOnIndicatorRegion;
    public static TextureRegion accelerometerOnIndicatorRegion;
    public static TextureRegion gesturesOnIndicatorRegion;
    public static TextureRegion singleTouchOffIndicatorRegion;
    public static TextureRegion multiTouchOffIndicatorRegion;
    public static TextureRegion accelerometerOffIndicatorRegion;
    public static TextureRegion gesturesOffIndicatorRegion;
    
    public static Texture gameGrid;
    public static TextureRegion gameGridBackgroundRegion;
    
    public static TextureRegion leftArrowRegion;
    public static TextureRegion rightArrowRegion;
    public static TextureRegion overlayRegion;
    public static TextureRegion selectionRegion;
    public static TextureRegion checkMarkRegion;
    public static TextureRegion overlayIconRegion;
    public static TextureRegion speedOneRegion;
    public static TextureRegion speedTwoRegion;
    public static TextureRegion speedThreeRegion;
    public static TextureRegion levelOneRegion;
    public static TextureRegion levelTwoRegion;
    public static TextureRegion levelThreeRegion;   
    
    public static Texture gameGridIconsPageOne;
    public static TextureRegion gameGridIconsPageTwoRegion;
    public static TextureRegion broFistIconRegion;
    public static TextureRegion flyIconRegion;
    public static TextureRegion fireIconRegion;
    public static TextureRegion trafficIconRegion;
    public static TextureRegion circuitIconRegion;
    public static TextureRegion lazerBallIconRegion;
    
    public static Texture gameGridIconsPageTwo;
    public static TextureRegion gameGridIconsPageOneRegion;
    public static TextureRegion aquariumIconRegion;
    public static TextureRegion dirtBikeIconRegion;
    public static TextureRegion tossIconRegion;
    
    public static Texture soundToggle;
    public static TextureRegion soundOnRegion;
    public static TextureRegion soundOffRegion;
    public static Texture backArrow;
    public static TextureRegion backArrowRegion; 
    public static Texture pauseToggle;
    public static TextureRegion pauseRegion;
    public static TextureRegion unpauseRegion;
       
    public static Texture vergeFontTexture;
    public static Font vergeFont;

    // ------------------------
  	// --- Testing Assets -----
  	// ------------------------
    
    public static Texture boundOverlay;
    public static TextureRegion boundOverlayRegion;
 
    // ------------------------
  	// --- Sound Assets -----
  	// ------------------------
    public static SoundManager soundManager; // Required to use sound objects.
    
    public static Music music;
    
    public static Sound punchSound;
    public static Sound explosionSound;
    public static Sound gruntSound;
    
    public static Sound flyBuzzSound;
    
    public static Sound burningSound;
    public static Sound splashSound;
    
    public static Sound jumpSound;
    public static Sound highJumpSound;
    public static Sound hitSound;
    public static Sound coinSound;
    public static Sound clickSound;
    public static Sound firinMahLazer;
    public static Sound	pop;
    
    /** Load Persistent Assets */   
    public static void load(Game game) {
    	// *** Initialize Menu Assets. ***
        background = new Texture("background.png");
        backgroundRegion = new TextureRegion(background, 0, 0, 1280, 800);
        backgroundGreyFillRegion = new TextureRegion(background, 1312, 32, 96, 96);
        
        mainMenuButtons = new Texture("mainmenubuttons.png");
        mainMenuButtonsRegion = new TextureRegion(mainMenuButtons, 0, 0, 1280, 800);
        
        playMenuButtons = new Texture("playmenubuttons.png");
        playMenuButtonsRegion = new TextureRegion(playMenuButtons, 0, 0, 1280, 800);
        
        transition = new Texture("transition.png");
        transitionBackgroundRegion = new TextureRegion(transition, 0, 0, 854, 480);
        meterBarOutlineRegion = new TextureRegion(transition, 8, 483, 113, 31);
        meterGreenBarEmptyRegion = new TextureRegion(transition, 8, 524, 109, 26);
        meterGreenBarFillRegion = new TextureRegion(transition, 133, 486, 109, 26);
        
        meterYellowBarEmptyRegion = new TextureRegion(transition, 133, 524, 109, 26);
        meterYellowBarFillRegion = new TextureRegion(transition, 258, 486, 109, 26);
        meterRedBarEmptyRegion = new TextureRegion(transition, 258, 524, 109, 26);
        meterRedBarFillRegion = new TextureRegion(transition, 383, 486, 109, 26);
        
        singleTouchOnIndicatorRegion = new TextureRegion(transition, 867, 8, 75, 75);
        multiTouchOnIndicatorRegion = new TextureRegion(transition, 942, 8, 75, 75);
        accelerometerOnIndicatorRegion = new TextureRegion(transition, 867, 83, 75, 75);
        gesturesOnIndicatorRegion = new TextureRegion(transition, 942, 83, 75, 75);
        singleTouchOffIndicatorRegion = new TextureRegion(transition, 867, 158, 75, 75);
        multiTouchOffIndicatorRegion = new TextureRegion(transition, 942, 158, 75, 75);
        accelerometerOffIndicatorRegion = new TextureRegion(transition, 867, 233, 75, 75);
        gesturesOffIndicatorRegion = new TextureRegion(transition, 942, 233, 75, 75);
        
        gameGrid = new Texture("gamegrid.png");
        gameGridBackgroundRegion = new TextureRegion(gameGrid, 0, 0, 1280, 800);
        
        leftArrowRegion = new TextureRegion(gameGrid, 1280, 0, 80, 120);
        rightArrowRegion = new TextureRegion(gameGrid, 1360, 0, 80, 120);
        overlayRegion = new TextureRegion(gameGrid, 1300, 140, 720, 360);
        selectionRegion = new TextureRegion(gameGrid, 1280, 520, 560, 240);
        checkMarkRegion = new TextureRegion(gameGrid, 1860, 540, 160, 160);
        overlayIconRegion = new TextureRegion(gameGrid, 1840, 700, 200, 200);
        speedOneRegion = new TextureRegion(gameGrid, 0, 800, 300, 200);
        speedTwoRegion = new TextureRegion(gameGrid, 300, 800, 300, 200);
        speedThreeRegion = new TextureRegion(gameGrid, 600, 800, 300, 200);
        levelOneRegion = new TextureRegion(gameGrid, 900, 800, 300, 200);
        levelTwoRegion = new TextureRegion(gameGrid, 1200, 800, 300, 200);
        levelThreeRegion = new TextureRegion(gameGrid, 1500, 800, 300, 200);      
        
        gameGridIconsPageOne = new Texture("gamegridicons1.png");
        gameGridIconsPageOneRegion = new TextureRegion(gameGridIconsPageOne, 0, 0, 1024, 800);
        broFistIconRegion = new TextureRegion(gameGridIconsPageOne, 280, 180, 240, 220);
        flyIconRegion = new TextureRegion(gameGridIconsPageOne, 520, 180, 240, 220);
        fireIconRegion = new TextureRegion(gameGridIconsPageOne, 760, 180, 240, 220);
        trafficIconRegion = new TextureRegion(gameGridIconsPageOne, 280, 410, 240, 220);
        circuitIconRegion = new TextureRegion(gameGridIconsPageOne, 520, 410, 240, 220);
        lazerBallIconRegion = new TextureRegion(gameGridIconsPageOne, 760, 410, 240, 220);
        
        gameGridIconsPageTwo = new Texture("gamegridicons2.png");
        gameGridIconsPageTwoRegion = new TextureRegion(gameGridIconsPageTwo, 0, 0, 1024, 800);
        aquariumIconRegion = new TextureRegion(gameGridIconsPageTwo, 280, 180, 240, 220);
        dirtBikeIconRegion = new TextureRegion(gameGridIconsPageTwo, 520, 180, 240, 220);
        tossIconRegion = new TextureRegion(gameGridIconsPageTwo, 760, 180, 240, 220);
        
        soundToggle = new Texture("volumetoggle.png");
        soundOnRegion = new TextureRegion(soundToggle, 0, 0, 140, 140);
        soundOffRegion = new TextureRegion(soundToggle, 140, 0, 140, 140);
        backArrow = new Texture("backarrow.png");
        backArrowRegion = new TextureRegion(backArrow, 0, 0, 140, 140); 
        pauseToggle = new Texture("pausetoggle.png");
        pauseRegion = new TextureRegion(pauseToggle, 0, 0, 140, 140);
        unpauseRegion = new TextureRegion(pauseToggle, 140, 0, 140, 140);
          
        vergeFontTexture = new Texture("verge_font.png");
        vergeFont = new Font(vergeFontTexture, 0, 0, 15, 17, 32);
                
        // *** Initialize Testing Assets. ***
        boundOverlay = new Texture("boundoverlay.png");
        boundOverlayRegion = new TextureRegion(boundOverlay, 0, 0, 1280, 800);
        
        soundManager = SoundManager.getInstance(); // instance of soundManager-- needed for sound objects
        
        music = game.getAudio().newMusic(R.raw.music);
        music.loop();
        music.setVolume(0);
        if(Settings.soundEnabled)
            music.play();
        
        // BroFistMicroGame Sound Assets.
        punchSound = game.getAudio().newSound(R.raw.punch);
        punchSound.setVolume(80);
        explosionSound = game.getAudio().newSound(R.raw.explosion);
        gruntSound = game.getAudio().newSound(R.raw.grunt);
        
        // FlyMicroGame Sound Assets.
        flyBuzzSound = game.getAudio().newSound(R.raw.flybuzz);
        flyBuzzSound.loop();
        
        // FireMicroGame Sound Assets.
        burningSound = game.getAudio().newSound(R.raw.burning);
        splashSound = game.getAudio().newSound(R.raw.splash);
        
        jumpSound = game.getAudio().newSound(R.raw.jump);
        highJumpSound = game.getAudio().newSound(R.raw.highjump);
        hitSound = game.getAudio().newSound(R.raw.hit);
        coinSound = game.getAudio().newSound(R.raw.coin);
        clickSound = game.getAudio().newSound(R.raw.click);
        clickSound.setVolume(20);
        firinMahLazer = game.getAudio().newSound(R.raw.firin_mah_lazer);
        pop = game.getAudio().newSound(R.raw.pop);
    }      

    public static void reload() {
    	// *** Reload Menu Assets. ***
        background.reload();
        mainMenuButtons.reload();
        playMenuButtons.reload();
        transition.reload();
        gameGrid.reload();
        gameGridIconsPageOne.reload();
        gameGridIconsPageTwo.reload();
        soundToggle.reload();
        backArrow.reload();
        pauseToggle.reload();
        vergeFontTexture.reload();
    
        // *** Reload MicroGame Assets. ***
        reloadCurrentMicroGame();
        
        // *** Reload Testing Assets. ***
        boundOverlay.reload();
        
        if(Settings.soundEnabled)
            music.play();
    }
    
    public static void playSound(Sound sound) {
        if(Settings.soundEnabled)
            sound.play();
    }
    
    public static void playSound(Sound sound, float playbackSpeed) {
        if(Settings.soundEnabled){
        	sound.setPlaySpeedMultiplier(playbackSpeed);
            sound.play(100);
        }
    }
}
