package com.cyberkingdom.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.HashMap;
import java.util.Map;

public class SpriteManager {
    private final Map<String, Texture> textures = new HashMap<>();

    public SpriteManager() {
        loadTextures();
    }

    public void loadTextures() {
        try {
            loadTexture("assets/entities/player.png", "Player");
            loadTexture("assets/entities/witch_vpn.png", "WITCH_VPN");
            loadTexture("assets/entities/cat_miner.png", "CAT_MINER");
            loadTexture("assets/platform.png", "Platform");
            loadTexture("assets/Coin.png", "COIN");
            loadItemTexture("HARDWARE_WALLET");
            loadItemTexture("VPN_TOKEN");
            loadItemTexture("CRYPTO_COIN");
            loadItemTexture("USB_SCATTER");
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Error loading textures: " + e.getMessage(), e);
        }
    }

    private void loadItemTexture(String itemName) {
        String path = "assets/items/" + itemName.toLowerCase() + ".png";
        try {
            Texture texture = new Texture(Gdx.files.internal(path));
            textures.put(itemName, texture);
            Gdx.app.debug("SpriteManager", "Loaded texture: " + path);
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Error loading item texture: " + itemName + " from path: " + path, e);
            textures.put(itemName, createPlaceholderTexture());
        }
    }

    private void loadTexture(String path, String name) {
        try {
            Texture texture = new Texture(Gdx.files.internal(path));
            textures.put(name, texture);
            Gdx.app.debug("SpriteManager", "Loaded texture: " + path);
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Error loading texture: " + path, e);
            textures.put(name, createPlaceholderTexture());
        }
    }

    private Texture createPlaceholderTexture() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.MAGENTA);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public TextureRegion[] getFrames(String name) {
        String baseName = name.split("_")[0];
        Texture texture = textures.getOrDefault(baseName, createPlaceholderTexture());
        return new TextureRegion[]{new TextureRegion(texture)};
    }

    public void dispose() {
        for (Texture texture : textures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
    }
}