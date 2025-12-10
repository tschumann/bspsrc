package info.ata4.bspsrc.decompiler.util;

import info.ata4.bspsrc.lib.struct.*;

public class VectorUtil {

    public static double matchingAreaPercentage(
            DOccluderPolyData occluderPolyData,
            DBrush brush,
            DBrushSide brushSide,
            BspData bsp,
            WindingFactory windingFactory
    ) {
        if (occluderPolyData.planenum != brushSide.pnum)
            return 0;

        var w1 = windingFactory.fromOccluder(bsp, occluderPolyData);
        var w2 = windingFactory.fromSide(bsp, brush, brushSide);
        var clippedWinding = w1.clipWinding(w2, bsp.planes.get(brushSide.pnum).normal.toDouble());
        return clippedWinding.getArea() / w1.getArea();
    }

    public static double matchingAreaPercentage(
            DAreaportal areaportal,
            DBrush brush,
            DBrushSide brushSide,
            BspData bsp,
            WindingFactory windingFactory
    ) {
        if (areaportal.planenum != brushSide.pnum)
            return 0;

        var w1 = windingFactory.fromAreaportal(bsp, areaportal);
        var w2 = windingFactory.fromSide(bsp, brush, brushSide);
        var clippedWinding = w1.clipWinding(w2, bsp.planes.get(brushSide.pnum).normal.toDouble());
        return clippedWinding.getArea() / w1.getArea();
    }
}
