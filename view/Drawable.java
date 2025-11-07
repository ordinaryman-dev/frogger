// Drawable接口（规范绘制行为）
package com.ucd.frogger.view;
import javafx.scene.canvas.GraphicsContext;
public interface Drawable {
    void draw(GraphicsContext gc); // 绘制对象
    int getDrawPriority(); // 绘制优先级（0最低，10最高）
}