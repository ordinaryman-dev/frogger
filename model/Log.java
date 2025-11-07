package com.ucd.frogger.model;

import com.ucd.frogger.view.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 原木类：河流中的载体（向右移动），青蛙可站在上面避免落水
 */
public class Log extends GameObject implements Movable {
    // 原木特有属性
    private int lane; // 所属河流车道编号（从下往上递增）
    private double speed; // 移动速度（像素/秒，固定为正=右）
    private int capacity; // 承载能力（最多容纳几只青蛙，默认1）

    // 构造方法：初始化原木位置、车道、速度
    public Log(int lane, double speed, double spawnX) {
        // 车道Y坐标：中间安全区上方 + 车道编号×车道高度
        double laneY = (GameConfig.INIT_RIVER_LANES + 1) * GameConfig.LANE_HEIGHT + lane * GameConfig.LANE_HEIGHT;
        super(
                spawnX,
                laneY,
                GameConfig.LOG_WIDTH,
                GameConfig.LOG_HEIGHT
        );
        this.lane = lane;
        this.speed = speed > 0 ? speed : -speed; // 强制向右（speed为正）
        this.capacity = 1; // 原木默认仅能承载1只青蛙
    }

    // ------------------------------ Movable接口实现 ------------------------------
    private double nextX;
    private double nextY;

    @Override
    public void calculateMove(double deltaTime) {
        double moveDistance = speed * deltaTime;
        nextX = getX() + moveDistance;
        nextY = getY();

        // 循环移动：向右超出屏幕后从左侧重新进入
        if (nextX > GameConfig.SCREEN_WIDTH) {
            nextX = -GameConfig.LOG_WIDTH;
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

        // 绘制原木（棕色矩形，带纹理效果）
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());

        // 绘制纹理（浅棕色横线，增加辨识度）
        gc.setFill(Color.PERU);
        double stripeHeight = 5.0;
        for (double y = getY() + 5; y < getY() + getHeight() - 5; y += stripeHeight + 3) {
            gc.fillRect(getX() + 5, y, getWidth() - 10, stripeHeight);
        }
    }

    // 原木优先级5（低于青蛙）
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