package com.example.wabisabi.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wabisabi.Activity.users.User;
import com.example.wabisabi.Activity.users.UsersAdapter;
import com.example.wabisabi.databinding.AddParticipantsToChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddParticipantsToGroup extends Fragment {

    private AddParticipantsToChatBinding binding;
    private SearchView searchView;
    private ArrayList<User> users = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddParticipantsToChatBinding.inflate(inflater, container, false);


            binding.backFromChatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getParentFragmentManager().popBackStack();
                }
            });
        loadUsers();
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<User> filteredUsers = filter(users, newText);
                UsersAdapter adapter = new UsersAdapter(filteredUsers); // Обновляем адаптер
                binding.usersRv.setAdapter(adapter); // Обновляем RecyclerView
                return true;
            }
        });
    }
    private void loadUsers() {
        users.clear();

        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String uid = userSnapshot.getKey();

                    if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        continue;
                    }

                    String username = userSnapshot.child("username").getValue(String.class);
                    String profileImage = userSnapshot.child("profileImage").getValue(String.class);
                    String status = userSnapshot.child("status").getValue(String.class);

                    if (username != null && profileImage != null) {
                        users.add(new User(uid, username, profileImage/*, status*/));
                    }
                }

                // Создаем адаптер после загрузки данных
                UsersAdapter adapter = new UsersAdapter(users);
                binding.usersRv.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.usersRv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                binding.usersRv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private ArrayList<User> filter(ArrayList<User> users, String query) {
        ArrayList<User> filteredUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }
    private void createChat(User user) {

    }

}