package com.cyberkingdom.rendering;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationComponent {
    private Animation currentAnimation;
    private TextureRegion staticFrame;

    public void setCurrentAnimation(Animation animation) {
        this.currentAnimation = animation;
        this.staticFrame = null;
    }

    public void setStaticFrame(TextureRegion frame) {
        this.staticFrame = frame;
        this.currentAnimation = null;
    }

    public void update(float deltaTime) {
        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
        }
    }

    public TextureRegion getCurrentFrame() {
        if (currentAnimation != null) {
            return currentAnimation.getCurrentFrame();
        }
        return staticFrame;
    }
}