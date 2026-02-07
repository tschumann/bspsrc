package info.ata4.bspsrc.lib.vector;

import info.ata4.io.DataReader;
import info.ata4.io.DataWriter;

import java.io.IOException;

/// An immutable four-dimensional vector class for float values.
/// This class extends [VectorXf] for 4 components (x, y, z, w).
/// All modification methods return a new `Vector4f` instance.
public final class Vector4f extends VectorXf<Vector4f> {

    /// Reads four float values (x, y, z, w) from the provided [DataReader]
    /// and constructs a new `Vector4f`.
    ///
    /// @param in The data reader to read from.
    /// @return A new `Vector4f` instance.
    /// @throws IOException If an I/O error occurs.
    public static Vector4f read(DataReader in) throws IOException {
        float x = in.readFloat();
        float y = in.readFloat();
        float z = in.readFloat();
        float w = in.readFloat();
        return new Vector4f(x, y, z, w);
    }

    /// Writes the x, y, z, and w components of the given vector to the provided
    /// [DataWriter].
    ///
    /// @param out The data writer to write to.
    /// @param vec The vector to write.
    /// @throws IOException If an I/O error occurs.
    public static void write(DataWriter out, Vector4f vec) throws IOException {
        out.writeFloat(vec.x());
        out.writeFloat(vec.y());
        out.writeFloat(vec.z());
        out.writeFloat(vec.w());
    }

    // frequently used pre-defined vectors
    public static final Vector4f NULL = new Vector4f(0, 0, 0, 0);
    public static final Vector4f MAX_VALUE = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Vector4f MIN_VALUE = MAX_VALUE.scalar(-1); // don't use Float.MIN_VALUE here

    /// Constructs a new Vector4f from x, y, z and w components.
    ///
    /// @param x the vector x component
    /// @param y the vector y component
    /// @param z the vector z component
    /// @param w the vector w component
    public Vector4f(float x, float y, float z, float w) {
        super(new float[]{x, y, z, w});
    }

    private Vector4f(float[] storage) {
        super(storage);
    }

    /// Creates a new `Vector4f` from a float array.
    ///
    /// @param array The float array (must have a length of 4).
    /// @return A new `Vector4f` instance.
    /// @throws IllegalArgumentException if the array length is not 4.
    public static Vector4f fromArray(float[] array) {
        verifySize(array, 4);
        return new Vector4f(array.clone());
    }

    @Override
    protected Vector4f copy() {
        return new Vector4f(storage.clone());
    }

    /// @return The X component of the vector.
    public float x() { return storage[0]; }
    /// @return The Y component of the vector.
    public float y() { return storage[1]; }
    /// @return The Z component of the vector.
    public float z() { return storage[2]; }
    /// @return The W component of the vector.
    public float w() { return storage[3]; }

    /// @param value The new float value for the X component.
    /// @return A new vector with the X component set to the given value.
    public Vector4f withX(float value) { return with(0, value); }
    /// @param value The new float value for the Y component.
    /// @return A new vector with the Y component set to the given value.
    public Vector4f withY(float value) { return with(1, value); }
    /// @param value The new float value for the Z component.
    /// @return A new vector with the Z component set to the given value.
    public Vector4f withZ(float value) { return with(2, value); }
    /// @param value The new float value for the W component.
    /// @return A new vector with the W component set to the given value.
    public Vector4f withW(float value) { return with(3, value); }
}