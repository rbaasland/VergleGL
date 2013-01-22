package com.dbz.framework.audio;
import java.security.InvalidParameterException;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * SoundPool Service Wrapper
 * 
 * <br><br>Provides single instance of a SoundPool with basic multimedia functions. 
 * <br>These functions are used to support ShortSound objects. 
 * @author Jason
 * TODO: OnCompletionListener
 */
public class SoundManager {

	private static final String TAG = "ShortSoundManager";

	static private SoundManager _instance; //singleton pattern
	private static SoundPool mSoundPool;
	private static SparseIntArray mSoundPoolMap; //More efficient map for integers.
	private static SparseIntArray mLoadStatusMap; //stores the load status of each sound added to sound pool
	private static AudioManager  mAudioManager;
	private static Context mContext;

	//CONSTANTS
	/** Default number of concurrent sound streams. Calling init() directly bypasses default value. */
	public final static int MAX_STREAMS = 16;

	/** Constant for a single play through*/
	public final static int LOOP_1_TIME = 0;
	/** Constant for infinite loop */
	public final static int LOOP_INFINITE = -1;
	
	
	/** Constant for max playback speed */
	public final static float PLAYBACK_MAX_SPEED = 3.0f;
	/** Constant for max playback speed */
	public final static float PLAYBACK_MIN_SPEED = .5f;
	/** Constant for normal playback speed */
	public final static float PLAYBACK_NORMAL_SPEED = 1.0f; 
	/** Constant for half playback speed */
	public final static float PLAYBACK_HALF_SPEED = 0.5f;
	/** Constant for double playback speed */
	public final static float PLAYBACK_DOUBLE_SPEED = 2.0f;
	/** Constant for double playback speed */
	public final static float PLAYBACK_TRIPLE_SPEED = 3.0f;
	
	
	/** Constant for setting max volume */
	public final static float VOLUME_MAX = 1f;
	/** Constant for setting half volume */
	public final static float VOLUME_HALF = .50f;
	/** Constant for muting volume */
	public final static float VOLUME_MUTE = .00f;

	/**
	 * Plays sound, but restarts when play() is called again.
	 */
	final static int RESTART_AND_PLAY = 1;
	/**
	 * Plays sound over itself each time play() is called
	 */
	final static int OVERRLAP_AND_PLAY = 2;


	private SoundManager(){
	}

	/**
	 * Requests the instance of the Sound Manager and creates it
	 * if it does not exist.
	 *
	 * @return Returns the single instance of the SoundManager
	 */
	public static synchronized  SoundManager getInstance(){
		if (_instance == null)
			_instance = new SoundManager();
		return _instance;
	}

	/**
	 * Initializes the storage for the sounds
	 *
	 * @param theContext The Application context
	 * @param maxStreams The number of concurrent sounds that can be played by SoundManager
	 */	
	public static void init(Context c, int maxStreams){

		mContext = c;
		mSoundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
		mAudioManager = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
		mSoundPoolMap = new SparseIntArray();
		mLoadStatusMap = new SparseIntArray();
	}


	/**
	 * Checks that ShortSoundManager's SoundPool has been initialized.
	 * @return Returns true if SoundManager has been initialized 
	 */
	public static boolean isInit(){

		if (mSoundPool == null)
			return false;
		return true;
	}

	/**
	 * Add a new Sound to the SoundPool
	 *
	 * @param rawResourceID - The Android ID for the Sound asset (Reference to sound file in R.raw).
	 */
	protected  void addSound(final int rawResourceID){

		if (mSoundPoolMap == null) //logging null, but allowing program to error out. 
			Log.e("null_pointer", TAG + "Resource ID " + rawResourceID + " requires initialized SoundManager");
		
		//TODO: OnCompletionListener has passed basic tests
		//note that i don't think the status is really needed as soundpool handles cases where the load hasn't finished.
		//see "sample 0 not ready" errors. Removing the mLoadStatusMap should have no effect on functionality.
		mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
		      public void onLoadComplete(SoundPool soundPool, int sampleId,
		          int status) {
		    	  mLoadStatusMap.put(rawResourceID, status); //update map with status value, 0 if success.
		      }
		    });

		mSoundPoolMap.put(rawResourceID, mSoundPool.load(mContext, rawResourceID, 1)); //soundPoolMap maps rawID to the sound

	}

	/**
	 * Should not be used outside of ShortSound.java
	 * @return mStreamID
	 */
	protected synchronized int play(int mStreamID, int rawResourceID, int loopNumber, float playbackSpeed, float audioVolume, int playBehavior) {

		//checks if loaded
		if(mLoadStatusMap.get(rawResourceID) != 0) {
			Log.d("Pool Load Status:" , "ResID "+ rawResourceID + " not loaded");
			return mStreamID; 	//returns back origin streamID
		}
		
		switch (playBehavior){

		case RESTART_AND_PLAY:

			mSoundPool.stop(mStreamID);

			return mSoundPool.play(mSoundPoolMap.get(rawResourceID), audioVolume, 
					audioVolume, 1, loopNumber, playbackSpeed);

		case OVERRLAP_AND_PLAY:

			return mSoundPool.play(mSoundPoolMap.get(rawResourceID), audioVolume, 
					audioVolume, 1, loopNumber, playbackSpeed);

		default:
			//Invalid parameter set for sound
			throw new InvalidParameterException("Sound Play Behavior is invalid.  See Available playback constants in ShortSoundManager");
		}

	}

	protected  void pause(int mStreamID) {

		mSoundPool.pause(mStreamID);
	}

	protected  void stop(int mStreamID) {

		mSoundPool.stop(mStreamID);
	}

	protected  void resume(int mStreamID) {

		mSoundPool.resume(mStreamID);
	} 

	protected  void release(int mStreamID) {

		mSoundPool.unload(mStreamID);
	} 

	/**
	 *  Calculates current media volume set on user device.
	 * @return media volume
	 */
	protected static float getMediaVolume() {

		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

		return streamVolume;
	}

	/**
	 * Pause all sounds currently playing.
	 */
	public static void pauseAll(){
		mSoundPool.autoPause();   
	}

	/**
	 * Resume all paused sounds.
	 */
	public static void resumeAll(){
		mSoundPool.autoResume();

	}

	/**
	 * Deallocates the resources and Instance of SoundManager
	 */
	public static void releaseAll(){
		mSoundPool.release();
		mSoundPool = null;
		mSoundPoolMap.clear();
		mLoadStatusMap.clear();
		mAudioManager.unloadSoundEffects();

		_instance = null;

	}
}
