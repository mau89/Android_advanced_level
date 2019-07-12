package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
        sendSMS(view);
        return view;
    }

    private void sendSMS(View view) {
        EditText editText = view.findViewById(R.id.message);
        Button button = view.findViewById(R.id.send_developer);
        button.setOnClickListener(v -> {
            String toSMS = "89220000000";
            String message = editText.getText().toString();
            try {
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("smsto:" + Uri.encode(toSMS)));
                sendIntent.putExtra("sms_body", message);
                startActivity(sendIntent);
            } catch (Exception e) {
                Toast.makeText(getActivity(),
                        "SMS не отправлено, попытайтесь еще!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            Objects.requireNonNull(getActivity()).onBackPressed();
        });
    }

}
