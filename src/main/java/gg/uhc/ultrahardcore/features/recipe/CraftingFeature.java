package gg.uhc.ultrahardcore.features.recipe;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Collection;
import java.util.List;

public class CraftingFeature {

    // ghosting of recipe clientside
    // FIXED - recipe output via PacketedResultRecipe
    // TODO - recipe input after taking output out, no idea how to fix

    protected static final ItemStack REPLACED_WITH_INGOTS = new ItemStack(Items.paper).setStackDisplayName("UNCRAFTABLE: USE GOLDEN INGOTS INSTEAD");
    protected static final ItemStack REMOVED = new ItemStack(Items.paper).setStackDisplayName("UNCRAFTABLE: ITEM REMOVED");

    protected static final ItemStack GOLDEN_INGOT = new ItemStack(Items.gold_ingot);
    protected static final ItemStack GOLDEN_CARROT = new ItemStack(Items.golden_carrot);
    protected static final ItemStack GLISTERING_MELON = new ItemStack(Items.speckled_melon);
    protected static final ItemStack CARROT = new ItemStack(Items.carrot);
    protected static final ItemStack MELON = new ItemStack(Items.melon);

    protected static final IRecipe NEW_GOLDEN_CARROT = new ShapedRecipes(3, 3,
            new ItemStack[] {
                    GOLDEN_INGOT, GOLDEN_INGOT, GOLDEN_INGOT,
                    GOLDEN_INGOT, CARROT,       GOLDEN_INGOT,
                    GOLDEN_INGOT, GOLDEN_INGOT, GOLDEN_INGOT
            },
            GOLDEN_CARROT
    );

    protected static final IRecipe NEW_GLISTERING_MELON = new ShapedRecipes(3, 3,
            new ItemStack[] {
                    GOLDEN_INGOT, GOLDEN_INGOT, GOLDEN_INGOT,
                    GOLDEN_INGOT, MELON,        GOLDEN_INGOT,
                    GOLDEN_INGOT, GOLDEN_INGOT, GOLDEN_INGOT
            },
            GLISTERING_MELON
    );

    public CraftingFeature() {
        FMLCommonHandler.instance().bus().register(this);

        CraftingManager crafting = CraftingManager.getInstance();

        List<IRecipe> recipeList = crafting.getRecipeList();

        Multimap<Item, IRecipe> recipes = HashMultimap.create();

        for(IRecipe recipe : recipeList) {
            ItemStack output = recipe.getRecipeOutput();

            if (output != null) recipes.put(output.getItem(), recipe);
        }

        // handle glistering melons
        Collection<IRecipe> melonRecipes = recipes.get(Items.speckled_melon);

        recipeList.removeAll(melonRecipes);
        for (IRecipe recipe : melonRecipes) {
            crafting.addRecipe(new PacketedResultRecipe(recipe, REPLACED_WITH_INGOTS));
        }

        // handle golden carrots
        Collection<IRecipe> carrotRecipes = recipes.get(Items.golden_carrot);

        recipeList.removeAll(carrotRecipes);
        for (IRecipe recipe : carrotRecipes) {
            crafting.addRecipe(new PacketedResultRecipe(recipe, REPLACED_WITH_INGOTS));
        }

        // handle notch apples
        for (IRecipe recipe : recipes.get(Items.golden_apple)) {
            // only remove notch apples
            if (recipe.getRecipeOutput().getItemDamage() == 1) {
                recipeList.remove(recipe);
                crafting.addRecipe(new PacketedResultRecipe(recipe, REMOVED));
            }
        }

        // add the new versions of the recipes using a packeted version
        crafting.addRecipe(new PacketedResultRecipe(NEW_GOLDEN_CARROT, GOLDEN_CARROT));
        crafting.addRecipe(new PacketedResultRecipe(NEW_GLISTERING_MELON, GLISTERING_MELON));
    }

    @SubscribeEvent
    public void onCraft(PlayerEvent.ItemCraftedEvent event) {
        System.out.println(event.craftMatrix.getClass());
        if (!(event.craftMatrix instanceof InventoryCrafting)) return;

        InventoryCrafting crafting = (InventoryCrafting) event.craftMatrix;

        if (NEW_GLISTERING_MELON.matches(crafting, event.player.worldObj) || NEW_GOLDEN_CARROT.matches(crafting, event.player.worldObj)) {
            S30PacketWindowItems packet = new S30PacketWindowItems(crafting.eventHandler.windowId, crafting.eventHandler.getInventory());

            // TODO doesn't do anything
            ((EntityPlayerMP) event.player).playerNetServerHandler.sendPacket(packet);
        }
    }
}
