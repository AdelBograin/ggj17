package edu.bsu.ggj17.core;

import playn.core.Platform;
import playn.scene.SceneGame;
import tripleplay.game.ScreenStack;

public class FlappyPitchGame extends SceneGame {


    public FlappyPitchGame(Platform plat) {
        super(plat, 33); // update our "simulation" 33ms (30 times per second)
        ScreenStack screenStack = new ScreenStack(this, rootLayer);
        screenStack.push(new GameScreen(this));
    }

}
