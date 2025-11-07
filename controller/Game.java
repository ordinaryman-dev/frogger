package com.ucd.frogger.controller;

public interface Game {
    /**
     * 单帧游戏逻辑处理（关键方法）
     * 1. 计算所有对象的移动（0.016秒内的位移）
     * 2. 检测碰撞（青蛙与障碍物、载体等）
     * 3. 更新游戏状态（得分、生命、关卡）
     */
    void updateGame();

    /**
     * 检查游戏是否结束（所有生命耗尽）
     * @return true=游戏结束，false=继续
     */
    boolean isGameOver();

    /**
     * 检查当前关卡是否通关（所有Home被青蛙占用）
     * @return true=通关，false=未通关
     */
    boolean isLevelCleared();
}