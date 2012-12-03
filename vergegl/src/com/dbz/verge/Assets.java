package com.dbz.verge;

import com.dbz.framework.gl.Animation;
import com.dbz.framework.gl.Font;
import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.impl.GLGame;
import com.dbz.framework.impl.Music;
import com.dbz.framework.impl.Sound;
import com.dbz.framework.impl.SoundManager;

public class Assets {
	
	// -------------------
	// --- Menu Assets ---
	// -------------------
	
    public static Texture background;
    public static TextureRegion backgroundRegion;
    public static Texture mainMenuButtons;
    public static TextureRegion mainMenuButtonsRegion;
    
    public static Texture playMenuButtons;
    public static TextureRegion playMenuButtonsRegion;
    
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
    
    public static Texture gameGridIcons;
    public static TextureRegion gameGridIconsRegion;
    
    public static Texture soundToggle;
    public static TextureRegion soundOnRegion;
    public static TextureRegion soundOffRegion;
    public static Texture backArrow;
    public static TextureRegion backArrowRegion; 
    public static Texture pauseToggle;
    public static TextureRegion pauseRegion;
    public static TextureRegion unpauseRegion;
       
    public static Texture vergeFont;
    public static Font terminalFont;
    
    // ------------------------
 	// --- MicroGame Assets ---
 	// ------------------------
    
    public static Texture broFistBackground;
    public static TextureRegion broFistBackgroundRegion;
    public static Texture broFist;
    public static TextureRegion broFistRegion;
    
    public static Texture flyBackground;
    public static TextureRegion flyBackgroundRegion;
    public static Texture fly;
    public static TextureRegion flyRegion;
    
    public static Texture fire;
    public static TextureRegion fireBackgroundRegion;
    public static TextureRegion fireWindowRegion;
    public static TextureRegion clearWindowRegion;
    
    public static Texture traffic;
    public static TextureRegion trafficBackgroundRegion;
    public static TextureRegion trafficBlueCarRegion;
    public static TextureRegion trafficRedCarRegion;
    public static TextureRegion trafficBlackCarRegion;
    
    public static Texture circuitBackground;
    public static TextureRegion circuitBackgroundRegion;
    public static Texture circuit;
    public static TextureRegion circuitLinesRegion; //height 768 width 1280
    
    public static TextureRegion circuitVerticalGapOnRegion;
    public static TextureRegion circuitVerticalGapOffRegion;
    
    public static TextureRegion circuitHorizontalGapOnRegion;
    public static TextureRegion circuitHorizontalGapOffRegion;
    
    public static TextureRegion circuitSparkState1Region;
    public static TextureRegion circuitSparkState2Region;
    
    public static TextureRegion circuitLightOnRegion;
    public static TextureRegion circuitLightOffRegion;
    
    public static Animation circuitHorizontalGapAnim;
    public static Animation circuitVerticalGapAnim;
    public static Animation circuitSparkAnim;

    public static Texture lazerBackground;
    public static TextureRegion lazerBackgroundRegion;
    public static Texture lazer;
    public static TextureRegion lazerBall;
    public static TextureRegion lazerFace;
    public static TextureRegion lazerState3Region;
    public static TextureRegion lazerState4Region;
    public static TextureRegion lazerState5Region;
    public static TextureRegion lazerState6Region;
    public static TextureRegion lazerFireButtonInitialRegion;
    public static TextureRegion lazerFireButtonReadyRegion;
    public static TextureRegion lazerTargetRegion;
    public static Animation lazerChargingAnim;
    public static Animation lazerFireButtonAnim;
    
    public static Texture aquariumBackround;
    public static Texture aquariumTank;
    public static TextureRegion aquariumBackroundRegion;
    public static TextureRegion aquariumTankRegion;
    public static TextureRegion aquariumCrack;
    
    public static Texture dirtBikeBackground;
    public static TextureRegion dirtBikeBackgroundRegion;
    public static TextureRegion dirtBikeRegion;
    public static TextureRegion gasPedalRegion;
    
    public static Texture toss;
    public static TextureRegion tossBallRegion;
    public static TextureRegion tossBackgroundRegion;
    
    // ------------------------
  	// --- Sound Assets -----
  	// ------------------------
    public static SoundManager soundManager; //needed to use Sound Objects
    
    // ------------------------
  	// --- Testing Assets -----
  	// ------------------------
    
    public static Texture boundOverlay;
    public static TextureRegion boundOverlayRegion;
 
    public static Music music;
    public static Sound jumpSound;
    public static Sound highJumpSound;
    public static Sound hitSound;
    public static Sound coinSound;
    public static Sound clickSound;
    public static Sound firinMahLazer;
    
    public static void load(GLGame game) {
    	// *** Initialize Menu Assets. ***
        background = new Texture(game, "background.png");
        backgroundRegion = new TextureRegion(background, 0, 0, 1280, 800);
        mainMenuButtons = new Texture(game, "mainmenubuttons.png");
        mainMenuButtonsRegion = new TextureRegion(mainMenuButtons, 0, 0, 1280, 800);
        
        playMenuButtons = new Texture(game, "playmenubuttons.png");
        playMenuButtonsRegion = new TextureRegion(playMenuButtons, 0, 0, 1280, 800);
        
        gameGrid = new Texture(game, "gamegrid.png");
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
        
        gameGridIcons = new Texture(game, "gamegridicons.png");
        gameGridIconsRegion = new TextureRegion(gameGridIcons, 0, 0, 1024, 800);
        
        soundToggle = new Texture(game, "volumetoggle.png");
        soundOnRegion = new TextureRegion(soundToggle, 0, 0, 140, 140);
        soundOffRegion = new TextureRegion(soundToggle, 140, 0, 140, 140);
        backArrow = new Texture(game, "backarrow.png");
        backArrowRegion = new TextureRegion(backArrow, 0, 0, 140, 140); 
        pauseToggle = new Texture(game, "pausetoggle.png");
        pauseRegion = new TextureRegion(pauseToggle, 0, 0, 140, 140);
        unpauseRegion = new TextureRegion(pauseToggle, 140, 0, 140, 140);
          
        vergeFont = new Texture(game, "verge_font.png");
        terminalFont = new Font(vergeFont, 0, 0, 15, 17, 32);
        
        // *** Initialize MicroGame Assets. ***
        broFistBackground = new Texture(game, "brofistbackground.png");
        broFistBackgroundRegion = new TextureRegion(broFistBackground, 0, 0, 1280, 800);
        broFist = new Texture(game, "brofist.png");
        broFistRegion = new TextureRegion(broFist, 0, 0, 320, 240);
        
        flyBackground = new Texture(game, "flybackground.png");
        flyBackgroundRegion = new TextureRegion(flyBackground, 0, 0, 1280, 800);
        fly = new Texture(game, "fly.png");
        flyRegion = new TextureRegion(fly, 0, 0, 80, 60);
        
        fire = new Texture(game, "firehouse.png");
        fireBackgroundRegion = new TextureRegion(fire, 0, 0, 1280, 800);
        fireWindowRegion = new TextureRegion(fire, 1300, 20, 180, 260);
        clearWindowRegion = new TextureRegion(fire, 1500, 20, 180, 260);
        
        traffic = new Texture(game, "traffic.png");
        trafficBackgroundRegion = new TextureRegion(traffic, 0, 0, 1280, 800);
        trafficBlueCarRegion = new TextureRegion(traffic, 0, 800, 80, 170);
        trafficRedCarRegion = new TextureRegion(traffic, 80, 800, 80, 170);
        trafficBlackCarRegion = new TextureRegion(traffic, 160, 800, 80, 170);
        
        circuitBackground = new Texture(game, "circuit_background.png");
        circuitBackgroundRegion = new TextureRegion(circuitBackground, 0, 0, 1280, 800);
          
        circuit = new Texture(game, "circuit_items.png");
        circuitLinesRegion = new TextureRegion(circuit, 0, 0, 1280, 800);
        
        circuitSparkState1Region = new TextureRegion(circuit, 1536, 0, 128, 128);
        circuitSparkState2Region = new TextureRegion(circuit, 1664, 0, 128, 128);
        
        circuitHorizontalGapOffRegion = new TextureRegion(circuit, 1792, 0, 128, 32);
        circuitHorizontalGapOnRegion  = new TextureRegion(circuit, 1792, 32, 128, 32);
       
        circuitVerticalGapOnRegion = new TextureRegion(circuit, 1952, 0, 32, 128);
        circuitVerticalGapOffRegion  = new TextureRegion(circuit, 1984, 0, 32, 128);

        circuitLightOnRegion = new TextureRegion(circuit, 1536, 128, 256, 128);
        circuitLightOffRegion  = new TextureRegion(circuit, 1792, 128, 256, 128);
        
        circuitHorizontalGapAnim =  new Animation(0, circuitHorizontalGapOffRegion, circuitHorizontalGapOnRegion);
        circuitVerticalGapAnim =  new Animation(0, circuitVerticalGapOffRegion, circuitVerticalGapOnRegion);
        circuitSparkAnim = new Animation(0.2f, circuitSparkState1Region, circuitSparkState2Region);
        
        lazerBackground = new Texture(game, "lazerBackground.png");
        lazerBackgroundRegion = new TextureRegion(lazerBackground, 0, 0, 1280, 800);
        lazer = new Texture(game, "lazerItems.png");
        lazerBall= new TextureRegion(lazer, 0, 0, 192, 192);
        lazerFace = new TextureRegion(lazer, 256, 0, 224, 320);
        //lazerState3Region = new TextureRegion(lazer, 512, 0, 256, 256);
        //lazerState4Region = new TextureRegion(lazer, 768, 0, 256, 256);
        //lazerState5Region = new TextureRegion(lazer, 0, 256, 256, 256);
        //lazerState6Region = new TextureRegion(lazer, 256, 256, 256, 256);        
        //lazerFireButtonInitialRegion = new TextureRegion(lazer, 512, 256, 128, 256);
        //lazerFireButtonReadyRegion = new TextureRegion(lazer, 640, 256, 128, 256);
        //lazerTargetRegion = new TextureRegion(lazer, 896, 256, 128, 128);
        //lazerChargingAnim = new Animation(0, lazerState1Region, lazerState2Region, 
        								//lazerState3Region, lazerState4Region, lazerState5Region, lazerState6Region);
        //lazerFireButtonAnim = new Animation(0, lazerFireButtonInitialRegion, lazerFireButtonReadyRegion);
        
        aquariumBackround=new Texture(game,"aquariumBackground.png");
        aquariumBackroundRegion=new TextureRegion(aquariumBackround,0,0,1280,800);
        aquariumTank=new Texture(game,"aquariumTank.png");
        aquariumTankRegion=new TextureRegion(aquariumTank,0,0,1280,800);
        aquariumCrack=new TextureRegion(aquariumTank,1285,0,128,128);
        
        dirtBikeBackground = new Texture(game,"DirtBikeScreen.png");
        dirtBikeBackgroundRegion = new TextureRegion(dirtBikeBackground,0,0,1300,700);
        dirtBikeRegion = new TextureRegion(dirtBikeBackground, 1400,1,256,256);
        gasPedalRegion = new TextureRegion(dirtBikeBackground,1440,300,160,160);
        
        toss = new Texture(game,"toss.png");
        tossBallRegion = new TextureRegion(toss, 0,800,80,80);
        tossBackgroundRegion = new TextureRegion(toss,0,0,1280,800);
        
        // *** Initialize Testing Assets. ***
        boundOverlay = new Texture(game, "boundoverlay.png");
        boundOverlayRegion = new TextureRegion(boundOverlay, 0, 0, 1280, 800);
        
        soundManager = SoundManager.getInstance(); //instance of soundManger-- needed for sound objects
        
        music = game.getAudio().newMusic(R.raw.music);
        music.loop();
        music.setVolume(50);
        if(Settings.soundEnabled)
            music.play();
        
        jumpSound = game.getAudio().newSound(R.raw.jump);
        highJumpSound = game.getAudio().newSound(R.raw.highjump);
        hitSound = game.getAudio().newSound(R.raw.hit);
        coinSound = game.getAudio().newSound(R.raw.coin);
        clickSound = game.getAudio().newSound(R.raw.click);   
        firinMahLazer = game.getAudio().newSound(R.raw.firin_mah_lazer);
        
    }      
    
    public static void reload() {
    	// *** Reload Menu Assets. ***
        background.reload();
        mainMenuButtons.reload();
        playMenuButtons.reload();
        gameGrid.reload();
        gameGridIcons.reload();
        soundToggle.reload();
        backArrow.reload();
        pauseToggle.reload();
        vergeFont.reload();

        // *** Reload MicroGame Assets. ***
        aquariumBackround.reload();
        aquariumTank.reload();
        dirtBikeBackground.reload();
        broFistBackground.reload();
        broFist.reload();
        flyBackground.reload();
        fly.reload();
        fire.reload();
        traffic.reload();
        lazerBackground.reload();
        lazer.reload();
        circuitBackground.reload();
        circuit.reload();
        toss.reload();
        
        // *** Reload Testing Assets. ***
        boundOverlay.reload();
        
        if(Settings.soundEnabled)
            music.play();
    }
    
    public static void playSound(Sound sound) {
        if(Settings.soundEnabled)
            sound.play(100);
    }
    
    public static void playSound(Sound sound, float playbackSpeed) {
        if(Settings.soundEnabled){
        	sound.setPlaySpeedMultiplier(playbackSpeed);
            sound.play(100);
        }
    }
}
