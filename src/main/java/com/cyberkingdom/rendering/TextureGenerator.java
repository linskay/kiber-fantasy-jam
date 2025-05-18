package com.cyberkingdom.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;

public class TextureGenerator {
    private static final Color CYBER_BLUE = new Color(0.3f, 0.8f, 1f, 1f);
    private static final Color DARK_BLUE = new Color(0.1f, 0.1f, 0.2f, 1f);
    private static final Color GRID_COLOR = new Color(0.2f, 0.2f, 0.3f, 1f);
    
    public static Texture generateCyberBackground() {
        Pixmap pixmap = new Pixmap(800, 600, Pixmap.Format.RGBA8888);
        
        // Заполняем фон
        pixmap.setColor(DARK_BLUE);
        pixmap.fill();
        
        // Добавляем сетку
        pixmap.setColor(GRID_COLOR);
        drawGrid(pixmap, 40);
        
        // Добавляем случайные символы
        pixmap.setColor(CYBER_BLUE);
        drawRandomSymbols(pixmap, 50);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    private static void drawGrid(Pixmap pixmap, int spacing) {
        for (int x = 0; x < pixmap.getWidth(); x += spacing) {
            pixmap.drawLine(x, 0, x, pixmap.getHeight());
        }
        for (int y = 0; y < pixmap.getHeight(); y += spacing) {
            pixmap.drawLine(0, y, pixmap.getWidth(), y);
        }
    }
    
    private static void drawRandomSymbols(Pixmap pixmap, int count) {
        for (int i = 0; i < count; i++) {
            int x = MathUtils.random(0, pixmap.getWidth() - 10);
            int y = MathUtils.random(0, pixmap.getHeight() - 10);
            drawSymbol(pixmap, x, y);
        }
    }
    
    private static void drawSymbol(Pixmap pixmap, int x, int y) {
        // Рисуем символ как линию с случайным наклоном
        float angle = MathUtils.random(0, 360);
        int length = MathUtils.random(5, 15);
        int endX = x + (int)(Math.cos(Math.toRadians(angle)) * length);
        int endY = y + (int)(Math.sin(Math.toRadians(angle)) * length);
        pixmap.drawLine(x, y, endX, endY);
    }
    
    public static Texture generateCodeSymbol() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        
        // Заполняем фон прозрачным
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        
        // Рисуем символ
        pixmap.setColor(CYBER_BLUE);
        drawCodeSymbol(pixmap);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    private static void drawCodeSymbol(Pixmap pixmap) {
        int centerX = pixmap.getWidth() / 2;
        int centerY = pixmap.getHeight() / 2;
        int size = Math.min(pixmap.getWidth(), pixmap.getHeight()) / 2 - 4;
        
        // Рисуем X
        pixmap.drawLine(centerX - size, centerY - size, centerX + size, centerY + size);
        pixmap.drawLine(centerX + size, centerY - size, centerX - size, centerY + size);
    }
    
    public static Texture generateWifiKey() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        
        // Заполняем фон прозрачным
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        
        // Рисуем WiFi символ
        pixmap.setColor(CYBER_BLUE);
        drawWifiSymbol(pixmap);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    private static void drawWifiSymbol(Pixmap pixmap) {
        int centerX = pixmap.getWidth() / 2;
        int centerY = pixmap.getHeight() / 2;
        int radius = Math.min(pixmap.getWidth(), pixmap.getHeight()) / 2 - 4;
        
        // Рисуем дуги WiFi
        for (int i = 0; i < 3; i++) {
            int currentRadius = radius - (i * 4);
            // Рисуем полукруг с помощью линий
            for (int angle = 0; angle < 180; angle += 5) {
                float rad = (float) Math.toRadians(angle);
                int x1 = centerX + (int)(Math.cos(rad) * currentRadius);
                int y1 = centerY + (int)(Math.sin(rad) * currentRadius);
                int x2 = centerX + (int)(Math.cos(Math.toRadians(angle + 5)) * currentRadius);
                int y2 = centerY + (int)(Math.sin(Math.toRadians(angle + 5)) * currentRadius);
                pixmap.drawLine(x1, y1, x2, y2);
            }
        }
        
        // Рисуем точку в центре
        pixmap.fillCircle(centerX, centerY, 2);
    }
    
    public static Texture generateBoss() {
        Pixmap pixmap = new Pixmap(48, 48, Pixmap.Format.RGBA8888);
        
        // Заполняем фон прозрачным
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        
        // Рисуем босса
        drawBoss(pixmap);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    private static void drawBoss(Pixmap pixmap) {
        int centerX = pixmap.getWidth() / 2;
        int centerY = pixmap.getHeight() / 2;
        int radius = Math.min(pixmap.getWidth(), pixmap.getHeight()) / 2 - 4;
        
        // Тело босса
        pixmap.setColor(1f, 0.2f, 0.2f, 1f);
        pixmap.fillCircle(centerX, centerY, radius);
        
        // Глаза
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fillCircle(centerX - radius/3, centerY - radius/4, radius/6);
        pixmap.fillCircle(centerX + radius/3, centerY - radius/4, radius/6);
        
        // Рот
        pixmap.setColor(1f, 0.2f, 0.2f, 1f);
        pixmap.drawLine(centerX - radius/2, centerY + radius/3, 
                       centerX + radius/2, centerY + radius/3);
    }
    
    public static Texture generateGlitchEffect(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        
        // Заполняем фон прозрачным
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        
        // Создаем эффект глитча
        for (int i = 0; i < 10; i++) {
            int y = MathUtils.random(0, height);
            int glitchHeight = MathUtils.random(1, 5);
            pixmap.setColor(CYBER_BLUE);
            pixmap.fillRectangle(0, y, width, glitchHeight);
        }
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
} 