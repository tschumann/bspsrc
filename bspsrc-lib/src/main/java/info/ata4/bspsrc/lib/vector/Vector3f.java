/*
** 2011 April 5
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
 * An immutable fluent interface three-dimensional vector class for float values.
 * 
 * Original class name: unmap.Vec
 * Original author: Bob (Mellish?)
 * Original creation date: January 20, 2005, 7:41 PM
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public final class Vector3f extends VectorXf<Vector3f> {

    public static Vector3f read(DataReader in) throws IOException {
        float x = in.readFloat();
        float y = in.readFloat();
        float z = in.readFloat();
        return new Vector3f(x, y, z);
    }

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
    
    /**
     * Constructs a new Vector3f from x, y and z components
     *
     * @param x the vector x component
     * @param y the vector y component
     * @param z the vector z component
     */
    public Vector3f(float x, float y, float z) {
        super(new float[]{x, y, z});
    }

    private Vector3f(float[] storage) {
        super(storage);
    }
    
    public static Vector3f from_array(float[] array) {
        verifySize(array, 3);
        return new Vector3f(array.clone());
    }

    @Override
    protected Vector3f copy() {
        return new Vector3f(storage.clone());
    }

    public float x() { return storage[0]; }
    public float y() { return storage[1]; }
    public float z() { return storage[2]; }

    public Vector3f withX(float value) { return with(0, value); }
    public Vector3f withY(float value) { return with(1, value); }
    public Vector3f withZ(float value) { return with(2, value); }


    /**
     * Vector cross product: this x that
     * 
     * @param that the vector to take a cross product
     * @return the cross-product vector
     */
    public Vector3f cross(Vector3f that) {
        float rx = this.y() * that.z() - this.z() * that.y();
        float ry = this.z() * that.x() - this.x() * that.z();
        float rz = this.x() * that.y() - this.y() * that.x();

        return new Vector3f(rx, ry, rz);
    }

    /**
     * Rotates the vector.
     * 
     * @param angles angles for each component in degrees
     * @return rotated vector
     */
    public Vector3f rotate(Vector3f angles) {
        if (angles.x() == 0 && angles.y() == 0 && angles.z() == 0) {
            // nothing to do here
            return this;
        }

        double rx = x();
        double ry = y();
        double rz = z();

        // rotate x (pitch)
        if (angles.x() != 0) {
            Point2d p = new Point2d(ry, rz).rotate(angles.x());
            ry = p.x;
            rz = p.y;
        }

        // rotate y (yaw)
        if (angles.y() != 0) {
            Point2d p = new Point2d(rx, rz).rotate(angles.y());
            rx = p.x;
            rz = p.y;
        }

        // rotate z (roll)
        if (angles.z() != 0) {
            Point2d p = new Point2d(rx, ry).rotate(angles.z());
            rx = p.x;
            ry = p.y;
        }

        return new Vector3f((float)rx, (float)ry, (float)rz);
    }

    public Vector2f getAsPointOnPlane(Vector3f origin, Vector3f axis1, Vector3f axis2) {
        return new Vector2f(
                axis1.dot(this.sub(origin)),
                axis2.dot(this.sub(origin))
        );
    }

    /**
     * Private helper class for rotation
     */
    private static class Point2d {

        private final double x;
        private final double y;

        private Point2d(double x, double y) {
            this.x = x;
            this.y = y;
        }

        private Point2d rotate(double angle) {
            // normalize angle
            angle %= 360;

            // special cases
            if (angle == 0) {
                return this;
            }
            if (angle == 90) {
                return new Point2d(-y, x);
            }
            if (angle == 180) {
                return new Point2d(-x, -y);
            }
            if (angle == 270) {
                return new Point2d(y, -x);
            }

            // convert degrees to radians
            angle = Math.toRadians(angle);

            double r = Math.hypot(x, y);
            double theta = Math.atan2(y, x);

            double rx = r * Math.cos(theta + angle);
            double ry = r * Math.sin(theta + angle);

            return new Point2d(rx, ry);
        }
    }
}
