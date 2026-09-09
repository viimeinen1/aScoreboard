package io.github.viimeinen1.ascoreboard.scoreboard;

import io.github.viimeinen1.ascoreboard.ConfigData;
import io.github.viimeinen1.ascoreboard.aScoreboard;
import io.github.viimeinen1.ascoreboard.placeholders.Placeholder;
import io.github.viimeinen1.ascoreboard.placeholders.PlaceholderConsumer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all scoreboards in a server.<br>
 * <br>
 * There shouldn't be a reason to create new manager. Use {@link aScoreboard#getManager()}.
 */
public class ScoreboardManager extends PlaceholderConsumer {

    /**
     * All scoreboards
     */
    public HashMap<String, Scoreboard> scoreboards = new HashMap<>();
    private final ConcurrentHashMap<UUID, ScoreboardPlayer> players = new ConcurrentHashMap<>();

    /**
     * Default scoreboard displayed to all players when they join.
     */
    public Scoreboard defaultScoreboard = null;

    /**
     * new ScoreboardManager
     */
    public ScoreboardManager() {
        addPlaceholder(new NamePlaceholder());
        addPlaceholder(new PlayeramountPlaceholder());
        addPlaceholder(new TimePlaceholder());
        addPlaceholder(new PlaceholderAPIPlaceholder());
    }

    @ApiStatus.Internal
    protected void addPlayer(Player player) {
        var scoreboardPlayer = new ScoreboardPlayer(player);
        scoreboardPlayer.updateBoard(true);
        players.put(player.getUniqueId(), scoreboardPlayer);
    }

    @ApiStatus.Internal
    protected void removePlayer(Player player) {
        var scoreboardPlayer = players.remove(player.getUniqueId());
        if (scoreboardPlayer != null && scoreboardPlayer.board != null) scoreboardPlayer.board.delete();
    }

    /**
     * Get ScoreboardPlayer from player.
     * @param player player
     * @return ScoreboardPlayer
     */
    public ScoreboardPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    /**
     * Reload playerlist to counter desyncs.
     */
    public void reloadPlayerlist() {
        players.values().stream()
            .map(pl -> pl.player)
            .filter(pl -> !pl.isOnline())
            .forEach(this::removePlayer);

        Bukkit.getOnlinePlayers().stream()
            .filter(pl -> !players.containsKey(pl.getUniqueId()))
            .forEach(this::addPlayer);
    }

    /**
     * Update all players' scoreboards
     */
    public void updateAll() {
        updateAll(false);
    }

    /**
     * Update all players' scoreboards
     * @param fullUpdate if false, only changed lines are sent to player
     */
    public void updateAll(boolean fullUpdate) {
        players.values().forEach(pl -> pl.updateBoard(fullUpdate));
    }

    /**
     * Disable scoreboard, and change the scoreboard of all players using that scoreboard into the replacement scoreboard.
     * @param scoreboard scoreboard to disable
     * @param replacement replacement scoreboard, or null if none
     */
    public void disableScoreboard(@NotNull Scoreboard scoreboard, @Nullable Scoreboard replacement) {
        Bukkit.getOnlinePlayers().stream()
            .map(this::getPlayer)
            .filter(pl -> scoreboard.equals(pl.getScoreboard()))
            .forEach(pl -> {
                pl.setScoreboard(replacement);
                pl.updateBoard(true);
            });
    }

    private BukkitTask scoreboardTask = null;

    /**
     * Start new scoreboard update task if one doesn't yet exist.
     */
    @ApiStatus.Internal
    public void startScoreboardTask() {
        if (scoreboardTask != null) return;

        updateAll(true);

        scoreboardTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
            aScoreboard.getPlugin(),
            () -> updateAll(),
            20L,
            ConfigData.updateSpeed
        );
    }

    /**
     * Stop scoreboard update task.
     */
    @ApiStatus.Internal
    public void stopScoreboardTask() {
        if (scoreboardTask == null) return;
        scoreboardTask.cancel();
        scoreboardTask = null;
    }

    /**
     * Parse scoreboard from configuration
     * @param conf config
     * @return Scoreboard or null if couldn't parse anything.
     */
    public @Nullable Scoreboard parseScoreboard(@Nullable ConfigurationSection conf) {
        if (conf == null) return null;
        if (!conf.contains("title") || !conf.contains("lines")) return null;
        var title = conf.getString("title", "");
        var lines = conf.getStringList("lines");
        return new Scoreboard(title, lines);
    }

    @ApiStatus.Internal
    private static class NamePlaceholder implements Placeholder {
        @Override
        public String apply(Player player, String line) {
            return line.replace("%player%", player.getName());
        }
    }

    @ApiStatus.Internal
    private static class PlayeramountPlaceholder implements Placeholder {
        @Override
        public String apply(Player player, String line) {
            return line.replace("%players%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        }
    }

    @ApiStatus.Internal
    private static class TimePlaceholder implements Placeholder {
        @Override
        public String apply(Player player, String line) {
            for (var entry : ConfigData.timeFormats.entrySet()) {
                line = line.replaceAll("%time_" + entry.getKey() + "%", entry.getValue().format(new Date()));
            }
            return line;
        }
    }

    @ApiStatus.Internal
    private static class PlaceholderAPIPlaceholder implements Placeholder {
        @Override
        public String apply(Player player, String line) {
            if (!aScoreboard.placeholderAPIDetected) return line;
            return PlaceholderAPI.setPlaceholders(player, line);
        }
    }

}
