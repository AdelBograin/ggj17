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

import com.google.common.base.Preconditions;
import playn.core.Game;
import react.Slot;
import react.UnitSignal;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

public class AboutScreen extends ScreenStack.UIScreen {

    public final UnitSignal done = new UnitSignal();

    private final FlappyPitchGame game;

    public AboutScreen(FlappyPitchGame game) {
        super(game.plat);
        Styles textStyles = Styles.make(Style.COLOR.is(Colors.WHITE), Style.TEXT_WRAP.on);
        this.game = Preconditions.checkNotNull(game);
        iface.createRoot(AxisLayout.vertical(), GameStyles.newSheet(game.plat.graphics()), layer)
                .setSize(size())
                .addStyles(Style.BACKGROUND.is(Background.blank().inset(size().width()*0.13f, 0)))
                .add(new Label("ABOUT").addStyles(textStyles),
                        new Label("Sing or whistle a comfortable tone as the game begins, and then raise or lower "
                                + "your pitch to move Fermata up and down. Hit a rest for a brief pause to take " +
                                "a breath, but watch out for end bars!")
                                .addStyles(textStyles),
                        new Label("Use the Configuration Menu to select different audio input mixers if the default "
                                + "is not working.")
                                .addStyles(textStyles),
                        new Shim(6, 6),
                        new Label("Created by Paul Gestwicki, Alex Hoffman, and Darby Siscoe for Global Game Jam 2017")
                                .addStyles(textStyles),
                        new Shim(12, 12),
                        new Button("Back").onClick(new Slot<Button>() {
                            @Override
                            public void onEmit(Button button) {
                                done.emit();
                            }
                        }));
    }

    @Override
    public Game game() {
        return game;
    }
}
