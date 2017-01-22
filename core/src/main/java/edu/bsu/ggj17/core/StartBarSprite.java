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

import playn.core.Assets;

public class StartBarSprite extends AbstractObstacleSprite {

    public StartBarSprite(Assets assets) {
        super(assets.getImage("images/start-bar.png"));
    }

    @Override
    public boolean isDeadly() {
        return false;
    }


}
