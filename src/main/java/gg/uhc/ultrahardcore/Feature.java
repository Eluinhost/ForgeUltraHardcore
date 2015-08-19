package gg.uhc.ultrahardcore;

import com.google.common.collect.ImmutableClassToInstanceMap;
import gg.uhc.ultrahardcore.features.AbsorptionRemoval;
import gg.uhc.ultrahardcore.features.PotionNerfs;
import gg.uhc.ultrahardcore.features.recipe.CraftingFeature;
import gg.uhc.ultrahardcore.features.freeze.FreezeFeature;
import gg.uhc.ultrahardcore.features.GhastDropFeature;

public enum Feature {
    GHAST_DROPS(new GhastDropFeature()),
    CRAFTING(new CraftingFeature()),
    FREEZE(new FreezeFeature()),
    POTIONS(new PotionNerfs()),
    ABSORPTION(new AbsorptionRemoval())
    ;

    public final ImmutableClassToInstanceMap handlers;

    Feature(Object... handlers) {
        ImmutableClassToInstanceMap.Builder b = ImmutableClassToInstanceMap.builder();

        for (Object o : handlers) {
            b.put(o.getClass(), o);
        }

        this.handlers = b.build();
    }
}
