package com.dbz.framework.impl;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import com.dbz.framework.Audio;

public class AndroidAudio implements Audio {
		
	    private Context appContext; // application context

	    public AndroidAudio(Activity activity) {
	        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC); //keep
	        appContext = activity.getApplicationContext();
	        
	        SoundManager.getInstance();
	        SoundManager.init(appContext, 8);
	    }
	    
		/**
		 * Returns a LongSound object backed by the native MediaPlayer class.
		 * 
		 * @param resourceID resource ID of the sound file. Pass values from R.raw
		 */
	    @Override
	    public Music newMusic(int resourceID) {
	    	return new Music(appContext, resourceID);
	    	
	    }
	    
		/**
		 * Returns a ShortSound object backed by the native SoundPool class.
		 * 
		 * @param resourceID resource ID of the sound file. Pass values from R.raw
		 */
	    @Override
	    public Sound newSound(int resourceID) {
	    	 return new Sound(resourceID);
	    	
	    }

}
