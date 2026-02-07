package info.ata4.bspsrc.lib.vector;

import info.ata4.io.DataReader;
import info.ata4.io.DataWriter;

import java.io.IOException;

/// An immutable two-dimensional vector class for float values.
/// This class extends [VectorXf] for 2 components (x, y) and provides
/// 2D-specific operations like cross product (z-component) and rotation.
/// All modification methods return a new `Vector2f` instance.
public final class Vector2f extends VectorXf<Vector2f> {

	/// Reads two float values (x, y) from the provided [DataReader]
	/// and constructs a new `Vector2f`.
	///
	/// @param in The data reader to read from.
	/// @return A new `Vector2f` instance.
	/// @throws IOException If an I/O error occurs.
	public static Vector2f read(DataReader in) throws IOException {
		float x = in.readFloat();
		float y = in.readFloat();
		return new Vector2f(x, y);
	}

	/// Writes the x and y components of the given vector to the provided
	/// [DataWriter].
	///
	/// @param out The data writer to write to.
	/// @param vec The vector to write.
	/// @throws IOException If an I/O error occurs.
	public static void write(DataWriter out, Vector2f vec) throws IOException {
		out.writeFloat(vec.x());
		out.writeFloat(vec.y());
	}

	// frequently used pre-defined vectors
	public static final Vector2f NULL = new Vector2f(0, 0);
	public static final Vector2f MAX_VALUE = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
	public static final Vector2f MIN_VALUE = MAX_VALUE.scalar(-1); // don't use Float.MIN_VALUE here

	/// Constructs a new Vector2f from x and y components.
	///
	/// @param x the vector x component
	/// @param y the vector y component
	public Vector2f(float x, float y) {
		super(new float[]{x, y});
	}

	private Vector2f(float[] storage) {
		super(storage);
	}

	/// Creates a new `Vector2f` from a float array.
	///
	/// @param array The float array.
	/// @return A new `Vector2f` instance.
	/// @throws IllegalArgumentException if the array length is not 2.
	public static Vector2f fromArray(float[] array) {
		verifySize(array, 2);
		return new Vector2f(array.clone());
	}

	@Override
	protected Vector2f copy() {
		return new Vector2f(storage.clone());
	}

	/// @return The X component of the vector.
	public float x() { return storage[0]; }
	/// @return The Y component of the vector.
	public float y() { return storage[1]; }

	/// @param value The new float value for the X component.
	/// @return A new vector with the X component set to the given value.
	public Vector2f withX(float value) { return with(0, value); }
	/// @param value The new float value for the Y component.
	/// @return A new vector with the Y component set to the given value.
	public Vector2f withY(float value) { return with(1, value); }

	/// Calculates the Z-component of the cross product for two 2D vectors
	/// lying on a 3D XY-plane.
	/// 
	/// @param that the vector to take a cross product with.
	/// @return The scalar Z component of the resulting cross-product vector.
	public float cross(Vector2f that) {
		return this.x() * that.y() - that.x() * this.y();
	}

	/// @param angle angle rotation in degrees.
	/// @return A new, rotated vector around the origin (0, 0) by the specified angle.
	public Vector2f rotate(float angle) {
		// normalize angle
		angle %= 360;

		// special cases
		if (angle == 0)
			return this;
		if (angle == 90)
			return new Vector2f(-y(), x());
		if (angle == 180)
			return new Vector2f(-x(), -y());
		if (angle == 270)
			return new Vector2f(y(), -x());

		// convert degrees to radians
		double radians = Math.toRadians(angle);

		double r = Math.hypot(x(), y());
		double theta = Math.atan2(y(), x());

		double rx = r * Math.cos(theta + radians);
		double ry = r * Math.sin(theta + radians);

		return new Vector2f((float) rx, (float) ry);
	}
}