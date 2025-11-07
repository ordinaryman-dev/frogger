package com.ucd.frogger.model;

import com.ucd.frogger.view.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 汽车类：马路中的障碍物，同车道同速同向移动
 */
public class Vehicle extends GameObject implements Movable {
    // 汽车特有属性
    private int lane; // 所属车道编号（马路车道从下往上递增）
    private double speed; // 移动速度（像素/秒，正=右，负=左）
    private Direction direction; // 移动方向（与speed符号对应）

    // 构造方法：初始化汽车位置、车道、速度
    public Vehicle(int lane, double speed, double spawnX) {
        // 车道Y坐标：底部安全区上方 + 车道编号×车道高度
        double laneY = GameConfig.SCREEN_HEIGHT - 2 * GameConfig.LANE_HEIGHT - lane * GameConfig.LANE_HEIGHT;
        super(
                spawnX, // 生成X坐标（外部控制，避免重叠）
                laneY,
                GameConfig.VEHICLE_WIDTH,
                GameConfig.VEHICLE_HEIGHT
        );
        this.lane = lane;
        this.speed = speed;
        this.direction = speed > 0 ? Direction.RIGHT : Direction.LEFT;
    }

    // ------------------------------ Movable接口实现 ------------------------------
    private double nextX; // 计算后的下一个X坐标
    private double nextY; // Y坐标不变（汽车仅水平移动）

    @Override
    public void calculateMove(double deltaTime) {
        // 计算单帧移动距离（速度×时间，deltaTime=0.016秒，每秒60帧）
        double moveDistance = speed * deltaTime;
        nextX = getX() + moveDistance;
        nextY = getY(); // 汽车仅水平移动，Y不变

        // 循环移动逻辑：超出屏幕后从另一侧重新进入
        if (direction == Direction.RIGHT && nextX > GameConfig.SCREEN_WIDTH) {
            nextX = -GameConfig.VEHICLE_WIDTH; // 从左侧重新进入
        } else if (direction == Direction.LEFT && nextX < -GameConfig.VEHICLE_WIDTH) {
            nextX = GameConfig.SCREEN_WIDTH; // 从右侧重新进入
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

        // 绘制汽车车身（红色矩形）
        gc.setFill(Color.RED);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());

        // 绘制汽车窗户（蓝色小矩形，增加辨识度）
        gc.setFill(Color.BLUE);
        double windowWidth = getWidth() / 3;
        double windowHeight = getHeight() / 2;
        // 前窗
        gc.fillRect(
                getX() + getWidth() - windowWidth - 5,
                getY() + 5,
                windowWidth,
                windowHeight
        );
        // 后窗
        gc.fillRect(
                getX() + 5,
                getY() + 5,
                windowWidth,
                windowHeight
        );
    }

    // 汽车优先级5（低于青蛙，避免遮挡玩家）
    @Override
    public int getDrawPriority() {
        return 5;
    }

    // Getter（汽车属性无需外部修改，仅提供读取）
    public int getLane() {
        return lane;
    }

    public double getSpeed() {
        return speed;
    }

    public Direction getDirection() {
        return direction;
    }
}