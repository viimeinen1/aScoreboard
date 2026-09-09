package io.github.viimeinen1.ascoreboard.placeholders;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses list of placeholders to apply at once.
 */
public class PlaceholderConsumer {
    private final List<Placeholder> placeholders = new ArrayList<>();

    /**
     * Add placeholder to this consumer.
     * @param placeholder placeholder
     */
    public void addPlaceholder(Placeholder placeholder) {
        placeholders.add(placeholder);
    }

    /**
     * Apply placeholders in this consumer to the line.
     * @param player player
     * @param line line to apply the placeholders to
     * @return line with placeholders applied into it
     */
    public String applyPlaceholders(Player player, String line) {
        for (Placeholder placeholder : placeholders) {
            line = placeholder.apply(player, line);
        }
        return line;
    }
}
