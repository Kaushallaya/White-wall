package com.example.whitewall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    EditText inputName,inputEmail,inputPassword,inputConfigPassword;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+";
    ProgressDialog progressDialog;
    Dialog foodDialog,medDialog,addDialog;
    int tem=0;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inputName=findViewById(R.id.name);
        inputEmail=findViewById(R.id.email);
        inputPassword=findViewById(R.id.password);
        inputConfigPassword=findViewById(R.id.password2);
        progressDialog=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
    }

    public void signUp(View view) {

        String msg="";
        if(tem==0){Toast.makeText(SignUpActivity.this, "Please Fill the Patient Detail", Toast.LENGTH_SHORT).show();}
        else if(tem==1){Toast.makeText(SignUpActivity.this, "Please Fill the Foods Detail", Toast.LENGTH_SHORT).show();}
        else if(tem==2){Toast.makeText(SignUpActivity.this, "Please Fill the Medicine Detail", Toast.LENGTH_SHORT).show();}
        else { performAuth(); }

//        Intent in = new Intent(this,SignInActivity.class);0
//        startActivity(in);
    }

    private void performAuth() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String configPassword = inputConfigPassword.getText().toString();
        String name = inputName.getText().toString();

        if (!email.matches(emailPattern)) {
            inputEmail.setError("Enter valid email");
        }else if(password.isEmpty() || password.length()<6){
            inputPassword.setError("Enter valid password");
        }else if(!password.equals(configPassword)){
            inputConfigPassword.setError("Password not match");
        }else{
            progressDialog.setMessage("Please wait for Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this,"Registration success",Toast.LENGTH_SHORT).show();

                        //pass next activity
                        Intent in = new Intent(SignUpActivity.this,SignInActivity.class);
                        startActivity(in);
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    private void createAddDialog() {
        final int[] hour = new int[1];
        final int[] minute = new int[1];
        final String[] saveDate = new String[1];
        final DatePickerDialog[] datePickerDialog = new DatePickerDialog[1];

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

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month +1;
                        String stdate = makeDateString(day,month,year);
                        date.setText(stdate);
                        saveDate[0] = stdate;
                    }
                };

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DATE);

                int style = AlertDialog.THEME_HOLO_DARK;
                datePickerDialog[0] = new DatePickerDialog(SignUpActivity.this,style,dateSetListener,year,month,day);

                datePickerDialog[0].show();
            }

            private String makeDateString(int day, int month, int year) {
                return getMonthFormat(month)+" "+day+" "+year;
            }

            private String getMonthFormat(int month) {
                if(month == 1){return "JAN";}
                if(month == 2){return "FEB";}
                if(month == 3){return "MAR";}
                if(month == 4){return "APR";}
                if(month == 5){return "MAY";}
                if(month == 6){return "JUN";}
                if(month == 7){return "JUL";}
                if(month == 8){return "AUG";}
                if(month == 9){return "SEP";}
                if(month == 10){return "OCT";}
                if(month == 11){return "NOV";}
                if(month == 12){return "DEC";}

                return "JAN";
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

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

                //Toast.makeText(SignUpActivity.this, "Data:"+savePname+saveTimeDate+saveDate[0], Toast.LENGTH_SHORT).show();

                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("PatientDetail").child("pName").setValue(savePname);
                mDatabase.child("PatientDetail").child("Age").setValue(saveAge);
                mDatabase.child("PatientDetail").child("dName").setValue(saveDName);
                mDatabase.child("PatientDetail").child("date").setValue(saveDate[0]);
                mDatabase.child("PatientDetail").child("time").setValue(saveTimeDate);
                mDatabase.child("PatientDetail").child("place").setValue(savePlace).addOnSuccessListener(suc ->
                {
                    progressDialog.dismiss();
                }).addOnFailureListener(er ->{
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                });


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

    private void createFoodDialog() {
        final int[] hour = new int[3];
        final int[] minute = new int[3];
        final String[] saveDate = new String[1];
        final DatePickerDialog[] datePickerDialog = new DatePickerDialog[1];

        foodDialog = new Dialog(this);
        foodDialog.setContentView(R.layout.custom_dialog_addfood);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            foodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        foodDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        foodDialog.setCancelable(false); //Optional
        foodDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        DAOAlert dao = new DAOAlert();

        Button Okay = foodDialog.findViewById(R.id.btn_okay);
        Button Cancel = foodDialog.findViewById(R.id.btn_cancel);
        Button btime = foodDialog.findViewById(R.id.Btime);
        Button ltime = foodDialog.findViewById(R.id.Ltime);
        Button Dtime = foodDialog.findViewById(R.id.Dtime);


        btime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour[0] =selectedHour;
                        minute[0] =selectedMinute;
                        btime.setText(String.format(Locale.getDefault(),"%02d:%02d", hour[0], minute[0]));
                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

                timePickerDialog.setTitle("select Time");
                timePickerDialog.show();
            }
        });
        ltime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour[1] =selectedHour;
                        minute[1] =selectedMinute;
                        ltime.setText(String.format(Locale.getDefault(),"%02d:%02d", hour[1], minute[1]));
                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

                timePickerDialog.setTitle("select Time");
                timePickerDialog.show();
            }
        });
        Dtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour[2] =selectedHour;
                        minute[2] =selectedMinute;
                        Dtime.setText(String.format(Locale.getDefault(),"%02d:%02d", hour[2], minute[2]));
                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

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
                String saveBrTime =  hour[0]+"h "+minute[0]+"min";
                String saveluTime =  hour[1]+"h "+minute[1]+"min";
                String saveDiTime =  hour[2]+"h "+minute[2]+"min";

                //Toast.makeText(SignUpActivity.this, "Data:"+savePname+saveTimeDate+saveDate[0], Toast.LENGTH_SHORT).show();

                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("FoodDetail").child("Lunch").setValue(saveluTime);
                mDatabase.child("FoodDetail").child("Dinner").setValue(saveDiTime);
                mDatabase.child("FoodDetail").child("Breakfast").setValue(saveBrTime).addOnSuccessListener(suc ->
                {
                    progressDialog.dismiss();
                }).addOnFailureListener(er ->{
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                });


                //Toast.makeText(HomeActivity.this, "Time"+saveTime, Toast.LENGTH_SHORT).show();
                foodDialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Toast.makeText(HomeActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                foodDialog.dismiss();
            }
        });

    }

    private void createMedDialog() {
        final int[] hour = new int[3];
        final int[] minute = new int[3];
        final String[] saveDate = new String[1];
        final DatePickerDialog[] datePickerDialog = new DatePickerDialog[1];

        medDialog = new Dialog(this);
        medDialog.setContentView(R.layout.custom_dialog_addmedi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            medDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        medDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        medDialog.setCancelable(false); //Optional
        medDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        DAOAlert dao = new DAOAlert();

        Button Okay = medDialog.findViewById(R.id.btn_okay);
        Button Cancel = medDialog.findViewById(R.id.btn_cancel);
        Button btime = medDialog.findViewById(R.id.Btime);
        Button ltime = medDialog.findViewById(R.id.Ltime);
        EditText descrip1 = medDialog.findViewById(R.id.Des1);
        EditText descrip2 = medDialog.findViewById(R.id.Des2);


        btime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour[0] =selectedHour;
                        minute[0] =selectedMinute;
                        btime.setText(String.format(Locale.getDefault(),"%02d:%02d", hour[0], minute[0]));
                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

                timePickerDialog.setTitle("select Time");
                timePickerDialog.show();
            }
        });
        ltime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour[1] =selectedHour;
                        minute[1] =selectedMinute;
                        ltime.setText(String.format(Locale.getDefault(),"%02d:%02d", hour[1], minute[1]));
                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

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
                String descrption1 = descrip1.getText().toString();
                String descrption2 = descrip2.getText().toString();
                String saveFstTime =  hour[0]+"h "+minute[0]+"min";
                String saveSecTime =  hour[1]+"h "+minute[1]+"min";

                //Toast.makeText(SignUpActivity.this, "Data:"+savePname+saveTimeDate+saveDate[0], Toast.LENGTH_SHORT).show();

                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("MedicineDetail").child("time1").setValue(saveFstTime);
                mDatabase.child("MedicineDetail").child("descrption1").setValue(descrption1);
                mDatabase.child("MedicineDetail").child("descrption2").setValue(descrption2);
                mDatabase.child("MedicineDetail").child("time2").setValue(saveSecTime).addOnSuccessListener(suc ->
                {
                    progressDialog.dismiss();
                }).addOnFailureListener(er ->{
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                });

                //Toast.makeText(HomeActivity.this, "Time"+saveTime, Toast.LENGTH_SHORT).show();
                medDialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Toast.makeText(HomeActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                medDialog.dismiss();
            }
        });

    }

    public void addPation(View view) {
        tem=1;
        createAddDialog();
        addDialog.show();
    }

    public void addFood(View view) {
        tem=2;
        createFoodDialog();
        foodDialog.show();
    }

    public void addMedicine(View view) {
        tem=3;
        createMedDialog();
        medDialog.show();
    }
}