package gg.uhc.ultrahardcore.features;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PotionNerfs {

    public PotionNerfs() {
        PotionNerfedAttackDamage potion = new PotionNerfedAttackDamage();
        potion.setPotionName("potion.damageBoost");
        potion.registerPotionAttributeModifier(SharedMonsterAttributes.attackDamage, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 1.5D, 0);

        Potion.damageBoost = potion;
    }

    @SubscribeEvent
    public void on(PotionBrewEvent.Pre event) {
        if (event.getItem(3).getItem().equals(Items.gunpowder)) event.setCanceled(true);
    }

    class PotionNerfedAttackDamage extends Potion {
        protected PotionNerfedAttackDamage() {
            super(5, new ResourceLocation("strength"), false, 9643043);
        }

        public double getAttributeModifierAmount(int p_111183_1_, AttributeModifier modifier) {
            return modifier.getAmount() * (double)(p_111183_1_ + 1);
        }
    }
}
