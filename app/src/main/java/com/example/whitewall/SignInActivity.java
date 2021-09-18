package com.example.whitewall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Locale;

public class SignInActivity extends AppCompatActivity {

    EditText inputEmail,inputPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+";
    ProgressDialog progressDialog;
    Dialog foodDialog,medDialog,addDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        inputEmail=findViewById(R.id.email);
        inputPassword=findViewById(R.id.password);
        progressDialog=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
    }

    public void goHome(View view) {
//        Intent in = new Intent(this,HomeActivity.class);
//        startActivity(in);

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();


        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter valid email");
        }else if(password.isEmpty() || password.length()<6){
            inputPassword.setError("Enter valid password");
        }else {
            progressDialog.setMessage("Please wait for Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        //Toast.makeText(SignInActivity.this,"Login success",Toast.LENGTH_SHORT).show();

                        //pass next activity
                        Intent in = new Intent(SignInActivity.this,HomeActivity.class);
                        startActivity(in);
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void createAddDialog() {
        final int[] hour = new int[1];
        final int[] minute = new int[1];

        addDialog = new Dialog(this);
        addDialog.setContentView(R.layout.custom_dialog_addpatient);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        addDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addDialog.setCancelable(false); //Optional
        addDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        DAOAlert dao = new DAOAlert();

        Button Okay = addDialog.findViewById(R.id.btn_okay);
        Button Cancel = addDialog.findViewById(R.id.btn_cancel);
        Button time = addDialog.findViewById(R.id.time);
        Button date = addDialog.findViewById(R.id.date);
        EditText pname = addDialog.findViewById(R.id.pName);
        EditText age = addDialog.findViewById(R.id.Age);
        EditText dname = addDialog.findViewById(R.id.dName);
        EditText place = addDialog.findViewById(R.id.place);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour[0] =selectedHour;
                        minute[0] =selectedMinute;
                        time.setText(String.format(Locale.getDefault(),"%02d:%02d", hour[0], minute[0]));
                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignInActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

                timePickerDialog.setTitle("select Time");
                timePickerDialog.show();
            }
        });

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create process dialog
                progressDialog.setMessage("Please wait for Saving ...");
                progressDialog.setTitle("Saving");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                //define title & description for save
                String savePname = pname.getText().toString();
                String saveAge = age.getText().toString();
                String saveDName = dname.getText().toString();
                String savePlace = place.getText().toString();
                String saveTimeDate =  hour[0]+"h "+minute[0]+"min";

//                DatabaseReference mDatabase;
//                mDatabase = FirebaseDatabase.getInstance().getReference();
//                mDatabase.child("Alert").child(saveTitle).child("time").setValue(saveTime);
//                mDatabase.child("Alert").child(saveTitle).child("description").setValue(saveDescription);
//                mDatabase.child("Alert").child(saveTitle).child("title").setValue(saveTitle);

                //call data saving methods
//                Alert alt = new Alert(saveTitle,saveDescription,saveTime);
//                dao.add(alt).addOnSuccessListener(suc ->
//                {
//                    progressDialog.dismiss();
//                    //Toast.makeText(HomeActivity.this, "recode success", Toast.LENGTH_SHORT).show();
//                }).addOnFailureListener(er ->{
//                    progressDialog.dismiss();
//                    Toast.makeText(SignInActivity.this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
//                });

                //Toast.makeText(HomeActivity.this, "Time"+saveTime, Toast.LENGTH_SHORT).show();
                addDialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Toast.makeText(HomeActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                addDialog.dismiss();
            }
        });

    }

    public void signUp(View view) {
        Intent in = new Intent(this,SignUpActivity.class);
        startActivity(in);
    }
}