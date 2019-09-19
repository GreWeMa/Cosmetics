package dev.gwm.spongeplugin.cosmetics.superobject.effect.base;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.Cosmetics;
import dev.gwm.spongeplugin.cosmetics.utils.CosmeticsSuperObjectCategories;
import dev.gwm.spongeplugin.cosmetics.utils.LocationLocatable;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.utils.SuperObjectCategory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface CosmeticEffect extends SuperObject {

    @Override
    default SuperObjectCategory<CosmeticEffect> category() {
        return CosmeticsSuperObjectCategories.COSMETIC_EFFECT;
    }

    default void play(Viewer viewer, Locatable locatable, Vector3d offset) {
        createTask(viewer, locatable, offset).run();
    }

    default void play(Viewer viewer, Locatable locatable) {
        play(viewer, locatable, getOffset());
    }

    default void play(Viewer viewer, Location<World> location, Vector3d offset) {
        play(viewer, new LocationLocatable(location), offset);
    }

    default void play(Viewer viewer, Location<World> location) {
        play(viewer, location, getOffset());
    }

    default Task activate(Viewer viewer, Locatable locatable, Vector3d offset) {
        return Sponge.getScheduler().createTaskBuilder().
                execute(createTask(viewer, locatable, offset)).
                intervalTicks(getDelay()).
                submit(Cosmetics.getInstance());
    }

    default Task activate(Viewer viewer, Locatable locatable) {
        return activate(viewer, locatable, getOffset());
    }

    default Task activate(Viewer viewer, Location<World> location, Vector3d offset) {
        return activate(viewer, new LocationLocatable(location), offset);
    }

    default Task activate(Viewer viewer, Location<World> location) {
        return activate(viewer, location, getOffset());
    }

    Runnable createTask(Viewer viewer, Locatable locatable, Vector3d offset);

    default String getId() {
        return id().get();
    }

    default long getDelay() {
        return defaultDelay();
    }

    default Vector3d getOffset() {
        return defaultOffset();
    }

    default Vector3d defaultOffset() {
        return Vector3d.ZERO;
    }

    default long defaultDelay() {
        return 1;
    }
}
