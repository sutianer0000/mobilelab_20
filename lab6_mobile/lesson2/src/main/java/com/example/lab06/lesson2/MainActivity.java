package com.example.lab06.lesson2;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "data.txt";

    private EditText edtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtContent = findViewById(R.id.edtContent);
        File internal = new File(getFilesDir(), FILE_NAME);
        File external = new File(getExternalFilesDir(null), FILE_NAME);

        findViewById(R.id.btnReadInternal).setOnClickListener(v -> readFile(internal));
        findViewById(R.id.btnReadExternal).setOnClickListener(v -> readFile(external));
        findViewById(R.id.btnWriteInternal).setOnClickListener(v -> writeFile(internal));
        findViewById(R.id.btnWriteExternal).setOnClickListener(v -> writeFile(external));
    }

    private void readFile(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            edtContent.setText(new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException e) {
            Toast.makeText(this, R.string.read_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void writeFile(File file) {
        try {
            byte[] bytes = edtContent.getText().toString().getBytes(StandardCharsets.UTF_8);
            Files.write(file.toPath(), bytes);
            Toast.makeText(this, getString(R.string.written, file.getParent()), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, R.string.write_error, Toast.LENGTH_SHORT).show();
        }
    }
}
