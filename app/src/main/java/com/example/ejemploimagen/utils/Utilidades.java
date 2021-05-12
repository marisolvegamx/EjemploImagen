package com.example.misgastos.utils;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utilidades {

    public static JSONObject cur2Json(Cursor cursor)  {

        try {
            JSONArray resultSet = new JSONArray();
            cursor.moveToFirst();
            JSONObject rowObject = new JSONObject();
            while (cursor.isAfterLast() == false) {
                int totalColumn = cursor.getColumnCount();

                for (int i = 0; i < totalColumn; i++) {
                    if (cursor.getColumnName(i) != null) {

                            rowObject.put(cursor.getColumnName(i),
                                    cursor.getString(i));

                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();
            }

            cursor.close();
            return rowObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }
}
