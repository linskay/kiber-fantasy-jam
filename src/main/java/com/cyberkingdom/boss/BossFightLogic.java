package com.cyberkingdom.boss;

import com.badlogic.gdx.Gdx;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.gameengine.GameEngine;
import com.cyberkingdom.world.LevelLoader;

public class BossFightLogic {
    private final LevelLoader levelLoader;
    private Player player;
    private final GameEngine gameEngine;
    private boolean isBossFightActive = false;

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BossFightLogic(LevelLoader levelLoader, GameEngine gameEngine) {
        this.levelLoader = levelLoader;
        this.gameEngine = gameEngine;
        Gdx.app.log("BossFightLogic", "Initialized successfully");
    }

    public void update(float delta) {
        if (!isBossFightActive) {
            return;
        }

        // Логика босса будет добавлена позже
    }

    public void startBossFight() {
        isBossFightActive = true;
        Gdx.app.log("BossFight", "Boss fight started");
    }

    public void endBossFight() {
        isBossFightActive = false;
        Gdx.app.log("BossFight", "Boss fight ended");
    }

    public boolean isBossFightActive() {
        return isBossFightActive;
    }
} 