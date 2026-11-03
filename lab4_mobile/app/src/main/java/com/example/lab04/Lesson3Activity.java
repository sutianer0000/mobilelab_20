package com.example.lab04;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Lesson3Activity extends AppCompatActivity {

    private final List<Phone> phones = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson3);

        for (String name : new String[]{"Apple", "Samsung", "Nokia", "Oppo"}) {
            phones.add(new Phone(name));
        }

        PhoneAdapter adapter = new PhoneAdapter(phones);
        RecyclerView list = findViewById(R.id.listPhones);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list.setAdapter(adapter);

        findViewById(R.id.btnRemoveAll).setOnClickListener(v -> {
            phones.clear();
            adapter.notifyDataSetChanged();
        });
        findViewById(R.id.btnRemoveSelected).setOnClickListener(v -> {
            phones.removeIf(phone -> phone.checked);
            adapter.notifyDataSetChanged();
        });
    }

    private static class Phone {
        final String name;
        boolean checked = false;

        Phone(String name) {
            this.name = name;
        }
    }

    private static class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.Holder> {
        private final List<Phone> phones;

        PhoneAdapter(List<Phone> phones) {
            this.phones = phones;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Phone phone = phones.get(position);
            holder.txtName.setText(phone.name);
            holder.checkPhone.setChecked(phone.checked);
            holder.checkPhone.setOnClickListener(v -> phone.checked = holder.checkPhone.isChecked());
        }

        @Override
        public int getItemCount() {
            return phones.size();
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView txtName;
            final CheckBox checkPhone;

            Holder(View view) {
                super(view);
                txtName = view.findViewById(R.id.txtName);
                checkPhone = view.findViewById(R.id.checkPhone);
            }
        }
    }
}
