package edu.bsu.ggj17.core;

import com.google.common.collect.Lists;
import playn.core.Game;
import playn.core.Image;
import playn.scene.ImageLayer;
import pythagoras.f.Rectangle;
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

import java.util.List;

public class GameScreen extends ScreenStack.UIScreen implements Updateable {

    private final FlappyPitchGame game;
    private final Value<Float> startingPitch = Value.create(null);
    private float topPitch;
    private float bottomPitch;
    private PlayerSprite playerSprite;

    private final List<ObstacleSprite> obstacles = Lists.newArrayList();

    public GameScreen(final FlappyPitchGame game) {
        super(game.plat);
        this.game = game;

        Image bgImage = game.plat.assets().getImage("images/bg.png");
        ImageLayer bgLayer = new ImageLayer(bgImage);
        bgLayer.setSize(game.plat.graphics().viewSize);
        layer.add(bgLayer);

        playerSprite = new PlayerSprite(game);
        layer.addAt(playerSprite.layer, 50, game.plat.graphics().viewSize.height() / 2);
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

                        iface.anim.tweenY(playerSprite.layer)
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

        for (int y=10; y<game.plat.graphics().viewSize.height(); y+= 95) {
            ObstacleSprite obstacleSprite = new ObstacleSprite(game.plat.graphics());
            obstacles.add(obstacleSprite);
            layer.addAt(obstacleSprite.layer, game.plat.graphics().viewSize.width(), y);
        }
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
    public void update(int deltaMS) {
        if (startingPitch.get() != null) {
            doUpdateBecauseTheGameHasStarted(deltaMS);
        }
    }

    private final Rectangle playerRect = new Rectangle();
    private final Rectangle otherRect = new Rectangle();
    private final List<ObstacleSprite> toRemove = Lists.newArrayList();

    private void doUpdateBecauseTheGameHasStarted(int deltaMS) {
        playerRect.setBounds(playerSprite.layer.tx(), playerSprite.layer.ty(), playerSprite.layer.width(),
                playerSprite.layer.height());
        for (ObstacleSprite obstacle : obstacles) {
            obstacle.update(deltaMS);
            otherRect.setBounds(obstacle.layer.tx(), obstacle.layer.ty(), obstacle.layer.width(),
                    obstacle.layer.height());
            if (playerRect.intersects(otherRect)){
                toRemove.add(obstacle);
            }
        }
        while (!toRemove.isEmpty()) {
            ObstacleSprite sprite = toRemove.remove(0);
            obstacles.remove(sprite);
            layer.remove(sprite.layer);
        }

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
