package com.ucd.frogger.model;

import com.ucd.frogger.view.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * 青蛙类：玩家控制的核心对象，实现跳跃、碰撞响应等逻辑
 */
public class Frog extends GameObject implements Movable {
    // 青蛙特有属性
    private int lives; // 剩余生命数
    private int maxLaneReached; // 已到达的最远车道编号（从下往上递增，底部为0）
    private boolean onCarrier; // 是否在载体上（Log/Turtle）
    private GameObject currentCarrier; // 当前所在的载体（null=不在载体上）

    // 构造方法：初始化青蛙位置（屏幕底部中间）和状态
    public Frog() {
        // 初始位置：X居中（屏幕宽度/2 - 青蛙宽度/2），Y在底部安全区（离底部一个车道高度）
        super(
                (GameConfig.SCREEN_WIDTH - GameConfig.FROG_WIDTH) / 2,
                GameConfig.SCREEN_HEIGHT - GameConfig.LANE_HEIGHT - GameConfig.FROG_HEIGHT,
                GameConfig.FROG_WIDTH,
                GameConfig.FROG_HEIGHT
        );
        this.lives = GameConfig.INIT_LIVES;
        this.maxLaneReached = 0; // 初始车道为底部安全区（编号0）
        this.onCarrier = false;
        this.currentCarrier = null;
    }

    /**
     * 青蛙跳跃逻辑（响应方向键）
     * @param dir 跳跃方向
     */
    public void jump(Direction dir) {
        // 跳跃距离固定为车道高度，根据方向计算新坐标
        double newX = getX();
        double newY = getY();
        switch (dir) {
            case UP:
                newY -= GameConfig.FROG_JUMP_DISTANCE;
                // 向上跳跃时，更新最远车道（车道编号=Y坐标/车道高度，向下为正）
                int newLane = (int) (newY / GameConfig.LANE_HEIGHT);
                if (newLane < maxLaneReached) { // 车道编号越小，位置越靠上
                    maxLaneReached = newLane;
                }
                break;
            case DOWN:
                newY += GameConfig.FROG_JUMP_DISTANCE;
                break;
            case LEFT:
                newX -= GameConfig.FROG_JUMP_DISTANCE;
                break;
            case RIGHT:
                newX += GameConfig.FROG_JUMP_DISTANCE;
                break;
        }

        // 边界检测：确保青蛙不跳出屏幕
        if (isWithinScreen(newX, newY)) {
            setX(newX);
            setY(newY);
        }

        // 跳跃后离开载体（仅向上/下/左/右主动跳跃时）
        if (onCarrier) {
            leaveCarrier();
        }
    }

    /**
     * 检查新坐标是否在屏幕内（避免青蛙出界）
     */
    private boolean isWithinScreen(double x, double y) {
        return x >= 0
                && x + GameConfig.FROG_WIDTH <= GameConfig.SCREEN_WIDTH
                && y >= 0
                && y + GameConfig.FROG_HEIGHT <= GameConfig.SCREEN_HEIGHT;
    }

    /**
     * 青蛙重置：生命减少后回到初始位置（保留最远车道，避免重复得分）
     */
    public void reset() {
        // 重置位置到初始状态
        setX((GameConfig.SCREEN_WIDTH - GameConfig.FROG_WIDTH) / 2);
        setY(GameConfig.SCREEN_HEIGHT - GameConfig.LANE_HEIGHT - GameConfig.FROG_HEIGHT);
        // 重置载体关联
        leaveCarrier();
        // 生命减少（但不低于0）
        if (lives > 0) {
            lives--;
        }
    }

    /**
     * 登上载体（Log/Turtle）
     */
    public void enterCarrier(GameObject carrier) {
        this.onCarrier = true;
        this.currentCarrier = carrier;
    }

    /**
     * 离开载体（跳跃或载体移动出界时）
     */
    public void leaveCarrier() {
        this.onCarrier = false;
        this.currentCarrier = null;
    }

    /**
     * 检查是否在安全区（底部安全区、中间安全区、顶部Home区）
     */
    public boolean isInSafeArea() {
        double y = getY();
        double topSafeY = GameConfig.INIT_RIVER_LANES * GameConfig.LANE_HEIGHT; // 顶部Home区Y上限
        double middleSafeY = (GameConfig.INIT_RIVER_LANES + 1) * GameConfig.LANE_HEIGHT; // 中间安全区Y
        double bottomSafeY = GameConfig.SCREEN_HEIGHT - 2 * GameConfig.LANE_HEIGHT; // 底部安全区Y下限

        // 顶部Home区（Y < topSafeY）、中间安全区（Y在middleSafeY附近）、底部安全区（Y > bottomSafeY）
        return y < topSafeY
                || (y > middleSafeY - 5 && y < middleSafeY + 5)
                || y > bottomSafeY;
    }

    // ------------------------------ Movable接口实现 ------------------------------
    // 青蛙主动移动仅通过jump()，calculateMove用于载体带动时的被动移动
    private double deltaX; // 被动移动的X偏移量（载体带动时）
    private double deltaY; // 被动移动的Y偏移量（暂未使用）

    @Override
    public void calculateMove(double deltaTime) {
        // 若在载体上，同步载体的移动偏移量（载体的deltaX即青蛙的deltaX）
        if (onCarrier && currentCarrier instanceof Movable) {
            Movable carrier = (Movable) currentCarrier;
            // 先计算载体的移动（实际载体的updatePosition已调用，此处直接取载体的位置变化）
            this.deltaX = currentCarrier.getX() - (getX() - carrier.getX());
        } else {
            deltaX = 0;
            deltaY = 0;
        }
    }

    @Override
    public void updatePosition() {
        // 应用被动移动偏移量
        setX(getX() + deltaX);
        setY(getY() + deltaY);

        // 被动移动出界检测：若被载体带出屏幕，标记为非激活（触发死亡）
        if (getX() < -GameConfig.FROG_WIDTH || getX() > GameConfig.SCREEN_WIDTH) {
            setActive(false);
        }
    }

    // ------------------------------ Drawable接口实现 ------------------------------
    @Override
    public void draw(GraphicsContext gc) {
        if (!isActive()) {
            return; // 非激活状态不绘制
        }

        // 绘制青蛙身体（绿色矩形）
        gc.setFill(Color.GREEN);
        gc.fillRect(getX(), getY(), getWidth(), getHeight());

        // 绘制青蛙眼睛（黑色圆形，位于头部两侧）
        gc.setFill(Color.BLACK);
        double eyeRadius = 3.0;
        // 左眼（左上角）
        gc.fillOval(
                getX() + 5,
                getY() + 5,
                eyeRadius * 2,
                eyeRadius * 2
        );
        // 右眼（右上角）
        gc.fillOval(
                getX() + getWidth() - 5 - eyeRadius * 2,
                getY() + 5,
                eyeRadius * 2,
                eyeRadius * 2
        );
    }

    // 青蛙优先级10（最高，确保不被其他对象遮挡）
    @Override
    public int getDrawPriority() {
        return 10;
    }

    // Getter与Setter（仅暴露必要权限）
    public int getLives() {
        return lives;
    }

    public int getMaxLaneReached() {
        return maxLaneReached;
    }

    public boolean isOnCarrier() {
        return onCarrier;
    }

    public GameObject getCurrentCarrier() {
        return currentCarrier;
    }
}