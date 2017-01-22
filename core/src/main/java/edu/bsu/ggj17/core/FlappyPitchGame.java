/*
 * Copyright 2017 Paul Gestwicki, Alex Hoffman, and Darby Siscoe
 *
 * This file is part of Fermata
 *
 * Fermata is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fermata is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fermata.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.bsu.ggj17.core;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import playn.core.Clock;
import playn.core.Key;
import playn.core.Keyboard;
import playn.core.Platform;
import playn.scene.Pointer;
import playn.scene.SceneGame;
import react.Slot;
import react.Value;
import tripleplay.game.ScreenStack;

import javax.sound.sampled.*;

public class FlappyPitchGame extends SceneGame {

    public final Value<Mixer> mixer = Value.create(findFirstWorkingMixer());
    public final Value<Float> pitch = Value.create(0f);
    public final boolean debugMode;
    private AudioDispatcher dispatcher;
    private final ScreenStack screenStack;

    public boolean immortal = false;

    public FlappyPitchGame(final Platform plat, boolean debugMode) {
        super(plat, 33); // update our "simulation" 33ms (30 times per second)
        this.debugMode = debugMode;
        handleMixerChanges();
        mixerChanged(mixer.get());
        screenStack = new ScreenStack(this, rootLayer);
        if (mixer.get() == null) {
            screenStack.push(new NoMixerScreen(this));
        } else {
            screenStack.push(new MenuScreen(this, screenStack));
            new Pointer(plat, rootLayer, true);

            if (debugMode) {
                plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
                    @Override
                    public void onEmit(Keyboard.KeyEvent keyEvent) {
                        if (keyEvent.key.equals(Key.I)) {
                            immortal = true;
                        }
                    }
                });
            }
        }
    }

    private Mixer findFirstWorkingMixer() {
        Mixer result = null;
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            try {
                result = AudioSystem.getMixer(info);
                break;
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    private void handleMixerChanges() {
        mixer.connect(new Slot<Mixer>() {
            @Override
            public void onEmit(Mixer mixer) {
                mixerChanged(mixer);
            }
        });
    }

    private void mixerChanged(Mixer mixer) {
        if (mixer==null) {
            plat.log().warn("Mixer is null, no mixer here");
            return;
        }
        stopPreviousDispatcherAsNecessary();
        float sampleRate = 44100;
        int bufferSize = 1024;
        int overlap = 0;

        try {
            final AudioFormat format = new AudioFormat(sampleRate, 16, 1, true,
                    true);
            final DataLine.Info dataLineInfo = new DataLine.Info(
                    TargetDataLine.class, format);
            TargetDataLine line;
            line = (TargetDataLine) mixer.getLine(dataLineInfo);
            line.open(format, bufferSize);
            line.start();
            final AudioInputStream stream = new AudioInputStream(line);

            JVMAudioInputStream audioStream = new JVMAudioInputStream(stream);
            dispatcher = new AudioDispatcher(audioStream, bufferSize,
                    overlap);
            dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.YIN, sampleRate, bufferSize, new PitchDetectionHandler() {
                @Override
                public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                    if (pitchDetectionResult.getPitch() != -1) {
                        float pitch = pitchDetectionResult.getPitch();
                        FlappyPitchGame.this.pitch.update(pitch);
                    } else {
                        FlappyPitchGame.this.pitch.update(null);
                    }
                }
            }));
            new Thread(dispatcher, "Audio dispatcher for " + mixer.getMixerInfo().getName()).start();
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        }
    }

    private void stopPreviousDispatcherAsNecessary() {
        if (dispatcher != null) {
            dispatcher.stop();
        }
    }

    @Override
    public void update(Clock clock) {
        super.update(clock);
        ScreenStack.Screen screen = screenStack.top();
        if (screen instanceof Updateable) {
            ((Updateable) screen).update(clock.dt);
        }
    }
}
