package com.cyberkingdom.rendering;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteManager {
    private Texture entitiesTexture;

    public SpriteManager() {
        try {
            entitiesTexture = new Texture("assets/entities.png");
        } catch (Exception e) {
            System.err.println("Не удалось загрузить entities.png: " + e.getMessage());
            entitiesTexture = new Texture(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        }
    }

    public TextureRegion[] getFrames(String animationName) {
        TextureRegion[] frames = new TextureRegion[1];
        frames[0] = new TextureRegion(entitiesTexture, 0, 0, 32, 32); // Заглушка
        return frames;
    }

    public void dispose() {
        if (entitiesTexture != null) entitiesTexture.dispose();
    }
}