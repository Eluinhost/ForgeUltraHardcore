package gg.uhc.ultrahardcore.features;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class GhastDropFeature {

    @SubscribeEvent
    public void onDeath(LivingDropsEvent event) {
        if (!(event.entity instanceof EntityGhast)) return;

        List<EntityItem> drops = event.drops;

        // replace all ghast tears with gold ingots
        for (EntityItem item : drops) {
            ItemStack stack = item.getEntityItem();
            if (stack.getItem().equals(Items.ghast_tear)) {
                stack.setItem(Items.gold_ingot);
            }
        }
    }
}
