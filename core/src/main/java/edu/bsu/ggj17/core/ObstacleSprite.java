package edu.bsu.ggj17.core;

import playn.core.Assets;
import playn.scene.ImageLayer;
import playn.scene.Layer;

public class ObstacleSprite {

    private static final float SPEED_PPS = 150;
    public final Layer layer;

    public ObstacleSprite(Assets assets) {
        this.layer = new ImageLayer(assets.getImage("images/quarter-rest.png"));
        this.layer.setOrigin(Layer.Origin.CENTER);
    }

    public void update(int deltaMS) {
        final float oldX = layer.tx();
        final float newX = oldX - SPEED_PPS * deltaMS / 1000;
        this.layer.setTx(newX);
    }

}
