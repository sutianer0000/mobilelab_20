package com.example.lab04;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Lesson5Activity extends AppCompatActivity {

    private static final int STEP = 5;

    private final List<User> users = new ArrayList<>();
    private UserAdapter adapter;
    private TextView txtTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson5);

        txtTotal = findViewById(R.id.txtTotal);
        adapter = new UserAdapter(users);

        RecyclerView list = findViewById(R.id.listUsers);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list.setAdapter(adapter);

        findViewById(R.id.btnAdd).setOnClickListener(v -> addUsers());
        findViewById(R.id.btnRemove).setOnClickListener(v -> removeUsers());
        updateList();
    }

    private void addUsers() {
        for (int i = 0; i < STEP; i++) {
            int number = users.size() + 1;
            users.add(new User("User " + number, "user" + number + "@tdtu.edu.vn"));
        }
        updateList();
    }

    private void removeUsers() {
        users.subList(Math.max(0, users.size() - STEP), users.size()).clear();
        updateList();
        if (users.isEmpty()) {
            Toast.makeText(this, R.string.list_empty, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateList() {
        adapter.notifyDataSetChanged();
        txtTotal.setText(getString(R.string.total_users, users.size()));
    }

    private static class User {
        final String name;
        final String email;

        User(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }

    private static class UserAdapter extends RecyclerView.Adapter<UserAdapter.Holder> {
        private final List<User> users;

        UserAdapter(List<User> users) {
            this.users = users;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            User user = users.get(position);
            holder.txtName.setText(user.name);
            holder.txtEmail.setText(user.email);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView txtName;
            final TextView txtEmail;

            Holder(View view) {
                super(view);
                txtName = view.findViewById(R.id.txtName);
                txtEmail = view.findViewById(R.id.txtEmail);
            }
        }
    }
}
