package edu.bsu.ggj17.core;

import playn.core.Canvas;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import tripleplay.util.Colors;

public class PlayerSprite {

    public final ImageLayer layer;

    public PlayerSprite(FlappyPitchGame game) {
        Canvas canvas = game.plat.graphics().createCanvas(30,30);
        canvas.setFillColor(Colors.BLACK);
        canvas.fillRect(0,0,30,30);
        layer = new ImageLayer(canvas.image);
        layer.setOrigin(Layer.Origin.CENTER);
    }

}
