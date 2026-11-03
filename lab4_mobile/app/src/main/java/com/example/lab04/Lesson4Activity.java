package com.example.lab04;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lesson4Activity extends AppCompatActivity {

    private static final int COLOR_ON = 0xFF1565C0;
    private static final int COLOR_OFF = 0xFFBDBDBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson4);

        int count = new Random().nextInt(91) + 10;
        boolean[] isOn = new boolean[count];
        List<String> items = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            items.add("PC " + i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_pc, R.id.txtPc, items) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View cell = super.getView(position, convertView, parent);
                ImageView imgPc = cell.findViewById(R.id.imgPc);
                imgPc.setColorFilter(isOn[position] ? COLOR_ON : COLOR_OFF);
                return cell;
            }
        };

        GridView grid = findViewById(R.id.gridPc);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener((parent, view, position, id) -> {
            isOn[position] = !isOn[position];
            adapter.notifyDataSetChanged();
        });
    }
}
