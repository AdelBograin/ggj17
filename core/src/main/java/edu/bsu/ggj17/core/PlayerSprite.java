package edu.bsu.ggj17.core;

import playn.scene.ImageLayer;

public class PlayerSprite {

    public final ImageLayer layer;

    public PlayerSprite(FlappyPitchGame game) {
        layer = new ImageLayer(game.plat.assets().getImage("images/fermata.png"));
        layer.setOrigin(15,15);
    }

}
