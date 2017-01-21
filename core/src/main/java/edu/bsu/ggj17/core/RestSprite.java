package edu.bsu.ggj17.core;

import playn.core.Assets;
import playn.scene.ImageLayer;
import playn.scene.Layer;

public class RestSprite extends AbstractObstacleSprite {

    public RestSprite(Assets assets) {
        super(assets.getImage("images/quarter-rest.png"));
    }

    @Override
    public boolean isDeadly() {
        return false;
    }
}
