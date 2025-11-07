package com.ucd.frogger.model;

import com.ucd.frogger.view.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Home类：青蛙的目标区域，每个Home仅能容纳1只青蛙
 */
public class Home extends GameObject {
    // Home特有属性
    private boolean isOccupied; // 是否被青蛙占用

    // 构造方法：初始化Home位置（屏幕顶部，均匀分布）
    public Home(int index) {
        // 5个Home均匀分布在顶部安全区（X坐标：index×间隔 + 偏移）
        double spacing = GameConfig.SCREEN_WIDTH / (GameConfig.INIT_ROAD_LANES + 2); // 间隔
        double homeX = spacing * (index + 1) - GameConfig.HOME_RADIUS; // X坐标（圆形左上角）
        double homeY = GameConfig.LANE_HEIGHT / 2 - GameConfig.HOME_RADIUS; // Y坐标（居中）
        // Home为圆形，宽度/高度=2×半径
        super(
                homeX,
                homeY,
                GameConfig.HOME_RADIUS * 2,
                GameConfig.HOME_RADIUS * 2
        );
        this.isOccupied = false;
    }

    /**
     * 圆形碰撞检测：判断青蛙是否完全进入Home（比矩形碰撞更精准）
     * @param frog 青蛙对象
     * @return true=青蛙进入Home，false=未进入
     */
    public boolean isFrogIn(Frog frog) {
        if (!isActive() || isOccupied || !frog.isActive()) {
            return false;
        }

        // 计算青蛙中心与Home中心的距离
        double distance = Math.sqrt(
                Math.pow(frog.getCenterX() - getCenterX(), 2)
                        + Math.pow(frog.getCenterY() - getCenterY(), 2)
        );

        // 距离 < Home半径 + 青蛙半径的一半 → 视为完全进入
        double frogRadius = Math.min(frog.getWidth(), frog.getHeight()) / 2;
        return distance < GameConfig.HOME_RADIUS + frogRadius / 2;
    }

    /**
     * 占用Home（青蛙进入时调用）
     */
    public void occupy() {
        this.isOccupied = true;
    }

    /**
     * 重置Home（关卡重置时调用）
     */
    public void reset() {
        this.isOccupied = false;
        setActive(true);
    }

    // ------------------------------ Drawable接口实现 ------------------------------
    @Override
    public void draw(GraphicsContext gc) {
        if (!isActive()) {
            return;
        }

        // 绘制Home：未占用=黄色，已占用=绿色
        gc.setFill(isOccupied ? Color.GREEN : Color.YELLOW);
        gc.fillOval(getX(), getY(), getWidth(), getHeight());

        // 绘制Home边框（黑色，增加辨识度）
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(getX(), getY(), getWidth(), getHeight());
    }

    // Home优先级8（高于载体/汽车，低于青蛙）
    @Override
    public int getDrawPriority() {
        return 8;
    }

    // Getter
    public boolean isOccupied() {
        return isOccupied;
    }
}