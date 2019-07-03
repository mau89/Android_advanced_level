package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class SendDeveloperFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_developer, container, false);
        Button button = view.findViewById(R.id.send_developer);
        button.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Сообщение отправлено", Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(getActivity()).onBackPressed();
        });
        return view;

    }
}
