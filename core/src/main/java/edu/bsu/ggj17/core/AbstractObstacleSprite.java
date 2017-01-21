package edu.bsu.ggj17.core;

import playn.core.Image;
import playn.scene.ImageLayer;
import playn.scene.Layer;

public abstract class AbstractObstacleSprite {

    private static final float SPEED_PPS = 150;
    public final Layer layer;

    protected AbstractObstacleSprite(Image image) {
        this.layer = new ImageLayer(image);
        this.layer.setOrigin(Layer.Origin.CENTER);
    }

    public void update(int deltaMS) {
        final float oldX = layer.tx();
        final float newX = oldX - SPEED_PPS * deltaMS / 1000;
        this.layer.setTx(newX);
    }

    public abstract boolean isDeadly();
}
