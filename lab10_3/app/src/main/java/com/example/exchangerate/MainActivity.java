package com.example.exchangerate;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL =
            "https://v6.exchangerate-api.com/v6/" + BuildConfig.API_KEY + "/";
    private static final String[] DefaultCurrencies = {"USD", "EUR", "JPY", "GBP"};

    private List<String> currencies;
    private String currentBase = "VND";
    private AutoCompleteTextView inputBase;
    private AutoCompleteTextView inputTarget;
    private TextView txtUpdated;
    private TextView txtTargetRate;
    private ArrayAdapter<String> ratesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputBase = findViewById(R.id.inputBase);
        inputTarget = findViewById(R.id.inputTarget);
        txtUpdated = findViewById(R.id.txtUpdated);
        txtTargetRate = findViewById(R.id.txtTargetRate);
        ListView listRates = findViewById(R.id.listRates);

        currencies = Arrays.asList(getResources().getStringArray(R.array.currencies));

        ratesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listRates.setAdapter(ratesAdapter);

        setupInput(inputBase, () -> {
            if (isValid(inputBase)) loadRates(getCode(inputBase));
        });
        setupInput(inputTarget, this::loadTarget);

        // tap once to replace the whole code
        inputBase.setOnClickListener(v -> inputBase.selectAll());
        inputTarget.setOnClickListener(v -> inputTarget.selectAll());

        txtUpdated.setOnClickListener(v -> loadRates(currentBase));
        txtTargetRate.setOnClickListener(v -> loadTarget());

        inputBase.setText(currentBase);
    }

    private void setupInput(AutoCompleteTextView input, Runnable onChanged) {
        input.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, currencies));
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onChanged.run();
            }
        });
    }

    private void loadRates(String base) {
        currentBase = base;
        ratesAdapter.clear();
        txtUpdated.setText(R.string.loading);
        loadTarget();

        new Thread(() -> {
            try {
                JSONObject json = new JSONObject(download("latest/" + base));
                JSONObject rates = json.getJSONObject("conversion_rates");

                List<String> rows = new ArrayList<>();
                for (String code : DefaultCurrencies) {
                    rows.add(formatRate(code, rates.getDouble(code), base));
                }

                Date updated = new Date(json.getLong("time_last_update_unix") * 1000);
                String time = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(updated);

                runOnUiThread(() -> {
                    // user may have picked another base while this was loading
                    if (!base.equals(currentBase)) return;
                    ratesAdapter.clear();
                    ratesAdapter.addAll(rows);
                    txtUpdated.setText(getString(R.string.last_updated, time));
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (!base.equals(currentBase)) return;
                    txtUpdated.setText(R.string.load_error);
                });
            }
        }).start();
    }

    private void loadTarget() {
        String base = currentBase;
        String target = getCode(inputTarget);
        txtTargetRate.setText("");

        // blank or still typing
        if (!isValid(inputTarget)) return;
        txtTargetRate.setText(R.string.loading);

        new Thread(() -> {
            try {
                JSONObject json = new JSONObject(download("pair/" + base + "/" + target));
                String row = formatRate(target, json.getDouble("conversion_rate"), base);

                runOnUiThread(() -> {
                    if (isOldTarget(base, target)) return;
                    txtTargetRate.setText(row);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (isOldTarget(base, target)) return;
                    txtTargetRate.setText(R.string.load_error);
                });
            }
        }).start();
    }

    private String getCode(AutoCompleteTextView input) {
        return input.getText().toString().trim().toUpperCase(Locale.US);
    }

    private boolean isValid(AutoCompleteTextView input) {
        String code = getCode(input);
        if (currencies.contains(code)) return true;
        if (code.length() == 3) input.setError(getString(R.string.unknown_currency));
        return false;
    }

    private boolean isOldTarget(String base, String target) {
        return !base.equals(currentBase) || !target.equals(getCode(inputTarget));
    }

    private String formatRate(String code, double rate, String base) {
        // the api only gives 4 significant digits
        BigDecimal value = new BigDecimal(1 / rate, new MathContext(4));
        return "1 " + code + " = " + value.toPlainString() + " " + base;
    }

    private String download(String path) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(API_URL + path).openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } finally {
            conn.disconnect();
        }
    }
}
