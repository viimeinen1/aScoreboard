package io.github.viimeinen1.ascoreboard.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.viimeinen1.amsg.aMsg;
import io.github.viimeinen1.ascoreboard.aScoreboard;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class aScoreboardCommand {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("ascoreboard")
            .then(Commands.literal("reload")
                .executes(aScoreboardCommand::reload)
            )
            .build();
    }

    public static int reload(CommandContext<CommandSourceStack> ctx) {
        aMsg.send(ctx.getSource().getSender(), "<green>Reloading aScoreboard...");
        aScoreboard.reload();
        aMsg.send(ctx.getSource().getSender(), "<green>aScoreboard reloaded!");
        return Command.SINGLE_SUCCESS;
    }

}
