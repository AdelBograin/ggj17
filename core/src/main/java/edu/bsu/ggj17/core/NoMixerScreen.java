/*
 * Copyright 2017 Paul Gestwicki, Alex Hoffman, and Darby Siscoe
 *
 * This file is part of flappypitch
 *
 * flappypitch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * flappypitch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with flappypitch.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.bsu.ggj17.core;

import com.google.common.base.Preconditions;
import playn.core.Game;
import tripleplay.game.ScreenStack;
import tripleplay.ui.Label;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

public class NoMixerScreen extends ScreenStack.UIScreen {

    private final Game game;

    public NoMixerScreen(Game game) {
        super(game.plat);
        this.game = Preconditions.checkNotNull(game);

        iface.createRoot(AxisLayout.vertical().gap(6), GameStyles.newSheet(game.plat.graphics()), layer)
                .setSize(size())
                .add(new Label("Sorry, we cannot find any viable audio hardware to run the game.")
                                .addStyles(Style.COLOR.is(Colors.WHITE), Style.TEXT_WRAP.on),
                        new Label("You lose. Game over.")
                                .addStyles(Style.COLOR.is(Colors.WHITE), Style.TEXT_WRAP.on));
    }

    @Override
    public Game game() {
        return game;
    }
}
