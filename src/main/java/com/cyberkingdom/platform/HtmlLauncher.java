package com.cyberkingdom.platform;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.cyberkingdom.gameengine.GameEngine;

public class HtmlLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        // Разрешение 800x600 для веб-версии
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(800, 600);
        config.antialiasing = true;
        return config;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new GameEngine();
    }
}

//package com.cyberkingdom.entities;
//
//import com.cyberkingdom.rendering.AnimationComponent;
//
//public class GameEntity {
//    // ... существующие поля
//
//    private AnimationComponent animationComponent;
//
//    public GameEntity(String id, EntityType type) {
//        // ... существующая инициализация
//        this.animationComponent = new AnimationComponent();
//    }
//
//    public AnimationComponent getAnimationComponent() {
//        return animationComponent;
//    }
//
//    public void update(float deltaTime) {
//        // ... существующая логика
//        animationComponent.update(deltaTime);
//    }
//}