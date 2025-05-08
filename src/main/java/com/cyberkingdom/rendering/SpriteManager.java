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
    private final Map<String, TextureRegion[]> spriteRegions = new HashMap<>();

    public SpriteManager() {
        loadTextures();
        setupSpriteRegions();
    }

    public void loadTextures() {
        try {
            // Загружаем основные текстуры
            loadTexture("assets/platform.png", "Platform");
            loadTexture("assets/Coin.png", "COIN");
            loadTexture("assets/entities/player.png", "Player");
            loadTexture("assets/entities.png", "WITCH_VPN");
            loadTexture("assets/entities.png", "CAT_MINER");
            
            // Загружаем текстуры предметов
            loadTexture("assets/items.png", "HARDWARE_WALLET");
            loadTexture("assets/items.png", "VPN_TOKEN");
            loadTexture("assets/items.png", "CRYPTO_COIN");
            loadTexture("assets/items.png", "USB_SCATTER");
            
            Gdx.app.log("SpriteManager", "All textures loaded successfully");
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Error loading textures: " + e.getMessage(), e);
            throw new RuntimeException("Failed to load textures", e);
        }
    }

    private void loadTexture(String path, String name) {
        try {
            if (!Gdx.files.internal(path).exists()) {
                Gdx.app.error("SpriteManager", "Texture file not found: " + path);
                throw new RuntimeException("Texture file not found: " + path);
            }
            
            Texture texture = new Texture(Gdx.files.internal(path));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textures.put(name, texture);
            Gdx.app.log("SpriteManager", "Successfully loaded texture: " + path + " for " + name + 
                " (width=" + texture.getWidth() + ", height=" + texture.getHeight() + ")");
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Error loading texture: " + path, e);
            throw new RuntimeException("Failed to load texture: " + path, e);
        }
    }

    private void setupSpriteRegions() {
        // Настройка регионов для платформы
        Texture platformTexture = textures.get("Platform");
        if (platformTexture != null) {
            Gdx.app.log("SpriteManager", "Setting up regions for platform texture (width=" + 
                platformTexture.getWidth() + ", height=" + platformTexture.getHeight() + ")");
            
            TextureRegion[] platformRegions = new TextureRegion[1];
            platformRegions[0] = new TextureRegion(platformTexture, 0, 0, platformTexture.getWidth(), platformTexture.getHeight());
            spriteRegions.put("Platform", platformRegions);
            Gdx.app.log("SpriteManager", "Set up platform regions");
        } else {
            Gdx.app.error("SpriteManager", "platform texture is null");
        }

        // Настройка регионов для entities.png
        Texture entitiesTexture = textures.get("Player");
        if (entitiesTexture != null) {
            Gdx.app.log("SpriteManager", "Setting up regions for entities texture (width=" + 
                entitiesTexture.getWidth() + ", height=" + entitiesTexture.getHeight() + ")");
            
            TextureRegion[] playerRegions = new TextureRegion[1];
            playerRegions[0] = new TextureRegion(entitiesTexture, 0, 0, 64, 64);
            spriteRegions.put("Player", playerRegions);
            Gdx.app.log("SpriteManager", "Set up player regions at (0,0)");

            int tileSize = 32;
            TextureRegion[] witchRegions = new TextureRegion[1];
            witchRegions[0] = new TextureRegion(entitiesTexture, tileSize, 0, tileSize, tileSize);
            spriteRegions.put("WITCH_VPN", witchRegions);
            Gdx.app.log("SpriteManager", "Set up witch regions at (" + tileSize + ",0)");

            TextureRegion[] catRegions = new TextureRegion[1];
            catRegions[0] = new TextureRegion(entitiesTexture, tileSize * 2, 0, tileSize, tileSize);
            spriteRegions.put("CAT_MINER", catRegions);
            Gdx.app.log("SpriteManager", "Set up cat regions at (" + (tileSize * 2) + ",0)");
        } else {
            Gdx.app.error("SpriteManager", "entities texture is null");
        }

        // Настройка регионов для монеты
        Texture coinTexture = textures.get("COIN");
        if (coinTexture != null) {
            Gdx.app.log("SpriteManager", "Setting up regions for coin texture (width=" + 
                coinTexture.getWidth() + ", height=" + coinTexture.getHeight() + ")");
            
            TextureRegion[] coinRegions = new TextureRegion[1];
            coinRegions[0] = new TextureRegion(coinTexture, 0, 0, coinTexture.getWidth(), coinTexture.getHeight());
            spriteRegions.put("COIN", coinRegions);
            Gdx.app.log("SpriteManager", "Set up coin regions");
        } else {
            Gdx.app.error("SpriteManager", "coin texture is null");
        }

        // Настройка регионов для items.png
        Texture itemsTexture = textures.get("HARDWARE_WALLET");
        if (itemsTexture != null) {
            Gdx.app.log("SpriteManager", "Setting up regions for items texture (width=" + 
                itemsTexture.getWidth() + ", height=" + itemsTexture.getHeight() + ")");
            
            int tileSize = 32;
            TextureRegion[] hardwareWalletRegions = new TextureRegion[1];
            hardwareWalletRegions[0] = new TextureRegion(itemsTexture, tileSize, 0, tileSize, tileSize);
            spriteRegions.put("HARDWARE_WALLET", hardwareWalletRegions);
            Gdx.app.log("SpriteManager", "Set up hardware wallet regions at (" + tileSize + ",0)");

            TextureRegion[] vpnTokenRegions = new TextureRegion[1];
            vpnTokenRegions[0] = new TextureRegion(itemsTexture, tileSize * 2, 0, tileSize, tileSize);
            spriteRegions.put("VPN_TOKEN", vpnTokenRegions);
            Gdx.app.log("SpriteManager", "Set up VPN token regions at (" + (tileSize * 2) + ",0)");

            TextureRegion[] cryptoCoinRegions = new TextureRegion[1];
            cryptoCoinRegions[0] = new TextureRegion(itemsTexture, tileSize * 3, 0, tileSize, tileSize);
            spriteRegions.put("CRYPTO_COIN", cryptoCoinRegions);
            Gdx.app.log("SpriteManager", "Set up crypto coin regions at (" + (tileSize * 3) + ",0)");

            TextureRegion[] usbScatterRegions = new TextureRegion[1];
            usbScatterRegions[0] = new TextureRegion(itemsTexture, tileSize * 4, 0, tileSize, tileSize);
            spriteRegions.put("USB_SCATTER", usbScatterRegions);
            Gdx.app.log("SpriteManager", "Set up USB scatter regions at (" + (tileSize * 4) + ",0)");
        } else {
            Gdx.app.error("SpriteManager", "items texture is null");
        }
    }

    public TextureRegion[] getFrames(String name) {
        TextureRegion[] regions = spriteRegions.get(name);
        if (regions != null) {
            Gdx.app.log("SpriteManager", "Found " + regions.length + " frames for: " + name);
            return regions;
        }
        Gdx.app.error("SpriteManager", "No frames found for: " + name);
        return null;
    }

    private Texture createPlaceholderTexture() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.MAGENTA);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void dispose() {
        for (Texture texture : textures.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textures.clear();
        spriteRegions.clear();
    }
}