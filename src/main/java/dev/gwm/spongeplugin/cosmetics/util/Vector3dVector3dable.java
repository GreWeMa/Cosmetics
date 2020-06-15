package dev.gwm.spongeplugin.cosmetics.util;

import com.flowpowered.math.vector.Vector3d;

public class Vector3dVector3dable implements Vector3dable {

    private final Vector3d vector3d;

    public Vector3dVector3dable(Vector3d vector3d) {
        this.vector3d = vector3d;
    }

    @Override
    public Vector3d getVector3d() {
        return vector3d;
    }
}
