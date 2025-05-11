package com.cyberkingdom.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationComponent {
    protected static final float DEFAULT_FRAME_DURATION = 0.1f;
    protected static final boolean DEFAULT_LOOPING = true;

    protected Array<Animation<TextureRegion>> animations;
    protected float stateTime;
    protected int currentAnimationIndex;
    protected boolean isPlaying;
    protected float frameDuration;
    protected boolean looping;

    public AnimationComponent() {
        this(DEFAULT_FRAME_DURATION, DEFAULT_LOOPING);
    }

    public AnimationComponent(float frameDuration, boolean looping) {
        this.animations = new Array<>();
        this.stateTime = 0;
        this.currentAnimationIndex = 0;
        this.isPlaying = true;
        this.frameDuration = frameDuration;
        this.looping = looping;
    }

    public void addAnimation(Array<TextureRegion> frames) {
        addAnimation(frames, frameDuration);
    }

    public void addAnimation(Array<TextureRegion> frames, float duration) {
        Animation<TextureRegion> animation = new Animation<>(duration, frames);
        animations.add(animation);
    }

    public void setCurrentAnimation(int index) {
        if (index >= 0 && index < animations.size) {
            currentAnimationIndex = index;
            stateTime = 0;
        }
    }

    public int getCurrentAnimation() {
        return currentAnimationIndex;
    }

    public TextureRegion getCurrentFrame(float deltaTime) {
        if (!isPlaying || animations.size == 0) return null;
        
        stateTime += deltaTime;
        Animation<TextureRegion> currentAnimation = animations.get(currentAnimationIndex);
        return currentAnimation.getKeyFrame(stateTime, looping);
    }

    public void update(float deltaTime) {
        if (isPlaying) {
            stateTime += deltaTime;
        }
    }

    public void play() {
        isPlaying = true;
    }

    public void pause() {
        isPlaying = false;
    }

    public void stop() {
        isPlaying = false;
        stateTime = 0;
    }

    public void setFrameDuration(float duration) {
        this.frameDuration = duration;
        for (Animation<TextureRegion> animation : animations) {
            animation.setFrameDuration(duration);
        }
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isAnimationFinished() {
        if (animations.size == 0) return true;
        return animations.get(currentAnimationIndex).isAnimationFinished(stateTime);
    }

    public void dispose() {
        animations.clear();
    }
}