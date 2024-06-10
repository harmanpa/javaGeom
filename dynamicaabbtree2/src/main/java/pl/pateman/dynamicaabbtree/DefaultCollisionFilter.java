package pl.pateman.dynamicaabbtree;

public class DefaultCollisionFilter<T extends Boundable & Identifiable> implements CollisionFilter<T>
{
   @Override
   public boolean test(T t, T t2)
   {
      return true;
   }
}
