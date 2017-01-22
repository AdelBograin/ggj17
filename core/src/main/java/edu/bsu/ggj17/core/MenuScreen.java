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
        Font smaller = new Font("Bold", 22);

        root.add(new Label("Fermata").setStyles(Style.COLOR.is(Colors.WHITE), Style.FONT.is(font)),
                new Label("or").setStyles(Style.COLOR.is(Colors.WHITE), Style.FONT.is(smaller)),
                new Label("Don't Breathe").setStyles(Style.COLOR.is(Colors.WHITE), Style.FONT.is(font)),
                new Shim(12,12),
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
