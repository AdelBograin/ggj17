package edu.bsu.ggj17.core;

import playn.core.Assets;

public class EndBarSprite extends AbstractObstacleSprite {

 public EndBarSprite(Assets assets) {
     super(assets.getImage("images/repeat.png"));
 }

    @Override
    public boolean isDeadly() {
        return true;
    }
}
