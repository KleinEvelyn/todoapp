package com.example.todo;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class SortByDate implements Comparator<ToDoModel> {
    @Override
    public int compare(ToDoModel o1, ToDoModel o2) {
        try {
            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse(o1.getDate());
            Date date2 = new SimpleDateFormat("dd-MM-yyyy").parse(o2.getDate());

            return date2.compareTo(date1);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("MyError", e.getMessage());
            return 0;
        }
    }
}
