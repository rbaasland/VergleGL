package com.dbz.verge;

import com.dbz.framework.gl.SpriteBatcher;
import com.dbz.framework.impl.GLGraphics;

public class MicroWorldRenderer extends WorldRenderer {
	
	public MicroWorldRenderer(GLGraphics glGraphics, SpriteBatcher batcher, World world) {
		super(glGraphics, batcher, world);
	}
	
	@Override
	public void renderBackground() {}
	
	@Override
	public void renderObjects() {
		// Could make instruction temporary this way...
//		if (totalRunningTime < 3) {
//			batcher.beginBatch(Assets.items);
//			Assets.font.drawText(batcher, "BROFIST!", 600, 700);
//			batcher.endBatch();
//		}
		// ...or could just dedicate screen space for it for the entire microgame.
		batcher.beginBatch(Assets.items);
		Assets.font.drawText(batcher, "BROFIST!", 600, 700);
		batcher.endBatch();
		
		// Draw Brofist.
		batcher.beginBatch(Assets.brofist);
		batcher.drawSprite(480, 280, 320, 240, Assets.brofistRegion);
		batcher.endBatch();

		// Bounding Boxes
//		batcher.beginBatch(Assets.boundOverlay);
//	    batcher.drawSprite(480, 280, 320, 240, Assets.boundOverlayRegion); // Brofist Bounding Box
//	    batcher.endBatch();
	}
}
