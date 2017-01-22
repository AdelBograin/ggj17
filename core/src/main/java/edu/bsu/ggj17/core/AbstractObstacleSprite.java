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

import playn.core.Image;
import playn.scene.ImageLayer;
import playn.scene.Layer;

public abstract class AbstractObstacleSprite {

    private static final float SPEED_PPS = 150;
    public final Layer layer;

    protected AbstractObstacleSprite(Image image) {
        this.layer = new ImageLayer(image);
        this.layer.setOrigin(Layer.Origin.CENTER);
    }

    public void update(int deltaMS) {
        final float oldX = layer.tx();
        final float newX = oldX - SPEED_PPS * deltaMS / 1000;
        this.layer.setTx(newX);
    }

    public abstract boolean isDeadly();
}
