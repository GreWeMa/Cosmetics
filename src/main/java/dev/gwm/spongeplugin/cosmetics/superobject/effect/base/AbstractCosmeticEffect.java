package dev.gwm.spongeplugin.cosmetics.superobject.effect.base;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.util.CosmeticsSuperObjectCategories;
import dev.gwm.spongeplugin.cosmetics.util.Vector3dable;
import dev.gwm.spongeplugin.cosmetics.util.Viewerable;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.superobject.AbstractSuperObject;
import dev.gwm.spongeplugin.library.util.GWMLibraryUtils;
import dev.gwm.spongeplugin.library.util.SuperObjectCategory;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.world.Locatable;

import java.util.Optional;

public abstract class AbstractCosmeticEffect extends AbstractSuperObject implements CosmeticEffect {

    private final long delay;
    private final Vector3d offset;

    public AbstractCosmeticEffect(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode delayNode = node.getNode("DELAY");
            ConfigurationNode offsetNode = node.getNode("OFFSET");
            if (delayNode.isVirtual()) {
                delay = defaultDelay();
            } else {
                delay = delayNode.getLong();
            }
            if (delay <= 0) {
                throw new IllegalArgumentException("Delay is equal to or less than 0!");
            }
            if (offsetNode.isVirtual()) {
                offset = defaultOffset();
            } else {
                offset = GWMLibraryUtils.parseVector3d(offsetNode);
            }
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public AbstractCosmeticEffect(String id,
                                  Optional<Long> delay, Optional<Vector3d> offset) {
        super(id);
        if (delay.isPresent() && delay.get() <= 0) {
            throw new IllegalArgumentException("Delay is equal to or less than 0!");
        }
        this.delay = delay.orElse(defaultDelay());
        this.offset = offset.orElse(defaultOffset());
    }

    @Override
    public final SuperObjectCategory<CosmeticEffect> category() {
        return CosmeticsSuperObjectCategories.COSMETIC_EFFECT;
    }

    @Override
    public long getDelay() {
        return delay;
    }

    @Override
    public Vector3d getOffset() {
        return offset;
    }

    protected static abstract class AbstractEffectRunnable implements Runnable {

        private final Viewerable viewerable;
        private final Locatable locatable;
        private final Vector3dable offset;

        public AbstractEffectRunnable(Viewerable viewerable, Locatable locatable, Vector3dable offset) {
            this.viewerable = viewerable;
            this.locatable = locatable;
            this.offset = offset;
        }

        public Vector3d getPosition() {
            return locatable.getLocation().getPosition().add(offset.getVector3d());
        }

        public Viewerable getViewerable() {
            return viewerable;
        }

        public Locatable getLocatable() {
            return locatable;
        }

        public Vector3dable getOffset() {
            return offset;
        }
    }
}
