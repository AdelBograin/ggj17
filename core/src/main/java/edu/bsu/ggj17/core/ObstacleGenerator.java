package edu.bsu.ggj17.core;

import playn.core.Graphics;
import playn.scene.GroupLayer;
import pythagoras.f.MathUtil;
import react.Signal;

import java.util.Random;

public class ObstacleGenerator implements Updateable {

    private static final int MILLIS_BETWEEN_OBSTACLES = 1000;
    private static final Random RANDOM = new Random();

    public final Signal<ObstacleSprite> onGenerate = Signal.create();

    private int millisUntilNextSpawn = MILLIS_BETWEEN_OBSTACLES;
    private final GroupLayer target;
    private final Graphics graphics;

    public ObstacleGenerator(Graphics graphics, GroupLayer target) {
        this.target = target;
        this.graphics= graphics;
    }

    @Override
    public void update(int deltaMS) {
        millisUntilNextSpawn -= deltaMS;
        if (millisUntilNextSpawn <=0) {
            spawnObstacle();
            millisUntilNextSpawn += MILLIS_BETWEEN_OBSTACLES;
        }
    }

    private void spawnObstacle() {
        ObstacleSprite obstacleSprite = new ObstacleSprite(graphics);
        float y = RANDOM.nextFloat() * graphics.viewSize.height();
        target.addAt(obstacleSprite.layer, graphics.viewSize.width(), y);
        onGenerate.emit(obstacleSprite);
    }

}
