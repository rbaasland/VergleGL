package com.dbz.framework;

import java.util.List;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

public class Input {

    public static class KeyEvent {
        public static final int KEY_DOWN = 0;
        public static final int KEY_UP = 1;

        public int type;
        public int keyCode;
        public char keyChar;

        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (type == KEY_DOWN)
                builder.append("key down, ");
            else
                builder.append("key up, ");
            builder.append(keyCode);
            builder.append(",");
            builder.append(keyChar);
            return builder.toString();
        }
    }

    public static class TouchEvent {
        public static final int TOUCH_DOWN = 0;
        public static final int TOUCH_UP = 1;
        public static final int TOUCH_DRAGGED = 2;

        public int type;
        public int x, y;
        public int pointer;

        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (type == TOUCH_DOWN)
                builder.append("touch down, ");
            else if (type == TOUCH_DRAGGED)
                builder.append("touch dragged, ");
            else
                builder.append("touch up, ");
            builder.append(pointer);
            builder.append(",");
            builder.append(x);
            builder.append(",");
            builder.append(y);
            return builder.toString();
        }
    }
    
    AccelerometerHandler accelHandler;
    KeyboardHandler keyHandler;
    TouchHandler touchHandler;

    public Input(Context context, View view, float scaleX, float scaleY) {
        accelHandler = new AccelerometerHandler(context);
        keyHandler = new KeyboardHandler(view);               
        if(Integer.parseInt(VERSION.SDK) < 5) 
            touchHandler = new SingleTouchHandler(view, scaleX, scaleY);
        else
            touchHandler = new MultiTouchHandler(view, scaleX, scaleY);        
    }

    public boolean isKeyPressed(int keyCode) {
        return keyHandler.isKeyPressed(keyCode);
    }

    public boolean isTouchDown(int pointer) {
        return touchHandler.isTouchDown(pointer);
    }

    public int getTouchX(int pointer) {
        return touchHandler.getTouchX(pointer);
    }

    public int getTouchY(int pointer) {
        return touchHandler.getTouchY(pointer);
    }

    public float getAccelX() {
        return accelHandler.getAccelX();
    }

    public float getAccelY() {
        return accelHandler.getAccelY();
    }

    public float getAccelZ() {
        return accelHandler.getAccelZ();
    }

    public List<TouchEvent> getTouchEvents() {
        return touchHandler.getTouchEvents();
    }
    
    public List<KeyEvent> getKeyEvents() {
        return keyHandler.getKeyEvents();
    }
    
}
