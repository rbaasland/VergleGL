package com.dbz.framework;

import com.dbz.framework.impl.FileIO;

public interface Game {
    public Input getInput();

    public FileIO getFileIO();

    public Audio getAudio();

    public void setScreen(Screen screen);

    public Screen getCurrentScreen();

    public Screen getStartScreen();
}