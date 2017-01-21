package edu.bsu.ggj17.java;

import playn.java.JavaPlatform;
import playn.java.LWJGLPlatform;

import edu.bsu.ggj17.core.FlappyPitchGame;

import java.awt.*;

public class FlappyPitchGameJava {

    public static void main(String[] args) {
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
        config.width = 800;
        config.height = 600;
        LWJGLPlatform plat = new LWJGLPlatform(config);
        new FlappyPitchGame(plat);
        registerFonts(plat);
        plat.start();
    }

    private static void registerFonts(JavaPlatform plat) {
        try {
            Font bold = plat.assets().getFont("fonts/Alegreya-Bold.ttf");
            plat.graphics().registerFont("Bold", bold);
            Font regular = plat.assets().getFont("fonts/Alegreya-Regular.ttf");
            plat.graphics().registerFont("Regular", regular);
        } catch (Exception e) {
            plat.log().error("Cannot register fonts. " + e);
        }
    }
}
