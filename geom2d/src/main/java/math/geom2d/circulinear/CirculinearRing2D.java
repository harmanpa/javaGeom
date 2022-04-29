/**
 * File: 	CirculinearRing2D.java
 * Project: javaGeom
 *
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */
package math.geom2d.circulinear;

import math.geom2d.ClosedShape2D;

/**
 * Interface for circulinear contours which are both bounded and closed.
 *
 * @author dlegland
 * @see GenericCirculinearRing2D
 */
public interface CirculinearRing2D extends CirculinearContour2D, ClosedShape2D {

    @Override
    public CirculinearDomain2D domain();

    @Override
    public CirculinearRing2D parallel(double d);

    @Override
    public CirculinearRing2D reverse();
}
