package io.github.viimeinen1.ascoreboard.placeholders;

import org.bukkit.entity.Player;

/**
 * Placeholder for {@link PlaceholderConsumer}.
 */
@FunctionalInterface
public interface Placeholder {

    /**
     * Apply placeholder to the line via returning a modified line.
     * @param player player
     * @param line line as a string
     * @return final placeholder
     */
    String apply(Player player, String line);
}
