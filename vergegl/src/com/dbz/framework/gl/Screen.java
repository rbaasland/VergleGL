package com.dbz.framework.gl;

import com.dbz.framework.Game;


public abstract class Screen {
    protected final GLGraphics glGraphics;
    public static Game game;
    
    public Screen() {
        glGraphics = game.getGLGraphics();
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
