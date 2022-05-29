package com.example.todo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class CreateTodo extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener
        , DatePickerDialog.OnDateSetListener {

    ImageView imageViewCancel;

    EditText editTextName, editTextDescription;
    TextView textViewDate, textViewTime, textViewAction;
    Button buttonSave;
    SQLiteDatabaseHelper sqLiteDatabaseHelper;
    LoginData loginData;
    int id;
    String actionType;
    ToDoModel oldToDoModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_todo);
        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);
        loginData = new LoginData(this);
        getDataFromIntent();
        initViews();
    }

    private void getDataFromIntent() {
        id = getIntent().getIntExtra("id", -1);
        actionType = getIntent().getStringExtra("actionType");
        if (actionType.equals("edit")) {
            oldToDoModel = sqLiteDatabaseHelper.getSingleToDo(id);
        }

    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        textViewDate = findViewById(R.id.textViewDate);
        textViewTime = findViewById(R.id.textViewTime);
        textViewAction = findViewById(R.id.textViewAction);
        buttonSave = findViewById(R.id.buttonSave);
        imageViewCancel = findViewById(R.id.imageViewCancel);
        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePicker();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePicker();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        if (actionType.equals("edit")) {
            editTextName.setText(oldToDoModel.getTodoName());
            editTextDescription.setText(oldToDoModel.getTodoDescription());
            textViewAction.setText("Ändern");
            buttonSave.setText("Speichern");
            textViewDate.setText(oldToDoModel.getDate());
            textViewTime.setText(oldToDoModel.getTime());
        }


    }

    private void saveData() {
        ToDoModel toDoModel = new ToDoModel();
        toDoModel.setUserName(loginData.getUserName());

        if (actionType.equals("edit")) {
            toDoModel.setTodoStatus(oldToDoModel.getTodoStatus());
            toDoModel.setIsFavourite(oldToDoModel.getIsFavourite());
        }
        toDoModel.setTodoStatus("nicht erledigt");
        toDoModel.setIsFavourite("nein");
        toDoModel.setTodoName(editTextName.getText().toString());
        toDoModel.setTodoDescription(editTextDescription.getText().toString());
        String date = textViewDate.getText().toString();
        String time = textViewTime.getText().toString();
        toDoModel.setDate(date);
        toDoModel.setTime(time);

        if (toDoModel.getTodoName().equals("")) {
            Toast.makeText(this, "Name erforderlich", Toast.LENGTH_SHORT).show();
        } else if (toDoModel.getTodoDescription().equals("")) {
            Toast.makeText(this, "Beschreibung erforderlich", Toast.LENGTH_SHORT).show();
        } else if (date.equals("")) {
            Toast.makeText(this, "Datum erforderlich", Toast.LENGTH_SHORT).show();
        } else if (time.equals("")) {
            Toast.makeText(this, "Uhrzeit erforderlich", Toast.LENGTH_SHORT).show();
        } else {  //  everything okay save data
            if (actionType.equals("edit")) {
                toDoModel.setTodoID(id);
                if (sqLiteDatabaseHelper.updateToDo(toDoModel)) {
                    Toast.makeText(this, "Geändert", Toast.LENGTH_SHORT).show();
                    finish(); //  close activity
                } else {
                    Toast.makeText(this, "Speichern fehlgeschlagen ", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (sqLiteDatabaseHelper.addNewTODO(toDoModel)) {
                    Toast.makeText(this, "Speichern erfolgreich", Toast.LENGTH_SHORT).show();
                    finish(); //  close activity
                } else {
                    Toast.makeText(this, "Speichern fehlgeschlagen", Toast.LENGTH_SHORT).show();
                }
            }

        }


    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int dayOfMonth) {
        String date = "";
        if (dayOfMonth < 10) {
            date = date + "0" + dayOfMonth + "-";
        } else {
            date = date + dayOfMonth + "-";
        }
        if (month < 10) {
            date = date + "0" + (month + 1) + "-";
        } else {
            date = date + (month + 1) + "-";
        }
        date = date + year;
        textViewDate.setText(date);
    }

    @Override
    public void onTimeSet(android.widget.TimePicker timePicker, int hourOfDay, int minute) {
        String time = "";
        if (hourOfDay < 10) {
            time = time + "0" + hourOfDay + ":";
        } else {
            time = time + hourOfDay + ":";
        }
        if (minute < 10) {
            time = time + "0" + minute;
        } else {
            time = time + minute;
        }
        textViewTime.setText(time);
    }
}
