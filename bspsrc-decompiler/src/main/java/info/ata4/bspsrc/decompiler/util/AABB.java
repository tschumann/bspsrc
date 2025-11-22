/*
 ** 2014 June 20
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.bspsrc.decompiler.util;

import info.ata4.bspsrc.lib.vector.Vector3d;

/**
 * Class for axis-aligned bounding boxes.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class AABB {

    public static final AABB ZERO = new AABB(Vector3d.MAX_VALUE, Vector3d.MIN_VALUE);

    private final Vector3d min;
    private final Vector3d max;

    public AABB(Vector3d mins, Vector3d maxs) {
        this.min = mins;
        this.max = maxs;
    }

    public Vector3d getMin() {
        return min;
    }

    public Vector3d getMax() {
        return max;
    }

    public Vector3d getSize() {
        return max.sub(min);
    }

    public boolean intersectsWith(AABB that) {
        return that.max.x() > this.min.x() && that.min.x() < this.max.x()
                && that.max.y() > this.min.y() && that.min.y() < this.max.y()
                && that.max.z() > this.min.z() && that.min.z() < this.max.z();
    }

    public AABB include(AABB that) {
        return new AABB(
                min.min(that.min),
                max.max(that.max)
        );
    }

    public AABB expand(Vector3d v) {
        return new AABB(
                min.sub(v),
                max.add(v)
        );
    }

    public AABB expand(float e) {
        return expand(new Vector3d(e, e, e));
    }

    @Override
    public String toString() {
        return min.toString() + " -> " + max.toString();
    }
}
