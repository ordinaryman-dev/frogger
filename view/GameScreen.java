package com.ucd.frogger.view;

import com.ucd.frogger.model.GameConfig;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 游戏画布类：单一负责所有对象的绘制，符合"计算与图形分离"原则
 */
public class GameScreen {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final List<Drawable> drawableObjects; // 存储所有可绘制对象

    // 初始化画布（尺寸从GameConfig读取，确保统一）
    public GameScreen() {
        this.canvas = new Canvas(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
        this.drawableObjects = new ArrayList<>();
    }

    /**
     * 添加可绘制对象（自动按优先级排序）
     */
    public void addDrawable(Drawable obj) {
        drawableObjects.add(obj);
        // 按优先级降序排序（优先级高的后绘制，避免被遮挡）
        drawableObjects.sort(Comparator.comparingInt(Drawable::getDrawPriority).reversed());
    }

    /**
     * 移除可绘制对象（如死亡的青蛙）
     */
    public void removeDrawable(Drawable obj) {
        drawableObjects.remove(obj);
    }

    /**
     * 绘制所有元素（每帧调用一次）
     */
    public void drawAll() {
        clearScreen(); // 清空画布，避免残影
        drawBackground(); // 先绘制背景（优先级最低）
        // 绘制所有游戏对象
        for (Drawable obj : drawableObjects) {
            obj.draw(gc);
        }
    }

    /**
     * 绘制游戏背景（区分马路、河流、安全区）
     */
    private void drawBackground() {
        // 1. 底部安全区（青蛙初始位置，绿色）
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(0, GameConfig.SCREEN_HEIGHT - GameConfig.LANE_HEIGHT * 2,
                GameConfig.SCREEN_WIDTH, GameConfig.LANE_HEIGHT * 2);

        // 2. 马路（灰色，3条初始车道）
        gc.setFill(Color.GRAY);
        int roadTopY = GameConfig.SCREEN_HEIGHT - GameConfig.LANE_HEIGHT * (2 + GameConfig.ROAD_LANES);
        gc.fillRect(0, roadTopY,
                GameConfig.SCREEN_WIDTH, GameConfig.LANE_HEIGHT * GameConfig.ROAD_LANES);
        // 马路车道线（白色虚线）
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        for (int i = 1; i < GameConfig.ROAD_LANES; i++) {
            double laneY = roadTopY + i * GameConfig.LANE_HEIGHT;
            drawDashedLine(laneY);
        }

        // 3. 中间安全区（马路与河流之间，浅绿色）
        gc.setFill(Color.LIGHTGREEN);
        int middleSafeY = roadTopY - GameConfig.LANE_HEIGHT;
        gc.fillRect(0, middleSafeY,
                GameConfig.SCREEN_WIDTH, GameConfig.LANE_HEIGHT);

        // 4. 河流（蓝色，2条初始车道）
        gc.setFill(Color.SKYBLUE);
        int riverTopY = middleSafeY - GameConfig.LANE_HEIGHT * GameConfig.RIVER_LANES;
        gc.fillRect(0, riverTopY,
                GameConfig.SCREEN_WIDTH, GameConfig.LANE_HEIGHT * GameConfig.RIVER_LANES);
        // 河流车道线（白色虚线）
        for (int i = 1; i < GameConfig.RIVER_LANES; i++) {
            double laneY = riverTopY + i * GameConfig.LANE_HEIGHT;
            drawDashedLine(laneY);
        }

        // 5. 顶部安全区（Home所在区域，深绿色）
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0,
                GameConfig.SCREEN_WIDTH, riverTopY);
    }

    /**
     * 绘制虚线（用于车道线）
     */
    private void drawDashedLine(double y) {
        double dashLength = 20; // 虚线段长度
        double gapLength = 10;  // 虚线间隔
        for (double x = 0; x < GameConfig.SCREEN_WIDTH; x += dashLength + gapLength) {
            gc.strokeLine(x, y, x + dashLength, y);
        }
    }

    // Getter：供外部获取Canvas（如添加到Scene）
    public Canvas getCanvas() {
        return canvas;
    }

    // 清空画布
    private void clearScreen() {
        gc.clearRect(0, 0, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
    }
}