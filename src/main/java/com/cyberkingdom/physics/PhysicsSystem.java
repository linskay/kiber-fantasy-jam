package com.cyberkingdom.physics;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.cyberkingdom.entities.EntitySystem;
import com.cyberkingdom.entities.Player;
import com.cyberkingdom.input.InputHandler;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem {
    private List<Rectangle> platforms = new ArrayList<>();
    private float gravity = -900f;
    private Player player;
    private InputHandler inputHandler;

    public void setPlayer(Player player) {
        this.player = player;
        this.inputHandler = new InputHandler(player);
    }

    public PhysicsSystem(EntitySystem entitySystem, float worldWidth, float worldHeight) {
    }

    public void update(float deltaTime) {
        if (inputHandler != null) {
            inputHandler.update(deltaTime);
        }

        if (player != null && player.isActive()) {
            // Обновление физики игрока
            Vector2 velocity = player.getVelocity();
            Vector2 position = player.getPosition();

            // Применение гравитации
            velocity.y += gravity * deltaTime;

            // Обновление позиции
            position.mulAdd(velocity, deltaTime);

            // Обработка коллизий
            handlePlayerCollisions(player);
        }
    }

    private void handlePlayerCollisions(Player player) {
        Rectangle playerBounds = player.getCollisionComponent().getBounds();
        boolean onGround = false;

        for (Rectangle platform : platforms) {
            if (playerBounds.overlaps(platform)) {
                float overlapLeft = playerBounds.x + playerBounds.width - platform.x;
                float overlapRight = platform.x + platform.width - playerBounds.x;
                float overlapTop = playerBounds.y + playerBounds.height - platform.y;
                float overlapBottom = platform.y + platform.height - playerBounds.y;

                float minOverlap = Math.min(
                        Math.min(overlapLeft, overlapRight),
                        Math.min(overlapTop, overlapBottom)
                );

                if (minOverlap == overlapTop) {
                    player.getPosition().y = platform.y - playerBounds.height;
                    player.getVelocity().y = 0;
                } else if (minOverlap == overlapBottom) {
                    player.getPosition().y = platform.y + platform.height;
                    player.getVelocity().y = 0;
                    onGround = true;
                    player.setJumping(false);
                } else if (minOverlap == overlapLeft) {
                    player.getPosition().x = platform.x - playerBounds.width;
                    player.getVelocity().x = 0;
                } else if (minOverlap == overlapRight) {
                    player.getPosition().x = platform.x + platform.width;
                    player.getVelocity().x = 0;
                }
            }
        }
        player.setOnGround(onGround);
    }

    public void addPlatform(Rectangle platform) {
        platforms.add(platform);
    }

    public List<Rectangle> getPlatforms() {
        return new ArrayList<>(platforms);
    }

    public void clearPlatforms() {
        platforms.clear();
    }
}