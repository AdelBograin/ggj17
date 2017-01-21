package edu.bsu.ggj17.core;

import playn.core.Canvas;
import playn.core.Graphics;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import tripleplay.util.Colors;

public class ObstacleSprite {

    private static final float SPEED_PPS = 150;
    public final Layer layer;

    public ObstacleSprite(Graphics graphics) {
        Canvas canvas = graphics.createCanvas(50,50);
        canvas.setFillColor(Colors.BLUE);
        canvas.fillRect(0,0,50,50);
        this.layer = new ImageLayer(canvas.image);
        this.layer.setOrigin(Layer.Origin.CENTER);
    }

    public void update(int deltaMS) {
        final float oldX = layer.tx();
        final float newX = oldX - SPEED_PPS * deltaMS / 1000;
        this.layer.setTx(newX);
    }

}
