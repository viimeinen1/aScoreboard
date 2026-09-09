package io.github.viimeinen1.ascoreboard.scoreboard;

import fr.mrmicky.fastboard.adventure.FastBoard;
import io.github.viimeinen1.ascoreboard.aScoreboard;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Player with scoreboard.
 */
public class ScoreboardPlayer {

    private Scoreboard scoreboard = null;

    /**
     * Player
     */
    public final Player player;

    /**
     * FastBoard of this player
     */
    @ApiStatus.Internal
    protected FastBoard board;

    /**
     * new ScoreboardPlayer.<br>
     * <br>
     * Don't create new ScoreboardPlayers, use {@link ScoreboardManager#getPlayer(Player)} instead.
     * @param player player
     */
    @ApiStatus.Internal
    public ScoreboardPlayer(@NotNull Player player) {
        this.player = player;
    }

    /**
     * Set scoreboard that this player will see
     * @param scoreboard scoreboard this player will see, or null if scoreboard should be disabled
     */
    public void setScoreboard(@Nullable Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        updateBoard(true);
    }

    /**
     * Set scoreboard that this player will see. If there is no scoreboard with that name, scoreboard will be disabled for this player.
     * @param scoreboardKey name of the scoreboard, or null if scoreboard is disabled
     */
    public void setScoreboard(@Nullable String scoreboardKey) {
        this.scoreboard = aScoreboard.getManager().scoreboards.get(scoreboardKey);
        updateBoard(true);
    }

    /**
     * Get scoreboard that this player sees.
     * @return scoreboard
     */
    public @Nullable Scoreboard getScoreboard() { return this.scoreboard; }

    /**
     * Update scoreboard to this player.
     */
    public void updateBoard() {
        updateBoard(false);
    }

    /**
     * Update scoreboard to this player.
     * @param fullUpdate if false, only changed lines will be sent to player
     */
    public void updateBoard(boolean fullUpdate) {
        if (scoreboard != null) scoreboard.updateForPlayer(this, fullUpdate);
        else if (aScoreboard.getManager().defaultScoreboard != null) aScoreboard.getManager().defaultScoreboard.updateForPlayer(this, fullUpdate);
        else if (board != null) {
            board.delete();
            board = null;
        }
    }

}
