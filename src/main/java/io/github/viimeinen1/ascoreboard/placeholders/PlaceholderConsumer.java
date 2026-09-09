package io.github.viimeinen1.ascoreboard.placeholders;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlaceholderConsumer {
    private final List<Placeholder> placeholders = new ArrayList<>();
    public void addPlaceholder(Placeholder placeholder) {
        placeholders.add(placeholder);
    }
    public void removePlaceholder(Placeholder placeholder) {
        placeholders.remove(placeholder);
    }

    public String applyPlaceholders(Player player, String line) {
        for (Placeholder placeholder : placeholders) {
            line = placeholder.apply(player, line);
        }
        return line;
    }
}
