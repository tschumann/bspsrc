package info.ata4.bspsrc.lib.vector;

public final class Vector3d extends VectorXd<Vector3d> {
    
    // frequently used pre-defined vectors
    public static final Vector3d NULL = new Vector3d(0, 0, 0);
    public static final Vector3d MAX_VALUE = new Vector3d(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Vector3d MIN_VALUE = MAX_VALUE.scalar(-1); // don't use Float.MIN_VALUE here

    // base unit vectors
    public static final Vector3d BASE_VECTOR_X = new Vector3d(1, 0, 0);
    public static final Vector3d BASE_VECTOR_Y = new Vector3d(0, 1, 0);
    public static final Vector3d BASE_VECTOR_Z = new Vector3d(0, 0, 1);
    
    public Vector3d(double x, double y, double z) {
        super(new double[]{x, y, z});
    }
    
    private Vector3d(double[] storage) {
        super(storage);
    }
    
    public static Vector3d fromFloat(Vector3f vector) {
        return vector == null ? null : vector.toDouble();
    }

    @Override
    protected Vector3d copy() {
        return new Vector3d(storage.clone());
    }
    
    public double x() { return storage[0]; }
    public double y() { return storage[1]; }
    public double z() { return storage[2]; }
    
    public Vector3d withX(double value) { return with(0, value); }
    public Vector3d withY(double value) { return with(1, value); }
    public Vector3d withZ(double value) { return with(2, value); }
    
    /// @param that the vector to take a cross product with.
    /// @return A new vector instance representing the cross-product vector.
    public Vector3d cross(Vector3d that) {
        var rx = this.y() * that.z() - this.z() * that.y();
        var ry = this.z() * that.x() - this.x() * that.z();
        var rz = this.x() * that.y() - this.y() * that.x();

        return new Vector3d(rx, ry, rz);
    }

    /// Performs an **extrinsic (X-Y-Z)** rotation sequence in a **right-handed** coordinate system.
    /// 
    /// @param angles A vector where x, y, z are the rotation angles in **degrees** for the respective axes.
    /// @return A new, rotated vector instance.
    public Vector3d rotate(Vector3d angles) {
        if (angles.x() == 0 && angles.y() == 0 && angles.z() == 0) {
            return this;
        }

        var vx = x();
        var vy = y();
        var vz = z();

        var phi_x = Math.toRadians(angles.x());
        var phi_y = Math.toRadians(angles.y());
        var phi_z = Math.toRadians(angles.z());

        var cx = Math.cos(phi_x);
        var sx = Math.sin(phi_x);
        var cy = Math.cos(phi_y);
        var sy = Math.sin(phi_y);
        var cz = Math.cos(phi_z);
        var sz = Math.sin(phi_z);
        
        var r00 = cz * cy;
        var r01 = cz * sy * sx - sz * cx;
        var r02 = cz * sy * cx + sz * sx;
        var r10 = sz * cy;
        var r11 = sz * sy * sx + cz * cx;
        var r12 = sz * sy * cx - cz * sx;
        var r20 = -sy;
        var r21 = cy * sx;
        var r22 = cy * cx;

        var rotated_vx = r00 * vx + r01 * vy + r02 * vz;
        var rotated_vy = r10 * vx + r11 * vy + r12 * vz;
        var rotated_vz = r20 * vx + r21 * vy + r22 * vz;

        return new Vector3d(rotated_vx, rotated_vy, rotated_vz);
    }
}
