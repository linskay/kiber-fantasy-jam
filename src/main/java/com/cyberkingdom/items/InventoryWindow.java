package com.cyberkingdom.items;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.cyberkingdom.entities.*;
import com.cyberkingdom.physics.PhysicsSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InventoryWindow extends Window {
    private static final int ROWS = 3;
    private static final int COLS = 3;

    private EntitySystem entitySystem;
    private EntityFactory entityFactory;
    private Random random = new Random();
    private PhysicsSystem physicsSystem;
    private Button[][] cells = new Button[ROWS][COLS];
    private int selectedRow = 0;
    private int selectedCol = 0;

    private Skin skin;
    private Table gridTable;
    private Inventory inventory;
    private Player player;

    // Поля для подсказки
    private Label tooltipLabel;
    private Table tooltipTable;

    public InventoryWindow(Skin skin, float x, float y, Inventory inventory,
                           Player player, EntitySystem entitySystem,
                           EntityFactory entityFactory, PhysicsSystem physicsSystem) {
        super("sudo ls /Кэш(Е)", skin);
        this.skin = skin;
        this.inventory = inventory;
        this.player = player;
        this.entitySystem = entitySystem;
        this.entityFactory = entityFactory;
        this.physicsSystem = physicsSystem;
        
        // Создаем таблицу для сетки инвентаря
        gridTable = new Table(skin);
        gridTable.defaults().size(80, 80).pad(5);

        // Создаем ячейки и добавляем в таблицу
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Button cell = new Button(skin);
                cells[row][col] = cell;
                gridTable.add(cell);
            }
            gridTable.row();
        }

        // Добавляем таблицу в окно
        this.add(gridTable).expand().fill();
        this.setSize(300, 400);
        this.setVisible(false);
        this.setMovable(false);
        this.setPosition(x, y);

        // Инициализация подсказки
        tooltipLabel = new Label("", skin);
        tooltipLabel.setWrap(true);
        tooltipLabel.setWidth(200);

        tooltipTable = new Table(skin);
        tooltipTable.setBackground(skin.newDrawable("window-background"));
        tooltipTable.add(tooltipLabel).width(200).pad(10);
        tooltipTable.setVisible(false);
        this.addActor(tooltipTable);

        // Обновляем отображение
        refresh();
        updateSelection();

        // Обработка клавиш для навигации и выбора
        this.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                        selectedRow = (selectedRow - 1 + ROWS) % ROWS;
                        updateSelection();
                        return true;
                    case Input.Keys.S:
                        selectedRow = (selectedRow + 1) % ROWS;
                        updateSelection();
                        return true;
                    case Input.Keys.A:
                        selectedCol = (selectedCol - 1 + COLS) % COLS;
                        updateSelection();
                        return true;
                    case Input.Keys.D:
                        selectedCol = (selectedCol + 1) % COLS;
                        updateSelection();
                        return true;
                    case Input.Keys.SPACE:
                        onSelectCell(selectedRow, selectedCol);
                        return true;
                }
                return false;
            }
        });
    }

    /** Обновляет отображение предметов в ячейках */
    public void refresh() {
        if (inventory == null) return;
        
        List<Item> items = new ArrayList<>(inventory.getItems());
        int index = 0;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Button cell = cells[row][col];
                cell.clearChildren();

                if (index < items.size()) {
                    Item item = items.get(index);
                    if (item != null) {
                        Stack stack = new Stack();
                        
                        // Добавляем изображение предмета
                        if (item.getTexture() != null) {
                            Image img = new Image(new TextureRegionDrawable(new TextureRegion(item.getTexture())));
                            img.setScaling(Scaling.fit);
                            stack.add(img);
                        } else {
                            com.badlogic.gdx.Gdx.app.log("InventoryWindow", "Item " + item.getItemType() + " has no texture!");
                        }

                        // Добавляем количество предметов
                        if (item.getQuantity() > 1) {
                            Label.LabelStyle labelStyle = skin.get(Label.LabelStyle.class);
                            if (labelStyle == null) {
                                labelStyle = new Label.LabelStyle();
                                labelStyle.font = new BitmapFont();
                            }

                            Label qtyLabel = new Label(String.valueOf(item.getQuantity()), labelStyle);
                            qtyLabel.setColor(Color.WHITE);
                            qtyLabel.setFontScale(0.8f);
                            qtyLabel.setAlignment(Align.bottomRight);

                            Table qtyTable = new Table();
                            qtyTable.setFillParent(true);
                            qtyTable.bottom().right().pad(2);
                            qtyTable.add(qtyLabel);

                            stack.add(qtyTable);
                        }

                        cell.add(stack).size(64, 64);
                    }
                }
                index++;
            }
        }
        updateSelection();
    }

    /** Обновляет выделение выбранной ячейки и подсказку */
    private void updateSelection() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                cells[row][col].setColor(Color.WHITE);
            }
        }
        cells[selectedRow][selectedCol].setColor(Color.YELLOW);

        updateTooltip();
    }

    /** Обновляет текст и позицию подсказки */
    private void updateTooltip() {
        int index = selectedRow * COLS + selectedCol;
        List<Item> items = new ArrayList<>(inventory.getItems());
        if (index < items.size()) {
            Item item = items.get(index);
            String desc = item.getDescription();
            tooltipLabel.setText(desc);
            tooltipTable.setVisible(true);

            Button cell = cells[selectedRow][selectedCol];
            float x = cell.getX() + cell.getWidth() + 10;
            float y = cell.getY() + cell.getHeight() / 2;
            tooltipTable.setPosition(x, y);
        } else {
            tooltipTable.setVisible(false);
        }
    }

    private void onSelectCell(int row, int col) {
        int index = row * COLS + col;
        List<Item> items = new ArrayList<>(inventory.getItems());
        if (index < items.size()) {
            Item selectedItem = items.get(index);
            String itemType = selectedItem.getItemType().toLowerCase();

            switch (itemType) {
                case "crypto_coin":
                    float healAmount = player.getMaxHealth() * 0.5f;
                    player.setHealth(Math.min(player.getHealth() + healAmount, player.getMaxHealth()));
                    break;

                case "vpn_token":
                    int n = 5; // количество криптомонет
                    if (!physicsSystem.getPlatforms().isEmpty()) {
                        for (int i = 0; i < n; i++) {
                            Rectangle platform = getRandomPlatform();
                            float x = platform.x + random.nextFloat() * platform.width;
                            float y = platform.y + platform.height + 10;
                            Vector2 spawnPos = new Vector2(x, y);

                            Item cryptoCoin = entityFactory.createItem("crypto_coin", spawnPos, 1);
                            if (cryptoCoin != null) {
                                entitySystem.addEntity(cryptoCoin);
                            }
                        }
                    }
                    break;

                case "usb_scatert":
                    // Восстановить здоровье героя на 100%
                    player.setHealth(player.getMaxHealth());
                    System.out.println("Жизнь полностью восстановлена!");
                    break;

                case "hardware_wallet":
                    // Удаляем босса CAT_MINER, если он есть
                    boolean removed = false;
                    List<GameEntity> entities = new ArrayList<>(entitySystem.getEntities());
                    for (GameEntity entity : entities) {
                        if (entity instanceof Boss) {
                            Boss boss = (Boss) entity;
                            if ("CAT_MINER".equalsIgnoreCase(boss.getName())) {
                                entitySystem.removeEntity(boss);
                                removed = true;
                                System.out.println("Босс CAT_MINER удалён с карты!");
                                break; // удаляем только одного босса
                            }
                        }
                    }
                    if (!removed) {
                        System.out.println("Босс CAT_MINER не найден на карте.");
                    }
                    break;

                default:
                    System.out.println("Использован предмет: " + selectedItem.getItemType());
                    break;
            }

            // Уменьшаем количество предмета
            selectedItem.decreaseQuantity(1);

            // Если количество предметов 0, удаляем из инвентаря
            if (selectedItem.getQuantity() <= 0) {
                inventory.removeItem(selectedItem);
            }

            // Обновляем отображение инвентаря
            refresh();

        } else {
            System.out.println("Ячейка пуста");
        }
    }


    private Rectangle getRandomPlatform() {
        List<Rectangle> platforms = physicsSystem.getPlatforms();
        if (platforms == null || platforms.isEmpty()) {
            return new Rectangle(0, 0, 100, 20);
        }
        return platforms.get(random.nextInt(platforms.size()));
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        refresh();
    }
}






