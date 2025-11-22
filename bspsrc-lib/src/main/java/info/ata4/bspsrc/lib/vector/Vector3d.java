package info.ata4.bspsrc.lib.vector;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    /// @param angles A vector where x, y, z are the rotation angles in **degrees** for the respective axes.
    /// @return A new, rotated vector instance.
    public Vector3d rotate(Vector3d angles) {
        if (angles.x() == 0 && angles.y() == 0 && angles.z() == 0) {
            // nothing to do here
            return this;
        }

        // Input vector components
        final double vx = x();
        final double vy = y();
        final double vz = z();

        // 1. Convert clockwise degrees to counter-clockwise radians (standard convention)
        // Clockwise rotation by angle 'theta' is equivalent to CCW rotation by '-theta'.
        final double phi_x = -Math.toRadians(angles.x());
        final double phi_y = -Math.toRadians(angles.y());
        final double phi_z = -Math.toRadians(angles.z());

        // 2. Pre-calculate sines and cosines for efficiency
        final double cx = Math.cos(phi_x);
        final double sx = Math.sin(phi_x);
        final double cy = Math.cos(phi_y);
        final double sy = Math.sin(phi_y);
        final double cz = Math.cos(phi_z);
        final double sz = Math.sin(phi_z);

        // 3. The Combined Rotation Matrix R is Rz * Ry * Rx (Extrinsic Z-Y-X).
        // The rotated vector components (v'x, v'y, v'z) are calculated by multiplying
        // the vector (vx, vy, vz) by the explicit terms of the combined matrix R.

        // --- Row 0 of R (v'_x calculation) ---
        // R[0][0] = cz * cy
        final double R00 = cz * cy;
        // R[0][1] = cz*sy*sx - sz*cx
        final double R01 = cz * sy * sx - sz * cx;
        // R[0][2] = cz*sy*cx + sz*sx
        final double R02 = cz * sy * cx + sz * sx;

        final double rotated_vx = R00 * vx + R01 * vy + R02 * vz;

        // --- Row 1 of R (v'_y calculation) ---
        // R[1][0] = sz * cy
        final double R10 = sz * cy;
        // R[1][1] = sz*sy*sx + cz*cx
        final double R11 = sz * sy * sx + cz * cx;
        // R[1][2] = sz*sy*cx - cz*sx
        final double R12 = sz * sy * cx - cz * sx;

        final double rotated_vy = R10 * vx + R11 * vy + R12 * vz;

        // --- Row 2 of R (v'_z calculation) ---
        // R[2][0] = -sy
        final double R20 = -sy;
        // R[2][1] = cy*sx
        final double R21 = cy * sx;
        // R[2][2] = cy*cx
        final double R22 = cy * cx;

        final double rotated_vz = R20 * vx + R21 * vy + R22 * vz;

        // 4. Return the new vector
        return new Vector3d(rotated_vx, rotated_vy, rotated_vz);
    }

    /// Projects this 3D vector onto a 2D plane defined by an origin point and two
    /// orthogonal axis vectors.
    ///
    /// @param origin The origin point of the 2D plane.
    /// @param axis1 The X-axis of the 2D plane (must be normalized and orthogonal to axis2).
    /// @param axis2 The Y-axis of the 2D plane (must be normalized and orthogonal to axis1).
    /// @return A new `Vector2f` representing the projected point on the plane.
    public Vector2f projectOnPlane(Vector3d origin, Vector3d axis1, Vector3d axis2) {
        throw new UnsupportedOperationException();
    }
    
    public Vector3d round(int decimalPlaces) {
        return new Vector3d(
                new BigDecimal(x()).setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue(),
                new BigDecimal(y()).setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue(),
                new BigDecimal(z()).setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue()
        );
    }
}
