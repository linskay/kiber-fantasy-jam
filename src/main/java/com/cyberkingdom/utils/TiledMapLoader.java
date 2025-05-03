package com.cyberkingdom.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class TiledMapLoader {
    public static TiledMap loadMap(String path) {
        try {
            return new TmxMapLoader().load("assets/" + path);
        } catch (Exception e) {
            Gdx.app.error("TiledMapLoader", "Error loading map: " + path, e);
            throw new RuntimeException("Failed to load map: " + path);
        }
    }
}