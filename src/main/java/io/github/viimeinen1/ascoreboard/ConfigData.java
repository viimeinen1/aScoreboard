package io.github.viimeinen1.ascoreboard;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * All configuration data
 */
public class ConfigData {

    /**
     * Plugin prefix
     */
    public static String pluginPrefix;

    /**
     * If player commands are enabled
     */
    public static boolean commandEnabled;

    /**
     * How often scoreboards are updated
     */
    public static int updateSpeed;

    /**
     * Name of the default scoreboard
     */
    public static String defaultScoreboard;

    /**
     * Time formats
     */
    public static HashMap<String, SimpleDateFormat> timeFormats = new HashMap<>();

    /**
     * Timezone
     */
    public static String timezone;

    /**
     * Load data in configuration to memory
     */
    public static void loadConfig() {
        var config = aScoreboard.getPlugin().getConfig();

        pluginPrefix = config.getString("prefix", "");
        commandEnabled = config.getBoolean("scoreboard-command", true);
        updateSpeed = config.getInt("scoreboard-update-speed", 20);

        var scoreboardSection = config.getConfigurationSection("scoreboards");
        if (scoreboardSection != null) {
            for (var key : scoreboardSection.getKeys(false)) {
                var board = aScoreboard.getManager().parseScoreboard(scoreboardSection.getConfigurationSection(key));
                if (board != null) aScoreboard.getManager().scoreboards.put(key, board);
            }
        }

        defaultScoreboard = config.getString("default-scoreboard", "");
        aScoreboard.getManager().defaultScoreboard = aScoreboard.getManager().scoreboards.get(defaultScoreboard);

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
