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
    private final AboutScreen aboutScreen;

    public MenuScreen(final FlappyPitchGame game, final ScreenStack screenStack) {
        super(game.plat);
        this.game = game;

        configScreen = new ConfigScreen(game);
        configScreen.done.connect(new UnitSlot() {
            @Override
            public void onEmit() {
                screenStack.popTo(MenuScreen.this, screenStack.slide().right());
            }
        });
        aboutScreen = new AboutScreen(game);
        aboutScreen.done.connect(new UnitSlot() {
            @Override
            public void onEmit() {
                screenStack.popTo(MenuScreen.this, screenStack.slide().right());
            }
        });

        Group buttonGroup = new Group(AxisLayout.vertical().gap(12).offEqualize())
                .add(new Button("Play").onClick(new UnitSlot() {
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
                                        } else {
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
                        new Button("About").onClick(new UnitSlot() {
                            @Override
                            public void onEmit() {
                                screenStack.push(aboutScreen, screenStack.slide().left());
                            }
                        }),
                        new Button("Configure").onClick(new UnitSlot() {
                            @Override
                            public void onEmit() {
                                screenStack.push(configScreen, screenStack.slide().left());
                            }
                        }));


        Root root = iface.createRoot(AxisLayout.vertical().offEqualize(), GameStyles.newSheet(game.plat.graphics()), layer)
                .setSize(game.plat.graphics().viewSize);

        Styles bigFontStyles = Styles.make(Style.COLOR.is(Colors.WHITE), Style.FONT.is(new Font("Bold", 40)));
        Styles smallFontStyles = Styles.make(Style.COLOR.is(Colors.WHITE), Style.FONT.is(new Font("Bold", 28)));

        root.add(new Label("Fermata").setStyles(bigFontStyles),
                new Label("or").setStyles(smallFontStyles),
                new Label("Don't Breathe").setStyles(bigFontStyles),
                new Shim(24, 24),
                buttonGroup);
    }

    @Override
    public Game game() {
        return game;
    }
}
