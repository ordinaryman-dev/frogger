package si.model;

//关卡Level

import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Level {
    private Bunker[] bunkers; //掩体
    private Swarm swarm; //敌人集群
    private double startingSpeed; // 敌人初始移动速度
    private int rows; //敌人集群的行数
    private int cols; //敌人集群的列数
    private SpaceInvadersGame game; //游戏主类

    public Level(double ss, int row, int col, SpaceInvadersGame g) {
        game = g;
        startingSpeed = ss;
        rows = row;
        cols = col;
        reset();
    }

    public int getShipsRemaining() {
        return swarm.getShipsRemaining();
    }

    public int getBottomY() {
        return swarm.getBottomY();
    }

    public List<Hittable> getHittable() {
        List<Hittable> targets = new ArrayList<Hittable>();
        targets.addAll(swarm.getHittable());
        for (int i = 0; i < bunkers.length; i++) {
            targets.add(bunkers[i]);
        }
        return targets;
    }

    public List<Bullet> move() {
        swarm.move();
        List<EnemyShip> ships = swarm.getBottom();
        List<Bullet> eBullets = new ArrayList<Bullet>();
        for (EnemyShip s : ships) {
            Bullet b = s.fire();
            if (b != null) {
                eBullets.add(b);
            }
        }
        return eBullets;
    }

    public List<EnemyShip> getEnemyShips() {
        return swarm.getEnemyShips();
    }

    public List<Rectangle2D> getBunkers(){
        List<Rectangle2D> bricks = new ArrayList<>();
        for(Bunker b: bunkers){
            bricks.addAll(b.getBricks());
        }
        return bricks;
    }

    public void reset() {
        bunkers = new Bunker[4];
        for (int i = 0; i < bunkers.length; i++) {
            bunkers[i] = new Bunker((i + 1) * game.getScreenWidth() / 5, SpaceInvadersGame.BUNKER_TOP);
        }
        swarm = new Swarm(rows, cols, startingSpeed, 1, game);
    }
}
