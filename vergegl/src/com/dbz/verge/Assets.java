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
    
    public static Texture gameGridIconsPageOne;
    public static TextureRegion gameGridIconsPageTwoRegion;
    
    public static Texture gameGridIconsPageTwo;
    public static TextureRegion gameGridIconsPageOneRegion;
    
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
    public static TextureRegion circuitLine1;
    public static TextureRegion circuitLine2;
    public static TextureRegion circuitLine3;
    public static TextureRegion circuitLine4;
    public static TextureRegion circuitSparkState1Region;
    public static TextureRegion circuitSparkState2Region;
    public static Animation circuitSparkAnim;

    public static Texture lazerBackground;
    public static TextureRegion lazerBackgroundRegion;
    public static Texture lazer;
    public static TextureRegion lazerBall;
    public static TextureRegion lazerFace;
    
    public static Texture aquariumBackround;
    public static Texture aquariumTank;
    public static TextureRegion aquariumBackroundRegion;
    public static TextureRegion aquariumTankRegion;
    public static TextureRegion aquariumCrack;
    
    public static Texture dirtBikeBackground;
    public static TextureRegion dirtBikeBackgroundRegion;
    public static TextureRegion dirtBikeRegion;
    public static TextureRegion dirtBikeGasPedalRegion;
    public static TextureRegion dirtBikeJumpButtonRegion;
    public static TextureRegion dirtBikeFrameRegion;
    public static TextureRegion dirtBikeWheelRegion;
    
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
    public static Sound	pop;
    
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
        
        gameGridIconsPageOne = new Texture(game, "gamegridicons1.png");
        gameGridIconsPageOneRegion = new TextureRegion(gameGridIconsPageOne, 0, 0, 1024, 800);
        gameGridIconsPageTwo = new Texture(game, "gamegridicons2.png");
        gameGridIconsPageTwoRegion = new TextureRegion(gameGridIconsPageTwo, 0, 0, 1024, 800);
        
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
        circuitSparkState1Region = new TextureRegion(circuit, 0,896,128,128);
        circuitSparkState2Region = new TextureRegion(circuit, 128,896,128,128);
        circuitSparkAnim = new Animation(0.2f, circuitSparkState1Region, circuitSparkState2Region);
        //Circuit parts TODO draw lines based on vectors instead of images
        circuitLine1 = new TextureRegion(circuit, 0, 0, 405, 195);
        circuitLine2 = new TextureRegion(circuit, 0, 256, 328, 383);
        circuitLine3 = new TextureRegion(circuit, 416, 512, 325, 122);
        circuitLine4 = new TextureRegion(circuit, 436,10, 557, 480);

        lazerBackground = new Texture(game, "lazerBackground.png");
        lazerBackgroundRegion = new TextureRegion(lazerBackground, 0, 0, 1280, 800);
        lazer = new Texture(game, "lazerItems.png");
        lazerBall= new TextureRegion(lazer, 0, 0, 192, 192);
        lazerFace = new TextureRegion(lazer, 256, 0, 224, 320);
        
        aquariumBackround=new Texture(game,"aquariumBackground.png");
        aquariumBackroundRegion=new TextureRegion(aquariumBackround,0,0,1280,800);
        aquariumTank=new Texture(game,"aquariumTank.png");
        aquariumTankRegion=new TextureRegion(aquariumTank,0,0,1280,800);
        aquariumCrack=new TextureRegion(aquariumTank,1285,0,128,128);
        
        dirtBikeBackground = new Texture(game,"DirtBikeScreen.png");
        dirtBikeBackgroundRegion = new TextureRegion(dirtBikeBackground,0,0,1300,700);
        dirtBikeRegion = new TextureRegion(dirtBikeBackground, 1400,1,256,256);
        dirtBikeGasPedalRegion = new TextureRegion(dirtBikeBackground,1440,300,160,160);
        dirtBikeJumpButtonRegion = new TextureRegion(dirtBikeBackground,1440,460,160,160);
        dirtBikeFrameRegion = new TextureRegion(dirtBikeBackground,0,720,250,220);
        dirtBikeWheelRegion = new TextureRegion(dirtBikeBackground,270,720,100,100);
        
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
        pop=game.getAudio().newSound(R.raw.pop);
    }      
    
    public static void reload() {
    	// *** Reload Menu Assets. ***
        background.reload();
        mainMenuButtons.reload();
        playMenuButtons.reload();
        gameGrid.reload();
        gameGridIconsPageOne.reload();
        gameGridIconsPageTwo.reload();
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
