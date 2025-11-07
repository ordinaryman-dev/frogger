package com.ucd.frogger.model;

/**
 * 方向枚举：统一管理青蛙跳跃、对象移动的方向
 */
public enum Direction {
    UP,    // 上（向屏幕顶部，Y坐标减小）
    DOWN,  // 下（向屏幕底部，Y坐标增大）
    LEFT,  // 左（向屏幕左侧，X坐标减小）
    RIGHT  // 右（向屏幕右侧，X坐标增大）
}