package com.example.lab04;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String[] LESSONS = {"Lesson 1", "Lesson 2", "Lesson 3", "Lesson 4", "Lesson 5"};
    private static final Class<?>[] SCREENS = {Lesson1Activity.class, Lesson2Activity.class,
            Lesson3Activity.class, Lesson4Activity.class, Lesson5Activity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView list = findViewById(R.id.list);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, LESSONS));
        list.setOnItemClickListener((parent, view, position, id) ->
                startActivity(new Intent(this, SCREENS[position])));
    }
}
