package com.cyberkingdom.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.assets.AssetManager;

public class DialogSystem {
    private Texture characterTexture;
    private String dialogText;
    private float animationTime = 0;
    private float targetX;
    private float startX;
    private float currentX;
    private boolean isActive = false;
    private BitmapFont font;
    private static final float ANIMATION_DURATION = 1.0f;
    private static final float DIALOG_DURATION = 5.0f;
    private float dialogTimer = 0;
    private AssetManager assetManager;

    public DialogSystem() {
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
    }

    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void showWitchVPNDialog() {
        Gdx.app.log("DialogSystem", "Attempting to show witch VPN dialog");
        if (assetManager != null && assetManager.isLoaded("assets/entities/witchVPN_dialog.png", Texture.class)) {
            Gdx.app.log("DialogSystem", "Witch VPN dialog texture is loaded");
            characterTexture = assetManager.get("assets/entities/witchVPN_dialog.png", Texture.class);
            dialogText = "Ваш триал все равно закончился! Еще увидимся";
            startX = Gdx.graphics.getWidth();
            targetX = Gdx.graphics.getWidth() - characterTexture.getWidth() - 50;
            currentX = startX;
            isActive = true;
            animationTime = 0;
            dialogTimer = 0;
            Gdx.app.log("DialogSystem", "Dialog activated with text: " + dialogText + ", isActive: " + isActive);
        } else {
            Gdx.app.error("DialogSystem", "Failed to load witch VPN dialog texture: AssetManager is " + 
                (assetManager == null ? "null" : "not null") + 
                ", texture loaded: " + (assetManager != null && assetManager.isLoaded("assets/entities/witchVPN_dialog.png", Texture.class)));
        }
    }

    public void update(float delta) {
        if (!isActive) return;

        dialogTimer += delta;
        Gdx.app.log("DialogSystem", "Dialog timer: " + dialogTimer + "/" + DIALOG_DURATION);
        if (dialogTimer >= DIALOG_DURATION) {
            Gdx.app.log("DialogSystem", "Dialog duration expired, deactivating dialog");
            isActive = false;
            return;
        }

        animationTime = Math.min(animationTime + delta, ANIMATION_DURATION);
        float progress = animationTime / ANIMATION_DURATION;
        currentX = MathUtils.lerp(startX, targetX, Interpolation.sineOut.apply(progress));
    }

    public void render(SpriteBatch batch) {
        if (!isActive) return;

        Gdx.app.log("DialogSystem", "Rendering dialog at position: " + currentX + ", isActive: " + isActive);
        batch.begin();
        // Рисуем персонажа
        batch.draw(characterTexture, currentX, 100, characterTexture.getWidth(), characterTexture.getHeight());
        
        // Рисуем текст диалога
        font.draw(batch, dialogText, currentX - 300, 200);
        batch.end();
    }

    public void dispose() {
        if (characterTexture != null) {
            characterTexture.dispose();
        }
        font.dispose();
    }

    public boolean isActive() {
        return isActive;
    }
} 