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
        try {
            Gdx.app.log("SpriteManager", "Starting to load textures with correct paths");
            
            // Загрузка текстур игрока
            loadTexture("Player_Idle", "assets/entities/player.png"); // Дефолтное изображение
            loadTexture("Player_Run_Left_1", "assets/entities/oleg_run/left1.png");
            loadTexture("Player_Run_Left_2", "assets/entities/oleg_run/left2.png");
            loadTexture("Player_Run_Left_3", "assets/entities/oleg_run/left3.png");
            loadTexture("Player_Run_Left_4", "assets/entities/oleg_run/left4.png");
            loadTexture("Player_Run_Right_1", "assets/entities/oleg_run/right1.png");
            loadTexture("Player_Run_Right_2", "assets/entities/oleg_run/right2.png");
            loadTexture("Player_Run_Right_3", "assets/entities/oleg_run/right3.png");
            loadTexture("Player_Run_Right_4", "assets/entities/oleg_run/right4.png");
            loadTexture("Player_Death_1", "assets/entities/oleg_run/death1.png");
            loadTexture("Player_Death_2", "assets/entities/oleg_run/death2.png");
            loadTexture("Player_Death_3", "assets/entities/oleg_run/death3.png");
            loadTexture("Player_Death_4", "assets/entities/oleg_run/death4.png");
        
            // Загрузка текстур боссов
            loadTexture("WITCH_VPN", "assets/entities/witch_vpn.png");
            loadTexture("CAT_MINER", "assets/entities/cat_miner.png");
        
            // Загрузка текстур Дединсайда
            loadTexture("dedinsaid1", "assets/entities/dedinsaid/dedinsaid1.png");
            loadTexture("dedinsaid2", "assets/entities/dedinsaid/dedinsaid2.png");
            loadTexture("dedinsaid3", "assets/entities/dedinsaid/dedinsaid3.png");
            loadTexture("dedinsaid4", "assets/entities/dedinsaid/dedinsaid4.png");
        
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

            // Загрузка текстуры сердечка
            loadTexture("HEART", "assets/Heart.png");
        
            // Загрузка текстур для мини-игры
            loadTexture("VIRUS", "assets/entities/virus.png");
            loadTexture("KIMCHI_1", "assets/kimchi/1kimchi.png");
            loadTexture("KIMCHI_2", "assets/kimchi/2kimchi.png");
            loadTexture("KIMCHI_3", "assets/kimchi/3kimchi.png");
            loadTexture("MINIGAME_BACKGROUND", "assets/background_level_bonus.png");

            // Добавляем загрузку текстур для мини-игры, которые отсутствовали
            loadTexture("CodeSymbol", "assets/code_symbol.png"); // Предполагаемый путь
            loadTexture("Boss", "assets/generic_boss.png"); // Предполагаемый путь
            loadTexture("CyberBackground", "assets/background_cyber.png"); // Предполагаемый путь
        
            Gdx.app.log("SpriteManager", "Finished attempting to load textures");
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Error during texture loading process", e);
        }
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

    private void loadTexture(String key, String path) {
        try {
            Gdx.app.log("SpriteManager", "Attempting to load texture: " + path + " with key: " + key);
            if (!Gdx.files.internal(path).exists()) {
                Gdx.app.error("SpriteManager", "Texture file not found: " + path);
                textures.put(key, createPlaceholderTexture()); // Добавляем заглушку
                return;
            }
            Texture texture = new Texture(Gdx.files.internal(path));
            if (texture == null) {
                Gdx.app.error("SpriteManager", "Failed to create Texture object for path: " + path);
                textures.put(key, createPlaceholderTexture()); // Добавляем заглушку
                return;
            }
            textures.put(key, texture);
            Gdx.app.log("SpriteManager", "Successfully loaded texture: " + path + " with key: " + key);
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Error loading texture: " + path + " with key: " + key, e);
            textures.put(key, createPlaceholderTexture()); // Добавляем заглушку
        }
    }

    public void setupSpriteRegions() {
        try {
            Gdx.app.log("SpriteManager", "Setting up sprite regions");
        // Настройка регионов для платформы
        Texture platformTexture = textures.get("Platform");
        if (platformTexture != null) {
            frames.put("Platform", new TextureRegion[] { new TextureRegion(platformTexture) });
                Gdx.app.log("SpriteManager", "Platform regions setup successfully");
        } else {
                Gdx.app.error("SpriteManager", "Platform texture is null, cannot setup regions");
        }

        // Настройка регионов для монет
        Texture coinTexture = textures.get("COIN");
        if (coinTexture != null) {
            frames.put("COIN", new TextureRegion[] { new TextureRegion(coinTexture) });
                Gdx.app.log("SpriteManager", "Coin regions setup successfully");
        } else {
                Gdx.app.error("SpriteManager", "Coin texture is null, cannot setup regions");
        }

        // Настройка регионов для боссов
        Texture witchTexture = textures.get("WITCH_VPN");
        if (witchTexture != null) {
            frames.put("WITCH_VPN", new TextureRegion[] { new TextureRegion(witchTexture) });
                Gdx.app.log("SpriteManager", "Witch VPN regions setup successfully");
        } else {
                Gdx.app.error("SpriteManager", "Witch VPN texture is null, cannot setup regions");
        }

        Texture catTexture = textures.get("CAT_MINER");
        if (catTexture != null) {
            frames.put("CAT_MINER", new TextureRegion[] { new TextureRegion(catTexture) });
                Gdx.app.log("SpriteManager", "Cat Miner regions setup successfully");
        } else {
                Gdx.app.error("SpriteManager", "Cat Miner texture is null, cannot setup regions");
        }

        // Настройка регионов для предметов
        Texture usbTexture = textures.get("USB_SKATERT");
        if (usbTexture != null) {
            frames.put("USB_SKATERT", new TextureRegion[] { new TextureRegion(usbTexture) });
                Gdx.app.log("SpriteManager", "USB Skatert regions setup successfully");
        }

        Texture shovelTexture = textures.get("CRYPTO_SHOVEL");
        if (shovelTexture != null) {
            frames.put("CRYPTO_SHOVEL", new TextureRegion[] { new TextureRegion(shovelTexture) });
                Gdx.app.log("SpriteManager", "Crypto Shovel regions setup successfully");
        }

        Texture rtxTexture = textures.get("RTX_4090");
        if (rtxTexture != null) {
            frames.put("RTX_4090", new TextureRegion[] { new TextureRegion(rtxTexture) });
                Gdx.app.log("SpriteManager", "RTX 4090 regions setup successfully");
        }

        Texture tushonkaTexture = textures.get("TUSHENKA");
        if (tushonkaTexture != null) {
            frames.put("TUSHENKA", new TextureRegion[] { new TextureRegion(tushonkaTexture) });
                Gdx.app.log("SpriteManager", "Tushenka regions setup successfully");
        }

        Texture knigaTexture = textures.get("KNIGA");
        if (knigaTexture != null) {
            frames.put("KNIGA", new TextureRegion[] { new TextureRegion(knigaTexture) });
                Gdx.app.log("SpriteManager", "Kniga regions setup successfully");
        }

        Texture keyTexture = textures.get("WIFI_KEY");
        if (keyTexture != null) {
            frames.put("WIFI_KEY", new TextureRegion[] { new TextureRegion(keyTexture) });
                Gdx.app.log("SpriteManager", "Wifi Key regions setup successfully");
            }

            // Настройка регионов для анимаций игрока (используем старые ключи)
            if (textures.containsKey("Player_Idle")) frames.put("Player_Idle", new TextureRegion[] { new TextureRegion(textures.get("Player_Idle")) });
            if (textures.containsKey("Player_Run_Left_1")) frames.put("Player_Run_Left_1", new TextureRegion[] { new TextureRegion(textures.get("Player_Run_Left_1")) });
            if (textures.containsKey("Player_Run_Left_2")) frames.put("Player_Run_Left_2", new TextureRegion[] { new TextureRegion(textures.get("Player_Run_Left_2")) });
            if (textures.containsKey("Player_Run_Left_3")) frames.put("Player_Run_Left_3", new TextureRegion[] { new TextureRegion(textures.get("Player_Run_Left_3")) });
            if (textures.containsKey("Player_Run_Left_4")) frames.put("Player_Run_Left_4", new TextureRegion[] { new TextureRegion(textures.get("Player_Run_Left_4")) });
            if (textures.containsKey("Player_Run_Right_1")) frames.put("Player_Run_Right_1", new TextureRegion[] { new TextureRegion(textures.get("Player_Run_Right_1")) });
            if (textures.containsKey("Player_Run_Right_2")) frames.put("Player_Run_Right_2", new TextureRegion[] { new TextureRegion(textures.get("Player_Run_Right_2")) });
            if (textures.containsKey("Player_Run_Right_3")) frames.put("Player_Run_Right_3", new TextureRegion[] { new TextureRegion(textures.get("Player_Run_Right_3")) });
            if (textures.containsKey("Player_Run_Right_4")) frames.put("Player_Run_Right_4", new TextureRegion[] { new TextureRegion(textures.get("Player_Run_Right_4")) });
            if (textures.containsKey("Player_Death_1")) frames.put("Player_Death_1", new TextureRegion[] { new TextureRegion(textures.get("Player_Death_1")) });
            if (textures.containsKey("Player_Death_2")) frames.put("Player_Death_2", new TextureRegion[] { new TextureRegion(textures.get("Player_Death_2")) });
            if (textures.containsKey("Player_Death_3")) frames.put("Player_Death_3", new TextureRegion[] { new TextureRegion(textures.get("Player_Death_3")) });
            if (textures.containsKey("Player_Death_4")) frames.put("Player_Death_4", new TextureRegion[] { new TextureRegion(textures.get("Player_Death_4")) });
            
            Gdx.app.log("SpriteManager", "All sprite regions setup successfully");
        } catch (Exception e) {
            Gdx.app.error("SpriteManager", "Error during sprite region setup process", e);
        }
    }

    public Texture getTexture(String name) {
        Gdx.app.log("SpriteManager", "Attempting to get texture for key: " + name);
        Texture texture = textures.get(name);
        if (texture == null) {
            Gdx.app.error("SpriteManager", "Texture not found or is null for key: " + name);
        } else {
            Gdx.app.log("SpriteManager", "Successfully retrieved texture for key: " + name);
        }
        return texture;
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