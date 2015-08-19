package gg.uhc.ultrahardcore.commands;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

import java.util.List;

public abstract class CommandOneOrAllPlayers extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "<command> [playerName]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        MinecraftServer minecraftserver = MinecraftServer.getServer();

        // handle heal all
        if (args.length == 0) {
            List<EntityPlayer> players = minecraftserver.getConfigurationManager().playerEntityList;

            for (EntityPlayer player : players) {
                runForPlayer(player);
            }

            sender.addChatMessage(getConfirmation());

            return;
        }

        // handle heal specific player
        GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[0]);

        if (gameprofile == null)
        {
            throw new PlayerNotFoundException();
        }

        EntityPlayer player = minecraftserver.getConfigurationManager().getPlayerByUUID(gameprofile.getId());

        if (player == null) {
            throw new PlayerNotFoundException();
        }

        runForPlayer(player);

        // only send confirmation if it wasnt the sender
        if (!player.equals(sender)) {
            sender.addChatMessage(getConfirmation());
        }

    }

    public abstract void runForPlayer(EntityPlayer player);

    public abstract IChatComponent getConfirmation();

}
