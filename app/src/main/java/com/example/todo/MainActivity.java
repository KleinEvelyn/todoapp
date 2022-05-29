package com.example.todo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageView imageViewLogout, imageViewSort, imageViewAdd;
    LinearLayout layoutNoTodo;
    RecyclerView recyclerView;
    SQLiteDatabaseHelper sqLiteDatabaseHelper;
    LoginData loginData;
    List<ToDoModel> toDoModels;
    TodoAdapter todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        loginData = new LoginData(this);
        initViews();
        initRecyclerView();
        getAllUserTodo();
        //Toast.makeText(this, currentDate(), Toast.LENGTH_SHORT).show();
        currentDate();
    }


    private void getAllUserTodo() {
        toDoModels.clear();
        toDoModels.addAll(sqLiteDatabaseHelper.getUsertodos(loginData.getUserName()));
        if (toDoModels.size() > 0) {
            layoutNoTodo.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            layoutNoTodo.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        refreshAdapter();
    }

    private void initRecyclerView() {
        toDoModels = new ArrayList<>();
        todoAdapter = new TodoAdapter(toDoModels, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(todoAdapter);
        todoAdapter.setOnItemClickListener(new TodoAdapter.onItemClickListener() {
            @Override
            public void operation(String operationName, int position) {
                if (operationName.equals("Ändern")) {
                    editTodo(position);
                } else if (operationName.equals("Löschen")) {
                    deleteTodo(position);
                } else if (operationName.equals("Erledigen")) {
                    ToDoModel toDoModel = toDoModels.get(position);
                    toDoModel.setTodoStatus("erledigt");
                    if (sqLiteDatabaseHelper.updateToDo(toDoModel)) {
                        Toast.makeText(MainActivity.this, "Erledigt", Toast.LENGTH_SHORT).show();
                        getAllUserTodo();
                    } else {
                        Toast.makeText(MainActivity.this, "Etwas ist schiefgelaufen", Toast.LENGTH_SHORT).show();
                    }
                } else if (operationName.equals("Favorit")) {
                    ToDoModel toDoModel = toDoModels.get(position);
                    if (toDoModel.getIsFavourite().equals("ja"))
                        toDoModel.setIsFavourite("nein");
                    else toDoModel.setIsFavourite("ja");
                    if (sqLiteDatabaseHelper.updateToDo(toDoModel)) {
                        if (toDoModel.getIsFavourite().equals("ja"))
                            Toast.makeText(MainActivity.this, "To-do ist nun ein Favorit", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "To-do ist kein Favorit mehr", Toast.LENGTH_SHORT).show();
                        getAllUserTodo();
                    } else {
                        Toast.makeText(MainActivity.this, "Etwas ist schiefgelaufen", Toast.LENGTH_SHORT).show();
                    }
                } else if (operationName.equals("Teilen")) {
                    shareTodo(position);
                } else if (operationName.equals("Speichern")) {
                    saveTodo(position);
                }
            }
        });
    }

    private void saveTodo(int position) {
        if (!StoragePermission.isAllowedToReadStorage(MainActivity.this)) {
            return;
        }


        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_save_todo);
        dialog.show();
        TextView textViewName, textViewDate, textViewDescription, textViewStatus, textViewTime;
        ImageView imageViewStar;
        Button buttonSave, buttonCancel;
        LinearLayout layoutTodo;

        layoutTodo = dialog.findViewById(R.id.layoutTodo);
        layoutTodo.setDrawingCacheEnabled(true);

        buttonSave = dialog.findViewById(R.id.buttonSave);
        buttonCancel = dialog.findViewById(R.id.buttonCancel);
        textViewName = dialog.findViewById(R.id.textViewName);
        textViewDate = dialog.findViewById(R.id.textViewDate);
        textViewTime = dialog.findViewById(R.id.textViewTime);
        textViewDescription = dialog.findViewById(R.id.textViewDescription);
        textViewStatus = dialog.findViewById(R.id.textViewStatus);
        imageViewStar = dialog.findViewById(R.id.imageViewStar);
        textViewName.setText(toDoModels.get(position).getTodoName());
        textViewDate.setText(toDoModels.get(position).getDate());
        textViewTime.setText(toDoModels.get(position).getTime());
        textViewDescription.setText(toDoModels.get(position).getTodoDescription());
        textViewStatus.setText(toDoModels.get(position).getTodoStatus());
        if (toDoModels.get(position).getIsFavourite().equals("nein"))
            imageViewStar.setVisibility(View.GONE);
        else imageViewStar.setVisibility(View.VISIBLE);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bm = layoutTodo.getDrawingCache();
                if (bm != null) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    String path = MediaStore.Images.Media.insertImage(
                            getContentResolver(), bm, null, null);
                    Toast.makeText(MainActivity.this, "Speichern erfolgreich unter " + path, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Kein Bild gespeichert", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

    }

    private void shareTodo(int position) {
        String todoDetails = "";
        todoDetails += "Name: " + toDoModels.get(position).getTodoName() + "\n";
        todoDetails += "Beschreibung: " + toDoModels.get(position).getTodoDescription() + "\n";
        todoDetails += "Datum: " + toDoModels.get(position).getDate() + "\n";
        todoDetails += "Uhrzeit: " + toDoModels.get(position).getTime() + "\n";
        todoDetails += "Status: " + toDoModels.get(position).getTodoStatus() + "\n";
        todoDetails += "Favorit? : " + toDoModels.get(position).getIsFavourite() + "\n";
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mein To-do");
        emailIntent.putExtra(Intent.EXTRA_TEXT, todoDetails);
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void deleteTodo(int position) {
        new AlertDialog.Builder(this)
                .setMessage("Sicher, dass du Folgendes löschen möchtest?  " + toDoModels.get(position).getTodoName())
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqLiteDatabaseHelper.deleteTodo(toDoModels.get(position).getTodoID());
                        Toast.makeText(MainActivity.this, "Gelöscht", Toast.LENGTH_SHORT).show();
                        toDoModels.remove(position);
                        refreshAdapter();
                    }
                })
                .show();
    }

    private void editTodo(int position) {
        Intent intent = new Intent(MainActivity.this, CreateTodo.class);
        intent.putExtra("actionType", "edit");
        intent.putExtra("id", toDoModels.get(position).getTodoID());  //no id required while creating new todo
        startActivity(intent);
    }

    private void initViews() {
        imageViewLogout = findViewById(R.id.imageViewLogout);
        imageViewSort = findViewById(R.id.imageViewSort);
        imageViewAdd = findViewById(R.id.imageViewAdd);
        layoutNoTodo = findViewById(R.id.layoutNoTodo);
        recyclerView = findViewById(R.id.recyclerView);
        imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateTodo.class);
                intent.putExtra("actionType", "addNew");
                intent.putExtra("id", -1);  //no id required while creating new todo
                startActivity(intent);
            }
        });
        imageViewSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOptionMenu(view.getContext());
            }
        });

    }

    private void openOptionMenu(Context context) {

        PopupMenu popup = new PopupMenu(context, imageViewSort);
        popup.inflate(R.menu.menu_sort);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.all:
                        getAllUserTodo();
                        break;
                    case R.id.byDate:
                        Collections.sort(toDoModels, new SortByDate());
                        refreshAdapter();
                        break;
                    case R.id.byComplete:
                        SortByComplete();
                        break;
                    case R.id.byFavorite:
                        SortByFavorite();
                        break;
                    case R.id.byNeededToday:
                        showOnlyCurrentDate();
                        break;

                }
                return false;
            }
        });
        //displaying the popup
        popup.show();
    }

    private void showOnlyCurrentDate() {
        List<ToDoModel> tempList = new ArrayList<>();
        tempList.clear();
        for (ToDoModel tm : toDoModels) {
            if (tm.getDate().trim().equals(currentDate().trim())) {
                tempList.add(tm);
            }
        }
        toDoModels.clear();
        toDoModels.addAll(tempList);
        refreshAdapter();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshAdapter() {
        todoAdapter.notifyDataSetChanged();
        recyclerView.setItemViewCacheSize(toDoModels.size());
    }

    private void SortByFavorite() {
        List<ToDoModel> tempList = new ArrayList<>();
        List<ToDoModel> tempList2 = new ArrayList<>();
        tempList2.clear();
        tempList.clear();
        for (ToDoModel tm : toDoModels) {
            if (tm.getIsFavourite().equals("ja")) {
                tempList.add(tm);
            } else {
                tempList2.add(tm);
            }
        }

        toDoModels.clear();
        toDoModels.addAll(tempList);
        toDoModels.addAll(tempList2);
        refreshAdapter();
    }

    private void SortByComplete() {
        List<ToDoModel> tempList = new ArrayList<>();
        List<ToDoModel> tempList2 = new ArrayList<>();
        tempList2.clear();
        tempList.clear();
        for (ToDoModel tm : toDoModels) {
            if (tm.getTodoStatus().equals("erledigt")) {
                tempList.add(tm);
            } else {
                tempList2.add(tm);
            }
        }

        toDoModels.clear();
        toDoModels.addAll(tempList);
        toDoModels.addAll(tempList2);
        refreshAdapter();


    }


    private void logout() {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.logoutString))
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loginData.setLogin("", "");
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getAllUserTodo();
    }

    public String currentDate() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Berechtigung fehlt", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bitte erneut speichern", Toast.LENGTH_SHORT).show();
            }
        }

    }

}