package gg.uhc.ultrahardcore.commands;

import gg.uhc.ultrahardcore.Feature;
import gg.uhc.ultrahardcore.features.freeze.FreezeFeature;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class CommandFreeze extends CommandBase {

    protected static final IChatComponent confirmationFreeze = new ChatComponentText("[UHC] All players are now frozen");
    protected static final IChatComponent confirmationUnfreeze = new ChatComponentText("[UHC] All players are now unfrozen");

    protected static final FreezeFeature freezer = (FreezeFeature) Feature.FREEZE.handlers.getInstance(FreezeFeature.class);

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandName() {
        return "freeze";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "<command> [on|off|toggle]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            freezer.toggleFreeze();
        } else {
            switch (args[0].toLowerCase()) {
                case "on":
                    freezer.freezePlayers();
                    break;
                case "off":
                    freezer.unfreezePlayers();
                    break;
                case "toggle":
                    freezer.toggleFreeze();
                    break;
                default:
                    throw new WrongUsageException("Valid options are: on, off or toggle. No arguments is the same as toggle");
            }
        }

        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(freezer.isFrozen() ? confirmationFreeze : confirmationUnfreeze);
    }
}
