package com.cyberkingdom.utils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class TiledMapLoader {
    public static TiledMap loadMap(String path) {
        return new TmxMapLoader().load(path);
    }
}