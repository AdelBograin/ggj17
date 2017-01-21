package edu.bsu.ggj17.core;

import playn.core.Platform;
import playn.scene.Pointer;
import playn.scene.SceneGame;
import react.Slot;
import react.Value;
import tripleplay.game.ScreenStack;

import javax.sound.sampled.Mixer;

public class FlappyPitchGame extends SceneGame {

    public final Value<Mixer> mixer = Value.create(null);

    public FlappyPitchGame(final Platform plat) {
        super(plat, 33); // update our "simulation" 33ms (30 times per second)
        logMixerChanges();
        ScreenStack screenStack = new ScreenStack(this, rootLayer);
        screenStack.push(new MenuScreen(this, screenStack));
        new Pointer(plat, rootLayer, true);
    }

    private void logMixerChanges() {
        mixer.connect(new Slot<Mixer>() {
            @Override
            public void onEmit(Mixer mixer) {
                plat.log().debug("Mixer changed to " + mixer.getMixerInfo().getName());
            }
        });
    }

}
