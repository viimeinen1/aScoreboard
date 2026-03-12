package io.github.viimeinen1.ascoreboard.scoreboard;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ScoreboardManager.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ScoreboardManager.removePlayer(event.getPlayer());
    }

}
