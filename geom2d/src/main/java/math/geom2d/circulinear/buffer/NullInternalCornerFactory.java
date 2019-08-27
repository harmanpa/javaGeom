/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math.geom2d.circulinear.buffer;

import java.util.Deque;
import math.geom2d.circulinear.CirculinearContinuousCurve2D;

/**
 *
 * @author peter
 */
public class NullInternalCornerFactory implements InternalCornerFactory {

    @Override
    public boolean createInternalCorner(Deque<CirculinearContinuousCurve2D> parallelElementQueue, CirculinearContinuousCurve2D currentParallelElement) {
        return false;
    }

}
