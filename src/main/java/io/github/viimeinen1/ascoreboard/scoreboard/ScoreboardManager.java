package io.github.viimeinen1.ascoreboard.scoreboard;

import io.github.viimeinen1.ascoreboard.ConfigData;
import io.github.viimeinen1.ascoreboard.aScoreboard;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardManager {

    private static final ConcurrentHashMap<UUID, ScoreboardPlayer> players = new ConcurrentHashMap<>();
    public static Scoreboard defaultScoreboard = null;

    public static void addPlayer(Player player) {
        var scoreboardPlayer = new ScoreboardPlayer(player);
        scoreboardPlayer.updateBoard(true);
        players.put(player.getUniqueId(), scoreboardPlayer);
    }

    public static void removePlayer(Player player) {
        var scoreboardPlayer = players.remove(player.getUniqueId());
        if (scoreboardPlayer != null && scoreboardPlayer.board != null) scoreboardPlayer.board.delete();
    }

    public static ScoreboardPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public static void reloadPlayerlist() {
        players.values().stream()
            .map(pl -> pl.player)
            .filter(pl -> !pl.isOnline())
            .forEach(ScoreboardManager::removePlayer);

        Bukkit.getOnlinePlayers().stream()
            .filter(pl -> !players.containsKey(pl.getUniqueId()))
            .forEach(ScoreboardManager::addPlayer);
    }

    public static void updateAll() {
        updateAll(false);
    }

    public static void updateAll(boolean fullUpdate) {
        players.values().forEach(pl -> pl.updateBoard(fullUpdate));
    }

    public static void disableScoreboard(@NotNull Scoreboard scoreboard, @Nullable Scoreboard replacement) {
        Bukkit.getOnlinePlayers().stream()
            .map(ScoreboardManager::getPlayer)
            .filter(pl -> scoreboard.equals(pl.getScoreboard()))
            .forEach(pl -> {
                pl.setScoreboard(replacement);
                pl.updateBoard(true);
            });
    }

    public static Component applyPlaceholders(Player player, String line) {
        if (aScoreboard.placeholderAPIDetected) line = PlaceholderAPI.setPlaceholders(player, line);
        line = line.replaceAll("%name%", player.getName());
        line = line.replaceAll("%players%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        for (var entry : ConfigData.timeFormats.entrySet()) {
            line = line.replaceAll("%time_" + entry.getKey() + "%", entry.getValue().format(new Date()));
        }
        return MiniMessage.miniMessage().deserialize(line);
    }

    private static BukkitTask scoreboardTask = null;

    public static void startScoreboardTask() {
        if (scoreboardTask != null) return;

        updateAll(true);

        scoreboardTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
            aScoreboard.getPlugin(),
            () -> updateAll(),
            20L,
            ConfigData.updateSpeed
        );
    }

    public static void stopScoreboardTask() {
        if (scoreboardTask == null) return;
        scoreboardTask.cancel();
        scoreboardTask = null;
    }

    public static @Nullable Scoreboard parseScoreboard(@Nullable ConfigurationSection conf) {
        if (conf == null) return null;
        if (!conf.contains("title") || !conf.contains("lines")) return null;
        var title = conf.getString("title", "");
        var lines = conf.getStringList("lines");
        return new Scoreboard(title, lines);
    }

}
