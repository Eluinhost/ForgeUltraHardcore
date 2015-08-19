package gg.uhc.ultrahardcore.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.server.MinecraftServer;

import java.util.List;

public class CommandButcher extends CommandBase {
    @Override
    public String getCommandName() {
        return "butcher";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "<command>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        List<Entity> loaded = MinecraftServer.getServer().getEntityWorld().loadedEntityList;

        for (Entity entity : loaded) {
            if (!(entity instanceof EntityLiving)) continue;

            if (entity instanceof EntityMob || entity instanceof EntityGhast || entity instanceof EntitySlime) entity.setDead();
        }
    }
}
