package com.dbz.framework;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

/** 
 * Wrapper class for android.media.MediaPlayer.
 * 
 * <br><br>This class was designed to use large sound files, such as background music. 
 * <br>If smaller sound files (less than 1MB) are needed @see ShortSound
 * 
 * <br><br>Most media formats are acceptable for this class, but .OGG files are most reliable
 * <br>@see http://developer.android.com/guide/appendix/media-formats.html
 * 
 * @author Jason
 * TODO: Review Bug in stop() for android 2.2 or less.
 * 
 */
public class Music {

	private static final String TAG = "LongSound";

	private MediaPlayer mPlayer; 
	private String name;		//generally used for debugging

	//needed for setToUserVolume()
	private Context context;

	//sound states (not playing, playing, not looping, looping, not stopped, stopped) 
	private boolean mPlaying = false, mLoop = false; 


	/**
	 * Constructs a LongSound object backed by the native MediaPlayer class.
	 * 
	 * @param context Context of activity playing sound. Generally, 'this' reference is passed from current activity 
	 * @param resID resource ID of the sound file. Pass values from R.raw
	 */
	public Music(Context context, int resID) {

		this.context = context;
		//this.resID = resID;
		name = context.getResources().getResourceName(resID);

		mPlayer = MediaPlayer.create(context, resID);
		setToMediaVolume();

		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
			public void onCompletion(MediaPlayer mp) {
				mPlaying = false;

				if (mLoop) {
					mp.start();
				}
			}
		});
	}

	/**
	 * Constructs a LongSound object backed by the MediaPlayer library.
	 * 
	 * @param context Context of activity playing sound. Generally, 'this' reference is passed from current activity 
	 * @param uri String of characters used to identify name or resource on the Internet
	 */
	public Music(Context context, Uri uri) {

		this.context = context;
		//this.uri = uri;
		name = uri.toString();

		mPlayer = MediaPlayer.create(context, uri);
		setToMediaVolume();

		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
			public void onCompletion(MediaPlayer mp) {
				mPlaying = false;

				if ( mLoop) {
					mp.start();
				}
			}
		});
	}

	/**
	 * Play the sound.
	 * To prevent performance issues during runtime, set the volume prior to user interaction. Preferably onCreate()
	 */
	public synchronized void play () {

		if (mPlaying) { 
			//mPlayer.seekTo(0);
			return;
		}

		if (mPlayer != null ) {

			mPlaying = true;
			mPlayer.start();
		} 
	}

	/**
	 * Play the sound with given volume.
	 * 
	 * @param vol Range between 0-100
	 * <br> 0 will mute sound 
	 * <br> 100 will playback at user's media volume setting
	 */
	public synchronized void play (int vol) {

		if (mPlaying) {
			//mPlayer.seekTo(0);
			return;
		}

		if (mPlayer != null ) {
			mPlaying = true;

			Log.d(TAG, "Play " + name + " vol=" + vol);
			setVolume(vol);
			mPlayer.start();
		} 
	}

	/**
	 * Pause playing sound
	 */
	public synchronized void pause() {

		if (mPlaying){
			mPlaying = false;
			mPlayer.pause();
		}
	}

	/**
	 * Resume sound after pause() or stop()
	 */
	public synchronized void resume() {	

		if (!mPlaying){
			mPlaying = true;
			mPlayer.start();
		}	
	}


	/**
	 * Stop Sound. Seek to beginning.
	 * <br> Buffer flushing issue for Android 2.2 or less.
	 */
	/* 
	 * Known Bug in android 2.1 (eclair) and 2.2 (froyo) - Bug Code 4124
	 * As of 8/1/12 - approx. 20% of droid users affected
	 * 
	 * --Internal buffers are not being flushed after seek.
	 * 
	 * Symptom: Brief clip of the initial sound plays where it left off before call to pause().
	 * See: http://stackoverflow.com/questions/5985874/android-mediaplayer-clipping-on-restart-after-seekto
	 * 		http://code.google.com/p/android/issues/detail?id=9135
	 * 
	 * Workaround: Release media player and recreate.
	 * Code: 
			mPlayer.stop();
   			release();
	 * Caveat: Deallocating and reallocating during runtime can effect performance.
	 * 
	 */
	public synchronized void stop() {

		try {
			if ( mPlaying ) { 

				mLoop = false;
				mPlaying = false;   

				mPlayer.pause();
				mPlayer.seekTo(0); 

			}
		} catch (Exception e) {
			System.err.println("AudioClip::stop " + name + " " + e.toString());
		}
	}

	/**
	 * Deallocate Sound.
	 * <br>This method should be called when LongSound is no longer needed
	 */
	public void release() {

		if (mPlayer != null){ 

			mPlayer.release();
			mPlayer = null;
		}
	}

	/**
	 * Enable loop
	 */
	public synchronized void loop () {

		mLoop = true;
		mPlayer.setLooping(true);
	}

	public String getName() {
		return name;
	}

	/**
	 * Set volume - set volume is relative to user's current volume setting.
	 * @param vol Range between 0-100
	 * <br> 0 will mute sound 
	 * <br> 100 will playback at user's media volume setting
	 */
	public void setVolume (int vol) {

		if ( mPlayer != null) {

			float volume;

			if (vol >= 100){
				mPlayer.setVolume(100, 100);
				return;	
			}

			if ( vol <= 0)
				mPlayer.setVolume(0, 0);

			else { 
				volume = vol/100f; //Logarithmically, Math.log10(vol) /2 (less expensive to divide by 100)
				mPlayer.setVolume(volume, volume);
			}	
		}
	}

	/**
	 * Set sound to user's media volume level
	 */
	public void setToMediaVolume(){

		if (context == null)
			return;

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		float volume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		//(current volume / max volume)	
		volume /= (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		Log.d(TAG, "Sound " + name + " volume = " + volume);
		mPlayer.setVolume(volume, volume);
	}

	public boolean isPlaying(){
		return mPlaying;
	}

	public boolean isLooping(){
		return mLoop;
	}

}