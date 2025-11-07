package com.ucd.frogger.controller;

import com.ucd.frogger.model.Direction;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * 输入处理器：仅负责监听键盘输入，将输入指令传递给GameManager
 */
public class InputHandler {
    private final GameManager gameManager; // 关联GameManager，传递输入指令

    // 构造函数：关联游戏管理器
    public InputHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * 注册键盘监听（绑定到游戏场景）
     * @param scene 游戏场景（从MenuView切换过来的场景）
     */
    public void registerListeners(Scene scene) {
        // 监听键盘按下事件
        scene.setOnKeyPressed(event -> {
            // 游戏暂停/结束时不响应输入
            if (gameManager.isGamePaused() || gameManager.isGameOver()) {
                return;
            }

            KeyCode keyCode = event.getCode();
            // 根据方向键映射为Direction枚举，传递给GameManager
            switch (keyCode) {
                case UP:
                    gameManager.getFrog().jump(Direction.UP); // 向上跳跃（触发得分逻辑）
                    break;
                case DOWN:
                    gameManager.getFrog().jump(Direction.DOWN);
                    break;
                case LEFT:
                    gameManager.getFrog().jump(Direction.LEFT);
                    break;
                case RIGHT:
                    gameManager.getFrog().jump(Direction.RIGHT);
                    break;
                case ESCAPE:
                    gameManager.setGamePaused(!gameManager.isGamePaused()); // ESC键暂停/继续
                    break;
                default:
                    // 忽略其他键
                    break;
            }
        });
    }

    // 注：GameManager中需添加getFrog()和setGamePaused()方法（补充如下）
    // // GameManager中添加：
    // public Frog getFrog() {
    //     return frog;
    // }
    // public void setGamePaused(boolean isPaused) {
    //     this.isGamePaused = isPaused;
    // }
    // public boolean isGamePaused() {
    //     return isGamePaused;
    // }
}