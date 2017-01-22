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

package edu.bsu.ggj17.java;

import org.apache.commons.cli.*;
import playn.java.JavaPlatform;
import playn.java.LWJGLPlatform;

import edu.bsu.ggj17.core.FlappyPitchGame;

import java.awt.*;

public class FlappyPitchGameJava {

    private static final String DEBUG_FLAG = "debug";

    private static boolean debugMode = false;

    public static void main(String[] args) {
        processCommandLine(args);
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
        config.width = 800;
        config.height = 600;
        LWJGLPlatform plat = new LWJGLPlatform(config);
        new FlappyPitchGame(plat, debugMode);
        registerFonts(plat);
        plat.start();
    }

    private static void processCommandLine(String[] args) {
        try {
            Options options = new Options();
            options.addOption(DEBUG_FLAG, "Enable debug mode");
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("debug")) {
                debugMode = true;
            }
        } catch (ParseException pe) {
            System.out.println("Unrecognized command line");
            pe.printStackTrace();
        }
    }

    private static void registerFonts(JavaPlatform plat) {
        try {
            Font bold = plat.assets().getFont("fonts/Alegreya-Bold.ttf");
            plat.graphics().registerFont("Bold", bold);
            Font regular = plat.assets().getFont("fonts/Alegreya-Regular.ttf");
            plat.graphics().registerFont("Regular", regular);
        } catch (Exception e) {
            plat.log().error("Cannot register fonts. " + e);
        }
    }
}
