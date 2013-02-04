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

}