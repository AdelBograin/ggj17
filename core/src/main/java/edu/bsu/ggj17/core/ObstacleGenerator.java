package edu.bsu.ggj17.core;

import playn.core.Platform;
import playn.scene.GroupLayer;
import react.Signal;

import java.util.Random;

public class ObstacleGenerator implements Updateable {

    private static final int MILLIS_BETWEEN_OBSTACLES = 1000;
    private static final Random RANDOM = new Random();

    public final Signal<AbstractObstacleSprite> onGenerate = Signal.create();

    private int millisUntilNextSpawn = MILLIS_BETWEEN_OBSTACLES;
    private final GroupLayer target;
    private final Platform plat;
    private final float maxY;
    private final float minY;

    public ObstacleGenerator(Platform plat, GroupLayer target) {
        this.target = target;
        this.plat = plat;
        this.maxY = plat.graphics().viewSize.height() - 25;
        this.minY = 25;
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
        AbstractObstacleSprite restSprite =
                (RANDOM.nextFloat() < 0.5f) ? new EndBarSprite(plat.assets()) : new RestSprite(plat.assets());
        float y = RANDOM.nextFloat() * plat.graphics().viewSize.height();
        y = Math.min(maxY, Math.max(minY, y));
        target.addAt(restSprite.layer, plat.graphics().viewSize.width(), y);
        onGenerate.emit(restSprite);
    }

}
