package com.example.no0ne.appointmentsystem.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.no0ne.appointmentsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {

    private EditText mUserNameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mPhoneNumberEditText;
    private CheckBox teacherCheckBox;
    private Button signInButton;
    private EditText mCodeEditText;

    private String mUserName;
    private String mEmail;
    private String mPassword;
    private String mPhoneNumber;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserReference;

    private String mPhoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mVerificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mUserNameEditText = findViewById(R.id.edit_text_user_name);
        mEmailEditText = findViewById(R.id.edit_text_email);
        mPasswordEditText = findViewById(R.id.edit_text_password);
        mPhoneNumberEditText = findViewById(R.id.edit_text_phone_number);
        teacherCheckBox = findViewById(R.id.check_box_teacher);
        signInButton = findViewById(R.id.button_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserName = mUserNameEditText.getText().toString();
                mEmail = mEmailEditText.getText().toString();
                mPassword = mPasswordEditText.getText().toString();

                if (TextUtils.isEmpty(mUserName)) {
                    Toast.makeText(SignInActivity.this, "User Name can not be empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mEmail)) {
                    Toast.makeText(SignInActivity.this, "Email can not be empty!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mPassword)) {
                    Toast.makeText(SignInActivity.this, "Password can not be empty!", Toast.LENGTH_SHORT).show();
                } else if (teacherCheckBox.isChecked()) {
                    mPhoneNumberEditText.setVisibility(View.VISIBLE);

                    mPhoneNumber = mPhoneNumberEditText.getText().toString();

                    if (TextUtils.isEmpty(mPhoneNumber)) {
                        Toast.makeText(SignInActivity.this, "Please provide a phone number for verification!", Toast.LENGTH_SHORT).show();
                    } else {
                        sendCode();
                        showDialog();
                    }
                } else {
                    mProgressDialog = new ProgressDialog(SignInActivity.this);
                    mProgressDialog.setMessage("Signing In");
                    mProgressDialog.show();

                    mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                String currentUserId = currentUser.getUid();

                                mUserReference = mDatabase.getReference().child("Users").child(currentUserId);

                                HashMap<String, String> userMap = new HashMap<String, String>();
                                userMap.put("user_name", mUserName);
                                userMap.put("email", mEmail);
                                userMap.put("account_for", "Student");

                                mUserReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
//                                            Toast.makeText(SignInActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();

                                            mProgressDialog.cancel();
                                            Intent intent = new Intent(SignInActivity.this, StudentActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(SignInActivity.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_varification);
        dialog.setTitle("Enter Code");
        dialog.show();

        mCodeEditText = dialog.findViewById(R.id.verification_code);
        Button verifyButton = dialog.findViewById(R.id.button_verify);
        Button resendButton = dialog.findViewById(R.id.button_send_again);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog = new ProgressDialog(SignInActivity.this);
                mProgressDialog.setMessage("Signing In");
                mProgressDialog.show();

                String code = mCodeEditText.getText().toString();

                if (code.isEmpty()) {
                    Toast.makeText(SignInActivity.this, "Please enter the verification code!", Toast.LENGTH_SHORT).show();
                } else {
                    verifyCode();
                    dialog.dismiss();
                }
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendCode();
            }
        });
    }

    public void sendCode() {
        String phoneNumber = mPhoneNumberEditText.getText().toString();

        setUpVerificationCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,                // Phone number to verify
                60,                      // Timeout duration
                TimeUnit.SECONDS,          // Unit of timeout
                this,              // Activity (for callback binding)
                mVerificationCallbacks);
    }

    private void setUpVerificationCallbacks() {
        mVerificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("onVerificationFailed", e.toString());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // SMS quota exceeded
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                mPhoneVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                updateUI();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                                Log.d("***NOTICE***", task.getException().toString());
                            }
                        }
                    });
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(SignInActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                        Log.d("NOTICE", task.getException().toString());
                    }
                }
            }
        });
    }

    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String currentUserId = currentUser.getUid();

        mUserReference = mDatabase.getReference().child("Users").child(currentUserId);

        HashMap<String, String> userMap = new HashMap<String, String>();
        userMap.put("user_name", mUserName);
        userMap.put("email", mEmail);
        userMap.put("phone_number", mPhoneNumber);
        userMap.put("account_for", "Teacher");

        mUserReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mUserReference = mDatabase.getReference().child("Users").child("Teachers").child(currentUserId);

                    HashMap<String, String> teacherMap = new HashMap<String, String>();
                    teacherMap.put("user_name", mUserName);

                    mUserReference.setValue(teacherMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
//                          Toast.makeText(SignInActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SignInActivity.this, TeacherActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(SignInActivity.this, "Please Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verifyCode() {
        String code = mCodeEditText.getText().toString();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mPhoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    public void resendCode() {
        String phoneNumber = mPhoneNumberEditText.getText().toString();

        setUpVerificationCallbacks();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mVerificationCallbacks,
                mResendToken);
    }
}
