package io.github.viimeinen1.ascoreboard.scoreboard;

import fr.mrmicky.fastboard.adventure.FastBoard;
import io.github.viimeinen1.ascoreboard.aScoreboard;
import io.github.viimeinen1.ascoreboard.placeholders.PlaceholderConsumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Single scoreboard.<br>
 * <br>
 * All lines in the scoreboard will be parsed with:<br>
 * - This scoreboard's placeholders<br>
 * - Global placeholders (ScoreboardManager)<br>
 * - MiniMessage<br>
 */
public class Scoreboard extends PlaceholderConsumer {

    private String title = "";
    private final List<String> lines = new ArrayList<>();

    /**
     * new scoreboard.
     *
     * @param title title of the scoreboard
     * @param lines lines of the scoreboard
     */
    public Scoreboard(@Nullable String title, @Nullable List<String> lines) {
        if (title != null) this.title = title;
        if (lines != null) this.lines.addAll(lines);
    }

    /**
     * Title of this scoreboard
     * @return current title
     */
    public String title() { return this.title; }

    /**
     * Set the title of this scoreboard
     * @param title new title
     */
    public void title(String title) { this.title = title; }

    /**
     * Lines in this scoreboard
     * @return current lines
     */
    public List<String> lines() { return this.lines; }

    /**
     * Update this scoreboard for single player.
     * @param player player
     * @param fullUpdate if false, only changed lines will be sent to player.
     */
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

    /**
     * Parse all lines in list with {@link Scoreboard#getPlaceholders(Player, String)}.
     * @param player player
     * @param lines lines
     * @return list of parsed lines
     */
    public List<Component> getPlaceholders(Player player, List<String> lines) {
        List<Component> finalLines = new ArrayList<>();
        for (var line : lines) {
            finalLines.add(getPlaceholders(player, line));
        }
        return finalLines;
    }

    /**
     * Apply placeholders and parse as MiniMessage component.
     * @param player player
     * @param line line
     * @return line parsed with placeholders and MiniMessage.
     */
    public Component getPlaceholders(Player player, String line) {
        line = this.applyPlaceholders(player, line);
        line = aScoreboard.getManager().applyPlaceholders(player, line);
        return MiniMessage.miniMessage().deserialize(line);
    }

}
