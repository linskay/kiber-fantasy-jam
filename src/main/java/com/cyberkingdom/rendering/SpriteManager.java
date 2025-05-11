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
        // Загрузка текстур игрока
        loadTexture("Player", "assets/entities/player.png");
        
        // Загрузка текстур боссов
        loadTexture("WITCH_VPN", "assets/entities/witch_vpn.png");
        loadTexture("CAT_MINER", "assets/entities/cat_miner.png");
        
        // Загрузка текстур предметов
        loadTexture("COIN", "assets/Coin.png");
        loadTexture("USB_SKATERT", "assets/items/USB_Skatert.png");
        loadTexture("CRYPTO_SHOVEL", "assets/items/kriptoLopata.png");
        loadTexture("RTX_4090", "assets/items/RTX4090.png");
        loadTexture("TUSHENKA", "assets/items/tyshonka.png");
        loadTexture("KNIGA", "assets/items/kniga.png");
        loadTexture("WIFI_KEY", "assets/items/key.png");
        
        // Загрузка текстур платформы
        loadTexture("Platform", "assets/platform.png");
    }

    private void loadTexture(String name, String path) {
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

    public void setupSpriteRegions() {
        // Настройка регионов для платформы
        Texture platformTexture = textures.get("Platform");
        if (platformTexture != null) {
            spriteRegions.put("Platform", new TextureRegion[] { new TextureRegion(platformTexture) });
            Gdx.app.log("SpriteManager", "Platform texture loaded successfully");
        } else {
            Gdx.app.error("SpriteManager", "Platform texture is null");
        }

        // Настройка регионов для игрока
        Texture playerTexture = textures.get("Player");
        if (playerTexture != null) {
            spriteRegions.put("Player", new TextureRegion[] { new TextureRegion(playerTexture) });
            Gdx.app.log("SpriteManager", "Player texture loaded successfully");
        } else {
            Gdx.app.error("SpriteManager", "Player texture is null");
        }

        // Настройка регионов для монет
        Texture coinTexture = textures.get("COIN");
        if (coinTexture != null) {
            spriteRegions.put("COIN", new TextureRegion[] { new TextureRegion(coinTexture) });
            Gdx.app.log("SpriteManager", "Coin texture loaded successfully");
        } else {
            Gdx.app.error("SpriteManager", "Coin texture is null");
        }

        // Настройка регионов для боссов
        Texture witchTexture = textures.get("WITCH_VPN");
        if (witchTexture != null) {
            spriteRegions.put("WITCH_VPN", new TextureRegion[] { new TextureRegion(witchTexture) });
            Gdx.app.log("SpriteManager", "Witch VPN texture loaded successfully");
        } else {
            Gdx.app.error("SpriteManager", "Witch VPN texture is null");
        }

        Texture catTexture = textures.get("CAT_MINER");
        if (catTexture != null) {
            spriteRegions.put("CAT_MINER", new TextureRegion[] { new TextureRegion(catTexture) });
            Gdx.app.log("SpriteManager", "Cat Miner texture loaded successfully");
        } else {
            Gdx.app.error("SpriteManager", "Cat Miner texture is null");
        }

        // Настройка регионов для предметов
        Texture usbTexture = textures.get("USB_SKATERT");
        if (usbTexture != null) {
            spriteRegions.put("USB_SKATERT", new TextureRegion[] { new TextureRegion(usbTexture) });
            Gdx.app.log("SpriteManager", "USB Skatert texture loaded successfully");
        }

        Texture shovelTexture = textures.get("CRYPTO_SHOVEL");
        if (shovelTexture != null) {
            spriteRegions.put("CRYPTO_SHOVEL", new TextureRegion[] { new TextureRegion(shovelTexture) });
            Gdx.app.log("SpriteManager", "Crypto Shovel texture loaded successfully");
        }

        Texture rtxTexture = textures.get("RTX_4090");
        if (rtxTexture != null) {
            spriteRegions.put("RTX_4090", new TextureRegion[] { new TextureRegion(rtxTexture) });
            Gdx.app.log("SpriteManager", "RTX 4090 texture loaded successfully");
        }

        Texture tushonkaTexture = textures.get("TUSHENKA");
        if (tushonkaTexture != null) {
            spriteRegions.put("TUSHENKA", new TextureRegion[] { new TextureRegion(tushonkaTexture) });
            Gdx.app.log("SpriteManager", "Tushenka texture loaded successfully");
        }

        Texture knigaTexture = textures.get("KNIGA");
        if (knigaTexture != null) {
            spriteRegions.put("KNIGA", new TextureRegion[] { new TextureRegion(knigaTexture) });
            Gdx.app.log("SpriteManager", "Kniga texture loaded successfully");
        }

        Texture keyTexture = textures.get("WIFI_KEY");
        if (keyTexture != null) {
            spriteRegions.put("WIFI_KEY", new TextureRegion[] { new TextureRegion(keyTexture) });
            Gdx.app.log("SpriteManager", "Wifi Key texture loaded successfully");
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