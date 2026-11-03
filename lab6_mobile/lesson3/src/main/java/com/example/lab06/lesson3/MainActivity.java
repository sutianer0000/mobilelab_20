package com.example.lab06.lesson3;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class MainActivity extends AppCompatActivity {

    private static final int MENU_DELETE = 1;
    private static final int MENU_EDIT = 2;

    private EventDbHelper db;
    private EventAdapter adapter;
    private boolean showAll = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new EventDbHelper(this);
        adapter = new EventAdapter();

        ListView list = findViewById(R.id.listEvents);
        list.setAdapter(adapter);
        registerForContextMenu(list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        adapter.clear();
        adapter.addAll(db.getEvents(showAll));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SwitchCompat switchShowAll = (SwitchCompat) menu.findItem(R.id.menuShowAll).getActionView();
        switchShowAll.setChecked(showAll);
        switchShowAll.setOnCheckedChangeListener((button, isChecked) -> {
            showAll = isChecked;
            loadEvents();
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuAdd) {
            startActivity(new Intent(this, AddEventActivity.class));
            return true;
        }
        if (id == R.id.menuRemoveAll) {
            confirmRemoveAll();
            return true;
        }
        if (id == R.id.menuAbout) {
            Toast.makeText(this, R.string.about_text, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmRemoveAll() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.confirm_remove_all)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    db.deleteAll();
                    loadEvents();
                })
                .show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, MENU_DELETE, 0, R.string.delete);
        menu.add(Menu.NONE, MENU_EDIT, 1, R.string.edit);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Event event = adapter.getItem(info.position);
        if (item.getItemId() == MENU_DELETE) {
            db.delete(event.id);
            loadEvents();
        } else {
            Intent intent = new Intent(this, AddEventActivity.class);
            intent.putExtra(AddEventActivity.EXTRA_ID, event.id);
            startActivity(intent);
        }
        return true;
    }

    private class EventAdapter extends ArrayAdapter<Event> {

        EventAdapter() {
            super(MainActivity.this, R.layout.item_event);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
            }
            Event event = getItem(position);
            TextView txtName = row.findViewById(R.id.txtName);
            TextView txtPlace = row.findViewById(R.id.txtPlace);
            TextView txtDateTime = row.findViewById(R.id.txtDateTime);
            SwitchCompat switchEnabled = row.findViewById(R.id.switchEnabled);

            txtName.setText(event.name);
            txtPlace.setText(event.place);
            txtDateTime.setText(event.date + " " + event.time);
            switchEnabled.setChecked(event.enabled);
            switchEnabled.setOnClickListener(v -> {
                db.setEnabled(event.id, switchEnabled.isChecked());
                loadEvents();
            });
            return row;
        }
    }
}
