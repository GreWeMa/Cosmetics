package dev.gwm.spongeplugin.cosmetics.superobject.effect.base;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.Cosmetics;
import dev.gwm.spongeplugin.cosmetics.util.CosmeticsSuperObjectCategories;
import dev.gwm.spongeplugin.cosmetics.util.Vector3dable;
import dev.gwm.spongeplugin.cosmetics.util.Viewerable;
import dev.gwm.spongeplugin.library.superobject.SuperObject;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Locatable;

public interface CosmeticEffect extends SuperObject {

    @Override
    default SuperObjectCategory<CosmeticEffect> category() {
        return CosmeticsSuperObjectCategories.COSMETIC_EFFECT;
    }

    default void play(Viewerable viewerable, Locatable locatable, Vector3dable customOffset) {
        createTask(viewerable, locatable, customOffset).run();
    }

    default Task activate(Viewerable viewerable, Locatable locatable, Vector3dable customOffset) {
        return Sponge.getScheduler().createTaskBuilder().
                execute(createTask(viewerable, locatable, customOffset)).
                intervalTicks(getDelay()).
                submit(Cosmetics.getInstance());
    }

    Runnable createTask(Viewerable viewerable, Locatable locatable, Vector3dable customOffset);

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
