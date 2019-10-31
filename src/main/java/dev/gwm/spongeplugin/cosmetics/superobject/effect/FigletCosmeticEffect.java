package dev.gwm.spongeplugin.cosmetics.superobject.effect;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import dev.gwm.spongeplugin.cosmetics.superobject.effect.base.Abstract2dCosmeticEffect;
import dev.gwm.spongeplugin.library.exception.SuperObjectConstructionException;
import dev.gwm.spongeplugin.library.utils.GWMLibraryUtils;
import io.leego.banana.BananaUtils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.effect.particle.ParticleEffect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FigletCosmeticEffect extends Abstract2dCosmeticEffect {

    public static final String TYPE = "FIGLET";

    private final List<String> text;
    private final String font;
    private final boolean[][] dots;
    private final double horizontalParticleDistance;
    private final double verticalParticleDistance;
    private final double x1Multiplier;
    private final double x2Multiplier;
    private final double y1Multiplier;
    private final double y2Multiplier;
    private final double z1Multiplier;
    private final double z2Multiplier;

    public FigletCosmeticEffect(ConfigurationNode node) {
        super(node);
        try {
            ConfigurationNode textNode = node.getNode("TEXT");
            ConfigurationNode textFileNode = node.getNode("TEXT_FILE");
            ConfigurationNode fontNode = node.getNode("FONT");
            ConfigurationNode horizontalParticleDistanceNode = node.getNode("HORIZONTAL_PARTICLE_DISTANCE");
            ConfigurationNode verticalParticleDistanceNode = node.getNode("VERTICAL_PARTICLE_DISTANCE");
            ConfigurationNode xAngleNode = node.getNode("X_ROTATION");
            ConfigurationNode yAngleNode = node.getNode("Y_ROTATION");
            ConfigurationNode zAngleNode = node.getNode("Z_ROTATION");
            if (!textNode.isVirtual()) {
                text = textNode.getList(TypeToken.of(String.class));
            } else if (!textFileNode.isVirtual()) {
                text = new BufferedReader(new FileReader(new File(textFileNode.getString()))).
                        lines().
                        collect(Collectors.toList());
            } else {
                throw new IllegalArgumentException("Both TEXT and TEXT_FILE node do not exist!");
            }
            font = fontNode.getString("Banner");
            dots = convert(text, font);
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

    public FigletCosmeticEffect(String id,
                                Optional<Long> delay, Optional<Vector3d> offset,
                                ParticleEffect particleEffect, boolean perAnimationColor, boolean perParticleColor,
                                List<String> text, String font,
                                double horizontalParticleDistance, double verticalParticleDistance,
                                double xAngle, double yAngle, double zAngle) {
        super(id, delay, offset, particleEffect, perAnimationColor, perParticleColor);
        this.text = text;
        this.font = font;
        dots = convert(text, font);
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

    private boolean[][] convert(List<String> text, String font) {
        try {
            String[] splited = BananaUtils.bananaify(GWMLibraryUtils.joinString(text), font).split("\n");
            boolean[][] dots = new boolean[splited.length][];
            for (int i = splited.length - 1; i >= 0; i--) {
                String string = splited[i];
                boolean[] array = new boolean[string.length()];
                for (int j = 0; j < string.length(); j++) {
                    array[j] = string.charAt(j) != ' ';
                }
                dots[splited.length - 1 - i] = array;
            }
            return dots;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert text", e);
        }
    }

    @Override
    public String type() {
        return TYPE;
    }

    public List<String> getText() {
        return text;
    }

    public String getFont() {
        return font;
    }

    @Override
    public boolean[][] getDots() {
        return dots;
    }

    @Override
    public double getHorizontalParticleDistance() {
        return horizontalParticleDistance;
    }

    @Override
    public double getVerticalParticleDistance() {
        return verticalParticleDistance;
    }

    @Override
    public double getX1Multiplier() {
        return x1Multiplier;
    }

    @Override
    public double getX2Multiplier() {
        return x2Multiplier;
    }

    @Override
    public double getY1Multiplier() {
        return y1Multiplier;
    }

    @Override
    public double getY2Multiplier() {
        return y2Multiplier;
    }

    @Override
    public double getZ1Multiplier() {
        return z1Multiplier;
    }

    @Override
    public double getZ2Multiplier() {
        return z2Multiplier;
    }
}
