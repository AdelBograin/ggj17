package edu.bsu.ggj17.core;

import playn.core.Font;
import playn.core.Game;
import react.Slot;
import react.UnitSlot;
import tripleplay.game.ScreenStack;
import tripleplay.game.trans.FadeTransition;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

public class MenuScreen extends ScreenStack.UIScreen {

    private final FlappyPitchGame game;
    private final ConfigScreen configScreen;

    public MenuScreen(final FlappyPitchGame game, final ScreenStack screenStack) {
        super(game.plat);
        this.game = game;

        this.configScreen = new ConfigScreen(game);
        configScreen.done.connect(new UnitSlot() {
            @Override
            public void onEmit() {
                screenStack.popTo(MenuScreen.this, screenStack.slide().right());
            }
        });

        Root root = iface.createRoot(AxisLayout.vertical(), GameStyles.newSheet(game.plat.graphics()), layer)
                .setSize(game.plat.graphics().viewSize);

        Font font = new Font("Bold", 40);

        root.add(new Label("Fermata, or, Don't Breathe").setStyles(Style.COLOR.is(Colors.WHITE), Style.FONT.is(font)),
                new Button("Play").onClick(new UnitSlot() {
                    @Override
                    public void onEmit() {
                        startNewGame(true);
                    }

                    private void startNewGame(boolean first) {
                        GameScreen gameScreen = new GameScreen(game);
                        gameScreen.done.connect(new Slot<EndOption>() {
                            @Override
                            public void onEmit(EndOption endOption) {
                                if (endOption == EndOption.PLAY_AGAIN) {
                                    startNewGame(false);
                                }
                                else {
                                    screenStack.popTo(MenuScreen.this);
                                }
                            }
                        });
                        if (first) {
                            screenStack.push(gameScreen, new FadeTransition().duration(500));
                        } else {
                            screenStack.replace(gameScreen, new FadeTransition().duration(500));
                        }
                    }
                }),
                new Button("Configure").onClick(new UnitSlot() {
                    @Override
                    public void onEmit() {
                        screenStack.push(configScreen, screenStack.slide().left());
                    }
                })
        );
    }

    @Override
    public Game game() {
        return game;
    }
}
