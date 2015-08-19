package gg.uhc.ultrahardcore.features.freeze;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class FreezeFeature {

    protected static final AttributeModifier FREEZE_MODIFIER = new AttributeModifier("Frozen entity", -100, 2).setSaved(false);
    protected static final PotionEffect NO_JUMP = new PotionEffect(Potion.jump.getId(), Short.MAX_VALUE, -Byte.MAX_VALUE, false, false);
    protected static final PotionEffect NO_RUN = new PotionEffect(Potion.moveSpeed.getId(), Short.MAX_VALUE, -Byte.MAX_VALUE, false, false);
    protected static final PotionEffect NO_DIG = new PotionEffect(Potion.digSlowdown.getId(), Short.MAX_VALUE, 5, false, false);

    protected static final IChatComponent TOO_FAR_MESSAGE = new ChatComponentText("[UHC] You travelled too far from your freeze position, STAND STILL");

    protected boolean isFrozen = false;

    protected long ticksLived = 0L;

    // TODO block breaks
    // TODO damage
    public FreezeFeature() {
        // register for ticks
        FMLCommonHandler.instance().bus().register(this);
    }

    protected void freezePlayer(EntityPlayer player) {
        // no moving allowed
        player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed).applyModifier(FREEZE_MODIFIER);

        // send fake potions
        ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), NO_JUMP));
        ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), NO_RUN));
        ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), NO_DIG));

        // set last freeze location, should always exist
        FreezeLocationProperty location = FreezeLocationProperty.getFrom(player);
        location.setLocationAsNewFreeze();
    }

    protected void unfreezePlayer(EntityPlayer player) {
        // allow movement again
        player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed).removeModifier(FREEZE_MODIFIER);

        // remove the fake potion
        ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S1EPacketRemoveEntityEffect(player.getEntityId(), NO_JUMP));
        ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S1EPacketRemoveEntityEffect(player.getEntityId(), NO_RUN));
        ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S1EPacketRemoveEntityEffect(player.getEntityId(), NO_DIG));
    }

    protected List<EntityPlayerMP> getOnlinePlayers() {
        return MinecraftServer.getServer().getConfigurationManager().playerEntityList;
    }

    public void freezePlayers() {
        isFrozen = true;
        ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();

        for (EntityPlayer player : getOnlinePlayers()) {
            // don't freeze ops
            if (!manager.canSendCommands(player.getGameProfile())) {
                freezePlayer(player);
            }
        }
    }

    public void setFrozen(boolean freeze) {
        if (freeze) {
            freezePlayers();
        } else {
            unfreezePlayers();
        }
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void toggleFreeze() {
        setFrozen(!isFrozen());
    }

    public void unfreezePlayers() {
        isFrozen = false;
        for (EntityPlayer player : getOnlinePlayers()) {
            unfreezePlayer(player);
        }
    }

    // cancel all damage during a freeze
    @SubscribeEvent
    public void onDamage(LivingHurtEvent event) {
        if (isFrozen && (event.entityLiving instanceof EntityPlayer))  {
            event.setCanceled(true);
        }
    }

    // stop attacks hitting players
    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (isFrozen && event.entity instanceof EntityPlayer) event.setCanceled(true);
    }

    // cancel all player attacks in a freeze
    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if (isFrozen) event.setCanceled(true);
    }

    // stop player interactions (block interactions)
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (isFrozen) event.setCanceled(true);
    }

    // refreeze as required on join
    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent event) {
        if (!isFrozen || !(event.entity instanceof EntityPlayer)) return;

        freezePlayer((EntityPlayer) event.entity);
    }

    // used to add the new property on each player as they are created
    @SubscribeEvent
    public void onConstruct(EntityEvent.EntityConstructing event) {
        if (!(event.entity instanceof EntityPlayer)) return;

        event.entity.registerExtendedProperties(FreezeLocationProperty.PROPERTY_NAME, new FreezeLocationProperty());
    }

    // reposition occasionally on ticks
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        // run every 2 seconds whilst frozen, resets position
        if (isFrozen && event.phase == TickEvent.Phase.START && ticksLived++ % 40 == 0) {
            for (EntityPlayer player : (List<EntityPlayer>) MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
                FreezeLocationProperty location = FreezeLocationProperty.getFrom(player);

                double d1 = player.posX - location.getX();
                double d2 = player.posZ - location.getZ();
                double distanceSq = d1 * d1 + d2 * d2;

                // if travelled further than 4 blocks away in X/Z plane, reset to the original freeze position
                if (distanceSq > 16) {
                    player.setPositionAndUpdate(location.getX(), location.getY(), location.getZ());
                    player.addChatComponentMessage(TOO_FAR_MESSAGE);
                }
            }
        }
    }
}
