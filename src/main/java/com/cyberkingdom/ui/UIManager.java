package com.cyberkingdom.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    public UIManager(SpriteRenderer spriteRenderer, Player player, InputHandler inputHandler, GameEngine engine) {
        this.inputHandler = inputHandler;
        this.engine = engine;
        this.inventoryUI = new InventoryUI();
        this.healthBar = new HealthBar();
        this.stage = new Stage(new ScreenViewport());
        this.uiBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();

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
        uiBatch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        healthBar.render(player, shapeRenderer);

        font.setColor(Color.YELLOW);
        font.draw(uiBatch, "Coins: " + player.getCoins(), 220, 90);
        font.setColor(Color.WHITE);
        font.draw(uiBatch, "Inventory (E)", 220, 70);

        shapeRenderer.end();
        uiBatch.end();

        inventoryWindow.setVisible(inputHandler.isInventoryVisible());
        inventoryWindow.refresh();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        uiBatch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        stage.dispose();
        skin.dispose();
    }
}