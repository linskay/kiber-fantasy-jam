package com.cyberkingdom.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;

public class SpriteManager {
    private ObjectMap<String, Texture> textures;
    private ObjectMap<String, TextureRegion[]> frames;

    public SpriteManager() {
        this.textures = new ObjectMap<>();
        this.frames = new ObjectMap<>();
        loadTextures();
        setupSpriteRegions();
    }

    public void loadTextures() {
        // Загрузка анимаций игрока
        loadPlayerAnimations();
        
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

    private void loadPlayerAnimations() {
        try {
            // Загрузка анимации движения влево
            Array<TextureRegion> leftFrames = new Array<>();
            boolean leftFramesLoaded = true;
            for (int i = 1; i <= 4; i++) {
                String path = "assets/entities/oleg_run/left" + i + ".png";
                if (Gdx.files.internal(path).exists()) {
                    Gdx.app.log("SpriteManager", "Loading left animation frame: " + path);
                    Texture texture = new Texture(Gdx.files.internal(path));
                    TextureRegion region = new TextureRegion(texture);
                    leftFrames.add(region);
                    textures.put("Player_Left_" + i, texture);
                    Gdx.app.log("SpriteManager", "Successfully loaded left frame " + i);
                } else {
                    Gdx.app.error("SpriteManager", "Left animation frame not found: " + path);
                    leftFramesLoaded = false;
                }
            }
            if (leftFramesLoaded && !leftFrames.isEmpty()) {
                frames.put("Player_Left", leftFrames.toArray(TextureRegion.class));
                Gdx.app.log("SpriteManager", "Left animation loaded with " + leftFrames.size + " frames");
            } else {
                Gdx.app.error("SpriteManager", "Failed to load left animation frames!");
            }

            // Загрузка анимации движения вправо
            Array<TextureRegion> rightFrames = new Array<>();
            boolean rightFramesLoaded = true;
            for (int i = 1; i <= 4; i++) {
                String path = "assets/entities/oleg_run/right" + i + ".png";
                if (Gdx.files.internal(path).exists()) {
                    Gdx.app.log("SpriteManager", "Loading right animation frame: " + path);
                    Texture texture = new Texture(Gdx.files.internal(path));
                    TextureRegion region = new TextureRegion(texture);
                    rightFrames.add(region);
                    textures.put("Player_Right_" + i, texture);
                    Gdx.app.log("SpriteManager", "Successfully loaded right frame " + i);
                } else {
                    Gdx.app.error("SpriteManager", "Right animation frame not found: " + path);
                    rightFramesLoaded = false;
                }
            }
            if (rightFramesLoaded && !rightFrames.isEmpty()) {
                frames.put("Player_Right", rightFrames.toArray(TextureRegion.class));
                Gdx.app.log("SpriteManager", "Right animation loaded with " + rightFrames.size + " frames");
            } else {
                Gdx.app.error("SpriteManager", "Failed to load right animation frames!");
            }

            // Загрузка анимации смерти
            Array<TextureRegion> deathFrames = new Array<>();
            boolean deathFramesLoaded = true;
            for (int i = 1; i <= 4; i++) {
                String path = "assets/entities/oleg_run/death" + i + ".png";
                if (Gdx.files.internal(path).exists()) {
                    Gdx.app.log("SpriteManager", "Loading death animation frame: " + path);
                    Texture texture = new Texture(Gdx.files.internal(path));
                    TextureRegion region = new TextureRegion(texture);
                    deathFrames.add(region);
                    textures.put("Player_Death_" + i, texture);
                    Gdx.app.log("SpriteManager", "Successfully loaded death frame " + i);
                } else {
                    Gdx.app.error("SpriteManager", "Death animation frame not found: " + path);
                    deathFramesLoaded = false;
                }
            }
            if (deathFramesLoaded && !deathFrames.isEmpty()) {
                frames.put("Player_Death", deathFrames.toArray(TextureRegion.class));
                Gdx.app.log("SpriteManager", "Death animation loaded with " + deathFrames.size + " frames");
            } else {
                Gdx.app.error("SpriteManager", "Failed to load death animation frames!");
            }
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Error loading player animations", e);
        }
    }

    private void loadTexture(String name, String path) {
        try {
            Texture texture = new Texture(Gdx.files.internal(path));
            textures.put(name, texture);
            Gdx.app.log("SpriteManager", "Loaded texture: " + name);
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Failed to load texture: " + name, e);
        }
    }

    public void setupSpriteRegions() {
        // Настройка регионов для платформы
        Texture platformTexture = textures.get("Platform");
        if (platformTexture != null) {
            frames.put("Platform", new TextureRegion[] { new TextureRegion(platformTexture) });
            Gdx.app.log("SpriteManager", "Platform texture loaded successfully");
        } else {
            Gdx.app.error("SpriteManager", "Platform texture is null");
        }

        // Настройка регионов для монет
        Texture coinTexture = textures.get("COIN");
        if (coinTexture != null) {
            frames.put("COIN", new TextureRegion[] { new TextureRegion(coinTexture) });
            Gdx.app.log("SpriteManager", "Coin texture loaded successfully");
        } else {
            Gdx.app.error("SpriteManager", "Coin texture is null");
        }

        // Настройка регионов для боссов
        Texture witchTexture = textures.get("WITCH_VPN");
        if (witchTexture != null) {
            frames.put("WITCH_VPN", new TextureRegion[] { new TextureRegion(witchTexture) });
            Gdx.app.log("SpriteManager", "Witch VPN texture loaded successfully");
        } else {
            Gdx.app.error("SpriteManager", "Witch VPN texture is null");
        }

        Texture catTexture = textures.get("CAT_MINER");
        if (catTexture != null) {
            frames.put("CAT_MINER", new TextureRegion[] { new TextureRegion(catTexture) });
            Gdx.app.log("SpriteManager", "Cat Miner texture loaded successfully");
        } else {
            Gdx.app.error("SpriteManager", "Cat Miner texture is null");
        }

        // Настройка регионов для предметов
        Texture usbTexture = textures.get("USB_SKATERT");
        if (usbTexture != null) {
            frames.put("USB_SKATERT", new TextureRegion[] { new TextureRegion(usbTexture) });
            Gdx.app.log("SpriteManager", "USB Skatert texture loaded successfully");
        }

        Texture shovelTexture = textures.get("CRYPTO_SHOVEL");
        if (shovelTexture != null) {
            frames.put("CRYPTO_SHOVEL", new TextureRegion[] { new TextureRegion(shovelTexture) });
            Gdx.app.log("SpriteManager", "Crypto Shovel texture loaded successfully");
        }

        Texture rtxTexture = textures.get("RTX_4090");
        if (rtxTexture != null) {
            frames.put("RTX_4090", new TextureRegion[] { new TextureRegion(rtxTexture) });
            Gdx.app.log("SpriteManager", "RTX 4090 texture loaded successfully");
        }

        Texture tushonkaTexture = textures.get("TUSHENKA");
        if (tushonkaTexture != null) {
            frames.put("TUSHENKA", new TextureRegion[] { new TextureRegion(tushonkaTexture) });
            Gdx.app.log("SpriteManager", "Tushenka texture loaded successfully");
        }

        Texture knigaTexture = textures.get("KNIGA");
        if (knigaTexture != null) {
            frames.put("KNIGA", new TextureRegion[] { new TextureRegion(knigaTexture) });
            Gdx.app.log("SpriteManager", "Kniga texture loaded successfully");
        }

        Texture keyTexture = textures.get("WIFI_KEY");
        if (keyTexture != null) {
            frames.put("WIFI_KEY", new TextureRegion[] { new TextureRegion(keyTexture) });
            Gdx.app.log("SpriteManager", "Wifi Key texture loaded successfully");
        }
    }

    public Texture getTexture(String name) {
        return textures.get(name);
    }

    public TextureRegion[] getFrames(String name) {
        return frames.get(name);
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
            texture.dispose();
        }
        textures.clear();
        frames.clear();
    }
}