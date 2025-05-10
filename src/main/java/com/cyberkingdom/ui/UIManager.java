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
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.gameengine.GameEngine;
import com.cyberkingdom.input.InputHandler;
import com.cyberkingdom.items.InventoryWindow;
import com.cyberkingdom.rendering.SpriteRenderer;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

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

    public UIManager(SpriteRenderer spriteRenderer, Player player, InputHandler inputHandler, GameEngine engine) {
        this.inputHandler = inputHandler;
        this.engine = engine;
        this.inventoryUI = new InventoryUI();
        this.healthBar = new HealthBar();
        this.stage = new Stage(new ScreenViewport());
        this.uiBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.spriteManager = spriteRenderer.getSpriteManager();

        // Используем arial.ttf для поддержки кириллицы
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 15;
        parameter.characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя0123456789][_!$%#@|\\/?-+=()*&.;,{}\"´`'<> ";
        parameter.borderWidth = 2f;
        parameter.borderColor = Color.WHITE;
        parameter.color = Color.valueOf("#D6A4FF");
        this.font = generator.generateFont(parameter);
        generator.dispose();

        initializeUIComponents(player);
    }

    private void initializeUIComponents(Player player) {
        skin = new Skin();
        skin.add("default-font", engine.getMenuFont(), BitmapFont.class);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture whiteTexture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("white", whiteTexture, Texture.class);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.background = skin.newDrawable("white", Color.GRAY);
        windowStyle.titleFont = skin.getFont("default-font");
        windowStyle.titleFontColor = Color.WHITE;
        skin.add("default", windowStyle, Window.WindowStyle.class);

        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.up = skin.newDrawable("white", Color.LIGHT_GRAY);
        buttonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        skin.add("default", buttonStyle, Button.ButtonStyle.class);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle, Label.LabelStyle.class);

        inventoryWindow = new InventoryWindow(
                skin,
                Gdx.graphics.getWidth() - 350,
                50,
                player.getInventory(),
                player,
                null,
                null,
                null
        );
        stage.addActor(inventoryWindow);
    }

    public void render(Player player) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        healthBar.render(player, shapeRenderer);
        shapeRenderer.end();

        uiBatch.begin();
        TextureRegion[] coinRegions = spriteManager.getFrames("COIN");
        if (coinRegions != null && coinRegions.length > 0) {
            uiBatch.draw(coinRegions[0], 60, 48, 24, 24);
        }
        font.draw(uiBatch, "Дай.Токенов: " + player.getCoins(), 90, 65);
        font.setColor(Color.WHITE);
        font.draw(uiBatch, "sudo ls /Кэш(Е)", 90, 45);
        uiBatch.end();

        inventoryWindow.setVisible(inputHandler.isInventoryVisible());
        inventoryWindow.refresh();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void updateCoinCount(int coins) {
        Gdx.app.log("UIManager", "Updating coin count to: " + coins);
    }

    public void dispose() {
        uiBatch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        stage.dispose();
        skin.dispose();
    }
}