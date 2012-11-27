package com.dbz.framework.gl;

// TODO: Add more font formatting functions, such as right and left adjusted.
public class Font {
    public final Texture texture;
    public final int glyphWidth;
    public final int glyphHeight;
    public final TextureRegion[] glyphs = new TextureRegion[96];   
    
    public Font(Texture texture, 
                int offsetX, int offsetY,
                int glyphsPerRow, int glyphWidth, int glyphHeight) {        
        this.texture = texture;
        this.glyphWidth = glyphWidth;
        this.glyphHeight = glyphHeight;
        int x = offsetX;
        int y = offsetY;
        for(int i = 0; i < 96; i++) {
            glyphs[i] = new TextureRegion(texture, x, y, glyphWidth, glyphHeight);
            x += glyphWidth;
            if(x == offsetX + glyphsPerRow * glyphWidth) {
                x = offsetX;
                y += glyphHeight;
            }
        }        
    }
    
    public void drawText(SpriteBatcher batcher, String text, float x, float y) {
        int len = text.length();
        for(int i = 0; i < len; i++) {
            int c = text.charAt(i) - ' ';
            if(c < 0 || c > glyphs.length - 1) 
                continue;
            
            TextureRegion glyph = glyphs[c];
            batcher.drawSprite(x, y, glyphWidth, glyphHeight, glyph);
            x += glyphWidth;
        }
    }
    
    // Alternate Version of drawText(). Draws the text centered at provided x, and y.
    public void drawTextCentered(SpriteBatcher batcher, String text, float x, float y) {
    	int len = text.length();
        float charsToShift = len / 2;
 
        
        if ((len % 2) == 1)
        	charsToShift += 0.5f;
        
        x -= (charsToShift * glyphWidth);
        y += (0.5 * glyphHeight);
    	
        for(int i = 0; i < len; i++) {
            int c = text.charAt(i) - ' ';
            if(c < 0 || c > glyphs.length - 1) 
                continue;
            
            TextureRegion glyph = glyphs[c];
            batcher.drawSprite(x, y, glyphWidth, glyphHeight, glyph);
            x += glyphWidth;
        }
    }
    
    // Alternate Version of drawTextCentered(), with ability to scale font size.
    public void drawTextCentered(SpriteBatcher batcher, String text, float x, float y, float scale) {
    	int len = text.length();
        float charsToShift = len / 2;
        float scaledWidth = glyphWidth * scale;
        float scaledHeight = glyphHeight * scale;
        
        if ((len % 2) == 1)
        	charsToShift += 0.5f;
        
        x -= (charsToShift * scaledWidth);
        y += (0.5 * scaledHeight);
    	
        for(int i = 0; i < len; i++) {
            int c = text.charAt(i) - ' ';
            if(c < 0 || c > glyphs.length - 1) 
                continue;
            
            TextureRegion glyph = glyphs[c];
            batcher.drawSprite(x, y, scaledWidth, scaledHeight, glyph);
            x += scaledWidth;
        }
    }
    
 }
