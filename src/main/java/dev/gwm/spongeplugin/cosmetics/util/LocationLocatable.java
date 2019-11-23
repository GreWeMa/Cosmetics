package dev.gwm.spongeplugin.cosmetics.util;

import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class LocationLocatable implements Locatable {

    private final Location<World> location;

    public LocationLocatable(Location<World> location) {
        this.location = location;
    }

    @Override
    public Location<World> getLocation() {
        return location;
    }
}
