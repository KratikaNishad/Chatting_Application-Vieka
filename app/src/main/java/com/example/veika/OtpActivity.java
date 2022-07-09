package com.example.veika;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.veika.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity {

    private ActivityOtpBinding binding;
    private String verificationId;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Otp verifying....");
        dialog.setCancelable(false);

        editTextInput();

        binding.phoneview.setText(String.format(
                "+91-%s", getIntent().getStringExtra("phone")
        ));

        verificationId = getIntent().getStringExtra("verificationId");

        binding.resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText( OtpActivity.this, "OTP Send Successfully.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.pb2.setVisibility(View.VISIBLE);
                binding.verify.setVisibility(View.INVISIBLE);

                if (binding.otp1.getText().toString().trim().isEmpty() ||
                        binding.otp2.getText().toString().trim().isEmpty() ||
                        binding.otp3.getText().toString().trim().isEmpty() ||
                        binding.otp4.getText().toString().trim().isEmpty() ||
                        binding.otp5.getText().toString().trim().isEmpty() ||
                        binding.otp6.getText().toString().trim().isEmpty()){
                    Toast.makeText( OtpActivity.this, "Enter Valid OTP", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.show();
                    if (verificationId != null){
                        String code = binding.otp1.getText().toString().trim() +
                                binding.otp2.getText().toString().trim() +
                                binding.otp3.getText().toString().trim() +
                                binding.otp4.getText().toString().trim() +
                                binding.otp5.getText().toString().trim() +
                                binding.otp6.getText().toString().trim();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        FirebaseAuth.getInstance()
                                .signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            binding.pb2.setVisibility(View.VISIBLE);
                                            binding.verify.setVisibility(View.INVISIBLE);
                                            Intent intent = new Intent(OtpActivity.this , UserProfile.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            dialog.dismiss();

                                        }else {
                                            dialog.dismiss();
                                            binding.pb2.setVisibility(View.GONE);
                                            binding.verify.setVisibility(View.VISIBLE);
                                            Toast.makeText(OtpActivity.this, "OTP is not valid", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });
                    }
                }

            }
        });


    }

    private void editTextInput() {

        binding.otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.otp2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.otp3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.otp4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.otp5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.otp6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}