package si.model;
//障碍物
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bunker implements Hittable {
    private static final int BRICK_SCALE = 5;
    private ArrayList<Brick> bricks;
    private Rectangle2D hitBox;
    private int x, y;

    public List<Rectangle2D> getBricks() {
        List<Rectangle2D> brickShapes = new ArrayList<>();
        for(Brick b: bricks){
            brickShapes.add(b.hitBox);
        }
        return brickShapes;
    }

    private class Brick implements Hittable {
        private boolean destroyed;
        private Rectangle2D hitBox;

        public Brick(int x, int y) {
            destroyed = false;
            hitBox = new Rectangle2D(x, y, BRICK_SCALE, BRICK_SCALE);
        }

        public boolean isAlive() {
            return destroyed;
        }

        public int getPoints() {
            return 0;
        }

        public boolean isPlayer() {
            return false;
        }

        public boolean isHit(Bullet b) {
            boolean hit = hitBox.intersects(b.getHitBox());
            if (hit) {
                destroyed = true;
            }
            return hit;
        }
        public Rectangle2D getHitBox() {
            return new Rectangle2D(hitBox.getMinX(), hitBox.getMinY(), hitBox.getWidth(), hitBox.getHeight());
        }
    }


    public Bunker(int x1, int y1) {
        this.x = x1;
        this.y = y1;
        hitBox = new Rectangle2D(x,y, 10*BRICK_SCALE, 7 * BRICK_SCALE);
        bricks = new ArrayList<Brick>();

        for (int i = 0; i < 6; i++) { // Top row
            bricks.add(new Brick(x + 2 * BRICK_SCALE + i * BRICK_SCALE, y + 0 * BRICK_SCALE));
        }
        for (int i = 0; i < 8; i++) { // Second row
            bricks.add(new Brick(x + 1 * BRICK_SCALE + i * BRICK_SCALE, y + 1 * BRICK_SCALE));
        }
        for (int i = 0; i < 10; i++) { // Third row
            bricks.add(new Brick(x + 0 * BRICK_SCALE + i * BRICK_SCALE, y + 2 * BRICK_SCALE));
        }
        for (int i = 0; i < 4; i++) { // fourth row
            bricks.add(new Brick(x + 0 * BRICK_SCALE + i * BRICK_SCALE, y + 3 * BRICK_SCALE));
            bricks.add(new Brick(x + 6 * BRICK_SCALE + i * BRICK_SCALE, y + 3 * BRICK_SCALE));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                bricks.add(new Brick(x + 0 * BRICK_SCALE + i * BRICK_SCALE, y + (j + 3) * BRICK_SCALE));
                bricks.add(new Brick(x + 7 * BRICK_SCALE + i * BRICK_SCALE, y + (j + 3) * BRICK_SCALE));
            }
        }
    }
    public boolean isHit(Bullet b) {
        boolean hit = false;
        Brick remove = null;
        for (Brick br : bricks) {
            if (br.isHit(b)) {
                remove = br;
                hit = true;
            }
        }
        bricks.remove(remove);
        return hit;
    }

    public boolean isAlive() {
        return bricks.size() == 0;
    }

    public int getPoints() {
        return 0;
    }

    public boolean isPlayer() {
        return false;
    }

    public Rectangle2D getHitBox() {
        return hitBox;
    }

}
