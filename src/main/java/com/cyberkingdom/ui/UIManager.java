package com.cyberkingdom.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.cyberkingdom.entities.EntityFactory;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.gameengine.GameEngine;
import com.cyberkingdom.input.InputHandler;
import com.cyberkingdom.items.Inventory;
import com.cyberkingdom.items.InventoryWindow;
import com.cyberkingdom.physics.PhysicsSystem;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class UIManager {
    private InventoryUI inventoryUI;
    private HealthBar healthBar;
    private Stage stage;
    private InventoryWindow inventoryWindow;
    private BitmapFont font;
    private InputHandler inputHandler;
    private Skin skin;
    private GameEngine engine;
    private SpriteBatch uiBatch;
    private ShapeRenderer shapeRenderer;
    private SpriteManager spriteManager;
    private EntitySystem entitySystem;
    private EntityFactory entityFactory;
    private PhysicsSystem physicsSystem;
    private Player player;

    public UIManager(SpriteRenderer spriteRenderer, Player player, InputHandler inputHandler, GameEngine engine) {
        this.inputHandler = inputHandler;
        this.engine = engine;
        this.inventoryUI = new InventoryUI();
        this.healthBar = new HealthBar();
        this.stage = new Stage(new ScreenViewport());
        this.uiBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.spriteManager = spriteRenderer.getSpriteManager();
        this.entitySystem = engine.getEntitySystem();
        this.entityFactory = engine.getEntityFactory();
        this.physicsSystem = engine.getPhysicsSystem();
        this.player = player;

        // Создаем шрифт
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 15;
        parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<> ";
        parameter.borderWidth = 2f;
        parameter.borderColor = Color.WHITE;
        parameter.color = Color.valueOf("#D6A4FF");
        this.font = generator.generateFont(parameter);
        generator.dispose();

        // Создаем скин программно
        createSkin();
        
        initializeUIComponents(player);
    }

    private void createSkin() {
        skin = new Skin();
        
        // Добавляем шрифт
        skin.add("default-font", font);
        
        // Создаем стиль для Label
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);
        
        // Создаем стиль для Window
        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = font;
        windowStyle.titleFontColor = Color.WHITE;
        
        // Создаем текстуру для фона окна
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.1f, 0.1f, 0.1f, 0.9f);
        pixmap.fill();
        skin.add("window-background", new Texture(pixmap));
        pixmap.dispose();
        
        windowStyle.background = skin.newDrawable("window-background");
        skin.add("default", windowStyle);

        // Button
        Pixmap btnPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        btnPixmap.setColor(0.2f, 0.2f, 0.2f, 1f);
        btnPixmap.fill();
        Texture btnTexture = new Texture(btnPixmap);
        skin.add("button-background", btnTexture);
        btnPixmap.dispose();

        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = skin.newDrawable("button-background", Color.DARK_GRAY);
        buttonStyle.down = skin.newDrawable("button-background", Color.GRAY);
        buttonStyle.over = skin.newDrawable("button-background", Color.LIGHT_GRAY);
        skin.add("default", buttonStyle);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("button-background", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("button-background", Color.GRAY);
        textButtonStyle.over = skin.newDrawable("button-background", Color.LIGHT_GRAY);
        textButtonStyle.font = font;
        skin.add("default", textButtonStyle);
    }

    private void initializeUIComponents(Player player) {
        if (player != null) {
            // Создаем окно инвентаря
            float centerX = Gdx.graphics.getWidth() / 2f - 150; // половина ширины окна
            float centerY = Gdx.graphics.getHeight() / 2f - 200; // половина высоты окна
            inventoryWindow = new InventoryWindow(
                skin,
                centerX,
                centerY,
                player.getInventory(),
                player,
                entitySystem,
                entityFactory,
                physicsSystem
            );
            inventoryWindow.setVisible(false);
            stage.addActor(inventoryWindow);
        }
    }

    public void render(Player player) {
        if (player == null) return;

        // Рисуем полоску здоровья
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        healthBar.render(player, shapeRenderer);
        shapeRenderer.end();

        // Рисуем счетчик монет
        uiBatch.begin();
        TextureRegion[] coinRegions = spriteManager.getFrames("COIN");
        if (coinRegions != null && coinRegions.length > 0) {
            uiBatch.draw(coinRegions[0], 60, 48, 24, 24);
        }
        font.draw(uiBatch, "Дай.Токенов: " + player.getCoins(), 90, 65);
        font.setColor(Color.WHITE);
        font.draw(uiBatch, "sudo ls /Кэш(Е)", 90, 45);
        uiBatch.end();

        // Рисуем инвентарь, если он видим
        if (inputHandler != null && inputHandler.isInventoryVisible()) {
            Gdx.app.log("UIManager", "Rendering inventory window");
            inventoryWindow.setVisible(true);
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        } else {
            inventoryWindow.setVisible(false);
        }
    }

    public void updateCoinCount(int coins) {
        Gdx.app.log("UIManager", "Updating coin count to: " + coins);
    }

    public void dispose() {
        if (inventoryUI != null) {
            inventoryUI.dispose();
        }
        if (stage != null) {
            stage.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (uiBatch != null) {
            uiBatch.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }

    public Stage getStage() {
        return stage;
    }
}