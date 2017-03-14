package com.nippon;


import org.apache.commons.lang.StringUtils;
import org.grails.web.json.JSONArray;
import org.grails.web.json.JSONObject;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hupanpan on 2017/2/21.
 */
public class NpStringUtils {
    public static String getLabelByField(String field) {
        String[] r = field.split("(?=\\p{Upper})");
        List<String> list = new ArrayList<>();
        for (String one : r) {
            String oneWord = one.substring(0, 1).toUpperCase() + one.substring(1);
            list.add(oneWord);
        }
        return StringUtils.join(list, " ");
    }

    public static JSONObject ajaxJSONReturnTrue(JSONObject data) {
        return ajaxJSONReturn(true, data, null, "data");
    }

    public static JSONObject ajaxJSONReturnTrue(JSONObject data, String dataKey) {
        return ajaxJSONReturn(true, data, null, dataKey);
    }

    public static JSONObject ajaxJSONReturnFalse(Errors errors) {
        JSONArray jsonArray = new JSONArray();
        List<ObjectError> allErrors = errors.getAllErrors();
        for (ObjectError error : allErrors) {
            String defaultMessage = error.getDefaultMessage();
            jsonArray.put(defaultMessage);
        }
        return ajaxJSONReturn(false, null, jsonArray, "data");
    }

    public static JSONObject ajaxJSONReturn(boolean isSuccess, JSONObject data, JSONArray error, String dataKey) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", isSuccess);
        if (isSuccess) {
            jsonObject.put(dataKey, data);
            jsonObject.put("error", new JSONObject());
        } else {
            jsonObject.put("error", error);
            jsonObject.put("data", new JSONObject());
        }
        return jsonObject;
    }

    public static double toDouble(Object o) {
        Double aDouble = 0.0;
        try {
            if (o != null) {
                aDouble = Double.valueOf(o.toString());
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return aDouble;
    }

    public String toPercentage(Double a) {
        return Math.round(a * 100) * 1.0 / 100 + "%";
    }
}
