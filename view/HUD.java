package com.ucd.frogger.view;

import com.ucd.frogger.model.Frog;
import com.ucd.frogger.model.GameConfig;
import com.ucd.frogger.util.ScoreManager;
import com.ucd.frogger.util.Timer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * 实时信息显示类（HUD）：显示得分、剩余生命、倒计时
 */
public class HUD implements Drawable {
    private final ScoreManager scoreManager;
    private final Timer timer;
    private final Frog frog;

    // 初始化：关联得分管理器、计时器、青蛙（获取实时数据）
    public HUD(ScoreManager sm, Timer t, Frog f) {
        this.scoreManager = sm;
        this.timer = t;
        this.frog = f;
    }

    /**
     * 绘制HUD（顶部显示，白色文字，黑色阴影）
     */
    @Override
    public void draw(GraphicsContext gc) {
        Font font = Font.font("Arial", FontWeight.BOLD, 18);
        gc.setFont(font);
        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);

        // 1. 得分（左上角）
        drawTextWithShadow(gc, "Score: " + scoreManager.getCurrentScore(), 20, 30);

        // 2. 剩余生命（中间，用"F"表示青蛙生命）
        String livesText = "Lives: " + "F".repeat(frog.getLives()); // 3条生命显示"FFF"
        double livesX = GameConfig.SCREEN_WIDTH / 2 - gc.getFont().getSize() * livesText.length() / 2;
        drawTextWithShadow(gc, livesText, livesX, 30);

        // 3. 倒计时（右上角）
        gc.setTextAlign(javafx.scene.text.TextAlignment.RIGHT);
        drawTextWithShadow(gc, "Time: " + timer.getRemainingTimeStr(), GameConfig.SCREEN_WIDTH - 20, 30);
    }

    /**
     * 绘制带阴影的文字（提高可读性）
     */
    private void drawTextWithShadow(GraphicsContext gc, String text, double x, double y) {
        // 阴影（黑色，偏移2像素）
        gc.setFill(Color.BLACK);
        gc.fillText(text, x + 2, y + 2);
        // 正文（白色）
        gc.setFill(Color.WHITE);
        gc.fillText(text, x, y);
    }

    /**
     * HUD优先级：10（最高，确保不被其他对象遮挡）
     */
    @Override
    public int getDrawPriority() {
        return 10;
    }
}