// Movable接口（规范移动行为）
package com.ucd.frogger.model;
public interface Movable {
    void calculateMove(double deltaTime); // 计算单帧移动后的坐标
    void updatePosition(); // 更新对象实际坐标（计算后调用）
}