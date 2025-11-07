package com.ucd.frogger.controller;

import com.ucd.frogger.model.*;
import com.ucd.frogger.view.GameScreen;
import com.ucd.frogger.util.HighScoreUtil;
import com.ucd.frogger.util.ScoreManager;
import com.ucd.frogger.util.Timer;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 游戏管理器：实现Game接口，协调Model与View，控制游戏全流程
 */
public class GameManager implements Game {
    // ------------------------------ 依赖注入（通过setter或构造函数注入，避免硬编码）
    private GameScreen gameScreen; // 关联View层：游戏画布
    private Frog frog; // 关联Model层：玩家青蛙
    private ScoreManager scoreManager; // 关联Util层：得分管理
    private Timer timer; // 关联Util层：计时管理
    private InputHandler inputHandler; // 关联Controller层：输入处理

    // ------------------------------ 游戏状态属性
    private List<Vehicle> vehicles; // 马路汽车列表
    private List<Log> logs; // 河流原木列表
    private List<Turtle> turtles; // 河流乌龟列表
    private List<Home> homes; // 目标Home列表
    private int currentLevel; // 当前关卡（初始1）
    private AnimationTimer gameLoop; // 游戏循环（JavaFX提供，每秒60帧）
    private boolean isGamePaused; // 游戏是否暂停（默认false）

    // 构造函数：初始化画布与基础状态
    public GameManager(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.vehicles = new ArrayList<>();
        this.logs = new ArrayList<>();
        this.turtles = new ArrayList<>();
        this.homes = new ArrayList<>();
        this.currentLevel = 1;
        this.isGamePaused = false;
    }

    // 依赖注入：统一设置核心组件（避免多个构造函数）
    public void setDependencies(Frog frog, ScoreManager scoreManager, Timer timer, InputHandler inputHandler) {
        this.frog = frog;
        this.scoreManager = scoreManager;
        this.timer = timer;
        this.inputHandler = inputHandler;
        // 初始化第一关
        initLevel(currentLevel);
        // 初始化Home（5个，均匀分布）
        initHomes();
    }

    // ------------------------------ 关卡初始化：根据难度生成游戏对象
    /**
     * 初始化关卡：根据当前关卡调整车道数、速度、对象数量（难度升级核心逻辑）
     * @param level 当前关卡
     */
    public void initLevel(int level) {
        // 1. 清空上一关的对象（避免重叠）
        clearLevelObjects();

        // 2. 难度参数计算（每关速度提升10%，每2关增加1条车道）
        double speedMultiplier = 1.0 + (level - 1) * 0.1; // 速度系数（关卡越高越快）
        int roadLanes = GameConfig.INIT_ROAD_LANES + (level / 2); // 马路车道数（每2关+1）
        int riverLanes = GameConfig.INIT_RIVER_LANES + (level / 2); // 河流车道数（每2关+1）

        // 3. 生成马路汽车（同车道同速同向，左右方向交替）
        Random random = new Random();
        for (int lane = 0; lane < roadLanes; lane++) {
            // 车道Y坐标：底部安全区上方 + 车道编号×车道高度
            double laneY = GameConfig.SCREEN_HEIGHT - 2 * GameConfig.LANE_HEIGHT - lane * GameConfig.LANE_HEIGHT;
            // 方向：偶数车道向右，奇数车道向左
            Direction dir = (lane % 2 == 0) ? Direction.RIGHT : Direction.LEFT;
            double speed = GameConfig.BASE_VEHICLE_SPEED * speedMultiplier * (dir == Direction.RIGHT ? 1 : -1);
            // 生成3-5辆汽车（避免车道过空）
            int vehicleCount = 3 + random.nextInt(3);
            double spacing = GameConfig.SCREEN_WIDTH / (vehicleCount + 1); // 汽车间距
            for (int i = 0; i < vehicleCount; i++) {
                // 生成X坐标：错开分布，避免重叠
                double spawnX = i * spacing + random.nextDouble() * 50;
                Vehicle vehicle = new Vehicle(lane, speed, spawnX);
                vehicles.add(vehicle);
                gameScreen.addDrawable(vehicle); // 加入画布绘制列表
            }
        }

        // 4. 生成河流原木（仅向右移动，同车道同速）
        for (int lane = 0; lane < riverLanes / 2; lane++) { // 一半车道放原木
            double laneY = (GameConfig.INIT_RIVER_LANES + 1) * GameConfig.LANE_HEIGHT + lane * GameConfig.LANE_HEIGHT;
            double speed = GameConfig.BASE_LOG_SPEED * speedMultiplier; // 原木仅向右（正速度）
            int logCount = 2 + random.nextInt(2); // 每车道2-3根原木
            double spacing = GameConfig.SCREEN_WIDTH / (logCount + 1);
            for (int i = 0; i < logCount; i++) {
                double spawnX = i * spacing + random.nextDouble() * 50;
                Log log = new Log(lane, speed, spawnX);
                logs.add(log);
                gameScreen.addDrawable(log);
            }
        }

        // 5. 生成河流乌龟（仅向左移动，同车道同速）
        for (int lane = riverLanes / 2; lane < riverLanes; lane++) { // 另一半车道放乌龟
            double laneY = (GameConfig.INIT_RIVER_LANES + 1) * GameConfig.LANE_HEIGHT + lane * GameConfig.LANE_HEIGHT;
            double speed = GameConfig.BASE_TURTLE_SPEED * speedMultiplier * -1; // 乌龟仅向左（负速度）
            int turtleCount = 2 + random.nextInt(2); // 每车道2-3只乌龟
            double spacing = GameConfig.SCREEN_WIDTH / (turtleCount + 1);
            for (int i = 0; i < turtleCount; i++) {
                double spawnX = i * spacing + random.nextDouble() * 50;
                Turtle turtle = new Turtle(lane, speed, spawnX);
                turtles.add(turtle);
                gameScreen.addDrawable(turtle);
            }
        }
    }

    // 初始化Home（5个，均匀分布在顶部安全区）
    private void initHomes() {
        for (int i = 0; i < 5; i++) {
            Home home = new Home(i);
            homes.add(home);
            gameScreen.addDrawable(home);
        }
    }

    // 清空当前关卡的所有可移动对象（汽车、原木、乌龟）
    private void clearLevelObjects() {
        // 从画布移除并清空列表
        vehicles.forEach(gameScreen::removeDrawable);
        logs.forEach(gameScreen::removeDrawable);
        turtles.forEach(gameScreen::removeDrawable);
        vehicles.clear();
        logs.clear();
        turtles.clear();
    }

    // ------------------------------ Game接口实现：核心游戏逻辑
    /**
     * 单帧更新逻辑（每秒60次，JavaFX AnimationTimer调用）
     * 流程：移动对象 → 检测碰撞 → 更新计时 → 检查关卡状态 → 绘制画面
     */
    @Override
    public void updateGame() {
        if (isGamePaused || isGameOver()) {
            return; // 暂停或游戏结束时不更新
        }

        moveAllObjects(); // 1. 移动所有可移动对象
        detectAllCollisions(); // 2. 检测所有碰撞
        updateTimer(); // 3. 更新计时器
        checkLevelStatus(); // 4. 检查通关/游戏结束
        gameScreen.drawAll(); // 5. 通知画布绘制最新画面
    }

    // 1. 移动所有可移动对象（调用Model层Movable接口方法）
    private void moveAllObjects() {
        double deltaTime = 1.0 / 60; // 固定帧间隔（0.016秒）
        // 移动汽车
        vehicles.forEach(vehicle -> {
            vehicle.calculateMove(deltaTime);
            vehicle.updatePosition();
        });
        // 移动原木
        logs.forEach(log -> {
            log.calculateMove(deltaTime);
            log.updatePosition();
        });
        // 移动乌龟
        turtles.forEach(turtle -> {
            turtle.calculateMove(deltaTime);
            turtle.updatePosition();
        });
        // 移动青蛙（被动移动：载体带动）
        frog.calculateMove(deltaTime);
        frog.updatePosition();
    }

    // 2. 检测所有碰撞（核心逻辑：分场景处理）
    private void detectAllCollisions() {
        // 场景1：青蛙与汽车碰撞 → 减生命、重置
        boolean hitVehicle = vehicles.stream().anyMatch(vehicle -> frog.isColliding(vehicle));
        if (hitVehicle) {
            handleFrogDeath();
            return;
        }

        // 场景2：青蛙在河流区域（未在安全区）
        if (!frog.isInSafeArea() && frog.getY() < GameConfig.SCREEN_HEIGHT - 2 * GameConfig.LANE_HEIGHT) {
            // 子场景2.1：青蛙在载体上（原木/乌龟）→ 正常承载
            boolean onLog = logs.stream().anyMatch(log -> {
                if (frog.isColliding(log)) {
                    frog.enterCarrier(log);
                    return true;
                }
                return false;
            });
            boolean onTurtle = turtles.stream().anyMatch(turtle -> {
                if (frog.isColliding(turtle)) {
                    frog.enterCarrier(turtle);
                    return true;
                }
                return false;
            });

            // 子场景2.2：青蛙不在载体上 → 落水死亡
            if (!onLog && !onTurtle) {
                handleFrogDeath();
                return;
            }
        }

        // 场景3：青蛙进入Home → 占用Home、加分、加时间
        for (Home home : homes) {
            if (!home.isOccupied() && home.isFrogIn(frog)) {
                home.occupy();
                scoreManager.addScore(500); // 青蛙到家+500分
                timer.addTime(30); // 加30秒
                // 重置青蛙到初始位置，准备下一只青蛙
                frog.reset();
                break;
            }
        }

        // 场景4：青蛙被载体带出屏幕 → 死亡
        if (!frog.isActive()) {
            handleFrogDeath();
        }
    }

    // 3. 更新计时器（处理时间耗尽逻辑）
    private void updateTimer() {
        if (timer.isRunning() && System.currentTimeMillis() % 1000 == 0) { // 每秒更新一次
            timer.tick(); // 倒计时减1秒
            // 时间耗尽 → 减生命、重置
            if (timer.getRemainingTime() <= 0) {
                handleFrogDeath();
                timer.addTime(30); // 减生命后加30秒
            }
        }
    }

    // 4. 检查关卡状态（通关/游戏结束）
    private void checkLevelStatus() {
        // 通关：所有Home被占用
        if (isLevelCleared()) {
            isGamePaused = true;
            // 计算通关额外得分：10000分 + 剩余时间×250分
            int levelClearScore = 10000 + (int) timer.getRemainingTime() * 250;
            scoreManager.addScore(levelClearScore);
            // 显示通关提示
            showAlert("Level Cleared!", "Score: " + scoreManager.getCurrentScore() + "\nNext Level: " + (currentLevel + 1));
            // 进入下一关
            nextLevel();
            isGamePaused = false;
        }

        // 游戏结束：所有生命耗尽
        if (isGameOver()) {
            isGamePaused = true;
            // 保存高分（假设玩家名为“Player”，可后续扩展输入玩家名功能）
            HighScoreUtil.saveHighScore(scoreManager.getCurrentScore(), "Player");
            // 显示游戏结束提示
            showAlert("Game Over!", "Final Score: " + scoreManager.getCurrentScore() + "\nLives: " + frog.getLives());
            // 重置游戏
            restartGame();
        }
    }

    // ------------------------------ 辅助方法：处理青蛙死亡、关卡切换等
    // 处理青蛙死亡（减生命、重置位置、加时间）
    private void handleFrogDeath() {
        frog.reset(); // 重置青蛙位置，减生命
        timer.addTime(30); // 减生命后加30秒
        frog.setActive(true); // 重新激活青蛙（避免持续判定死亡）
    }

    // 进入下一关
    public void nextLevel() {
        currentLevel++;
        initLevel(currentLevel); // 重新初始化关卡（难度升级）
        // 重置Home状态（清空已占用）
        homes.forEach(Home::reset);
    }

    // 重新开始游戏（重置关卡、生命、得分、计时）
    public void restartGame() {
        currentLevel = 1;
        frog = new Frog(); // 重置青蛙
        scoreManager.reset(); // 重置得分
        timer.reset(); // 重置计时
        // 重新初始化关卡和Home
        initLevel(currentLevel);
        homes.forEach(Home::reset);
        // 重新添加青蛙到画布
        gameScreen.addDrawable(frog);
        isGamePaused = false;
    }

    // 显示提示弹窗（通关/游戏结束）
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // 等待用户点击确认
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                alert.close();
            }
        });
    }

    // ------------------------------ 游戏循环：启动/停止
    /**
     * 启动游戏循环（JavaFX AnimationTimer，每秒60帧）
     */
    public void startGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop(); // 避免重复启动
        }

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGame(); // 每帧调用updateGame
            }
        };
        gameLoop.start();
        timer.setRunning(true); // 启动计时器
    }

    /**
     * 停止游戏循环（暂停/退出时调用）
     */
    public void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        timer.setRunning(false); // 停止计时器
    }

    // ------------------------------ Game接口剩余方法：状态判断
    @Override
    public boolean isGameOver() {
        return frog.getLives() <= 0; // 生命耗尽 → 游戏结束
    }

    @Override
    public boolean isLevelCleared() {
        // 所有Home被占用 → 通关
        return homes.stream().allMatch(Home::isOccupied);
    }

    // ------------------------------ Getter：供外部访问（如MenuView）
    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
    // GameManager类中补充：
    public Frog getFrog() {
        return frog;
    }

    public void setGamePaused(boolean isPaused) {
        this.isGamePaused = isPaused;
    }

    public boolean isGamePaused() {
        return isGamePaused;
    }
}