package io.github.viimeinen1.ascoreboard.scoreboard;

import io.github.viimeinen1.ascoreboard.aScoreboard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        aScoreboard.getManager().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        aScoreboard.getManager().removePlayer(event.getPlayer());
    }

}
