package edu.bsu.ggj17.java;

import playn.java.LWJGLPlatform;

import edu.bsu.ggj17.core.FlappyPitchGame;

public class FlappyPitchGameJava {

    public static void main(String[] args) {
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
        config.width = 800;
        config.height = 600;
        LWJGLPlatform plat = new LWJGLPlatform(config);
        new FlappyPitchGame(plat);
        plat.start();
    }
}
