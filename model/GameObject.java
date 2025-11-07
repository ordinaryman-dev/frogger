package com.ucd.frogger.model;

import com.ucd.frogger.view.Drawable;

/**
 * 游戏对象基类：封装所有游戏对象的共性属性与方法
 */
public abstract class GameObject implements Drawable {
    // 核心属性（私有，通过getter/setter访问，确保封装）
    private double x; // 对象左上角X坐标（像素）
    private double y; // 对象左上角Y坐标（像素）
    private double width; // 对象宽度
    private double height; // 对象高度
    private boolean isActive; // 是否激活（false=不参与碰撞/绘制）

    // 构造方法：初始化核心属性
    public GameObject(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isActive = true; // 默认激活
    }

    /**
     * 矩形碰撞检测（基础版，适用于大部分矩形对象）
     * @param other 另一个游戏对象
     * @return true=碰撞，false=未碰撞
     */
    public boolean isColliding(GameObject other) {
        // 两个对象都激活才可能碰撞
        if (!this.isActive || !other.isActive) {
            return false;
        }
        // 矩形碰撞公式：A的右边界 > B的左边界，且A的左边界 < B的右边界；Y轴同理
        return this.x + this.width > other.x
                && this.x < other.x + other.width
                && this.y + this.height > other.y
                && this.y < other.y + other.height;
    }

    // Getter与Setter（仅暴露必要的修改权限，避免属性被随意修改）
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // 计算对象中心坐标（用于圆形碰撞、精准定位等场景）
    public double getCenterX() {
        return x + width / 2;
    }

    public double getCenterY() {
        return y + height / 2;
    }
}