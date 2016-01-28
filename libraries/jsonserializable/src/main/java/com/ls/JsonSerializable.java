package com.ls;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.ls.exceptions.JsonDeserializationException;
import com.ls.types.ListOfJson;
import com.ls.types.MapOfJson;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonSerializable {

    public String toJson() {
        return toJson(this);
    }

    public static String toJson(Object object) {
        Gson gson = createGson();
        return gson.toJson(object);
    }

    public static <T> List<T> toList(String json, Class<T> typeClazz) {

        List<T> list = fromJsonUnsafe(new ListOfJson<>(typeClazz), json);
        if (list == null) {
            list = new ArrayList<>();
        }

        return list;
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> typeKeyClass, Class<V> typeValueClass) {

        Map<K, V> map = fromJsonUnsafe(new MapOfJson<>(typeKeyClass, typeValueClass), json);
        if (map == null) {
            map = new HashMap<>();
        }

        return map;
    }

    public static Map<String, Object> toMap(String json) {
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        HashMap<String, Object> map = fromJsonUnsafe(type, json);

        if (map == null) {
            map = new HashMap<>();
        }

        return map;
    }

    public JsonElement toJsonTree() {
        Gson gson = createGson();
        return gson.toJsonTree(this);
    }

    public static <T> T fromJson(Class<T> classOfT, String json) throws JsonDeserializationException {
        return fromJson((Type) classOfT, json);
    }

    public static <T> T fromJsonUnsafe(Type type, String json) {
        try {
            return fromJson(type, json);
        } catch (JsonDeserializationException e) {
            return null;
        }
    }

    public static <T> T fromJson(Type type, String json) throws JsonDeserializationException {

        if (json == null || json.length() == 0) {
            return null;
        }

        String jsonInner = json.trim();

        T result = null;

        Gson gson = createGson();

        try {
            result = gson.fromJson(jsonInner, type);
        } catch (Exception e) {
            e.printStackTrace();
            //json string can include extra BOM symbol, we trying to exclude this symbol

            //get starting position of json object
            int jBeginPosition = jsonInner.indexOf("{");

            //+1 - method substring returns string containing the characters from start to end - 1
            int jLastPosition = jsonInner.lastIndexOf("}") + 1;

            if (jBeginPosition >= 0 && jLastPosition >= 0) {
                if (jBeginPosition <= jLastPosition && jLastPosition <= jsonInner.length()) {
                    jsonInner = jsonInner.substring(jBeginPosition, jLastPosition);

                    try {
                        result = gson.fromJson(jsonInner, type);
                    } catch (Exception eIn1) {
                        eIn1.printStackTrace();

                        try {
                            JsonReader lenientJsonReader = createLenientJsonReader(jsonInner);
                            result = gson.fromJson(lenientJsonReader, type);
                        } catch (Exception eIn2) {
                            eIn2.printStackTrace();

                            throw new JsonDeserializationException();
                        }
                    }
                }
            }
        }

        return result;
    }


    private static Gson createGson() {
        return new GsonBuilder()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        Expose expose = fieldAttributes.getAnnotation(Expose.class);
                        return expose != null && !expose.serialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                })
                .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        Expose expose = fieldAttributes.getAnnotation(Expose.class);
                        return expose != null && !expose.deserialize();
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                })
                .create();
    }

    private static JsonReader createLenientJsonReader(String json) {
        StringReader reader = new StringReader(json);

        JsonReader jsonReader = new JsonReader(reader);
        jsonReader.setLenient(true);

        return jsonReader;
    }
}
