package gg.uhc.ultrahardcore;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = UltraHardcore.MODID, version = UltraHardcore.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*")
public class UltraHardcore {
    public static final String MODID = "ultrahardcore";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        for (Feature feature : Feature.values()) {
            for (Object o : feature.handlers.values()) {
                MinecraftForge.EVENT_BUS.register(o);
            }
        }
    }

    @EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        for (Command command : Command.values()) {
            event.registerServerCommand(command.command);
        }
    }
}
