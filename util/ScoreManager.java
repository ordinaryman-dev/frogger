package com.ucd.frogger.util;

import com.ucd.frogger.model.Frog;

/**
 * 得分管理器：按游戏规则计算和管理得分，过滤重复得分
 */
public class ScoreManager {
    // 核心状态：当前总得分
    private int currentScore;
    // 辅助状态：记录青蛙已得分的最远车道（避免“后退再前进”重复加分）
    private int lastScoredLane;

    // 构造方法：初始化得分状态
    public ScoreManager() {
        this.currentScore = 0;
        this.lastScoredLane = Integer.MAX_VALUE; // 初始设为最大，确保首次跳跃可得分
    }

    /**
     * 基础加分（通用接口，如青蛙到家+500分、通关+10000分）
     * @param points 加分值（需符合游戏规则：150/500/10000等）
     */
    public void addScore(int points) {
        if (points <= 0) {
            return; // 无效分数不处理
        }
        currentScore += points;
    }

    /**
     * 青蛙跳跃得分（按“首次到达新车道”加分，150分/次）
     * @param frog 青蛙对象（获取当前所在车道和历史最远车道）
     */
    public void addLaneScore(Frog frog) {
        int currentLane = (int) (frog.getY() / GameConfig.LANE_HEIGHT); // 当前车道（Y越小，车道编号越小）
        // 规则：仅当当前车道 < 历史最远车道（即更靠近顶部），且未在安全区时加分
        if (currentLane < lastScoredLane && !frog.isInSafeArea()) {
            addScore(150); // 首次到达新车道+150分
            lastScoredLane = currentLane; // 更新历史最远车道，避免重复加分
        }
    }

    /**
     * 计算关卡通关得分（10000分 + 剩余时间×250分）
     * @param remainingTime 通关时的剩余秒数（从Timer获取）
     * @return 通关总加分（含基础10000分）
     */
    public int calculateLevelClearScore(int remainingTime) {
        int baseClearScore = 10000; // 通关基础分
        int timeBonus = remainingTime * 250; // 时间奖励分（每秒250分）
        int totalClearScore = baseClearScore + timeBonus;
        // 直接加分并返回（方便外部记录日志）
        addScore(totalClearScore);
        return totalClearScore;
    }

    /**
     * 重置得分（重新开始游戏时调用）
     */
    public void reset() {
        this.currentScore = 0;
        this.lastScoredLane = Integer.MAX_VALUE; // 重置车道得分记录
    }

    // Getter（仅暴露当前得分，不允许外部修改）
    public int getCurrentScore() {
        return currentScore;
    }
}