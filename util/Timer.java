package com.ucd.frogger.util;

import com.ucd.frogger.model.GameConfig;

/**
 * 计时器工具类：管理游戏倒计时，提供独立的计时逻辑
 */
public class Timer {
    // 核心状态：剩余时间（秒，整数避免浮点误差）、是否正在计时
    private int remainingTime;
    private boolean isRunning;
    // 时间戳：用于精准控制“每秒减1秒”（避免多线程或帧循环导致的计时偏差）
    private long lastTickTimestamp;

    // 构造方法：初始化计时器（默认使用GameConfig的初始倒计时）
    public Timer() {
        this.remainingTime = GameConfig.INIT_TIMER_SEC;
        this.isRunning = false;
        this.lastTickTimestamp = System.currentTimeMillis();
    }

    /**
     * 计时 ticks（每秒调用1次，减少1秒）
     * 注意：需外部控制调用频率（如GameManager每秒调用1次）
     */
    public void tick() {
        if (!isRunning) {
            return; // 未运行时不计时
        }

        // 计算当前时间与上次tick的间隔（确保每秒仅减1秒，避免帧循环导致的高频调用）
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTickTimestamp >= 1000) { // 间隔≥1000ms（1秒）才减时
            remainingTime = Math.max(0, remainingTime - 1); // 剩余时间不低于0
            lastTickTimestamp = currentTime; // 更新上次tick时间戳
        }
    }

    /**
     * 增加倒计时（青蛙死亡/到家时调用，加30秒）
     * @param seconds 增加的秒数（游戏规则固定为30秒）
     */
    public void addTime(int seconds) {
        if (seconds <= 0) {
            return; // 无效秒数不处理
        }
        remainingTime += seconds;
    }

    /**
     * 重置计时器（重新开始游戏时调用，恢复初始状态）
     */
    public void reset() {
        this.remainingTime = GameConfig.INIT_TIMER_SEC;
        this.lastTickTimestamp = System.currentTimeMillis();
        this.isRunning = false; // 重置后默认停止，需外部调用setRunning(true)启动
    }

    /**
     * 格式化剩余时间为“MM:SS”字符串（如“00:25”“01:03”）
     * @return 格式化后的时间字符串
     */
    public String getRemainingTimeStr() {
        int minutes = remainingTime / 60; // 分钟数
        int seconds = remainingTime % 60; // 剩余秒数
        // 补0确保两位数（如1秒→“01”，5分钟→“05”）
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Getter与Setter（仅暴露必要状态，避免外部随意修改）
    public int getRemainingTime() {
        return remainingTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
        // 启动时更新时间戳，避免暂停后恢复计时的偏差
        if (running) {
            lastTickTimestamp = System.currentTimeMillis();
        }
    }
}