package io.github.viimeinen1.ascoreboard.placeholders;

import org.bukkit.entity.Player;

public interface Placeholder {
    String apply(Player player, String line);
}
