package com.example.lab04;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lesson1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        List<String> items = new ArrayList<>();
        int count = new Random().nextInt(20) + 1;
        for (int i = 1; i <= count; i++) {
            items.add("item " + i);
        }

        ListView list = findViewById(R.id.list);
        list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items));
        list.setOnItemClickListener((parent, view, position, id) ->
                Toast.makeText(this, items.get(position), Toast.LENGTH_SHORT).show());
    }
}
