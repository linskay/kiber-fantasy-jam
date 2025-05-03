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

    private void loadTextures() {
        // Загрузка текстур предметов по базовым именам
        loadItemTexture("HARDWARE_WALLET");
        loadItemTexture("VPN_TOKEN");
        loadItemTexture("CRYPTO_COIN");
        loadItemTexture("USB_SCATTER");

        // Загрузка остальных текстур
        loadTexture("Player", "entities/player.png");
        loadTexture("WITCH_VPN", "entities/witch_vpn.png");
        loadTexture("CAT_MINER", "entities/cat_miner.png");
    }

    private void loadItemTexture(String itemName) {
        String path = "items/" + itemName.toLowerCase() + ".png";
        try {
            textures.put(itemName, new Texture(Gdx.files.internal("assets/" + path)));
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Error loading item texture: " + itemName, e);
            textures.put(itemName, createPlaceholderTexture());
        }
    }

    private void loadTexture(String name, String path) {
        try {
            textures.put(name, new Texture(Gdx.files.internal("assets/" + path)));
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
        textures.values().forEach(Texture::dispose);
    }
}