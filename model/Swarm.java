package si.model;
//敌人集群
import javafx.geometry.Rectangle2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 敌人集群类，负责管理一群敌人飞船的创建、移动、状态更新等行为
 * 实现Movable接口，具备移动能力
 */
public class Swarm implements Movable {
    // 存储所有敌人飞船的列表（用于统一管理存活的敌人）
    private List<EnemyShip> ships;
    // 移动方向标志：true表示向右移动，false表示向左移动
    private boolean direction = true;
    // 集群整体的x坐标（左上角基准点）
    private double x = 50;
    // 集群整体的y坐标（左上角基准点）
    private double y = 40;
    // 敌人飞船之间的间距（像素）
    private int space = 30;
    // 二维数组存储敌人飞船（按行列网格排列，方便按位置索引访问）
    private EnemyShip[][] shipGrid;
    // 敌人集群的行数
    private int rows;
    // 敌人集群的列数
    private int cols;
    // 计数器，用于控制移动频率（避免移动过快）
    private int count = 0;
    // x方向的移动步长
    private double moveX;
    // y方向的移动步长
    private double moveY;
    // 游戏主实例引用（用于获取屏幕尺寸等游戏参数）
    private SpaceInvadersGame game;

    /**
     * 敌人集群的构造方法
     * @param r 集群的行数
     * @param c 集群的列数
     * @param sX x方向的移动步长
     * @param sY y方向的移动步长
     * @param g 游戏主实例
     */
    public Swarm(int r, int c, double sX, double sY, SpaceInvadersGame g) {
        game = g;
        rows = r;
        cols = c;
        moveX = sX;
        moveY = sY;
        // 初始化二维网格数组
        shipGrid = new EnemyShip[r][c];
        ships = new ArrayList<EnemyShip>();
        // 循环创建每行每列的敌人飞船
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                EnemyShip a;
                // 根据行索引判断敌人类型（A、B、C型交替）
                if (i % 5 == 0) {
                    // 第0、5、10...行创建A型敌人
                    a = new EnemyShip((int) x + (1 + space) * j, (int) y + i * space, AlienType.A);
                } else if (i % 5 == 1 || i % 5 == 2) {
                    // 第1、2、6、7...行创建B型敌人
                    a = new EnemyShip((int) x + (1 + space) * j, (int) y + i * space, AlienType.B);
                } else {
                    // 其他行创建C型敌人
                    a = new EnemyShip((int) x + (1 + space) * j, (int) y + i * space, AlienType.C);
                }
                // 将创建的敌人添加到列表和网格中
                ships.add(a);
                shipGrid[i][j] = a;
            }
        }
    }

    /**
     * 敌人集群的移动方法
     * 负责更新所有存活敌人的位置，处理边界碰撞（左右屏幕边缘）并改变方向
     */
    public void move() {
        // 收集已死亡的敌人飞船，准备从列表中移除
        List<EnemyShip> remove = new ArrayList<EnemyShip>();
        for (EnemyShip s : ships) {
            if (!s.isAlive()) {
                remove.add(s);
            }
        }
        ships.removeAll(remove);

        // 控制移动频率：每累计25次tick才执行一次移动（避免移动过快）
        if (count % 25 == 0) {
            // 计算当前x方向的移动量（根据方向判断正负）
            double cX = ((direction) ? moveX : -moveX);
            // 初始y方向移动量为0（默认只左右移动）
            double cY = 0;

            // 判断是否碰到左右边界（屏幕边缘预留20像素缓冲）
            // 右侧边界：集群当前x + 宽度 + x移动量 > 屏幕宽度 - 20
            // 左侧边界：调整后的x（考虑左侧死亡飞船） + x移动量 < 20
            if (x + getWidth() + cX > game.getScreenWidth() - 20 || getAdjustedX() + cX < 20) {
                // 碰到边界则反向移动
                direction = !direction;
                // 同时向下移动（y方向移动量为步长的5倍，实现向下换行效果）
                cY = moveY * 5;
                // 重新计算反向后的x移动量
                cX = ((direction) ? moveX : -moveX);
                // 每次换向向右时，增加x方向步长（敌人移动逐渐加速）
                if (direction) {
                    moveX += 0.25;
                }
            }
            // 更新集群整体的位置坐标
            y = y + cY;
            x = x + cX;
            // 让所有存活的敌人飞船按计算的移动量移动
            for (EnemyShip s : ships) {
                s.move(cX, cY);
            }
        }
    }

    /**
     * 获取集群调整后的x坐标（考虑左侧已完全死亡的列，避免边界判断错误）
     * @return 调整后的左侧x坐标
     */
    private int getAdjustedX() {
        int deadColCount = 0; // 左侧完全死亡的列数
        // 从左到右检查每一列
        for (int i = 0; i < cols; i++) {
            int deadInCol = 0; // 当前列中死亡的敌人数量
            for (int j = 0; j < rows; j++) {
                if (!shipGrid[j][i].isAlive()) {
                    deadInCol++;
                }
            }
            // 如果当前列所有敌人都死亡，则计数+1
            if (deadInCol == rows) {
                deadColCount++;
            } else {
                // 遇到非全死亡列则停止检查（只统计连续的左侧全死列）
                break;
            }
        }
        // 调整后的x = 原始x + 死亡列数 * 间距（跳过死亡列的宽度）
        return (int) x + deadColCount * space;
    }

    /**
     * 获取集群最底部的y坐标（用于判断是否到达玩家区域）
     * @return 集群最底部的y坐标
     */
    public int getBottomY() {
        double bottomY = 0;
        // 遍历所有底部存活的敌人飞船
        for (EnemyShip e : getBottom()) {
            Rectangle2D hitBox = e.getHitBox(); // 获取敌人的碰撞盒
            // 计算敌人底部的y坐标（敌人y + 碰撞盒高度）
            double currentBottom = e.getY() + hitBox.getHeight();
            // 记录最大的底部y坐标（最下方的敌人）
            if (currentBottom > bottomY) {
                bottomY = currentBottom;
            }
        }
        return (int) bottomY;
    }

    /**
     * 计算集群当前的宽度（考虑右侧已完全死亡的列，只统计有效宽度）
     * @return 集群的有效宽度
     */
    private int getWidth() {
        int validColCount = cols; // 有效列数（初始为总列数）
        // 从右到左检查每一列
        for (int i = cols - 1; i >= 0; i--) {
            int deadInCol = 0; // 当前列中死亡的敌人数量
            for (int j = 0; j < rows; j++) {
                if (!shipGrid[j][i].isAlive()) {
                    deadInCol++;
                }
            }
            // 如果当前列所有敌人都死亡，则有效列数-1
            if (deadInCol == rows) {
                validColCount--;
            } else {
                // 遇到非全死亡列则停止检查（只统计连续的右侧全死列）
                break;
            }
        }
        // 集群宽度 = 有效列数 * 间距
        return validColCount * space;
    }

    /**
     * 每帧更新计数器（用于控制移动频率）
     */
    public void tick() {
        count++;
    }

    /**
     * 获取所有可被击中的对象（即存活的敌人飞船）
     * @return 可被击中对象的列表
     */
    public List<Hittable> getHittable() {
        // 将EnemyShip列表转换为Hittable接口列表（EnemyShip实现Hittable）
        return new ArrayList<Hittable>(ships);
    }

    /**
     * 获取每列中最底部的存活敌人飞船（用于判断敌人射击逻辑）
     * @return 每列底部存活敌人的列表
     */
    public List<EnemyShip> getBottom() {
        List<EnemyShip> bottomShips = new ArrayList<EnemyShip>();

        // 遍历每一列
        for (int i = 0; i < cols; i++) {
            boolean found = false;
            // 从当前列的最底部向上查找第一个存活的敌人
            for (int j = rows - 1; j >= 0 && !found; j--) {
                if (shipGrid[j][i].isAlive()) {
                    found = true;
                    bottomShips.add(shipGrid[j][i]);
                }
            }
        }
        return bottomShips;
    }

    /**
     * 获取所有存活的敌人飞船列表（副本，避免外部直接修改内部列表）
     * @return 存活敌人飞船的列表
     */
    public List<EnemyShip> getEnemyShips() {
        return new ArrayList<EnemyShip>(ships);
    }

    /**
     * 获取剩余的敌人飞船数量
     * @return 剩余敌人数量
     */
    public int getShipsRemaining() {
        return ships.size();
    }
}