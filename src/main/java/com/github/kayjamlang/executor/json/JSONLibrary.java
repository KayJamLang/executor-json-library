package com.github.kayjamlang.executor.json;

import com.github.kayjamlang.core.expressions.data.Argument;
import com.github.kayjamlang.core.Type;
import com.github.kayjamlang.executor.Context;
import com.github.kayjamlang.executor.Executor;
import com.github.kayjamlang.executor.libs.Library;
import com.github.kayjamlang.executor.libs.main.ArrayClass;
import com.github.kayjamlang.executor.libs.main.MapClass;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JSONLibrary extends Library {

    public JSONLibrary() throws Exception {
        classes.put("JSON", new JSONClass());
    }

    public Object decode(Executor executor, Object value) throws Exception {
        if(value instanceof JSONArray)
            return decodeArray(executor, (JSONArray) value);
        else if(value instanceof JSONObject)
            return decodeObject(executor, (JSONObject) value);
        else if(value instanceof Number)
            return ((Number) value).longValue();

        return value;
    }

    public Object encodeMap(Executor executor, MapClass object) throws Exception {
        Map<Object, Object> map = object.getMap((Context) object.data.get("ctx"));

        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<Object, Object> entry: map.entrySet()) {
            Object value = entry.getValue();
            if(value instanceof MapClass)
                value = encodeMap(executor, (MapClass) value);
            else if(value instanceof ArrayClass)
                value = encodeArray(executor, (ArrayClass) value);

            jsonObject.put(entry.getKey().toString(), value);
        }


        return jsonObject;
    }

    public Object encodeArray(Executor executor, ArrayClass object) throws Exception {
        List<Object> array = object.getVariable((Context) object.data.get("ctx"), "array");

        JSONArray jsonArray = new JSONArray();
        for(Object value: array) {
            if(value instanceof MapClass)
                value = encodeMap(executor, (MapClass) value);
            else if(value instanceof ArrayClass)
                value = encodeArray(executor, (ArrayClass) value);

            jsonArray.put(value);
        }


        return jsonArray;
    }

    public Object decodeObject(Executor executor, JSONObject object) throws Exception {
        Map<Object, Object> map = new HashMap<>();
        for(String key: object.keySet())
            map.put(key, decode(executor, object.get(key)));

        return MapClass.create(executor, map);
    }

    public ArrayClass decodeArray(Executor executor, JSONArray array) throws Exception {
        List<Object> list = new LinkedList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(decode(executor, array.get(i)));
        }

        return ArrayClass.create(executor, list);
    }

    public class JSONClass extends LibClass{

        public JSONClass() throws Exception {
            super("JSON", null);
            setCompanion(new LibObject((object -> {
                object.addFunction(new LibFunction("encode", Type.STRING, (mainContext, context) -> {
                    Object value = context.getVariable("jsonEncoded");
                    if(value instanceof ArrayClass)
                        return encodeArray(mainContext.executor, (ArrayClass) value);
                    else if(value instanceof MapClass)
                        return encodeMap(mainContext.executor, (MapClass) value);

                    return false;
                }, new Argument(Type.ANY, "jsonEncoded")));

                object.addFunction(new LibFunction("decode", Type.ANY, (mainContext, context) -> {
                    String jsonEncoded = context.getVariable("jsonEncoded");
                    try {
                        if(jsonEncoded.startsWith("{"))
                            return decodeObject(mainContext.executor, new JSONObject(jsonEncoded));
                        return decodeArray(mainContext.executor, new JSONArray(jsonEncoded));
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                }, new Argument(Type.STRING, "jsonEncoded")));
            })));
        }
    }
}
