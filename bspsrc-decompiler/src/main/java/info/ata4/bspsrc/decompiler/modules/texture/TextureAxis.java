/*
** 2011 April 5
**
** The author disclaims copyright to this source code.  In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
*/

package info.ata4.bspsrc.decompiler.modules.texture;

import info.ata4.bspsrc.lib.vector.Vector3d;

/**
 * A data structure for UV texture coordinates.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class TextureAxis {

    public final Vector3d axis;
    public final double tw;
    public final int shift;

    public TextureAxis(Vector3d axis, int shift, double tw) {
        this.axis = axis;
        this.tw = tw;
        this.shift = shift;
    }

    public TextureAxis(double x, double y, double z, int shift, double tw) {
        this.axis = new Vector3d(x, y, z);
        this.tw = tw;
        this.shift = shift;
    }

    public TextureAxis(Vector3d axis) {
        this(axis, 0, 0.25f);
    }

    public TextureAxis(double x, double y, double z) {
        this(x, y, z, 0, 0.25f);
    }

    @Override
    public String toString() {
        return axis + " " + shift + " [" + tw + "]";
    }
}
