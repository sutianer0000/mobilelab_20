package com.example.lab06.lesson1;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_COUNT = "count";
    private static final String KEY_TEXT_COLOR = "text_color";
    private static final String KEY_BG_COLOR = "bg_color";

    private SharedPreferences prefs;
    private TextView txtCounter;
    private EditText edtTextColor;
    private EditText edtBackgroundColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCounter = findViewById(R.id.txtCounter);
        edtTextColor = findViewById(R.id.edtTextColor);
        edtBackgroundColor = findViewById(R.id.edtBackgroundColor);
        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        int count = prefs.getInt(KEY_COUNT, 0) + 1;
        prefs.edit().putInt(KEY_COUNT, count).apply();
        txtCounter.setText(String.valueOf(count));

        String textColor = prefs.getString(KEY_TEXT_COLOR, "#FFFFFF");
        String bgColor = prefs.getString(KEY_BG_COLOR, "#2222FF");
        edtTextColor.setText(textColor);
        edtBackgroundColor.setText(bgColor);
        applyColors(textColor, bgColor);

        findViewById(R.id.btnSave).setOnClickListener(v -> saveColors());
    }

    private void saveColors() {
        String textColor = edtTextColor.getText().toString().trim();
        String bgColor = edtBackgroundColor.getText().toString().trim();
        try {
            applyColors(textColor, bgColor);
            prefs.edit().putString(KEY_TEXT_COLOR, textColor).putString(KEY_BG_COLOR, bgColor).apply();
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            Toast.makeText(this, R.string.invalid_color, Toast.LENGTH_SHORT).show();
        }
    }

    private void applyColors(String textColor, String bgColor) {
        int text = Color.parseColor(textColor);
        int background = Color.parseColor(bgColor);
        txtCounter.setTextColor(text);
        txtCounter.setBackgroundColor(background);
    }
}
