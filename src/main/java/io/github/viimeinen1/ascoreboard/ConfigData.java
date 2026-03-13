package io.github.viimeinen1.ascoreboard;

import io.github.viimeinen1.ascoreboard.scoreboard.ScoreboardManager;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

public class ConfigData {

    public static String pluginPrefix;
    public static boolean commandEnabled;
    public static int updateSpeed;
    public static String defaultScoreboard;
    public static HashMap<String, SimpleDateFormat> timeFormats = new HashMap<>();
    public static String timezone;

    public static void loadConfig() {
        var config = aScoreboard.getPlugin().getConfig();

        pluginPrefix = config.getString("prefix", "");
        commandEnabled = config.getBoolean("scoreboard-command", true);
        updateSpeed = config.getInt("scoreboard-update-speed", 20);

        var scoreboardSection = config.getConfigurationSection("scoreboards");
        if (scoreboardSection != null) {
            for (var key : scoreboardSection.getKeys(false)) {
                var board = ScoreboardManager.parseScoreboard(scoreboardSection.getConfigurationSection(key));
                if (board != null) ScoreboardManager.scoreboards.put(key, board);
            }
        }

        defaultScoreboard = config.getString("default-scoreboard", "");
        ScoreboardManager.defaultScoreboard = ScoreboardManager.scoreboards.get(defaultScoreboard);

        timezone = config.getString("timezone", "UTC");

        var timeformatSection = config.getConfigurationSection("timeformats");
        if (timeformatSection != null) {
            for (var key : timeformatSection.getKeys(false)) {
                var format = new SimpleDateFormat(timeformatSection.getString(key, "KK':'mm':'ss a MM/dd/yyyy"));
                format.setTimeZone(TimeZone.getTimeZone(timezone));
                timeFormats.put(key, format);
            }
        }
    }

}
