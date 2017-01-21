package edu.bsu.ggj17.core;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import playn.core.Game;
import playn.core.Image;
import playn.scene.ImageLayer;
import react.Slot;
import react.Value;
import tripleplay.game.ScreenStack;

import javax.sound.sampled.*;

public class GameScreen extends ScreenStack.UIScreen {

    private final FlappyPitchGame game;
    private Mixer mixer;
    private Value<Float> pitch = Value.create(0f);

    public GameScreen(FlappyPitchGame game) {
        super(game.plat);
        this.game = game;
        // create and add background image layer
        Image bgImage = game.plat.assets().getImage("images/bg.png");
        ImageLayer bgLayer = new ImageLayer(bgImage);
        // scale the background to fill the screen
        bgLayer.setSize(game.plat.graphics().viewSize);
        layer.add(bgLayer);

        final PlayerSprite sprite = new PlayerSprite(game);
        layer.addAt(sprite.layer(), 0, game.plat.graphics().viewSize.height() / 2);
        pitch.connect(new Slot<Float>() {
            private boolean shouldAnimateOnNextChange = true;

            @Override
            public void onEmit(Float newPitch) {
                if (shouldAnimateOnNextChange) {
                    shouldAnimateOnNextChange = false;
                    iface.anim.tweenY(sprite.layer())
                            .to(newPitch)
                            .in(200)
                            .then()
                            .action(new Runnable() {
                                @Override
                                public void run() {
                                    shouldAnimateOnNextChange = true;
                                }
                            });
                }
            }
        });

        initializeAudio();
    }

    private void initializeAudio() {
        final Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (final Mixer.Info mixerInfo : mixers) {
            if (AudioSystem.getMixer(mixerInfo).getTargetLineInfo().length != 0) {
                this.mixer = AudioSystem.getMixer(mixerInfo);
                break;
            }
        }
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
            AudioDispatcher dispatcher = new AudioDispatcher(audioStream, bufferSize,
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
                        game.plat.log().debug(message);
                        GameScreen.this.pitch.update(pitch);
                    }
                }
            }));
            new Thread(dispatcher, "Audio dispatching").start();
        } catch (LineUnavailableException lue) {
            throw new RuntimeException(lue);
        }
    }

    @Override
    public Game game() {
        return game;
    }


}
