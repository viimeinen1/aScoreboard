package io.github.viimeinen1.ascoreboard;

import io.github.viimeinen1.ascoreboard.scoreboard.Scoreboard;
import io.github.viimeinen1.ascoreboard.scoreboard.ScoreboardManager;

import java.util.HashMap;

public class ConfigData {

    public static String pluginPrefix;
    public static boolean commandEnabled;
    public static int updateSpeed;
    public static HashMap<String, Scoreboard> scoreboards = new HashMap<>();
    public static String defaultScoreboard;

    public static void loadConfig() {
        var config = aScoreboard.getPlugin().getConfig();

        pluginPrefix = config.getString("prefix", "");
        commandEnabled = config.getBoolean("scoreboard-command", true);
        updateSpeed = config.getInt("scoreboard-update-speed", 20);

        var scoreboardSection = config.getConfigurationSection("scoreboards");
        if (scoreboardSection != null) {
            for (var key : scoreboardSection.getKeys(false)) {
                var board = ScoreboardManager.parseScoreboard(scoreboardSection.getConfigurationSection(key));
                if (board != null) scoreboards.put(key, board);
            }
        }

        defaultScoreboard = config.getString("default-scoreboard", "");
        if (scoreboards.containsKey(defaultScoreboard)) ScoreboardManager.defaultScoreboard = scoreboards.get(defaultScoreboard);
    }

}
