package com.example.anonynotes;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WriteFragment extends Fragment {

    private EditText write_input;
    private Button submit;
    private TextView charCount;

    public WriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set the soft input mode for this fragment
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_write, container, false);

        write_input = view.findViewById(R.id.write_input);
        submit = view.findViewById(R.id.submit);
        charCount = view.findViewById(R.id.char_count);

        write_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Limit to 200 characters
                if (charSequence.length() > 200) {
                    write_input.setText(charSequence.subSequence(0, 200)); // Trim extra characters
                    write_input.setSelection(200); // Set cursor position to the end
                    Toast.makeText(getContext(), "Limit reached! Max 200 characters.", Toast.LENGTH_SHORT).show();
                }
                charCount.setText(charSequence.length() + "/200");
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        submit.setOnClickListener(v -> {
            String content = write_input.getText().toString();
            if (!content.isEmpty()) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String username = preferences.getString("username", null);
                String userIdString = preferences.getString("user_id", null);

                if (userIdString != null) {
                    try {
                        int userId = Integer.parseInt(userIdString);
                        sendNoteToServer(username, content, false, userId);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Invalid User ID format. Please log in again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter some content.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void sendNoteToServer(String username, String content, boolean anonymous, int userId) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Submitting note...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.get("application/json; charset=utf-8");

                JSONObject json = new JSONObject();
                try {
                    json.put("user_name", username);
                    json.put("content", content);
                    json.put("anonymous", anonymous ? 1 : 0);
                    json.put("user_id", userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                String token = preferences.getString("auth_token", null);

                RequestBody body = RequestBody.create(json.toString(), JSON);
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8000/api/notes")
                        //.url("http://192.168.100.27:8000/api/notes")
                        .post(body)
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    return response.body() != null ? response.body().string() : null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                progressDialog.dismiss();
                if (result != null) {
                    Toast.makeText(getContext(), "Note submitted successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate back to HomeFragment
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_layout, new HomeFragment()) // Use the correct container ID
                            .commit();

                    ((MainActivity) getActivity()).binding.bottomNavMenuNavigation.setSelectedItemId(R.id.Home_Button_Nav_Menu);

                } else {
                    Toast.makeText(getContext(), "Failed to submit note. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute();
    }
}
