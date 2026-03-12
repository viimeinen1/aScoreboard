package io.github.viimeinen1.ascoreboard.scoreboard;

import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Scoreboard {

    private String title = "";
    private final List<String> lines = new ArrayList<>();

    public Scoreboard(@Nullable String title, @Nullable List<String> lines) {
        if (title != null) this.title = title;
        if (lines != null) this.lines.addAll(lines);
    }

    public String title() { return this.title; }
    public void title(String title) { this.title = title; }
    public List<String> lines() { return this.lines; }

    public void updateForPlayer(ScoreboardPlayer player, boolean fullUpdate) {
        if (player.board == null) player.board = new FastBoard(player.player);

        if (fullUpdate) {
            player.board.updateTitle(getPlaceholders(player.player, title));
            player.board.updateLines(getPlaceholders(player.player, lines));
        } else {
            var finalTitle = getPlaceholders(player.player, title);
            if (!player.board.getTitle().equals(finalTitle)) player.board.updateTitle(finalTitle);

            var finalLines = getPlaceholders(player.player, lines);
            for (int i = 0; i < finalLines.size(); i++) {
                var line = finalLines.get(i);
                if (!player.board.getLine(i).equals(line)) player.board.updateLine(i, line);
            }
        }
    }

    public List<Component> getPlaceholders(Player player, List<String> lines) {
        List<Component> finalLines = new ArrayList<>();
        for (var line : lines) {
            finalLines.add(getPlaceholders(player, line));
        }
        return finalLines;
    }

    public Component getPlaceholders(Player player, String line) {
        return ScoreboardManager.applyPlaceholders(player, line);
    }

}
