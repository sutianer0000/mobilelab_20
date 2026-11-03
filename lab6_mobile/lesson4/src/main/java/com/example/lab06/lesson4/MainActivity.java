package com.example.lab06.lesson4;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final Set<File> selected = new HashSet<>();

    private File rootDir;
    private File currentDir;
    private FileAdapter adapter;
    private boolean askedPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootDir = Environment.getExternalStorageDirectory();
        currentDir = rootDir;
        adapter = new FileAdapter();

        ListView list = findViewById(R.id.listFiles);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, id) -> {
            File file = adapter.getItem(position);
            if (file.isDirectory()) openFolder(file);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (!currentDir.equals(rootDir)) openFolder(currentDir.getParentFile());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Environment.isExternalStorageManager()) {
            loadFiles();
        } else if (!askedPermission) {
            askedPermission = true;
            startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:" + getPackageName())));
        } else {
            Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_LONG).show();
        }
    }

    private void openFolder(File folder) {
        currentDir = folder;
        selected.clear();
        loadFiles();
    }

    private void loadFiles() {
        File[] files = currentDir.listFiles();
        adapter.clear();
        if (files == null) return;
        // folders first, then by name
        Arrays.sort(files, Comparator.comparing((File f) -> !f.isDirectory())
                .thenComparing(f -> f.getName().toLowerCase()));
        adapter.addAll(files);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuAddFolder) {
            showFolderDialog();
        } else if (id == R.id.menuAddFile) {
            showFileDialog();
        } else if (id == R.id.menuDeleteSelected) {
            confirmDelete(new ArrayList<>(selected));
        } else if (id == R.id.menuDeleteAll) {
            List<File> all = new ArrayList<>();
            for (int i = 0; i < adapter.getCount(); i++) {
                all.add(adapter.getItem(i));
            }
            confirmDelete(all);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void showFolderDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_folder, null);
        EditText edtName = view.findViewById(R.id.edtFolderName);
        new AlertDialog.Builder(this)
                .setTitle(R.string.create_folder)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, (dialog, which) -> createFolder(edtName.getText().toString().trim()))
                .show();
    }

    private void showFileDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_file, null);
        EditText edtName = view.findViewById(R.id.edtFileName);
        EditText edtContent = view.findViewById(R.id.edtFileContent);
        new AlertDialog.Builder(this)
                .setTitle(R.string.create_file)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, (dialog, which) ->
                        createFile(edtName.getText().toString().trim(), edtContent.getText().toString()))
                .show();
    }

    private void createFolder(String name) {
        if (!canCreate(name)) return;
        if (!new File(currentDir, name).mkdir()) {
            Toast.makeText(this, R.string.create_failed, Toast.LENGTH_SHORT).show();
        }
        loadFiles();
    }

    private void createFile(String name, String content) {
        if (!canCreate(name)) return;
        try {
            Files.write(new File(currentDir, name).toPath(), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Toast.makeText(this, R.string.create_failed, Toast.LENGTH_SHORT).show();
        }
        loadFiles();
    }

    private boolean canCreate(String name) {
        if (name.isEmpty() || name.contains("/")) {
            Toast.makeText(this, R.string.invalid_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (new File(currentDir, name).exists()) {
            Toast.makeText(this, R.string.already_exists, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void confirmDelete(List<File> files) {
        if (files.isEmpty()) {
            Toast.makeText(this, R.string.nothing_to_delete, Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.confirm_delete, files.size()))
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteFiles(files))
                .show();
    }

    private void deleteFiles(List<File> files) {
        boolean allDeleted = true;
        for (File file : files) {
            allDeleted &= deleteRecursive(file);
        }
        if (!allDeleted) {
            Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
        }
        selected.clear();
        loadFiles();
    }

    private boolean deleteRecursive(File file) {
        boolean ok = true;
        if (file.isDirectory() && !Files.isSymbolicLink(file.toPath())) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    ok &= deleteRecursive(child);
                }
            }
        }
        return file.delete() && ok;
    }

    private class FileAdapter extends ArrayAdapter<File> {

        FileAdapter() {
            super(MainActivity.this, R.layout.item_file);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = LayoutInflater.from(getContext()).inflate(R.layout.item_file, parent, false);
            }
            File file = getItem(position);
            ImageView imgIcon = row.findViewById(R.id.imgIcon);
            TextView txtFileName = row.findViewById(R.id.txtFileName);
            CheckBox checkFile = row.findViewById(R.id.checkFile);

            imgIcon.setImageResource(file.isDirectory() ? R.drawable.ic_folder : R.drawable.ic_file);
            txtFileName.setText(file.getName());
            checkFile.setChecked(selected.contains(file));
            checkFile.setOnClickListener(v -> {
                if (checkFile.isChecked()) {
                    selected.add(file);
                } else {
                    selected.remove(file);
                }
            });
            return row;
        }
    }
}
