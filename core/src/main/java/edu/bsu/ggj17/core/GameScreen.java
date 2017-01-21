package edu.bsu.ggj17.core;

import playn.core.Game;
import playn.core.Image;
import playn.scene.ImageLayer;
import react.Connection;
import react.Slot;
import react.UnitSlot;
import react.Value;
import tripleplay.game.ScreenStack;
import tripleplay.ui.Group;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.util.BoxPoint;

public class GameScreen extends ScreenStack.UIScreen {

    private final FlappyPitchGame game;
    private final Value<Float> startingPitch = Value.create(null);
    private float topPitch;
    private float bottomPitch;

    public GameScreen(final FlappyPitchGame game) {
        super(game.plat);
        this.game = game;

        Image bgImage = game.plat.assets().getImage("images/bg.png");
        ImageLayer bgLayer = new ImageLayer(bgImage);
        bgLayer.setSize(game.plat.graphics().viewSize);
        layer.add(bgLayer);

        final PlayerSprite sprite = new PlayerSprite(game);
        layer.addAt(sprite.layer(), 50, game.plat.graphics().viewSize.height() / 2);
        game.pitch.connect(new Slot<Float>() {
            private boolean shouldAnimateOnNextChange = true;

            @Override
            public void onEmit(Float newPitch) {
                if (newPitch!=null) {
                    if (startingPitch.get()==null) {
                        startingPitch.update(newPitch);
                        topPitch = newPitch + 100;
                        bottomPitch = newPitch - 100;
                    }
                    if (shouldAnimateOnNextChange) {
                        shouldAnimateOnNextChange = false;

                        float screenHeight = game.plat.graphics().viewSize.height();
                        float pitchWidth = topPitch - bottomPitch;
                        float pitchPercent = clamp((newPitch - bottomPitch) / pitchWidth);
                        float newY = screenHeight - (screenHeight * pitchPercent);

                        iface.anim.tweenY(sprite.layer())
                                .to(newY)
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
            }

            private float clamp(float value) {
                return Math.max(0, Math.min(1.0f, value));
            }
        });

        makeStartingMessageHUD();
        makeDebugHUD();
    }

    private void makeStartingMessageHUD() {
        final Root startingMessageHud = iface.createRoot(new AbsoluteLayout(), SimpleStyles.newSheet(game.plat.graphics()), layer)
                .setSize(game.plat.graphics().viewSize);
        startingMessageHud.add(new Label("Make a sound to begin").setConstraint(AbsoluteLayout.uniform(BoxPoint.CENTER)));
        startingPitch.connect(new UnitSlot() {
            @Override
            public void onEmit() {
                startingMessageHud.setVisible(false);
            }
        });
    }

    private void makeDebugHUD() {
        Root root = iface.createRoot(new AbsoluteLayout(), SimpleStyles.newSheet(game.plat.graphics()), layer)
                .setSize(game.plat.graphics().viewSize);
        root.add(new Group(AxisLayout.vertical())
                .add(new PitchLabel(),
                        new StartingPitchLabel())
                .setConstraint(AbsoluteLayout.uniform(BoxPoint.TR)));
    }


    @Override
    public Game game() {
        return game;
    }

    private final class PitchLabel extends Label {
        PitchLabel() {
            game.pitch.connect(new Slot<Float>() {
                @Override
                public void onEmit(Float aFloat) {
                    if (aFloat!=null) {
                        setText("Pitch: " + String.format("%2f", aFloat));
                    } else {
                        setText("--");
                    }
                }
            });
        }
    }

    private final class StartingPitchLabel extends Label {
        private Connection connection;
        StartingPitchLabel(){
            connection = game.pitch.connect(new Slot<Float>() {
                @Override
                public void onEmit(Float aFloat) {
                    if (aFloat!=null) {
                        setText("Starting: " + String.format("%2f", aFloat));
                        connection.close();
                    }
                }
            });
        }
    }
}
