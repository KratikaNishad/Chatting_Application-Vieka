package com.example.veika;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;
import android.widget.Toast;

import com.example.veika.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity {

    private ActivityPhoneNumberBinding binding;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        mAuth = FirebaseAuth.getInstance();

        // for jump main screen if user already verified
        /*if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(PhoneNumberActivity.this, MainScreenActivity.class);
            startActivity(intent);
            finish();
        }*/

        binding.getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.phonenumber.getText().toString().trim().isEmpty()){
                    Toast.makeText(PhoneNumberActivity.this,"Invalid Phone Number",Toast.LENGTH_SHORT).show();
                } else if (binding.phonenumber.getText().toString().trim().length() != 10) {
                    Toast.makeText(PhoneNumberActivity.this,"Type Valid Phone Number",Toast.LENGTH_SHORT).show();

                }else {
                    otpSend();
                }

            }
        });
    }

    private void otpSend() {


        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait....");
        dialog.setCancelable(false);

        binding.pb1.setVisibility(View.VISIBLE);
        binding.getotp.setVisibility(View.INVISIBLE);
        dialog.show();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                binding.pb1.setVisibility(View.GONE);
                binding.getotp.setVisibility(View.VISIBLE);
                Toast.makeText(PhoneNumberActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

            }


            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                binding.pb1.setVisibility(View.GONE);
                binding.getotp.setVisibility(View.VISIBLE);
                Intent intent = new Intent(PhoneNumberActivity.this, OtpActivity.class);
                intent.putExtra("phone" , binding.phonenumber.getText().toString().trim());
                intent.putExtra("verificationId",verificationId);
                startActivity(intent);
                dialog.dismiss();

            }
        };

        //OTP Generation....

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + binding.phonenumber.getText().toString().trim())  // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}