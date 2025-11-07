package com.ucd.frogger.util;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 高分工具类（静态类）：本地文件持久化高分榜，保留前10名高分
 */
public class HighScoreUtil {
    // 高分文件路径：用户目录下（跨平台兼容，如Windows的C:\Users\XXX，Mac的/Users/XXX）
    private static final String HIGH_SCORE_FILE_PATH =
            System.getProperty("user.home") + File.separator + "frogger_highscores.txt";
    // 高分榜最大容量（保留前10名）
    private static final int MAX_HIGH_SCORES = 10;

    /**
     * 保存高分到本地文件（按得分降序排序，相同得分按时间降序，保留前10）
     * @param score 待保存的得分
     * @param playerName 玩家名（可后续从UI输入，当前默认“Player”）
     */
    public static void saveHighScore(int score, String playerName) {
        // 1. 读取现有高分列表
        List<HighScoreEntry> highScores = readHighScoreEntries();

        // 2. 添加新高分（记录当前时间，用于相同得分时排序）
        HighScoreEntry newEntry = new HighScoreEntry(
                score,
                playerName.isEmpty() ? "Player" : playerName,
                new Date() // 记录得分时间
        );
        highScores.add(newEntry);

        // 3. 排序：先按得分降序，再按时间降序（新得分优先）
        List<HighScoreEntry> sortedScores = highScores.stream()
                .sorted((e1, e2) -> {
                    if (e1.getScore() != e2.getScore()) {
                        return Integer.compare(e2.getScore(), e1.getScore()); // 得分降序
                    } else {
                        return e2.getTimestamp().compareTo(e1.getTimestamp()); // 时间降序
                    }
                })
                .limit(MAX_HIGH_SCORES) // 保留前10名
                .collect(Collectors.toList());

        // 4. 写入文件（覆盖原有内容）
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE_PATH))) {
            for (HighScoreEntry entry : sortedScores) {
                // 文件格式：得分,玩家名,时间戳（便于读取时解析）
                writer.write(String.format("%d,%s,%d%n",
                        entry.getScore(),
                        entry.getPlayerName(),
                        entry.getTimestamp().getTime()));
            }
        } catch (IOException e) {
            // 异常处理：打印日志（不崩溃游戏）
            System.err.println("Failed to save high scores: " + e.getMessage());
        }
    }

    /**
     * 读取高分榜（返回格式化的字符串列表，如“1. 12500 - Player (2024-10-01 14:30)”）
     * @return 格式化后的高分列表（空列表表示无高分或读取失败）
     */
    public static List<String> getHighScores() {
        List<HighScoreEntry> entries = readHighScoreEntries();
        List<String> formattedScores = new ArrayList<>();

        // 格式化输出（添加排名、得分、玩家名、时间）
        for (int i = 0; i < entries.size(); i++) {
            HighScoreEntry entry = entries.get(i);
            String timeStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
                    .format(entry.getTimestamp());
            String formatted = String.format("%d. %d - %s (%s)",
                    i + 1, // 排名（从1开始）
                    entry.getScore(),
                    entry.getPlayerName(),
                    timeStr);
            formattedScores.add(formatted);
        }

        // 无高分时返回“暂无分数”
        if (formattedScores.isEmpty()) {
            formattedScores.add("No scores yet!");
        }
        return formattedScores;
    }

    /**
     * 内部方法：读取文件中的高分条目（转换为HighScoreEntry对象）
     * @return 高分条目列表（空列表表示文件不存在或解析失败）
     */
    private static List<HighScoreEntry> readHighScoreEntries() {
        List<HighScoreEntry> entries = new ArrayList<>();
        File file = new File(HIGH_SCORE_FILE_PATH);

        // 文件不存在时直接返回空列表（首次运行时）
        if (!file.exists()) {
            return entries;
        }

        // 读取文件并解析每一行
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // 跳过空行
                }

                // 解析行内容（格式：得分,玩家名,时间戳）
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    continue; // 格式错误的行跳过
                }

                try {
                    int score = Integer.parseInt(parts[0]);
                    String playerName = parts[1];
                    long timestamp = Long.parseLong(parts[2]);
                    Date date = new Date(timestamp);

                    entries.add(new HighScoreEntry(score, playerName, date));
                } catch (NumberFormatException e) {
                    // 数字解析失败时跳过该行
                    System.err.println("Invalid high score line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read high scores: " + e.getMessage());
        }

        return entries;
    }

    /**
     * 内部静态类：封装高分条目（得分、玩家名、时间戳）
     * 用于内存中管理高分数据，避免直接操作字符串
     */
    private static class HighScoreEntry {
        private final int score;
        private final String playerName;
        private final Date timestamp;

        public HighScoreEntry(int score, String playerName, Date timestamp) {
            this.score = score;
            this.playerName = playerName;
            this.timestamp = timestamp;
        }

        // Getter（仅暴露读取，不允许修改）
        public int getScore() {
            return score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public Date getTimestamp() {
            return timestamp;
        }
    }

    // 暴露文件路径（便于调试或外部查看）
    public static String getHighScoreFilePath() {
        return HIGH_SCORE_FILE_PATH;
    }
}