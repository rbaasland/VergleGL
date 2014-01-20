package com.dbz.verge;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.dbz.framework.Game;
import com.dbz.framework.gl.Screen;
import com.dbz.verge.menus.MainMenu;

public class VergeGame extends Game {
    boolean firstTimeCreate = true;
    
    public Screen getStartScreen() {
        return new MainMenu();
    }
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    	Screen.game = this;
        super.onSurfaceCreated(gl, config);
        if(firstTimeCreate) {
            Settings.load(getFileIO());
            AssetsManager.load(this);
            firstTimeCreate = false;            
        } else {
            AssetsManager.reload();
        }
    }     
    
    @Override
    public void onPause() {
        super.onPause();
        if(Settings.soundEnabled)
            AssetsManager.music.pause();
    }
    
    /* Used by hardware back button 
     * Each instance of screen implements its own onBackPressed() 
     * to define behavior when the back button is pressed. */
	@Override 
	public void onBackPressed() {
		Screen currScreen = getCurrentScreen();
		if(currScreen != null) 
		{
	    	AssetsManager.playSound(AssetsManager.clickSound);
			currScreen.onBackPressed();
		}
	}
	
}