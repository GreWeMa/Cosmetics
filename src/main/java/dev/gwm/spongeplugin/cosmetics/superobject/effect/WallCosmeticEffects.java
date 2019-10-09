package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.Abstract2dCosmeticEffect;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.particle.ParticleEffect;

import java.util.List;
import java.util.Optional;

public class WallCosmeticEffects extends Abstract2dCosmeticEffect {

    public static final String TYPE = "WALL";

    private final boolean[][] dots;
    private final double horizontalParticleDistance;
    private final double verticalParticleDistance;
    private final double x1Multiplier;
    private final double x2Multiplier;
    private final double y1Multiplier;
    private final double y2Multiplier;
    private final double z1Multiplier;
    private final double z2Multiplier;

    public WallCosmeticEffects(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode dotsNode = node.getNode("DOTS");
            ConfigurationNode horizontalParticleDistanceNode = node.getNode("HORIZONTAL_PARTICLE_DISTANCE");
            ConfigurationNode verticalParticleDistanceNode = node.getNode("VERTICAL_PARTICLE_DISTANCE");
            ConfigurationNode xAngleNode = node.getNode("X_ROTATION");
            ConfigurationNode yAngleNode = node.getNode("Y_ROTATION");
            ConfigurationNode zAngleNode = node.getNode("Z_ROTATION");
            List<String> dotsLines = dotsNode.getList(TypeToken.of(String.class));
            dots = new boolean[dotsLines.size()][];
            for (int i = dotsLines.size() - 1; i >= 0; i--) {
                String dotLine = dotsLines.get(i);
                boolean[] array = new boolean[dotLine.length()];
                for (int j = 0; j < dotLine.length(); j++) {
                    array[j] = dotLine.charAt(j) != ' ';
                }
                dots[dotsLines.size() - 1 - i] = array;
            }
            horizontalParticleDistance = horizontalParticleDistanceNode.getDouble(0.2);
            if (horizontalParticleDistance < 0 || horizontalParticleDistance >= 360) {
                throw new IllegalArgumentException("Horizontal Particle Distance is either equal to or greater than 360 or less than 0!");
            }
            verticalParticleDistance = verticalParticleDistanceNode.getDouble(0.4);
            if (verticalParticleDistance < 0 || verticalParticleDistance >= 360) {
                throw new IllegalArgumentException("Vertical Particle Distance is either equal to or greater than 360 or less than 0!");
            }
            double xAngle = xAngleNode.getDouble(0);
            if (xAngle < 0 || xAngle >= 360) {
                throw new IllegalArgumentException("X Angle is either equal to or greater than 360 or less than 0!");
            }
            x1Multiplier = Math.cos(Math.toRadians(xAngle));
            x2Multiplier = Math.sin(Math.toRadians(xAngle));
            double yAngle = yAngleNode.getDouble(0);
            if (yAngle < 0 || yAngle >= 360) {
                throw new IllegalArgumentException("Y Angle is either equal to or greater than 360 or less than 0!");
            }
            y1Multiplier = Math.cos(Math.toRadians(yAngle));
            y2Multiplier = Math.sin(Math.toRadians(yAngle));
            double zAngle = zAngleNode.getDouble(0);
            if (zAngle < 0 || zAngle >= 360) {
                throw new IllegalArgumentException("Z Angle is either equal to or greater than 360 or less than 0!");
            }
            z1Multiplier = Math.cos(Math.toRadians(zAngle));
            z2Multiplier = Math.sin(Math.toRadians(zAngle));
        } catch (Exception e) {
            throw new SuperObjectConstructionException(category(), type(), e);
        }
    }

    public WallCosmeticEffects(String id,
                               Optional<Long> delay, Optional<Vector3d> offset,
                               ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor,
                               boolean[][] dots, double horizontalParticleDistance, double verticalParticleDistance,
                               double xAngle, double yAngle, double zAngle) {
        super(id, delay, offset, particleEffect, perAnimationColor, perParticleColor);
        this.dots = dots;
        if (horizontalParticleDistance <= 0) {
            throw new IllegalArgumentException("Horizontal Particle Distance is equal to or less than 0!");
        }
        this.horizontalParticleDistance = horizontalParticleDistance;
        if (verticalParticleDistance <= 0 ) {
            throw new IllegalArgumentException("Vertical Particle Distance is equal to or less than 0!");
        }
        this.verticalParticleDistance = verticalParticleDistance;
        if (xAngle < 0 || xAngle >= 360) {
            throw new IllegalArgumentException("X Angle is either equal to or greater than 360 or less than 0!");
        }
        x1Multiplier = Math.cos(Math.toRadians(xAngle));
        x2Multiplier = Math.sin(Math.toRadians(xAngle));
        if (yAngle < 0 || yAngle >= 360) {
            throw new IllegalArgumentException("Y Angle is either equal to or greater than 360 or less than 0!");
        }
        y1Multiplier = Math.cos(Math.toRadians(yAngle));
        y2Multiplier = Math.sin(Math.toRadians(yAngle));
        if (zAngle < 0 || zAngle >= 360) {
            throw new IllegalArgumentException("Z Angle is either equal to or greater than 360 or less than 0!");
        }
        z1Multiplier = Math.cos(Math.toRadians(zAngle));
        z2Multiplier = Math.sin(Math.toRadians(zAngle));
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    protected boolean[][] getDots() {
        return dots;
    }

    @Override
    protected double getHorizontalParticleDistance() {
        return horizontalParticleDistance;
    }

    @Override
    protected double getVerticalParticleDistance() {
        return verticalParticleDistance;
    }

    @Override
    protected double getX1Multiplier() {
        return x1Multiplier;
    }

    @Override
    protected double getX2Multiplier() {
        return x2Multiplier;
    }

    @Override
    protected double getY1Multiplier() {
        return y1Multiplier;
    }

    @Override
    protected double getY2Multiplier() {
        return y2Multiplier;
    }

    @Override
    protected double getZ1Multiplier() {
        return z1Multiplier;
    }

    @Override
    protected double getZ2Multiplier() {
        return z2Multiplier;
    }
}
