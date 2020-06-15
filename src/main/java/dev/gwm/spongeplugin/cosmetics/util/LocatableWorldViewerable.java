package dev.gwm.spongeplugin.cosmetics.util;

import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.world.Locatable;

public class LocatableWorldViewerable implements Viewerable {

    private final Locatable locatable;

    public LocatableWorldViewerable(Locatable locatable) {
        this.locatable = locatable;
    }

    @Override
    public Viewer getViewer() {
        return locatable.getWorld();
    }

    public Locatable getLocatable() {
        return locatable;
    }
}
