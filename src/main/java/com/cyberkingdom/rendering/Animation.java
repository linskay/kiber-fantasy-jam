package com.cyberkingdom.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation {
    private TextureRegion[] frames;
    private float frameDuration;
    private float currentFrameTime;
    private int currentFrameIndex;
    private boolean looping;

    public Animation(TextureRegion[] frames, float frameDuration, boolean looping) {
        this.frames = frames;
        this.frameDuration = frameDuration;
        this.looping = looping;
        this.currentFrameIndex = 0;
        this.currentFrameTime = 0;
    }

    public void update(float deltaTime) {
        currentFrameTime += deltaTime;

        if (currentFrameTime >= frameDuration) {
            currentFrameTime -= frameDuration;
            currentFrameIndex++;

            if (currentFrameIndex >= frames.length) {
                if (looping) {
                    currentFrameIndex = 0;
                } else {
                    currentFrameIndex = frames.length - 1;
                }
            }
        }
    }

    public TextureRegion getCurrentFrame() {
        return frames[currentFrameIndex];
    }

    public void reset() {
        currentFrameIndex = 0;
        currentFrameTime = 0;
    }
}