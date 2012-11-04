package com.dbz.framework;

import com.dbz.framework.impl.Music;
import com.dbz.framework.impl.Sound;

//audio files are played from res>>raw
public interface Audio {
    public Music newMusic(int resourceID);

    public Sound newSound(int  resourceID);
}
