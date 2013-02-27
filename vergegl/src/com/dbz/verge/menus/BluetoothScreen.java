package com.dbz.verge.menus;

import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.dbz.framework.input.Input.TouchEvent;
import com.dbz.verge.AssetsManager;
import com.dbz.verge.Menu;

public class BluetoothScreen extends Menu {
	
	// --------------
	// --- Fields ---
	// --------------
	
    BluetoothAdapter btAdapter = game.mBtAdapter;
    private static final String NAME = "VergeBluetoothConnect";
    private static final UUID MY_UUID = UUID.fromString("7d2b2c7a-e370-4500-a82a-47e1a76287be");

    // -------------------
 	// --- Constructor ---
    // -------------------
    public BluetoothScreen() {
    	
    //	BluetoothAdapter btAdapter = game.mBtAdapter;
		if(!btAdapter.isEnabled()){
			btAdapter.enable();
		}
		
		//game.mPairedDevices = btAdapter.getBondedDevices(); //note, doesn't work right away.
		//bluetooth takes a few seconds to enable, and until its enabled, this will return null. 
		//for some reason, if i put this in update() the variable btAdapter is null.
		
		//game.ensureDiscoverable(); //puts device in discovery but causes paired list to not be displayed?
		game.startDiscovery(); // right now ending on back press (hardware)
		
		
    }       

    // ---------------------
 	// --- Update Method ---
 	// ---------------------
    @Override
    public void update(float deltaTime) {
  
    	game.mPairedDevices = btAdapter.getBondedDevices();
    	
    	// Gets all TouchEvents and stores them in a list.
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        
        // Cycles through and tests all touch events.
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
        	// Gets a single TouchEvent from the list.
            TouchEvent event = touchEvents.get(i);    

            // Only handle if TouchEvent is TOUCH_UP.
            if(event.type == TouchEvent.TOUCH_UP) {
                touchPoint.set(event.x, event.y);
                guiCam.touchToWorld(touchPoint);
                
                // put logic here to click on the screen to init a connection with first available device
                if(game.mNewDevices != null){
                	BluetoothDevice target = game.mNewDevices.iterator().next(); //get first bluetooth device
                	Log.d("Bluetooth", target.getName());
                	
                	//TODO here do threads to make connection? we know the device we want, just need to 
                	//connect to it.
                	
                }
            
                // Non-Unique, Super Class Bounds Check.
    	        super.update(touchPoint);
            }

        }
    }
    
    // ----------------------------
 	// --- Utility Draw Methods ---
 	// ----------------------------
    
    @Override
    public void drawBackground() {
       // batcher.beginBatch(AssetsManager.background);
       // batcher.drawSprite(0, 0, 1280, 800, AssetsManager.backgroundRegion);
       // batcher.endBatch();
    }
    
    @Override
    public void drawObjects() {
    	
    	int lineSpacer = 40;
    	
    	//here we want to display all available adapters
    	//use for loop and update x by like 20 pixels for each adapter
		batcher.beginBatch(AssetsManager.vergeFontTexture);
		//Paired Devices title
		AssetsManager.vergeFont.drawTextCentered(batcher, "Paried Devices", 640, 700, 1.7f);
		// list of paired devices
		if(game.mPairedDevices != null){
		for(BluetoothDevice pd : game.mPairedDevices){
			AssetsManager.vergeFont.drawTextCentered(batcher, pd.getName() + " : " + pd.getAddress() , 640, 700-lineSpacer, 1.5f);
			lineSpacer += 40;
		}
		}
		//New Devices title
		lineSpacer += 80;
		AssetsManager.vergeFont.drawTextCentered(batcher, "New Devices", 640, 700-lineSpacer, 1.7f);
		lineSpacer += 40;
		// list of new devices
		if(game.mNewDevices != null){
		for(BluetoothDevice nd : game.mNewDevices){
			AssetsManager.vergeFont.drawTextCentered(batcher, nd.getName() + " : " + nd.getAddress() , 640, 700-lineSpacer, 1.5f);
			lineSpacer += 40;
		}
		}
		
	//	AssetsManager.vergeFont.drawTextCentered(batcher, string, 640, 700, 1.5f);
		batcher.endBatch();
		
        super.drawObjects(); //pause button and stuff
    }

    
    @Override
    public void drawBounds() {}
    
    @Override
    public void onBackPressed(){
		if(game.mBtAdapter.isEnabled()){
			//game.mBtAdapter.disable();
		}
		game.endDiscovery();
		game.setScreen(new MainMenu());
    }

}
