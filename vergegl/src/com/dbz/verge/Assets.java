package com.dbz.verge;

import com.dbz.framework.Music;
import com.dbz.framework.Sound;
import com.dbz.framework.gl.Animation;
import com.dbz.framework.gl.Font;
import com.dbz.framework.gl.Texture;
import com.dbz.framework.gl.TextureRegion;
import com.dbz.framework.impl.GLGame;

public class Assets {
    public static Texture background;
    public static TextureRegion backgroundRegion;
    
    public static Texture mainMenuButtons;
    public static TextureRegion mainMenuButtonsRegion;
    
    public static Texture soundToggle;
    public static TextureRegion soundOnRegion;
    public static TextureRegion soundOffRegion;
    
    public static Texture gameGridBackground;
    public static TextureRegion gameGridBackgroundRegion;
    
    public static Texture backArrow;
    public static TextureRegion backArrowRegion;
    
    public static Texture pauseToggle;
    public static TextureRegion pauseRegion;
    public static TextureRegion unpauseRegion;
    
    public static Texture brofist;
    public static TextureRegion brofistRegion;
    
    public static Texture boundOverlay;
    public static TextureRegion boundOverlayRegion;
    
    public static Texture items;        
    // public static TextureRegion mainMenu;
    public static TextureRegion pauseMenu;
    public static TextureRegion ready;
    public static TextureRegion gameOver;
    public static TextureRegion highScoresRegion;
    public static TextureRegion logo;
    //public static TextureRegion soundOn;
    //public static TextureRegion soundOff;
    public static TextureRegion arrow;
    public static TextureRegion pause;    
    public static TextureRegion spring;
    public static TextureRegion castle;
    public static Animation coinAnim;
    public static Animation bobJump;
    public static Animation bobFall;
    public static TextureRegion bobHit;
    public static Animation squirrelFly;
    public static TextureRegion platform;
    public static Animation brakingPlatform;    
    public static Font font;
    
    public static Music music;
    public static Sound jumpSound;
    public static Sound highJumpSound;
    public static Sound hitSound;
    public static Sound coinSound;
    public static Sound clickSound;
    
    public static void load(GLGame game) {
        background = new Texture(game, "background.png");
        backgroundRegion = new TextureRegion(background, 0, 0, 1280, 800);
        
        mainMenuButtons = new Texture(game, "mainmenubuttons.png");
        mainMenuButtonsRegion = new TextureRegion(mainMenuButtons, 0, 0, 1280, 800);
        
        soundToggle = new Texture(game, "volumetoggle.png");
        soundOnRegion = new TextureRegion(soundToggle, 0, 0, 160, 160);
        soundOffRegion = new TextureRegion(soundToggle, 160, 0, 160, 160);
        
        gameGridBackground = new Texture(game, "gamegridbackground.png");
        gameGridBackgroundRegion = new TextureRegion(gameGridBackground, 0, 0, 1280, 800);
        
        backArrow = new Texture(game, "backarrow.png");
        backArrowRegion = new TextureRegion(backArrow, 0, 0, 160, 160);
        
        pauseToggle = new Texture(game, "pausetoggle.png");
        pauseRegion = new TextureRegion(pauseToggle, 0, 0, 160, 160);
        unpauseRegion = new TextureRegion(pauseToggle, 160, 0, 160, 160);
        
        brofist = new Texture(game, "brofist.png");
        brofistRegion = new TextureRegion(brofist, 0, 0, 320, 240);
        
        boundOverlay = new Texture(game, "boundoverlay.png");
        boundOverlayRegion = new TextureRegion(boundOverlay, 0, 0, 1280, 800);
        
        //load in image containing all items to be rendered in game
        //pull them out using TextureRegion()
        items = new Texture(game, "items.png");        
        // mainMenu = new TextureRegion(items, 0, 224, 300, 110);
        pauseMenu = new TextureRegion(items, 224, 128, 192, 96);
        ready = new TextureRegion(items, 320, 224, 192, 32);
        gameOver = new TextureRegion(items, 352, 256, 160, 96);
        highScoresRegion = new TextureRegion(Assets.items, 0, 257, 300, 110 / 3);
        logo = new TextureRegion(items, 0, 352, 274, 142);
        //soundOff = new TextureRegion(items, 0, 0, 64, 64);
        //soundOn = new TextureRegion(items, 64, 0, 64, 64);
        arrow = new TextureRegion(items, 0, 64, 64, 64);
        pause = new TextureRegion(items, 64, 64, 64, 64);
        
        spring = new TextureRegion(items, 128, 0, 32, 32);
        castle = new TextureRegion(items, 128, 64, 64, 64);
        //create animations by pulling out associated images.
        coinAnim = new Animation(0.2f,                                 
                                 new TextureRegion(items, 128, 32, 32, 32),
                                 new TextureRegion(items, 160, 32, 32, 32),
                                 new TextureRegion(items, 192, 32, 32, 32),
                                 new TextureRegion(items, 160, 32, 32, 32));
        bobJump = new Animation(0.2f,
                                new TextureRegion(items, 0, 128, 32, 32),
                                new TextureRegion(items, 32, 128, 32, 32));
        bobFall = new Animation(0.2f,
                                new TextureRegion(items, 64, 128, 32, 32),
                                new TextureRegion(items, 96, 128, 32, 32));
        bobHit = new TextureRegion(items, 128, 128, 32, 32);
        squirrelFly = new Animation(0.2f, 
                                    new TextureRegion(items, 0, 160, 32, 32),
                                    new TextureRegion(items, 32, 160, 32, 32));
        platform = new TextureRegion(items, 64, 160, 64, 16);
        brakingPlatform = new Animation(0.2f,
                                     new TextureRegion(items, 64, 160, 64, 16),
                                     new TextureRegion(items, 64, 176, 64, 16),
                                     new TextureRegion(items, 64, 192, 64, 16),
                                     new TextureRegion(items, 64, 208, 64, 16));
        
        font = new Font(items, 224, 0, 16, 16, 20);
        
        music = game.getAudio().newMusic("music.mp3");
        music.setLooping(true);
        music.setVolume(0.5f);
        if(Settings.soundEnabled)
            music.play();
        jumpSound = game.getAudio().newSound("jump.ogg");
        highJumpSound = game.getAudio().newSound("highjump.ogg");
        hitSound = game.getAudio().newSound("hit.ogg");
        coinSound = game.getAudio().newSound("coin.ogg");
        clickSound = game.getAudio().newSound("click.ogg");       
    }       
    
    public static void reload() {
        background.reload();
        mainMenuButtons.reload();
        soundToggle.reload();
        gameGridBackground.reload();
        backArrow.reload();
        pauseToggle.reload();
        brofist.reload();
        boundOverlay.reload();
        items.reload();
        if(Settings.soundEnabled)
            music.play();
    }
    
    public static void playSound(Sound sound) {
        if(Settings.soundEnabled)
            sound.play(1);
    }
}
