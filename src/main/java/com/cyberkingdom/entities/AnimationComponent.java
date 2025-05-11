package com.cyberkingdom.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationComponent {
    private Array<Animation<TextureRegion>> animations;
    private float stateTime;
    private int currentAnimationIndex;
    private static final float FRAME_DURATION = 0.1f;

    public AnimationComponent() {
        this.animations = new Array<>();
        this.stateTime = 0;
        this.currentAnimationIndex = 0;
    }

    public void addAnimation(Array<TextureRegion> frames) {
        Animation<TextureRegion> animation = new Animation<>(FRAME_DURATION, frames);
        animations.add(animation);
    }

    public void setCurrentAnimation(int index) {
        if (index >= 0 && index < animations.size) {
            currentAnimationIndex = index;
        }
    }

    public int getCurrentAnimation() {
        return currentAnimationIndex;
    }

    public TextureRegion getCurrentFrame(float deltaTime) {
        if (animations.size == 0) return null;
        stateTime += deltaTime;
        return animations.get(currentAnimationIndex).getKeyFrame(stateTime, true);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    public void dispose() {
        animations.clear();
    }
}