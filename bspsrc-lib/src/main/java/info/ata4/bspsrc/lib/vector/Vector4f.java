/*
** 2012 March 12
**
** The author disclaims copyright to this source code.  In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
*/

package info.ata4.bspsrc.lib.vector;

import info.ata4.io.DataReader;
import info.ata4.io.DataWriter;

import java.io.IOException;

/**
 * An immutable fluent interface four-dimensional vector class for float values.
 *
 * @author Sandern
 */
public final class Vector4f extends VectorXf<Vector4f> {

    public static Vector4f read(DataReader in) throws IOException {
        float x = in.readFloat();
        float y = in.readFloat();
        float z = in.readFloat();
        float w = in.readFloat();
        return new Vector4f(x, y, z, w);
    }

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

    /**
     * Constructs a new Vector4f from x, y, z and w components
     *
     * @param x the vector x component
     * @param y the vector y component
     * @param z the vector z component
     * @param w the vector w component
     */
    public Vector4f(float x, float y, float z, float w) {
        super(new float[]{x, y, z, w});
    }

    private Vector4f(float[] storage) {
        super(storage);
    }

    public static Vector4f from_array(float[] array) {
        verifySize(array, 4);
        return new Vector4f(array.clone());
    }

    @Override
    protected Vector4f copy() {
        return new Vector4f(storage.clone());
    }

    public float x() { return storage[0]; }
    public float y() { return storage[1]; }
    public float z() { return storage[2]; }
    public float w() { return storage[3]; }

    public Vector4f withX(float value) { return with(0, value); }
    public Vector4f withY(float value) { return with(1, value); }
    public Vector4f withZ(float value) { return with(2, value); }
    public Vector4f withW(float value) { return with(3, value); }
}
