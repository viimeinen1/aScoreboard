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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardManager extends PlaceholderConsumer {
    public HashMap<String, Scoreboard> scoreboards = new HashMap<>();
    private final ConcurrentHashMap<UUID, ScoreboardPlayer> players = new ConcurrentHashMap<>();
    public Scoreboard defaultScoreboard = null;

    public ScoreboardManager() {
        addPlaceholder(new NamePlaceholder());
        addPlaceholder(new PlayeramountPlaceholder());
        addPlaceholder(new TimePlaceholder());
        addPlaceholder(new PlaceholderAPIPlaceholder());
    }

    public void addPlayer(Player player) {
        var scoreboardPlayer = new ScoreboardPlayer(player);
        scoreboardPlayer.updateBoard(true);
        players.put(player.getUniqueId(), scoreboardPlayer);
    }

    public void removePlayer(Player player) {
        var scoreboardPlayer = players.remove(player.getUniqueId());
        if (scoreboardPlayer != null && scoreboardPlayer.board != null) scoreboardPlayer.board.delete();
    }

    public ScoreboardPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public void reloadPlayerlist() {
        players.values().stream()
            .map(pl -> pl.player)
            .filter(pl -> !pl.isOnline())
            .forEach(this::removePlayer);

        Bukkit.getOnlinePlayers().stream()
            .filter(pl -> !players.containsKey(pl.getUniqueId()))
            .forEach(this::addPlayer);
    }

    public void updateAll() {
        updateAll(false);
    }

    public void updateAll(boolean fullUpdate) {
        players.values().forEach(pl -> pl.updateBoard(fullUpdate));
    }

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

    public void stopScoreboardTask() {
        if (scoreboardTask == null) return;
        scoreboardTask.cancel();
        scoreboardTask = null;
    }

    public @Nullable Scoreboard parseScoreboard(@Nullable ConfigurationSection conf) {
        if (conf == null) return null;
        if (!conf.contains("title") || !conf.contains("lines")) return null;
        var title = conf.getString("title", "");
        var lines = conf.getStringList("lines");
        return new Scoreboard(title, lines);
    }

    private static class NamePlaceholder implements Placeholder {
        @Override
        public String apply(Player player, String line) {
            return line.replace("%player%", player.getName());
        }
    }

    private static class PlayeramountPlaceholder implements Placeholder {
        @Override
        public String apply(Player player, String line) {
            return line.replace("%players%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        }
    }

    private static class TimePlaceholder implements Placeholder {
        @Override
        public String apply(Player player, String line) {
            for (var entry : ConfigData.timeFormats.entrySet()) {
                line = line.replaceAll("%time_" + entry.getKey() + "%", entry.getValue().format(new Date()));
            }
            return line;
        }
    }

    private static class PlaceholderAPIPlaceholder implements Placeholder {
        @Override
        public String apply(Player player, String line) {
            if (!aScoreboard.placeholderAPIDetected) return line;
            return PlaceholderAPI.setPlaceholders(player, line);
        }
    }

}
