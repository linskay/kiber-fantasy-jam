package com.cyberkingdom;

import com.badlogic.gdx.Game;
import com.cyberkingdom.screens.GameScreen;

public class GameMain extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}