package com.cyberkingdom.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DialogueSystem {
    private String currentText;
    private boolean isShowing;

    public void show(String text) {
        currentText = text;
        isShowing = true;
    }

    public void render(SpriteBatch batch) {
        if (isShowing) {
            // Отрисовка диалогового окна
        }
    }
}