package dev.gwm.spongeplugin.cosmetics.superobject.effect.base;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.utils.CosmeticsUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.world.Locatable;

import java.util.Optional;

public abstract class Abstract2dCosmeticEffect extends BaseCosmeticEffect {

    public Abstract2dCosmeticEffect(ConfigurationNode node) {
        super(node);
    }

    public Abstract2dCosmeticEffect(String id, Optional<Long> delay, Optional<Vector3d> offset,
                                    ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor) {
        super(id, delay, offset, particleEffect, perAnimationColor, perParticleColor);
    }

    @Override
    public Runnable createTask(Viewer viewer, Locatable locatable, Vector3d offset) {
        return new DotsEffectRunnable(viewer, locatable, offset);
    }

    protected abstract boolean[][] getDots();

    protected abstract double getHorizontalParticleDistance();

    protected abstract double getVerticalParticleDistance();

    protected abstract double getX1Multiplier();

    protected abstract double getX2Multiplier();

    protected abstract double getY1Multiplier();

    protected abstract double getY2Multiplier();

    protected abstract double getZ1Multiplier();

    protected abstract double getZ2Multiplier();

    protected class DotsEffectRunnable extends AbstractEffectRunnable {

        public DotsEffectRunnable(Viewer viewer, Locatable locatable, Vector3d offset) {
            super(viewer, locatable, offset);
        }

        @Override
        public void run() {
            Vector3d position = getPosition();
            ParticleEffect effect = isPerAnimationColor() ?
                    ParticleEffect.builder().
                            from(getParticleEffect()).
                            option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                            build() :
                    getParticleEffect();
            for (int i = 0; i < getDots().length; i++) {
                boolean[] array = getDots()[i];
                for (int j = 0; j < array.length; j++) {
                    if (isPerParticleColor()) {
                        effect = ParticleEffect.builder().
                                from(getParticleEffect()).
                                option(ParticleOptions.COLOR, CosmeticsUtils.getRandomColor()).
                                build();
                    }
                    if (array[j]) {
                        //TODO learn math and implement rotation on X and Z axises.
                        //Plz help.
                        getViewer().spawnParticles(effect, position.
                                add(getY1Multiplier() * j * getHorizontalParticleDistance(),
                                        i * getVerticalParticleDistance(),
                                        getY2Multiplier() * j * getHorizontalParticleDistance()));
                    }
                }
            }
        }
    }
}