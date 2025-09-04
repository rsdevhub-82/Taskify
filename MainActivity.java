package com.example.taskify;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // 1. Declare all UI components at class level
    private EditText editTextTask;
    private Button buttonAdd;
    private ListView listViewTasks;

    private ArrayList<String> tasks;
    private ArrayAdapter<String> adapter;

    private GestureDetector gestureDetector;
    private int swipedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Initialize UI components
        editTextTask = findViewById(R.id.editTextTask);
        buttonAdd = findViewById(R.id.buttonAdd);
        listViewTasks = findViewById(R.id.listViewTasks);

        // 3. Setup task list and adapter
        tasks = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                tasks
        );
        listViewTasks.setAdapter(adapter);

        // 4. Set click listeners
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        // 5. GestureDetector for swipe detection
        gestureDetector = new GestureDetector(this, new GestureListener());

        // 6. Attach touch listener to ListView
        listViewTasks.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        // 7. Track which item is swiped
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            swipedPosition = position;
        });
    }

    private void addTask() {
        String task = editTextTask.getText().toString().trim();
        if (!task.isEmpty()) {
            tasks.add(task);
            adapter.notifyDataSetChanged();
            editTextTask.setText("");
            Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTask(int position) {
        if (position >= 0 && position < tasks.size()) {
            tasks.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Task deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void editTask(int position) {
        if (position >= 0 && position < tasks.size()) {
            EditText input = new EditText(this);
            input.setText(tasks.get(position));

            new AlertDialog.Builder(this)
                    .setTitle("Edit Task")
                    .setView(input)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newTask = input.getText().toString().trim();
                        if (!newTask.isEmpty()) {
                            tasks.set(position, newTask);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    // Custom GestureListener to detect left/right swipes
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe Right → Delete
                        deleteTask(swipedPosition);
                    } else {
                        // Swipe Left → Edit
                        editTask(swipedPosition);
                    }
                    return true;
                }
            }
            return false;
        }
    }
}
