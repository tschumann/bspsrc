package info.ata4.bspsrc.lib.vector;

import info.ata4.io.DataReader;
import info.ata4.io.DataWriter;

import java.io.IOException;

/// An immutable three-dimensional vector class for float values.
/// This class extends [VectorXf] for 3 components (x, y, z) and provides
/// 3D-specific operations like cross product and rotation (pitch, yaw, roll).
/// All modification methods return a new `Vector3f` instance.
public final class Vector3f extends VectorXf<Vector3f> {

    /// Reads three float values (x, y, z) from the provided [DataReader]
    /// and constructs a new `Vector3f`.
    ///
    /// @param in The data reader to read from.
    /// @return A new `Vector3f` instance.
    /// @throws IOException If an I/O error occurs.
    public static Vector3f read(DataReader in) throws IOException {
        float x = in.readFloat();
        float y = in.readFloat();
        float z = in.readFloat();
        return new Vector3f(x, y, z);
    }

    /// Writes the x, y, and z components of the given vector to the provided
    /// [DataWriter].
    ///
    /// @param out The data writer to write to.
    /// @param vec The vector to write.
    /// @throws IOException If an I/O error occurs.
    public static void write(DataWriter out, Vector3f vec) throws IOException {
        out.writeFloat(vec.x());
        out.writeFloat(vec.y());
        out.writeFloat(vec.z());
    }

    // frequently used pre-defined vectors
    public static final Vector3f NULL = new Vector3f(0, 0, 0);
    public static final Vector3f MAX_VALUE = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Vector3f MIN_VALUE = MAX_VALUE.scalar(-1); // don't use Float.MIN_VALUE here

    // base unit vectors
    public static final Vector3f BASE_VECTOR_X = new Vector3f(1, 0, 0);
    public static final Vector3f BASE_VECTOR_Y = new Vector3f(0, 1, 0);
    public static final Vector3f BASE_VECTOR_Z = new Vector3f(0, 0, 1);

    /// Constructs a new Vector3f from x, y and z components.
    ///
    /// @param x the vector x component
    /// @param y the vector y component
    /// @param z the vector z component
    public Vector3f(float x, float y, float z) {
        super(new float[]{x, y, z});
    }

    private Vector3f(float[] storage) {
        super(storage);
    }

    /// Creates a new `Vector3f` from a float array.
    ///
    /// @param array The float array (must have a length of 3).
    /// @return A new `Vector3f` instance.
    /// @throws IllegalArgumentException if the array length is not 3.
    public static Vector3f fromArray(float[] array) {
        verifySize(array, 3);
        return new Vector3f(array.clone());
    }

    @Override
    protected Vector3f copy() {
        return new Vector3f(storage.clone());
    }

    /// @return The X component of the vector.
    public float x() { return storage[0]; }
    /// @return The Y component of the vector.
    public float y() { return storage[1]; }
    /// @return The Z component of the vector.
    public float z() { return storage[2]; }

    /// @param value The new float value for the X component.
    /// @return A new vector with the X component set to the given value.
    public Vector3f withX(float value) { return with(0, value); }
    /// @param value The new float value for the Y component.
    /// @return A new vector with the Y component set to the given value.
    public Vector3f withY(float value) { return with(1, value); }
    /// @param value The new float value for the Z component.
    /// @return A new vector with the Z component set to the given value.
    public Vector3f withZ(float value) { return with(2, value); }
    
    /// @param that the vector to take a cross product with.
    /// @return A new vector instance representing the cross-product vector.
    public Vector3f cross(Vector3f that) {
        var rx = this.y() * that.z() - this.z() * that.y();
        var ry = this.z() * that.x() - this.x() * that.z();
        var rz = this.x() * that.y() - this.y() * that.x();

        return new Vector3f(rx, ry, rz);
    }

    /// Performs an **extrinsic (X-Y-Z)** rotation sequence in a **right-handed** coordinate system.
    ///
    /// @param angles A vector where x, y, z are the rotation angles in **degrees** for the respective axes.
    /// @return A new, rotated vector instance.
    public Vector3f rotate(Vector3f angles) {
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

        return new Vector3f(
                (float) rotated_vx,
                (float) rotated_vy,
                (float) rotated_vz
        );
    }

    /// Projects this 3D vector onto a 2D plane defined by an origin point and two
    /// orthogonal axis vectors.
    /// 
    /// @param origin The origin point of the 2D plane.
    /// @param axis1 The X-axis of the 2D plane (must be normalized and orthogonal to axis2).
    /// @param axis2 The Y-axis of the 2D plane (must be normalized and orthogonal to axis1).
    /// @return A new `Vector2f` representing the projected point on the plane.
    public Vector2f projectOnPlane(Vector3f origin, Vector3f axis1, Vector3f axis2) {
        return new Vector2f(
                axis1.dot(this.sub(origin)),
                axis2.dot(this.sub(origin))
        );
    }
    
    public Vector3d toDouble() {
        return new Vector3d(x(), y(), z());
    }
}