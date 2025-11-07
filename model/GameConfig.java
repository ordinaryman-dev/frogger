package com.ucd.frogger.model;

/**
 * 游戏配置类：存储所有静态常量，统一参数管理
 */
public class GameConfig {
    // 屏幕尺寸（与UI保持一致）
    public static final double SCREEN_WIDTH = 800.0;
    public static final double SCREEN_HEIGHT = 600.0;

    // 车道与跳跃参数（青蛙跳跃距离=车道高度）
    public static final double LANE_HEIGHT = 40.0; // 每个车道高度
    public static final double FROG_JUMP_DISTANCE = LANE_HEIGHT; // 青蛙每次跳跃距离

    // 初始游戏状态
    public static final int INIT_LIVES = 3; // 初始生命数
    public static final int INIT_TIMER_SEC = 30; // 初始倒计时（秒）

    // 初始车道数量（难度升级时会增加）
    public static final int INIT_ROAD_LANES = 3; // 初始马路车道数
    public static final int INIT_RIVER_LANES = 2; // 初始河流车道数

    // 基础速度（像素/秒，难度升级时会提升）
    public static final double BASE_VEHICLE_SPEED = 150.0; // 汽车基础速度
    public static final double BASE_LOG_SPEED = 120.0; // 原木基础速度
    public static final double BASE_TURTLE_SPEED = 100.0; // 乌龟基础速度

    // 对象尺寸（统一设计，避免适配问题）
    public static final double FROG_WIDTH = 30.0;
    public static final double FROG_HEIGHT = 30.0;
    public static final double VEHICLE_WIDTH = 60.0;
    public static final double VEHICLE_HEIGHT = LANE_HEIGHT - 5; // 略小于车道高度，避免重叠
    public static final double LOG_WIDTH = 100.0;
    public static final double LOG_HEIGHT = LANE_HEIGHT - 5;
    public static final double TURTLE_WIDTH = 50.0;
    public static final double TURTLE_HEIGHT = LANE_HEIGHT - 5;
    public static final double HOME_RADIUS = 20.0; // Home为圆形，半径20px
}