package com.tujuego.serpiente.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.tujuego.serpiente.MainGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        int cellSize = 32;       // 👈 Usa el mismo valor que en MainGame
        int gridWidth = 20;
        int gridHeight = 20;

        int width = gridWidth * cellSize;
        int height = gridHeight * cellSize + 100; // 👈 Ahora tienes espacio suficiente para el HUD

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Juego Serpiente");
        config.setWindowedMode(width, height);
        config.setResizable(false);

        new Lwjgl3Application(new MainGame(), config);
    }
}
