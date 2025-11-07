package com.ucd.frogger.view;

import com.ucd.frogger.controller.GameManager;
import com.ucd.frogger.util.HighScoreUtil;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.List;

/**
 * 菜单界面类：负责主菜单、高分榜、控制说明的显示与交互
 */
public class MenuView {
    private final Stage primaryStage;
    private final GameManager gameManager;
    private final Scene menuScene; // 主菜单场景
    private final VBox mainMenuLayout; // 主菜单布局

    // 初始化：关联窗口和游戏管理器（用于触发游戏逻辑）
    public MenuView(Stage stage, GameManager gm) {
        this.primaryStage = stage;
        this.gameManager = gm;
        this.mainMenuLayout = createMainMenuLayout();
        this.menuScene = new Scene(mainMenuLayout, 800, 600);
        this.menuScene.setFill(Color.DARKSLATEBLUE); // 菜单背景色
    }

    /**
     * 创建主菜单布局（按钮：开始游戏、高分榜、控制说明、退出）
     */
    private VBox createMainMenuLayout() {
        VBox vbox = new VBox(30); // 垂直布局，组件间距30像素
        vbox.setAlignment(Pos.CENTER); // 居中对齐

        // 标题
        Label titleLabel = new Label("FROGGER GAME");
        titleLabel.setFont(Font.font("Arial", 40));
        titleLabel.setTextFill(Color.WHITE);

        // 按钮：开始游戏
        Button startBtn = createMenuButton("Start Game");
        startBtn.setOnAction(e -> startGame());

        // 按钮：查看高分榜
        Button highScoreBtn = createMenuButton("High Scores");
        highScoreBtn.setOnAction(e -> showHighScores());

        // 按钮：控制说明
        Button controlsBtn = createMenuButton("Controls");
        controlsBtn.setOnAction(e -> showControls());

        // 按钮：退出
        Button exitBtn = createMenuButton("Exit");
        exitBtn.setOnAction(e -> primaryStage.close());

        // 添加组件到布局
        vbox.getChildren().addAll(titleLabel, startBtn, highScoreBtn, controlsBtn, exitBtn);
        return vbox;
    }

    /**
     * 创建统一风格的菜单按钮（避免重复代码）
     */
    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", 20));
        btn.setPrefSize(200, 50); // 按钮大小
        btn.setTextFill(Color.DARKSLATEBLUE);
        btn.setStyle("-fx-background-color: white; -fx-border-radius: 10;");
        // 鼠标悬浮效果
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #87CEEB; -fx-border-radius: 10;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: white; -fx-border-radius: 10;"));
        return btn;
    }

    /**
     * 开始游戏：切换到游戏场景
     */
    private void startGame() {
        Scene gameScene = new Scene(gameManager.getGameScreen().getCanvas());
        // 注册输入监听（方向键控制青蛙）
        gameManager.getInputHandler().registerListeners(gameScene);
        // 切换场景并启动游戏循环
        primaryStage.setScene(gameScene);
        gameManager.startGameLoop();
    }

    /**
     * 显示高分榜（从HighScoreUtil读取历史分数）
     */
    private void showHighScores() {
        VBox highScoreLayout = new VBox(20);
        highScoreLayout.setAlignment(Pos.CENTER);
        highScoreLayout.setFillWidth(true);

        // 标题
        Label title = new Label("HIGH SCORES");
        title.setFont(Font.font("Arial", 30));
        title.setTextFill(Color.WHITE);

        // 读取高分数据（若为空则显示"暂无分数"）
        List<String> highScores = HighScoreUtil.getHighScores();
        VBox scoreList = new VBox(10);
        if (highScores.isEmpty()) {
            Label emptyLabel = new Label("No scores yet!");
            emptyLabel.setFont(Font.font("Arial", 18));
            emptyLabel.setTextFill(Color.WHITE);
            scoreList.getChildren().add(emptyLabel);
        } else {
            for (String score : highScores) {
                Label scoreLabel = new Label(score);
                scoreLabel.setFont(Font.font("Arial", 18));
                scoreLabel.setTextFill(Color.WHITE);
                scoreList.getChildren().add(scoreLabel);
            }
        }

        // 返回按钮
        Button backBtn = createMenuButton("Back to Menu");
        backBtn.setOnAction(e -> primaryStage.setScene(menuScene));

        highScoreLayout.getChildren().addAll(title, scoreList, backBtn);
        Scene highScoreScene = new Scene(highScoreLayout, 800, 600);
        highScoreScene.setFill(Color.DARKSLATEBLUE);
        primaryStage.setScene(highScoreScene);
    }

    /**
     * 显示控制说明（操作键+游戏规则）
     */
    private void showControls() {
        VBox controlsLayout = new VBox(20);
        controlsLayout.setAlignment(Pos.CENTER);
        controlsLayout.setFillWidth(true);

        // 标题
        Label title = new Label("GAME CONTROLS");
        title.setFont(Font.font("Arial", 30));
        title.setTextFill(Color.WHITE);

        // 控制说明文本
        Label control1 = new Label("• Arrow Keys: Jump Up/Down/Left/Right");
        Label control2 = new Label("• Objective: Guide 5 frogs to homes (1 frog per home)");
        Label control3 = new Label("• Road: Avoid cars (lose life if hit)");
        Label control4 = new Label("• River: Jump on logs/turtles (die in water)");
        Label control5 = new Label("• Timer: 30s initial (add 30s on life loss/home entry)");
        for (Label label : List.of(control1, control2, control3, control4, control5)) {
            label.setFont(Font.font("Arial", 18));
            label.setTextFill(Color.WHITE);
        }

        // 返回按钮
        Button backBtn = createMenuButton("Back to Menu");
        backBtn.setOnAction(e -> primaryStage.setScene(menuScene));

        controlsLayout.getChildren().addAll(title, control1, control2, control3, control4, control5, backBtn);
        Scene controlsScene = new Scene(controlsLayout, 800, 600);
        controlsScene.setFill(Color.DARKSLATEBLUE);
        primaryStage.setScene(controlsScene);
    }

    // 显示主菜单（应用启动时调用）
    public void showMainMenu() {
        primaryStage.setTitle("Frogger - Main Menu");
        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false); // 禁止窗口缩放
        primaryStage.show();
    }
}