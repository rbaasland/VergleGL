package com.dbz.framework.impl;

public abstract class Screen {
    protected final GLGraphics glGraphics;
    protected final Game game;
    
    public Screen(Game game) {
        this.game = game;
        glGraphics = ((Game)game).getGLGraphics();
    }
    
    public abstract void update(float deltaTime);

    public abstract void present(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();
    
    // Closes game by default onBackPressed.
    public void onBackPressed(){
    	game.finish();
    }
}
