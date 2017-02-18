package com.iglin.lab2rest;

import android.content.Intent;
import android.icu.text.MessagePattern;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iglin.lab2rest.model.Participant;

import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    protected EditText passwordEditText;
    protected EditText emailEditText;
    protected EditText nameEditText;
    protected EditText positionEditText;
    protected Button signUpButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        passwordEditText = (EditText)findViewById(R.id.passwordField);
        emailEditText = (EditText)findViewById(R.id.emailField);
        nameEditText = (EditText)findViewById(R.id.nameField);
        positionEditText = (EditText)findViewById(R.id.fieldPos);
        signUpButton = (Button)findViewById(R.id.signupButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                String email = emailEditText.getText().toString();
                final String name = nameEditText.getText().toString().trim();
                final String position = positionEditText.getText().toString().trim();

                password = password.trim();
                email = email.trim();

                if (password.isEmpty() || email.isEmpty() || name.isEmpty() || position.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.signup_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final Participant participant = new Participant();
                                        participant.setName(name);
                                        participant.setPosition(position);
                                        firebaseDatabase.getReference().child("users")
                                                .child(firebaseAuth.getCurrentUser().getUid()).setValue(participant);
                                      /*  firebaseDatabase.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map users = dataSnapshot.getValue(Map.class);
                                                users.put(firebaseAuth.getCurrentUser().getUid(), participant);
                                                firebaseDatabase.getReference().child("users").setValue(users);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                                builder.setMessage("Unable to save user data.")
                                                        .setTitle(R.string.login_error_title)
                                                        .setPositiveButton(android.R.string.ok, null);
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        });*/

                                        Intent intent = new Intent(SignUpActivity.this, MeetingListActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                        builder.setMessage(task.getException().getMessage())
                                                .setTitle(R.string.login_error_title)
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                }
            }
        });
    }

}