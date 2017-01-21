package edu.bsu.ggj17.core;

import playn.core.Game;
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

        Root root = iface.createRoot(AxisLayout.vertical(), SimpleStyles.newSheet(game.plat.graphics()), layer)
                .setSize(game.plat.graphics().viewSize);

        root.add(new Label("AN UNNAMED GAME").setStyles(Style.COLOR.is(Colors.WHITE)),
                new Button("Play").onClick(new UnitSlot() {
                    @Override
                    public void onEmit() {
                        screenStack.push(new GameScreen(game), new FadeTransition().duration(500));
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
