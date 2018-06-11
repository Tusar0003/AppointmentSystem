package com.example.no0ne.appointmentsystem.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.no0ne.appointmentsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity extends AppCompatActivity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLogInButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        mEmailEditText = findViewById(R.id.edit_text_email);
        mPasswordEditText = findViewById(R.id.edit_text_password);
        mLogInButton = findViewById(R.id.button_log_in);

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LogInActivity.this, "Email can not be empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LogInActivity.this, "Password can not be empty!", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(LogInActivity.this, "userLogin()", Toast.LENGTH_SHORT).show();
                    mProgressDialog = new ProgressDialog(LogInActivity.this);
                    mProgressDialog.setMessage("Logging In");
                    mProgressDialog.show();
                    userLogIn(email, password);
                }
            }
        });
    }

    private void userLogIn(String email, String password) {
//        Toast.makeText(LogInActivity.this, "userLogin(): "+email+" "+password, Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    Log.d("TASK_SUCCESSFUL", task.getResult().toString());
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    updateUI(currentUser);
                } else {
                    // If sign in fails, display a message to the user.
                    mProgressDialog.cancel();
                    Toast.makeText(LogInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Toast.makeText(LogInActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("account_for");

//        Log.e("USER-REFERENCE", mUserReference.toString());
//        Log.e("CURRENT_USER", mAuth.getCurrentUser().getUid().toString());

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Toast.makeText(LogInActivity.this, "Log In Successful!", Toast.LENGTH_SHORT).show();
//                Log.e("Account For:", dataSnapshot.getValue().toString());

                String accountFor = dataSnapshot.getValue().toString();
//                Log.e("Account For:", accountFor);

                if (accountFor.equalsIgnoreCase("Student")) {
                    mProgressDialog.cancel();
                    Intent intent = new Intent(LogInActivity.this, StudentActivity.class);
                    startActivity(intent);
                    finish();
                } else if (accountFor.equalsIgnoreCase("Teacher")) {
                    mProgressDialog.cancel();
                    Intent intent = new Intent(LogInActivity.this, TeacherActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    mProgressDialog.cancel();
                    Toast.makeText(LogInActivity.this, "Unknown account type!"+ accountFor, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(LogInActivity.this, "onCancelled!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
