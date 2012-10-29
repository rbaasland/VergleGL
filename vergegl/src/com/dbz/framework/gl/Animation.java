package com.dbz.framework.gl;


public class Animation {
    public static final int ANIMATION_LOOPING = 0;
    public static final int ANIMATION_NONLOOPING = 1;
    
    final TextureRegion[] keyFrames;
    final float frameDuration;
    
    // "Class ... varname" is how to pass in a variable number of objects into a method. 
    public Animation(float frameDuration, TextureRegion ... keyFrames) {
        this.frameDuration = frameDuration;
        this.keyFrames = keyFrames;
    }
    
    public TextureRegion getKeyFrame(float stateTime, int mode) {
        int frameNumber = (int)(stateTime / frameDuration);
        
        if(mode == ANIMATION_NONLOOPING) {
            frameNumber = Math.min(keyFrames.length-1, frameNumber);            
        } else {
            frameNumber = frameNumber % keyFrames.length;
        }        
        return keyFrames[frameNumber];
    }
    
    //added to return one of the keyFrames directly
    public TextureRegion getKeyFrame(int keyFrame) {
        return keyFrames[Math.min(keyFrames.length-1, keyFrame)]; //Math.min ensures no array out to bounds
    }
}
