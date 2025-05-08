package com.cyberkingdom.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.ArrayList;
import java.util.List;

public class AnimationComponent {
    private List<TextureRegion> frames;
    private float frameDuration;
    private int currentFrameIndex;
    private float stateTime;

    public AnimationComponent() {
        this.frames = new ArrayList<>();
        this.frameDuration = 0.1f; // Убедимся, что значение не ноль
        this.currentFrameIndex = 0;
        this.stateTime = 0f;
    }

    public void addFrame(TextureRegion frame) {
        if (frame != null) {
            frames.add(frame);
        }
    }

    public TextureRegion getCurrentFrame(float deltaTime) {
        if (frames.isEmpty()) {
            return null; // Возвращаем null, если нет кадров
        }
        stateTime += deltaTime;
        if (stateTime > frameDuration && !frames.isEmpty()) {
            currentFrameIndex = (currentFrameIndex + 1) % frames.size();
            stateTime = 0f;
        }
        return frames.get(currentFrameIndex);
    }

    public List<TextureRegion> getFrames() {
        return frames;
    }

    public void setFrameDuration(float duration) {
        this.frameDuration = Math.max(0.01f, duration); // Минимальное значение для избежания деления на ноль
    }

    public void dispose() {
        if (frames != null) {
            frames.clear();
            frames = null;
        }
        stateTime = 0f;
        currentFrameIndex = 0;
    }
}