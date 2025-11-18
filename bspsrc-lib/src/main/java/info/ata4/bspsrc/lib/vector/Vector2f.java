package info.ata4.bspsrc.lib.vector;

import info.ata4.io.DataReader;
import info.ata4.io.DataWriter;

import java.io.IOException;

public final class Vector2f extends VectorXf<Vector2f> {

	public static Vector2f read(DataReader in) throws IOException {
		float x = in.readFloat();
		float y = in.readFloat();
		return new Vector2f(x, y);
	}

	public static void write(DataWriter out, Vector2f vec) throws IOException {
		out.writeFloat(vec.x());
		out.writeFloat(vec.y());
	}

	// frequently used pre-defined vectors
	public static final Vector2f NULL = new Vector2f(0, 0);
	public static final Vector2f MAX_VALUE = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
	public static final Vector2f MIN_VALUE = MAX_VALUE.scalar(-1); // don't use Float.MIN_VALUE here
	
	/**
	 * Constructs a new Vector2f from x, y and z components
	 *
	 * @param x the vector x component
	 * @param y the vector y component
	 */
	public Vector2f(float x, float y) {
		super(new float[]{x, y});
	}

	private Vector2f(float[] storage) {
		super(storage);
	}

	public static Vector2f from_array(float[] array) {
		verifySize(array, 2);
		return new Vector2f(array.clone());
	}

	@Override
	protected Vector2f copy() {
		return new Vector2f(storage.clone());
	}

	public float x() { return storage[0]; }
	public float y() { return storage[1]; }

	public Vector2f withX(float value) { return with(0, value); }
	public Vector2f withY(float value) { return with(1, value); }

	/**
	 * Z-Component of the cross product with the 2 vectors lying on a 3d xy-plane
	 *
	 * @param that the vector to take a cross product
	 * @return the z component of the cross-product vector
	 */
	public float cross(Vector2f that) {
		return this.x() * that.y() - that.x() * this.y();
	}

	/**
	 * Rotates the vector.
	 *
	 * @param angle angle rotation in degrees
	 * @return rotated vector
	 */
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
