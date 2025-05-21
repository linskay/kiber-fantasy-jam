package com.cyberkingdom.platform;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.cyberkingdom.gameengine.GameEngine;

public class DesktopLauncher {
    public static void main(String[] arg) {
        try {
            System.out.println("Starting Cyber Kingdom...");
            
            Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
            config.setTitle("Cyber Kingdom");
            config.setWindowedMode(1200, 800);
            config.setResizable(false);
            config.useVsync(true);
            config.setForegroundFPS(60);
            config.setWindowIcon("assets/iconka.png");
            
            System.out.println("Configuration created");
            
            GameEngine game = new GameEngine();
            System.out.println("GameEngine instance created");
            
            new Lwjgl3Application(game, config);
            System.out.println("Application started");
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}