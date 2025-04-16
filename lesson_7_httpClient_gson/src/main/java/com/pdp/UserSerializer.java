package com.pdp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class UserSerializer implements JsonSerializer<User> {
    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("full_name", user.name.toUpperCase()); // Katta harflarga aylantirish
        jsonObject.addProperty("years", user.age); // Maydon nomini o‘zgartirish
        return jsonObject;
    }
}
