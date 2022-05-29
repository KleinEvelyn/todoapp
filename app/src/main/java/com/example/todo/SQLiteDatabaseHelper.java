package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    // database name
    private static final String DATABASE_NAME = "appDatabase.db";
    //database version
    private static final int DATABASE_VERSION = 1;
    //Tables names
    private static final String USER_TABLE = "user_table";
    private static final String TODO_TABLE = "todo_table";
    //user table column name
    private static final String USER_NAME = "user_name";
    private static final String USER_PASSWORD = "user_password";
    //Tasks table column name
    private static final String TODO_ID = "todo_id";
    private static final String TODO_USER_ID = "todo_user_id";
    private static final String TODO_NAME = "todo_name";
    private static final String TODO_DESCRIPTION = "todo_description";
    private static final String TODO_DATE = "todo_date";
    private static final String TODO_TIME = "todo_time";
    private static final String TODO_STATUS = "todo_status";
    private static final String TODO_IS_FAVOURITE = "todo_is_favourite";
    private Context context;


    // Database constructor
    public SQLiteDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {

        // query to create user table
        String queryUserTable = "CREATE TABLE " + USER_TABLE +
                " (" + USER_NAME + " TEXT PRIMARY KEY UNIQUE, " +
                USER_PASSWORD + " TEXT);";

        // query to create todo table
        String queryTodoTable = "CREATE TABLE " + TODO_TABLE +
                " (" + TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TODO_USER_ID + " TEXT, " +
                TODO_NAME + " TEXT, " +
                TODO_DESCRIPTION + " TEXT, " +
                TODO_DATE + " TEXT, " +
                TODO_STATUS + " TEXT, " +
                TODO_IS_FAVOURITE + " TEXT, " +
                TODO_TIME + " TEXT);";

        db.execSQL(queryUserTable);// execute query
        db.execSQL(queryTodoTable);
    }

    @Override  // onUpgrade drop tables if exist
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }


    // add new user function
    public boolean addNewUser(UserModel userModel) {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase(); //get writeable database
        ContentValues contentValues = new ContentValues();//create content values object
        contentValues.put(USER_NAME, userModel.getUserName()); // set user name
        contentValues.put(USER_PASSWORD, userModel.getUserPassword()); // set password
        long result = db.insert(USER_TABLE, null, contentValues); // insert data into table
        return result != -1;
    }

    public boolean addNewTODO(ToDoModel toDoModel) {
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TODO_USER_ID, toDoModel.getUserName());
        contentValues.put(TODO_NAME, toDoModel.getTodoName());
        contentValues.put(TODO_DESCRIPTION, toDoModel.getTodoDescription());
        contentValues.put(TODO_DATE, toDoModel.getDate());
        contentValues.put(TODO_TIME, toDoModel.getTime());
        contentValues.put(TODO_STATUS, toDoModel.getTodoStatus());
        contentValues.put(TODO_IS_FAVOURITE, toDoModel.getIsFavourite());
        long result = db.insert(TODO_TABLE, null, contentValues);
        return result != -1;
    }

    public boolean updateToDo(ToDoModel toDoModel) {//get updated module
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        // put values in content values
        contentValues.put(TODO_USER_ID, toDoModel.getUserName());
        contentValues.put(TODO_NAME, toDoModel.getTodoName());
        contentValues.put(TODO_DESCRIPTION, toDoModel.getTodoDescription());
        contentValues.put(TODO_DATE, toDoModel.getDate());
        contentValues.put(TODO_TIME, toDoModel.getTime());
        contentValues.put(TODO_STATUS, toDoModel.getTodoStatus());
        contentValues.put(TODO_IS_FAVOURITE, toDoModel.getIsFavourite());

        // update row in the table using todo id
        int result = db.update(TODO_TABLE, contentValues, TODO_ID + "=" + toDoModel.getTodoID(), null);
        return result != -1;
    }

    // this function will return all todos of specific users
    public List<ToDoModel> getUsertodos(String userName) {

        // get readable database
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();

        // execute query to get user todo into cursor
        Cursor cursor = db.rawQuery(" select * from " + TODO_TABLE + " where " + TODO_USER_ID + "='" + userName + "'", null);
        List<ToDoModel> toDoModels = new ArrayList<>(); // list of todo
        if (cursor.moveToFirst()) { // check if the cursor is not null
            do {
                ToDoModel tm = new ToDoModel();// create todo model

                tm.setTodoID(cursor.getInt(0));
                tm.setUserName(cursor.getString(1));
                tm.setTodoName(cursor.getString(2));
                tm.setTodoDescription(cursor.getString(3));
                tm.setDate(cursor.getString(4));
                tm.setTodoStatus(cursor.getString(5));
                tm.setIsFavourite(cursor.getString(6));
                tm.setTime(cursor.getString(7));
                toDoModels.add(tm);// add todo object to list
            } while (cursor.moveToNext()); // move cursor to next row
        }
        return toDoModels;// return list of tasks
    }

    // get single todo from table using todo id in the query
    public ToDoModel getSingleToDo(int id) {
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(" select * from " + TODO_TABLE + " where " + TODO_ID + "='" + id + "'", null);
        ToDoModel tm = new ToDoModel();
        if (cursor.moveToFirst()) {
            tm.setTodoID(cursor.getInt(0));
            tm.setUserName(cursor.getString(1));
            tm.setTodoName(cursor.getString(2));
            tm.setTodoDescription(cursor.getString(3));
            tm.setDate(cursor.getString(4));
            tm.setTodoStatus(cursor.getString(5));
            tm.setIsFavourite(cursor.getString(6));
            tm.setTime(cursor.getString(7));
            return tm;
        } else {
            return null;// if invalid todo
        }
    }


    // get user from user table using userName
    public UserModel getUser(String userName) {
        android.database.sqlite.SQLiteDatabase db = this.getReadableDatabase();

        //Query to get userData from user table
        Cursor cursor = db.rawQuery(" select * from " + USER_TABLE + " where " + USER_NAME + "='" + userName + "'", null);
        UserModel userModel = new UserModel();// create userModel object
        if (cursor.moveToFirst()) {// if user exist

            // write data from cursor into UserModel
            userModel.setUserName(cursor.getString(0));
            userModel.setUserPassword(cursor.getString(1));
            return userModel;// return user model
        } else {
            return null;// if user not exist return null
        }
    }

    public void deleteTodo(int task_id) { // task id
        android.database.sqlite.SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TODO_TABLE, TODO_ID + "=" + task_id, null);
        db.close();
    }
}
