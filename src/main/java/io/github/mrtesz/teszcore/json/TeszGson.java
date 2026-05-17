package io.github.mrtesz.teszcore.json;

import com.google.gson.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class TeszGson {

    private static GsonBuilder BUILDER = new GsonBuilder().setPrettyPrinting();
    private static final Gson DEFAULT_GSON = BUILDER.create();

    private static Gson gson = DEFAULT_GSON;

    // Gson-Methods

    public static String toJson(Object src) {
        return gson.toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        return gson.toJson(src, typeOfSrc);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(JsonElement json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(JsonElement json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }


    // Adapter Registration

    public static <T> void registerSerializer(Class<T> type, JsonSerializer<T> serializer) {
        BUILDER.registerTypeAdapter(type, serializer);

        gson = BUILDER.create();
    }

    public static <T> void registerDeserializer(Class<T> type, JsonDeserializer<T> deserializer) {
        BUILDER.registerTypeAdapter(type, deserializer);

        gson = BUILDER.create();
    }

    public static <T> void registerTypeAdapter(Class<T> type, Object adapter) {
        BUILDER.registerTypeAdapter(type, adapter);

        gson = BUILDER.create();
    }

    /**
     * Sets the builder used to create new Gson instances.
     * <p>
     * Warning: This resets all previously registered TypeAdapters.
     * The Gson instance will be rebuilt using the provided builder.
     * </p>
     *
     * @param BUILDER the new {@link GsonBuilder} to use for creating Gson instances
     */
    public static void setBUILDER(GsonBuilder BUILDER) {
        TeszGson.BUILDER = BUILDER;
        gson = BUILDER.create();
    }
}
