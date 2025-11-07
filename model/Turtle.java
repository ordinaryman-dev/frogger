package com.ucd.frogger.model;

import com.ucd.frogger.view.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 乌龟类：河流中的载体（向左移动），青蛙可站在上面避免落水
 */
public class Turtle extends GameObject implements Movable {
    // 乌龟特有属性
    private int lane; // 所属河流车道编号
    private double speed; // 移动速度（像素/秒，固定为负=左）
    private int capacity; // 承载能力（默认1）

    // 构造方法：初始化乌龟位置、车道、速度
    public Turtle(int lane, double speed, double spawnX) {
        double laneY = (GameConfig.INIT_RIVER_LANES + 1) * GameConfig.LANE_HEIGHT + lane * GameConfig.LANE_HEIGHT;
        super(
                spawnX,
                laneY,
                GameConfig.TURTLE_WIDTH,
                GameConfig.TURTLE_HEIGHT
        );
        this.lane = lane;
        this.speed = speed < 0 ? speed : -speed; // 强制向左（speed为负）
        this.capacity = 1;
    }

    // ------------------------------ Movable接口实现 ------------------------------
    private double nextX;
    private double nextY;

    @Override
    public void calculateMove(double deltaTime) {
        double moveDistance = speed * deltaTime;
        nextX = getX() + moveDistance;
        nextY = getY();

        // 循环移动：向左超出屏幕后从右侧重新进入
        if (nextX < -GameConfig.TURTLE_WIDTH) {
            nextX = GameConfig.SCREEN_WIDTH;
        }
    }

    @Override
    public void updatePosition() {
        setX(nextX);
        setY(nextY);
    }

    // ------------------------------ Drawable接口实现 ------------------------------
    @Override
    public void draw(GraphicsContext gc) {
        if (!isActive()) {
            return;
        }

        // 绘制乌龟身体（绿色椭圆形，而非矩形）
        gc.setFill(Color.OLIVEDRAB);
        gc.fillOval(getX(), getY(), getWidth(), getHeight());

        // 绘制乌龟头部（小椭圆形，位于左侧）
        gc.setFill(Color.DARKOLIVEGREEN);
        double headWidth = getWidth() / 4;
        double headHeight = getHeight() / 2;
        gc.fillOval(
                getX() - headWidth / 2,
                getY() + (getHeight() - headHeight) / 2,
                headWidth,
                headHeight
        );

        // 绘制乌龟腿（4个小矩形）
        double legSize = 8.0;
        // 前左腿
        gc.fillRect(getX() + 10, getY() - legSize / 2, legSize, legSize);
        // 前右腿
        gc.fillRect(getX() + getWidth() - 20, getY() - legSize / 2, legSize, legSize);
        // 后左腿
        gc.fillRect(getX() + 10, getY() + getHeight() - legSize / 2, legSize, legSize);
        // 后右腿
        gc.fillRect(getX() + getWidth() - 20, getY() + getHeight() - legSize / 2, legSize, legSize);
    }

    // 乌龟优先级5（低于青蛙）
    @Override
    public int getDrawPriority() {
        return 5;
    }

    // Getter
    public int getLane() {
        return lane;
    }

    public double getSpeed() {
        return speed;
    }

    public int getCapacity() {
        return capacity;
    }
}