package com.dbz.framework.impl;

import android.content.Context;
import android.util.Log;

/**
 * Wrapper class for android.media.SoundPool.  Used in conjunction with ShortSoundManager. 
 * 
 * This class was designed to use small sound files, such as sound FX. 
 * If smaller larger files (greater than 1MB) are needed @see LongSound
 * 
 * Most media formats are acceptable for this class, but .OGG files are most reliable
 * <a href=" http://developer.android.com/guide/appendix/media-formats.html"></a>
 * 
 * @author Jason
 */
public class Sound {

	private static final String TAG = "ShortSound";

	private int mSoundResourceId = -1; //raw resource ID
	private int mStreamID = 0; //streamID of the sound being played 

	private SoundManager mSoundManager = SoundManager.getInstance();

	//default settings
	private int mLoopCount = SoundManager.LOOP_1_TIME;
	private int playBehavior = SoundManager.OVERRLAP_AND_PLAY;
	private float mPlaySpeed = SoundManager.PLAYBACK_NORMAL_SPEED;
	private float mVolume = SoundManager.getMediaVolume();

	//pass in the resource id. aka the r.raw.filename of the sound
	//if this is used, must manually init SoundManager
	/**
	 * Constructs a LongSound object backed by the native SoundPool class.
	 * <br><br>The instance of ShortSoundManager must be initialized prior to using this constructor. 
	 * <br>The ShortSoundManager uses a SoundPool to manage several ShortSound objects at a time.
	 * <br>@see ShortSoundManager
	 * 
	 * @param rawResourceID Resource ID of audio file.
	 */
	public Sound(int rawResourceID){

		mVolume = SoundManager.getMediaVolume();
		Log.d(TAG, "Sound " + "SP" + " volume = " + mVolume);

		mSoundResourceId = rawResourceID;
		mSoundManager.addSound(rawResourceID);
	}

	//slightly less efficient construction. Auto inits ShortSoundManager.
	/**
	 * Constructs a ShortSound object backed by the native SoundPool class.
	 * <br>Auto initializes @see ShortSoundManager. 
	 * 
	 * @param Context of activity playing sound. Generally, 'this' reference is passed from current activity.
	 * @param rawResourceID Resource ID of audio file.
	 */
	public Sound(Context c, int rawResourceID){

		if (!SoundManager.isInit()){
			SoundManager.init(c, SoundManager.MAX_STREAMS);  //default is 8 streams
		}

		mVolume = SoundManager.getMediaVolume();

		mSoundResourceId = rawResourceID;
		mSoundManager.addSound(rawResourceID);
	}

	/**
	 * Constructs a ShortSound object backed by the native SoundPool class.
	 * <br><br>The instance of ShortSoundManager must be initialized prior to using this constructor. 
	 * <br>The ShortSoundManager uses a SoundPool to manage several ShortSound objects at a time.
	 * <br>@see ShortSoundManager
	 * 
	 * @param rawResourceID Resource ID of audio file.
	 * @param loopCount A loop value of -1 means loop forever. A value of 0 means no loop. Other positive values indicate the number of loops. e.g. a value of 1 plays the audio twice.
	 * @param volume Range between 0-100. Value of 0 mutes the sound.
	 * @param playSpeedMultiplier Alter the speed of playback rate. A value of 1.0 means play back at normal speed. 2.0 for double speed. 0.5 for half speed.
	 */
	public Sound(int rawResourceID, int loopCount, int volume, float playSpeedMultiplier){

		this.mLoopCount = loopCount;
		setPlaySpeedMultiplier(playSpeedMultiplier);
		setVolume(volume);

		mSoundResourceId = rawResourceID;
		mSoundManager.addSound(rawResourceID);
	}

	/**
	 * Play a sound.
	 * <br>Note that calling play() may cause another sound to stop playing if the maximum number of active streams is exceeded.
	 * <br>If SoundManager was not manually initialized then the default number of streams is @See ShortSoundManager.MAX_STREAMS
	 */
	public void play() {
		mStreamID = mSoundManager.play(mStreamID, mSoundResourceId, mLoopCount, mPlaySpeed, mVolume, playBehavior);
	}
	
	/**
	 * Play the sound with given volume.
	 * 
	 * @param vol Range between 0-100
	 * <br> 0 will mute sound 
	 * <br> 100 will playback at user's media volume setting
	 */
	public void play(int vol) {
		setVolume(vol);
		mStreamID = mSoundManager.play(mStreamID, mSoundResourceId, mLoopCount, mPlaySpeed, mVolume, playBehavior);
	}

	/**
	 * Pause a playback stream. 
	 * <br> If the stream is playing, it will be paused. 
	 * <br> If the stream is not playing (e.g. is stopped or was previously paused), calling this function will have no effect.
	 */
	public void pause() {

		mSoundManager.pause(mStreamID);
	}


	/**
	 * Stop a playback stream.
	 * <br> If the stream is playing, it will be stopped. 
	 * <br> If the stream is not playing, it will have no effect.
	 */
	public void stop() {

		mSoundManager.stop(mStreamID);
	}

	/**
	 * Resume a playback stream.
	 * <br> If the stream is paused, this will resume playback.
	 * <br> If the stream was not previously paused, calling this function will have no effect.
	 */
	public void resume() {

		mSoundManager.resume(mStreamID);
	}

	/**
	 * Deallocate Sound.
	 * <br> Removes Sound from SoundManager's SoundPool
	 */
	public void release() {
		mSoundManager.release(mStreamID);
	}

	/**
	 * Set number of times to loop the sound.
	 * @param loopCount A loop value of -1 means loop forever. A value of 0 means no loop. 
	 * <br>Positive values indicate the number of loops. e.g. a value of 1 plays the audio twice.
	 */
	public void setLoopCount(int loopCount) {
		this.mLoopCount = loopCount;
	}

	/**
	 * Enable infinite loop
	 */
	public synchronized void loop() {
		this.mLoopCount = SoundManager.LOOP_INFINITE;
	}

	/**
	 * Set the speed of the sounds Playback rate
	 * @param playSpeed Range .5f to 2.0f
	 * <br> 1f will playback at normal speed 
	 * <br> 2f will playback at double speed  
	 * <br>0.5f will playback at half speed
	 */
	public void setPlaySpeedMultiplier(float playSpeed) { //soundpool api indicates support for max of 2x speed.
		//global for max and min for play speed is 3f
		if (playSpeed >= SoundManager.PLAYBACK_MAX_SPEED){
			mPlaySpeed = SoundManager.PLAYBACK_MAX_SPEED;
			return;	
		}

		if (playSpeed <= SoundManager.PLAYBACK_MIN_SPEED)
			mPlaySpeed = SoundManager.PLAYBACK_MIN_SPEED;

		else mPlaySpeed = playSpeed; //Let playspeed range 0f - 2f
	}

	/**
	 * Set volume - set volume is relative to user's current volume setting.
	 * 
	 * @param vol Range between 0-100 
	 * <br> 0 will mute sound 
	 * <br> 100 will playback at user's media volume setting
	 */
	public void setVolume (int vol) {

		//TODO: set globals for max volumes 200??
		if (vol >= 100){
			mVolume = 1f;
			return;	
		}

		if (vol <= 0)
			mVolume = 0;

		else mVolume = vol/100f;
	}

	/**
	 * Defines the Play Behavior for each ShortSound object.
	 * Use the following constants in ShortSoundManager:
	 * 
	 * @param playBehavior Enter one of the following: 
	 * <br> ShortSoundManager.RESTART_AND_PLAY 
	 * <br> ShortSoundManager.OVERRLAP_AND_PLAY
	 */
	public void setPlayBehavior(int playBehavior) {
		this.playBehavior = playBehavior;
	}

}
