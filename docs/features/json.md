# Json
###### Last updated for version: 2.4.1

In TeszCore the class `TeszGson` is used for de-/serializing values to JSON and vice versa.<br>
But different to the normal `Gson` you would use, in this class you can register new de-/serializer adapters, 
which will stay registered for until the java process is terminated, which results in accessible de-/serializers for all dependent projects.

## De-/Serialisation

You can start serializing with normal objects directly, you don't even have to initialize the `TeszCoreAPI`.<br>
As in normal Gson you can serialize objects with `#toJson` and `#fromJson`:
```java
record Data(String foo, int bar) {}

//...

Data data = new Data("baz", 10);

String dataJson = TeszGson.toJson(data);

//...

Data data = TeszGson.fromJson(dataJson, Data.class);
```

## Register new adapters

When you have difficult objects which can't be automatically serialized by `Gson`, 
you can register a serializer and deserializer with `TeszGson#registerSerializer` and `TeszGson#registerDeserializer`:

```java
TeszGson.registerSerializer(DifficultObject.class, 
    (srcObj, typeOfSrcObj, context) -> {
        if (srcObj == null) return null;
        return new JsonPrimitive(/* Serialize your object to an object type, allowed for JsonPrimitive such as String */);
    }
);

TeszGson.registerDeserializer(ItemStack.class, 
    (json, typeOfT, context) -> {
        if (json.isJsonNull()) return null;
        return /* Deserialize your object from the serialized value */;
    }
);
```

after this, `#toJson` and `#fromJson` uses your functions to de-/serialize your object.