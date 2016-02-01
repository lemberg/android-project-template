package com.ls.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by vverbytskyy on 12/22/15.
 */
public class MapOfJson<K, V> implements ParameterizedType {

    private Class<? extends K> wrapper1;
    private Class<? extends V> wrapper2;


    public MapOfJson(Class<K> clazz1, Class<V> clazz2) {
        this.wrapper1 = clazz1;
        this.wrapper2 = clazz2;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{wrapper1, wrapper2};
    }

    @Override
    public Type getRawType() {
        return HashMap.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
