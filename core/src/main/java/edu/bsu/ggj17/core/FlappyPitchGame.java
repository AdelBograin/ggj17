package edu.bsu.ggj17.core;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import playn.core.Platform;
import playn.scene.Pointer;
import playn.scene.SceneGame;
import react.Slot;
import react.Value;
import tripleplay.game.ScreenStack;

import javax.sound.sampled.*;

public class FlappyPitchGame extends SceneGame {

    public final Value<Mixer> mixer = Value.create(AudioSystem.getMixer(AudioSystem.getMixerInfo()[0]));
    public final Value<Float> pitch = Value.create(0f);
    private AudioDispatcher dispatcher;

    public FlappyPitchGame(final Platform plat) {
        super(plat, 33); // update our "simulation" 33ms (30 times per second)
        handleMixerChanges();
        mixerChanged(mixer.get());
        ScreenStack screenStack = new ScreenStack(this, rootLayer);
        screenStack.push(new MenuScreen(this, screenStack));
        new Pointer(plat, rootLayer, true);
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
        plat.log().debug("Mixer changed to " + mixer);
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
                        double timeStamp = audioEvent.getTimeStamp();
                        float pitch = pitchDetectionResult.getPitch();
                        float probability = pitchDetectionResult.getProbability();
                        double rms = audioEvent.getRMS() * 100;
                        String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )\n", timeStamp, pitch, probability, rms);
                        plat.log().debug(message);
                        FlappyPitchGame.this.pitch.update(pitch);
                    } else {
                        FlappyPitchGame.this.pitch.update(null);
                    }
                }
            }));
            new Thread(dispatcher, "Audio dispatcher for " + mixer.getMixerInfo().getName()).start();
            System.out.println("Thread started");
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        }
    }

    private void stopPreviousDispatcherAsNecessary() {
        if (dispatcher!=null) {
            dispatcher.stop();
        }
    }

}
