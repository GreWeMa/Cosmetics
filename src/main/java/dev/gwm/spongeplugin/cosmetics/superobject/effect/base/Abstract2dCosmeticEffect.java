package dev.gwm.spongeplugin.cosmetics.superobject.effect.base;

import com.flowpowered.math.vector.Vector3d;
import dev.gwm.spongeplugin.cosmetics.util.Vector3dable;
import dev.gwm.spongeplugin.cosmetics.util.Viewerable;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.world.Locatable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Abstract2dCosmeticEffect extends BaseCosmeticEffect {

    public Abstract2dCosmeticEffect(ConfigurationNode node) {
        super(node);
    }

    public Abstract2dCosmeticEffect(String id, Optional<Long> delay, Optional<Vector3d> offset,
                                    ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor) {
        super(id, delay, offset, particleEffect, perAnimationColor, perParticleColor);
    }

    @Override
    public Runnable createTask(Viewerable viewerable, Locatable locatable, Vector3dable customOffset) {
        return new DotsEffectRunnable(viewerable, locatable, getOffset(), customOffset);
    }

    protected Map<Vector3d, ParticleEffect> colorDots(Map<Vector3d, ParticleEffect> dots) {
        if (isPerParticleColor()) {
            return dots.keySet().
                    stream().
                    collect(Collectors.toMap(Function.identity(), it -> getColoredParticleEffect()));
        } else if (isPerAnimationColor()){
            ParticleEffect particleEffect = getColoredParticleEffect();
            return dots.keySet().
                    stream().
                    collect(Collectors.toMap(Function.identity(), it -> particleEffect));
        } else {
            return dots;
        }
    }

    protected abstract Map<Vector3d, ParticleEffect> getDots();

    protected abstract Vector3d getRotation();

    protected class DotsEffectRunnable extends AbstractEffectRunnable {

        //https://en.wikipedia.org/wiki/Rotation_formalisms_in_three_dimensions#Euler_angles_.28_z-y.E2.80.99-x.E2.80.B3_intrinsic.29_.E2.86.92_Rotation_matrix
        private Vector3d rotate(Vector3d vector, Double xDegrees, Double yDegrees, Double zDegrees) {
            double cosX = Math.cos(Math.toRadians(xDegrees));
            double sinX = Math.sin(Math.toRadians(xDegrees));
            double cosY = Math.cos(Math.toRadians(yDegrees));
            double sinY = Math.sin(Math.toRadians(yDegrees));
            double cosZ = Math.cos(Math.toRadians(zDegrees));
            double sinZ = Math.sin(Math.toRadians(zDegrees));
            double x = vector.getX() * (cosY * cosZ) +
                    vector.getY() * (-cosX * sinZ + sinX * sinY * cosZ) +
                    vector.getZ() * (sinX * sinZ + cosX * sinY * cosZ);
            double y = vector.getX() * (cosY * sinZ) +
                    vector.getY() * (cosX * cosZ + sinX * sinY * sinZ) +
                    vector.getZ() * (-sinX * cosZ + cosX * sinY * sinZ);
            double z = vector.getX() * (-sinY) +
                    vector.getY() * (sinX * cosY) +
                    vector.getZ() * (cosX * cosY);
            return new Vector3d(x, y, z);
        }

        public DotsEffectRunnable(Viewerable viewerable, Locatable locatable, Vector3d offset, Vector3dable customOffset) {
            super(viewerable, locatable, offset, customOffset);
        }

        @Override
        public void run() {
            Viewer viewer = getViewerable().getViewer();
            Vector3d rotationAngles = getRotation();
            Vector3d center = getPosition();
            for (Map.Entry<Vector3d, ParticleEffect> entry : getDots().entrySet()) {
                Vector3d dot = entry.getKey();
                ParticleEffect particleEffect = entry.getValue();
                Vector3d point = center.add(rotate(dot, rotationAngles.getX(), rotationAngles.getY(), rotationAngles.getZ()));
                viewer.spawnParticles(particleEffect, point);
            }
        }
    }
}