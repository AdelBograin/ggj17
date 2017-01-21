package edu.bsu.ggj17.core;

import playn.core.Game;
import playn.core.Image;
import playn.scene.ImageLayer;
import react.Slot;
import tripleplay.game.ScreenStack;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.BorderLayout;
import tripleplay.ui.util.BoxPoint;

public class GameScreen extends ScreenStack.UIScreen {

    private final FlappyPitchGame game;

    public GameScreen(FlappyPitchGame game) {
        super(game.plat);
        this.game = game;

        Image bgImage = game.plat.assets().getImage("images/bg.png");
        ImageLayer bgLayer = new ImageLayer(bgImage);
        bgLayer.setSize(game.plat.graphics().viewSize);
        layer.add(bgLayer);

        final PlayerSprite sprite = new PlayerSprite(game);
        layer.addAt(sprite.layer(), 0, game.plat.graphics().viewSize.height() / 2);
        game.pitch.connect(new Slot<Float>() {
            private boolean shouldAnimateOnNextChange = true;

            @Override
            public void onEmit(Float newPitch) {
                if (newPitch!=null) {
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
            }
        });

        makeDebugHUD();
    }

    private void makeDebugHUD() {
        Root root = iface.createRoot(new AbsoluteLayout(), SimpleStyles.newSheet(game.plat.graphics()), layer)
                .setSize(game.plat.graphics().viewSize);
        root.add(new PitchLabel().setConstraint(AbsoluteLayout.uniform(BoxPoint.TR)));
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
}
