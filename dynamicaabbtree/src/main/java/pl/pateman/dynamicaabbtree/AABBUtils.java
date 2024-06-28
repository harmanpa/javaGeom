package pl.pateman.dynamicaabbtree;

import java.util.function.BiPredicate;
import org.joml.AABBf;
import math.geom3d.transform.AffineTransform3D;
import math.geom3d.Point3D;

/**
 * Created by pateman.
 */
public final class AABBUtils {

    private AABBUtils() {

    }

    public static float getMinDistance(AABBf a, AffineTransform3D transformA, AABBf b, AffineTransform3D transformB) {
        return getMinDistance(
                transformed(a, TEMP_AABB_1.get(), transformA),
                transformed(b, TEMP_AABB_2.get(), transformB)
        );
    }

    public static float getMinDistance(AABBf aabba, AABBf aabbb) {
        return (float) Math.hypot(Math.hypot(
                minDistance(aabba.minX, aabba.maxX, aabbb.minX, aabbb.maxX),
                minDistance(aabba.minY, aabba.maxY, aabbb.minY, aabbb.maxY)),
                minDistance(aabba.minZ, aabba.maxZ, aabbb.minZ, aabbb.maxZ)
        );
    }

    static float minDistance(float minA, float maxA, float minB, float maxB) {
        return (float) Math.min(Math.max(0.0f, minA - maxB), Math.max(0.0f, minB - maxA));
    }

    public static float getWidth(AABBf aabb) {
        return aabb.maxX - aabb.minX;
    }

    public static float getHeight(AABBf aabb) {
        return aabb.maxY - aabb.minY;
    }

    public static float getDepth(AABBf aabb) {
        return aabb.maxZ - aabb.minZ;
    }

    public static float getArea(AABBf aabb) {
        final float width = getWidth(aabb);
        final float height = getHeight(aabb);
        final float depth = getDepth(aabb);
        return 2.0f * (width * height + width * depth + height * depth);
    }

    public static AABBf transformed(AABBf original, AABBf transformed, AffineTransform3D transform) {
        Point3D p1 = new Point3D(original.minX, original.minY, original.minZ).transform(transform);
        Point3D p2 = new Point3D(original.maxX, original.maxY, original.maxZ).transform(transform);
        return transformed.setMin(
                (float) Math.min(p1.getX(), p2.getX()),
                (float) Math.min(p1.getY(), p2.getY()),
                (float) Math.min(p1.getZ(), p2.getZ()))
                .setMax(
                        (float) Math.max(p1.getX(), p2.getX()),
                        (float) Math.max(p1.getY(), p2.getY()),
                        (float) Math.max(p1.getZ(), p2.getZ()));
    }

    private static final ThreadLocal<AABBf> TEMP_AABB_1 = new ThreadLocal<AABBf>() {
        @Override
        protected AABBf initialValue() {
            return new AABBf();
        }
    };
    private static final ThreadLocal<AABBf> TEMP_AABB_2 = new ThreadLocal<AABBf>() {
        @Override
        protected AABBf initialValue() {
            return new AABBf();
        }
    };

    public static boolean test(AABBf a, AffineTransform3D transformA, AABBf b, AffineTransform3D transformB, BiPredicate<AABBf, AABBf> test) {
        return test.test(
                transformed(a, TEMP_AABB_1.get(), transformA),
                transformed(b, TEMP_AABB_2.get(), transformB)
        );
    }

    public static boolean testAABB(AABBf a, AffineTransform3D transformA, AABBf b, AffineTransform3D transformB) {
        return test(a, transformA, b, transformB, (AABBf at, AABBf bt) -> at.testAABB(bt));
    }

    public static boolean test(AABBf a, AABBf b, AffineTransform3D transformB, BiPredicate<AABBf, AABBf> test) {
        return test.test(
                a,
                transformed(b, TEMP_AABB_2.get(), transformB)
        );
    }

    public static boolean testAABB(AABBf a, AABBf b, AffineTransform3D transformB) {
        return test(a, b, transformB, (AABBf at, AABBf bt) -> at.testAABB(bt));
    }
}
