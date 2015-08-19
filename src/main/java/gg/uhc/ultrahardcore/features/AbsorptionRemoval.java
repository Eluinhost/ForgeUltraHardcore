package gg.uhc.ultrahardcore.features;

import net.minecraft.init.Items;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AbsorptionRemoval {

    @SubscribeEvent
    public void on(PlayerUseItemEvent.Finish event) {
        if (event.item.getItem() == Items.golden_apple) {
            event.entityPlayer.removePotionEffect(Potion.absorption.getId());
        }
    }
}
