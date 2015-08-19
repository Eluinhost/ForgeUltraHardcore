package gg.uhc.ultrahardcore.commands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class CommandHeal extends CommandOneOrAllPlayers {

    protected final ChatComponentText confirmation = new ChatComponentText("[UHC] All player/s healed");
    protected final ChatComponentText notice = new ChatComponentText("[UHC] You were healed to full health");

    @Override
    public String getCommandName() {
        return "heal";
    }

    @Override
    public void runForPlayer(EntityPlayer player) {
        player.setHealth(player.getMaxHealth());
        player.addChatMessage(notice);
    }

    @Override
    public IChatComponent getConfirmation() {
        return confirmation;
    }
}