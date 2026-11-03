package com.example.lab06.lesson3;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "event_id";

    private static final String[] PLACES = {"C201", "C202", "C203", "C204"};

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final SimpleDateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
    // starts at the current date and time, then keeps the last selection
    private final Calendar calendar = Calendar.getInstance();

    private EventDbHelper db;
    private Event editingEvent;
    private TextInputLayout layoutName, layoutPlace, layoutDate, layoutTime;
    private TextInputEditText edtName, edtPlace, edtDate, edtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new EventDbHelper(this);
        layoutName = findViewById(R.id.layoutName);
        layoutPlace = findViewById(R.id.layoutPlace);
        layoutDate = findViewById(R.id.layoutDate);
        layoutTime = findViewById(R.id.layoutTime);
        edtName = findViewById(R.id.edtName);
        edtPlace = findViewById(R.id.edtPlace);
        edtDate = findViewById(R.id.edtDate);
        edtTime = findViewById(R.id.edtTime);

        long id = getIntent().getLongExtra(EXTRA_ID, -1);
        if (id != -1) {
            editingEvent = db.getEvent(id);
            showEvent(editingEvent);
        }

        edtPlace.setOnClickListener(v -> showPlaceDialog());
        edtDate.setOnClickListener(v -> showDateDialog());
        edtTime.setOnClickListener(v -> showTimeDialog());
    }

    private void showEvent(Event event) {
        edtName.setText(event.name);
        edtPlace.setText(event.place);
        edtDate.setText(event.date);
        edtTime.setText(event.time);
        try {
            calendar.setTime(fullFormat.parse(event.date + " " + event.time));
        } catch (ParseException e) {
            calendar.setTimeInMillis(System.currentTimeMillis());
        }
    }

    private void showPlaceDialog() {
        int selected = Math.max(Arrays.asList(PLACES).indexOf(getText(edtPlace)), 0);
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_place)
                .setSingleChoiceItems(PLACES, selected, (dialog, which) -> {
                    edtPlace.setText(PLACES[which]);
                    dialog.dismiss();
                })
                .show();
    }

    private void showDateDialog() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            edtDate.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimeDialog() {
        new TimePickerDialog(this, (view, hour, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            edtTime.setText(timeFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuSave) {
            saveEvent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveEvent() {
        boolean nameOk = checkField(layoutName, edtName, R.string.error_name);
        boolean placeOk = checkField(layoutPlace, edtPlace, R.string.error_place);
        boolean dateOk = checkField(layoutDate, edtDate, R.string.error_date);
        boolean timeOk = checkField(layoutTime, edtTime, R.string.error_time);
        if (!nameOk || !placeOk || !dateOk || !timeOk) return;

        if (editingEvent == null) {
            db.insert(new Event(0, getText(edtName), getText(edtPlace), getText(edtDate), getText(edtTime), true));
        } else {
            editingEvent.name = getText(edtName);
            editingEvent.place = getText(edtPlace);
            editingEvent.date = getText(edtDate);
            editingEvent.time = getText(edtTime);
            db.update(editingEvent);
        }
        finish();
    }

    private boolean checkField(TextInputLayout layout, TextInputEditText input, int errorId) {
        boolean ok = !getText(input).isEmpty();
        layout.setError(ok ? null : getString(errorId));
        return ok;
    }

    private String getText(TextInputEditText input) {
        return input.getText().toString().trim();
    }
}
