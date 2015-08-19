package gg.uhc.ultrahardcore.features.freeze;

import com.google.common.base.Preconditions;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class FreezeLocationProperty implements IExtendedEntityProperties {
    public static final String PROPERTY_NAME = "UHCLastFreezeLocation";

    protected Entity tracked;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected boolean initialized = false;

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        // don't save
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        // don't restore
    }

    @Override
    public void init(Entity entity, World world) {
        this.tracked = entity;
    }

    public void setLocationAsNewFreeze() {
        posX = tracked.posX;
        posY = tracked.posY;
        posZ = tracked.posZ;

        initialized = true;
    }

    public boolean isFirstLocationSet() {
        return initialized;
    }

    public double getX() {
        return posX;
    }

    public double getY() {
        return posY;
    }

    public double getZ() {
        return posZ;
    }

    public static FreezeLocationProperty getFrom(Entity entity) {
        IExtendedEntityProperties properties = entity.getExtendedProperties(PROPERTY_NAME);
        Preconditions.checkArgument(properties != null, "Entity " + entity + " does not contain a last freeze location property");
        Preconditions.checkArgument(properties instanceof FreezeLocationProperty, "Entity " + entity + " has a freeze location property with an incorrect class");

        return (FreezeLocationProperty) properties;
    }

}
