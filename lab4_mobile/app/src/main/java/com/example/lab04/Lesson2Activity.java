package com.example.lab04;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lesson2Activity extends AppCompatActivity {

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
        list.setAdapter(new ArrayAdapter<String>(this, R.layout.item_remove, R.id.txtItem, items) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                row.findViewById(R.id.btnRemove).setOnClickListener(v -> remove(getItem(position)));
                return row;
            }
        });
    }
}
