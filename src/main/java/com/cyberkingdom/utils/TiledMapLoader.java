package com.cyberkingdom.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class TiledMapLoader {
    public static TiledMap loadMap(String path) {
        String fullPath = "assets/" + path;
        System.out.println("Попытка загрузки карты: " + fullPath);
        try {
            if (Gdx.files.internal(fullPath).exists()) {
                TiledMap map = new TmxMapLoader().load(fullPath);
                if (map != null) {
                    System.out.println("Карта успешно загружена: " + fullPath);
                    return map;
                }
            } else {
                System.err.println("Файл карты не найден: " + fullPath);
            }
        } catch (Exception e) {
            System.err.println("Ошибка загрузки карты: " + fullPath + ", ошибка: " + e.getMessage());
        }
        System.err.println("Возвращается пустая карта как заглушка");
        return new TiledMap();
    }
}