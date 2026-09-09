package io.github.viimeinen1.ascoreboard;

import io.github.viimeinen1.ascoreboard.scoreboard.ScoreboardListener;
import io.github.viimeinen1.amsg.aMsg;
import io.github.viimeinen1.ascoreboard.commands.aScoreboardCommand;
import io.github.viimeinen1.ascoreboard.scoreboard.ScoreboardManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Main class
 */
public class aScoreboard extends JavaPlugin {

    /**
     * If plugin has detected PlaceholderAPI, and will parse its placeholders in scoreboards.
     */
    public static boolean placeholderAPIDetected = false;

    private static JavaPlugin plugin;
    private static final ScoreboardManager scoreboardManager = new ScoreboardManager();

    /**
     * Get plugin instance.
     * @return JavaPlugin instance
     */
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Get main ScoreboardManager for managing scoreboards.
     * @return ScoreboardManager
     */
    public static ScoreboardManager getManager() { return scoreboardManager; }

    @Override
    public void onEnable() {
        super.onEnable();

        plugin = this;
        aMsg.plugin(plugin);

        aMsg.log(aMsg.LOG_COLOR.GREEN, "loading plugin...");

        saveDefaultConfig();
        ConfigData.loadConfig();

        aMsg.prefix(MiniMessage.miniMessage().deserialize(ConfigData.pluginPrefix));

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPIDetected = true;
            aMsg.log(aMsg.LOG_COLOR.GREEN, "PlaceholderAPI detected");
        } else {
            placeholderAPIDetected = false;
            aMsg.log(aMsg.LOG_COLOR.YELLOW, "PlaceholderAPI not detected!", "Placeholders from PlaceholderAPI will not work.");
        }

        if (ConfigData.commandEnabled) {
            getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                c -> c.registrar().register(aScoreboardCommand.createCommand(), List.of("scoreboard"))
            );
        } else {
            aMsg.log(aMsg.LOG_COLOR.YELLOW, "Plugin command is disabled.", "Only api access allowed.");
        }

        getServer().getPluginManager().registerEvents(new ScoreboardListener(), plugin);

        scoreboardManager.reloadPlayerlist();
        scoreboardManager.startScoreboardTask();

        aMsg.log(aMsg.LOG_COLOR.GREEN, "aScoreboard successfully loaded!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        aMsg.log(aMsg.LOG_COLOR.GREEN, "disabling plugin...");

        scoreboardManager.stopScoreboardTask();

        aMsg.log(aMsg.LOG_COLOR.GREEN, "aScoreboard successfully disabled!");
    }

    /**
     * Reload plugin
     */
    public static void reload() {
        aMsg.log(aMsg.LOG_COLOR.GREEN, "Reloading plugin...");

        plugin.reloadConfig();
        ConfigData.loadConfig();

        aMsg.prefix(MiniMessage.miniMessage().deserialize(ConfigData.pluginPrefix));

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPIDetected = true;
            aMsg.log(aMsg.LOG_COLOR.GREEN, "PlaceholderAPI detected");
        } else {
            placeholderAPIDetected = false;
            aMsg.log(aMsg.LOG_COLOR.YELLOW, "PlaceholderAPI not detected!", "Placeholders from PlaceholderAPI will not work.");
        }

        scoreboardManager.stopScoreboardTask();
        scoreboardManager.reloadPlayerlist();
        scoreboardManager.startScoreboardTask();

        aMsg.log(aMsg.LOG_COLOR.GREEN, "aScoreboard reloaded!");
    }

}