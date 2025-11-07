package com.ucd.frogger;

import com.ucd.frogger.controller.GameManager;
import com.ucd.frogger.controller.InputHandler;
import com.ucd.frogger.view.GameScreen;
import com.ucd.frogger.view.HUD;
import com.ucd.frogger.view.MenuView;
import com.ucd.frogger.model.Frog;
import com.ucd.frogger.util.ScoreManager;
import com.ucd.frogger.util.Timer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * 游戏入口类：初始化所有组件，串联UI与核心逻辑
 */
public class FroggerApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        // 1. 初始化view层组件
        GameScreen gameScreen = new GameScreen(); // 游戏画布
        ScoreManager scoreManager = new ScoreManager(); // 得分管理器
        Timer timer = new Timer(); // 计时器
        Frog frog = new Frog(GameConfig.SCREEN_WIDTH / 2 - 20,
                GameConfig.SCREEN_HEIGHT - GameConfig.LANE_HEIGHT * 2 + 10); // 青蛙初始位置

        // 2. 初始化controller层组件
        GameManager gameManager = new GameManager(gameScreen);
        InputHandler inputHandler = new InputHandler(gameManager); // 输入处理器
        gameManager.setDependencies(frog, scoreManager, timer, inputHandler); // 注入依赖

        // 3. 初始化HUD并添加到画布
        HUD hud = new HUD(scoreManager, timer, frog);
        gameScreen.addDrawable(hud);
        gameScreen.addDrawable(frog); // 青蛙添加到绘制列表

        // 4. 初始化菜单并显示
        MenuView menuView = new MenuView(primaryStage, gameManager);
        menuView.showMainMenu();
    }

    public static void main(String[] args) {
        launch(args); // 启动JavaFX应用
    }
}