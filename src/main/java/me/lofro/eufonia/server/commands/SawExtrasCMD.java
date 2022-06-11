package me.lofro.eufonia.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.lofro.eufonia.global.utils.ChatFormatter;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class SawExtrasCMD {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("sawextras").requires(source -> source.hasPermissionLevel(4));

        literalArgumentBuilder
                .then(CommandManager.literal("saturation")
                        .then(CommandManager.literal("add")
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .then(CommandManager.argument("level", IntegerArgumentType.integer())
                                                .executes(context ->
                                                        applySaturation(context, EntityArgumentType.getPlayers(context, "player"),
                                                                IntegerArgumentType.getInteger(context, "level")))))));

        dispatcher.register(literalArgumentBuilder);
    }

    public static int applySaturation(CommandContext<ServerCommandSource> commandContext, Collection<ServerPlayerEntity> players, int level) {
        try {
            players.forEach(player -> {
                player.getHungerManager().add(level, level);

                if (players.size() > 1) {
                    commandContext.getSource().sendFeedback(ChatFormatter.stringFormatWithPrefixToText("&7La saturación de &6" + players.size() + " &7 jugadores ha sido modificada correctamente."), true);
                } else {
                    commandContext.getSource().sendFeedback(ChatFormatter.stringFormatWithPrefixToText("&7La saturación del jugador &6" + player.getName().asString() + " &7ha sido modificada correctamente."), true);
                }
            });

            return 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }
}
