package io.github.viimeinen1.ascoreboard.scoreboard;

import fr.mrmicky.fastboard.adventure.FastBoard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScoreboardPlayer {

    private Scoreboard scoreboard = null;
    public final Player player;
    public FastBoard board;

    public ScoreboardPlayer(@NotNull Player player) {
        this.player = player;
    }

    public void setScoreboard(@Nullable Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        updateBoard(true);
    }

    public void setScoreboard(@Nullable String scoreboardKey) {
        this.scoreboard = ScoreboardManager.scoreboards.get(scoreboardKey);
        updateBoard(true);
    }

    public @Nullable Scoreboard getScoreboard() { return this.scoreboard; }

    public void updateBoard() {
        updateBoard(false);
    }

    public void updateBoard(boolean fullUpdate) {
        if (scoreboard != null) scoreboard.updateForPlayer(this, fullUpdate);
        else if (ScoreboardManager.defaultScoreboard != null) ScoreboardManager.defaultScoreboard.updateForPlayer(this, fullUpdate);
        else if (board != null) {
            board.delete();
            board = null;
        }
    }

}
