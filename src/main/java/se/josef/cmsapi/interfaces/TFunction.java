package se.josef.cmsapi.interfaces;

import java.util.Objects;
import java.util.function.Function;
@FunctionalInterface
public interface TFunction<T,R> extends Function<T,R> {


    default R apply(T t){
        try{
            return applyThrows(t);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    default <V> TFunction<T, V> andThen(TFunction<? super R, ? extends V> after){
        Objects.requireNonNull(after);
        try{
            return (T t) -> after.apply(apply(t));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    default <V> TFunction<V, R> compose(TFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        try {
            return (V v) -> apply(before.apply(v));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    R applyThrows(T t) throws Exception;
}