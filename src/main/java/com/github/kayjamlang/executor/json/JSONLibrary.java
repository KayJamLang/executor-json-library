package com.github.kayjamlang.executor.json;

import com.github.kayjamlang.core.Argument;
import com.github.kayjamlang.core.Type;
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
        addFunction(new LibFunction("jsonDecode", (mainContext, context) -> {
            String jsonEncoded = (String) context.variables.get("jsonEncoded");
            try {
                if(jsonEncoded.startsWith("{"))
                    return decodeObject(mainContext.executor, new JSONObject(jsonEncoded));
                return decodeArray(mainContext.executor, new JSONArray(jsonEncoded));
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }, new Argument(Type.STRING, "jsonEncoded")));
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
                object.addFunction(new LibFunction("decode", (mainContext, context) -> {
                    String jsonEncoded = (String) context.variables.get("jsonEncoded");
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
