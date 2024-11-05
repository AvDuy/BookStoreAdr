package com.example.bookstore;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class OTP_Verify extends AppCompatActivity {

    private EditText otpEt1, otpEt2, otpEt3, otpEt4;
    private TextView resendBtn;
    private boolean resendEnable = false;
    private int resendTime = 60;
    private int selectedETPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        otpEt1 = findViewById(R.id.et_otp_1);
        otpEt2 = findViewById(R.id.et_otp_2);
        otpEt3 = findViewById(R.id.et_otp_3);
        otpEt4 = findViewById(R.id.et_otp_4);

        resendBtn = findViewById(R.id.tv_resend);

        final Button btnVerify = findViewById(R.id.btn_verify);

        final TextView otpEmail = findViewById(R.id.otp_email);
        final TextView otpPhone = findViewById(R.id.otp_phone);

        //get email and phone from register through intent
        final String Email = getIntent().getStringExtra("userEmail");
        final String Phone = getIntent().getStringExtra("userPhone");

        //set email and phone to Textview
        otpEmail.setText(Email);
        otpPhone.setText(Phone);

        otpEt1.addTextChangedListener(textWatcher);
        otpEt2.addTextChangedListener(textWatcher);
        otpEt3.addTextChangedListener(textWatcher);
        otpEt4.addTextChangedListener(textWatcher);

        showKeyboard(otpEt1);

        startCountdown();

        resendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resendEnable){
                    startCountdown();
                }
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String generateOtp = otpEt1.getText().toString()
                        +otpEt2.getText().toString()
                        +otpEt3.getText().toString()
                        +otpEt4.getText().toString();

                if (generateOtp.length()==4){

                }
            }
        });

    }

    private void showKeyboard(EditText otpET){
        otpET.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpET,InputMethodManager.SHOW_IMPLICIT);
    }

    private void startCountdown(){

        resendEnable = false;
        resendBtn.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime*1000,1000) {

            @Override
            public void onTick(long l) {
                resendBtn.setText("Resend Code (+" + (l/60)+")");
            }

            @Override
            public void onFinish() {
                resendEnable = true;
                resendBtn.setText("Resend Code");
                resendBtn.setTextColor(getResources().getColor(R.color.pink));
            }
        }.start();
    }
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()>0){
                if(selectedETPosition == 0){
                    selectedETPosition = 1;
                    showKeyboard(otpEt2);
                } else if (selectedETPosition == 1) {
                    selectedETPosition = 2;
                    showKeyboard(otpEt3);
                } else if (selectedETPosition == 2){
                    selectedETPosition = 3;
                    showKeyboard(otpEt4);
                }
            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL){
            if(selectedETPosition == 3){
                selectedETPosition = 2;
                showKeyboard(otpEt3);
            }else if(selectedETPosition == 2){
                selectedETPosition = 1;
                showKeyboard(otpEt2);
            } else if (selectedETPosition == 1){
                selectedETPosition = 0;
                showKeyboard(otpEt1);
            }
            return true;
        }else{
            return super.onKeyUp(keyCode, event);
        }
    }
}