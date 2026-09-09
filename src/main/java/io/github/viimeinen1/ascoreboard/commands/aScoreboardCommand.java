package io.github.viimeinen1.ascoreboard.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.viimeinen1.amsg.aMsg;
import io.github.viimeinen1.ascoreboard.aScoreboard;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class aScoreboardCommand {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("ascoreboard")
            .then(Commands.literal("reload")
                .executes(aScoreboardCommand::reload)
            )
            .then(Commands.literal("set")
                .requires(c -> c.getSender().isOp() || c.getSender().hasPermission("ascoreboard.manage"))
                .then(Commands.argument("scoreboard", StringArgumentType.word())
                    .suggests((_, builder) -> CompletableFuture.supplyAsync(() -> {
                        aScoreboard.getManager().scoreboards.keySet().forEach(builder::suggest);
                        return builder.build();
                    }))
                    .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(aScoreboardCommand::setScoreboard)
                    )
                )
            )
            .then(Commands.literal("setdefault")
                .requires(c -> c.getSender().isOp() || c.getSender().hasPermission("ascoreboard.manage"))
                .then(Commands.argument("scoreboard", StringArgumentType.word())
                    .suggests((_, builder) -> CompletableFuture.supplyAsync(() -> {
                        aScoreboard.getManager().scoreboards.keySet().forEach(builder::suggest);
                        return builder.build();
                    }))
                    .executes(aScoreboardCommand::setDefaultScoreboard)
                )
            )
            .build();
    }

    public static int reload(CommandContext<CommandSourceStack> ctx) {
        aMsg.send(ctx.getSource().getSender(), "<green>Reloading aScoreboard...");
        aScoreboard.reload();
        aMsg.send(ctx.getSource().getSender(), "<green>aScoreboard reloaded!");
        return Command.SINGLE_SUCCESS;
    }

    public static int setScoreboard(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        String name = StringArgumentType.getString(ctx, "scoreboard");
        PlayerSelectorArgumentResolver resolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
        Player player = resolver.resolve(ctx.getSource()).getFirst();

        var board = aScoreboard.getManager().scoreboards.get(name);
        if (board == null) {
            aMsg.send(ctx.getSource().getSender(), "<red>No such scoreboard exists!");
            return Command.SINGLE_SUCCESS;
        }

        aScoreboard.getManager().getPlayer(player).setScoreboard(board);
        return Command.SINGLE_SUCCESS;
    }

    public static int setDefaultScoreboard(CommandContext<CommandSourceStack> ctx) {
        String name = StringArgumentType.getString(ctx, "scoreboard");

        if ("none".equalsIgnoreCase(name)) {
            aScoreboard.getManager().defaultScoreboard = null;
            return Command.SINGLE_SUCCESS;
        }

        var board = aScoreboard.getManager().scoreboards.get(name);

        if (board == null) {
            aMsg.send(ctx.getSource().getSender(), "<red>No such scoreboard exists!");
            return Command.SINGLE_SUCCESS;
        }

        aScoreboard.getManager().defaultScoreboard = board;
        return Command.SINGLE_SUCCESS;
    }

}
