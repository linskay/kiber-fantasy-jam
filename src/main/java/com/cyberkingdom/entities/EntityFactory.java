package com.cyberkingdom.entities;

import com.cyberkingdom.rendering.Animation;
import com.cyberkingdom.rendering.SpriteManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class EntityFactory {
    private SpriteManager spriteManager;

    public EntityFactory(SpriteManager spriteManager) {
        this.spriteManager = spriteManager;
    }

    public Player createPlayer(float x, float y) {
        Player player = new Player();
        TextureRegion[] walkFrames = spriteManager.getFrames("player_walk");
        Animation walkAnimation = new Animation(walkFrames, 0.1f, true);
        player.getAnimationComponent().setCurrentAnimation(walkAnimation);
        player.getPosition().set(x, y);
        return player;
    }

    // ... другие методы создания сущностей
}