package gg.uhc.ultrahardcore.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;

public class CommandFeed extends CommandOneOrAllPlayers {

    protected final ChatComponentText confirmation = new ChatComponentText("[UHC] All player/s fed");
    protected final ChatComponentText notice = new ChatComponentText("[UHC] You were fed to full hunger");

    @Override
    public String getCommandName() {
        return "feed";
    }

    @Override
    public void runForPlayer(EntityPlayer player) {
        FoodStats stats = player.getFoodStats();

        // set to max food level + saturation
        stats.addStats(20, 1);

        player.addChatMessage(notice);
    }

    @Override
    public IChatComponent getConfirmation() {
        return confirmation;
    }
}