package com.ls.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ListOfJson<T> implements ParameterizedType {

    private Class<?> wrapper;

    public ListOfJson(Class<T> wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{wrapper};
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public Type getRawType() {
        return List.class;
    }
}
