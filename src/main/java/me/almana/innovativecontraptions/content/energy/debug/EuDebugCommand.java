package me.almana.innovativecontraptions.content.energy.debug;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.almana.innovativecontraptions.InnovativeContraptions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public final class EuDebugCommand {

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal(InnovativeContraptions.MODID).then(
                        Commands.literal("eudebug").executes(EuDebugCommand::toggle)));
        event.getDispatcher().register(
                Commands.literal("ic").then(
                        Commands.literal("eudebug").executes(EuDebugCommand::toggle)));
    }

    private static int toggle(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        boolean on = EuDebugState.toggle(player.getUUID());
        if (!on) {
            PacketDistributor.sendToPlayer(player, new EuDebugPayload(List.of(), List.of()));
        }
        player.sendSystemMessage(Component.literal("EU debug overlay: " + (on ? "ON" : "OFF")), true);
        return 1;
    }

    private EuDebugCommand() {
    }
}
