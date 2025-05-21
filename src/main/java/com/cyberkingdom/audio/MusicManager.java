package com.cyberkingdom.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicManager {
    private static Music currentMusic = null;
    private static String currentPath = null;

    public static void play(String path, boolean looping) {
        if (currentPath != null && currentPath.equals(path) && currentMusic != null && currentMusic.isPlaying()) {
            // Уже играет нужная музыка — ничего не делаем
            return;
        }
        stop();
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentMusic.setLooping(looping);
        currentMusic.play();
        currentPath = path;
    }

    public static void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
            currentPath = null;
        }
    }

    public static void pause() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public static void resume() {
        if (currentMusic != null) {
            currentMusic.play();
        }
    }
} 