package com.dbz.framework.impl;

import com.dbz.framework.Game;
import com.dbz.framework.Screen;

public abstract class GLScreen extends Screen {
    protected final GLGraphics glGraphics;
    protected final GLGame glGame;
    
    public GLScreen(Game game) {
        super(game);
        glGame = (GLGame)game;
        glGraphics = ((GLGame)game).getGLGraphics();
    }
    
    //closes game by default onBackPressed
    public void onBackPressed(){
    	glGame.finish();
    }

}
