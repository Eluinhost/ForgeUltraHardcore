package gg.uhc.ultrahardcore.features.recipe;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.util.List;

public class PacketedResultRecipe implements IRecipe {

    protected final IRecipe original;
    protected final ItemStack replacement;

    public PacketedResultRecipe(IRecipe original, ItemStack replacement) {
        this.original = original;
        this.replacement = replacement;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        return original.matches(inv, world);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        // force update, 0 is results slot
        S2FPacketSetSlot packet = new S2FPacketSetSlot(inv.eventHandler.windowId, 0, replacement);

        List<EntityPlayerMP> players = playersWithWindowOpen(inv.eventHandler.windowId);

        for (EntityPlayerMP player : players) {
            player.playerNetServerHandler.sendPacket(packet);
        }

        return replacement.copy();
    }

    protected static List<EntityPlayerMP> playersWithWindowOpen(int windowId) {
        List<EntityPlayerMP> players = Lists.newArrayList();

        List<EntityPlayerMP> playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList;

        for (EntityPlayerMP player : playerList) {
            if (player.currentWindowId == windowId) {
                players.add(player);
            }
        }

        return players;
    }

    @Override
    public int getRecipeSize() {
        return original.getRecipeSize();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return replacement;
    }

    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting inv) {
        return original.getRemainingItems(inv);
    }
}
