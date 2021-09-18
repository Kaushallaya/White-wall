package com.example.whitewall;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    public Double lat=7.0,lon=80.0;
    Dialog sucDialog,falDialog,addDialog,foodDialog,medDialog,patDialog;
    RecyclerView recyclerView;
    DatabaseReference database;
    MyAdapter myAdapter;
    ArrayList<Alert> list;
    public int hour,minute;
    public String saveTime,saveTitle,saveDescription;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getLatitude();
        getLongitude();

        recyclerView=findViewById(R.id.reView);
        database = FirebaseDatabase.getInstance().getReference("Alert");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("mAlt","mAlert", IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        list = new ArrayList<>();
        myAdapter = new MyAdapter(this,list);
        recyclerView.setAdapter(myAdapter);
        progressDialog=new ProgressDialog(this);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Alert alert = dataSnapshot.getValue(Alert.class);
                    list.add(alert);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkSafe() {
        if(lat>6.0 && lat<8.0 && lon>70.0 && lon<90.0)
        {
            createSucDialog();
            sucDialog.show();
        }else {
            createFailDialog();
            falDialog.show();

            //android warning notification  (Not run my device)
            NotificationCompat.Builder builder = new NotificationCompat.Builder(HomeActivity.this,"mAlert");
            builder.setContentTitle("White Wall Notification");
            builder.setContentText("Your patient is on out of range. Please get quick action about this");
            builder.setSmallIcon(R.drawable.ic_arrow);
            builder.setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(HomeActivity.this);
            managerCompat.notify(1,builder.build());

            Toast.makeText(HomeActivity.this,"Failed to read value.",Toast.LENGTH_SHORT).show();
        }
    }

    private void createSucDialog() {
        sucDialog = new Dialog(this);
        sucDialog.setContentView(R.layout.custom_dialog_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sucDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        sucDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sucDialog.setCancelable(false); //Optional
        sucDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button Okay = sucDialog.findViewById(R.id.btn_okay);
        Button Cancel = sucDialog.findViewById(R.id.btn_cancel);

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(HomeActivity.this, "Okay", Toast.LENGTH_SHORT).show();
                sucDialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(HomeActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                sucDialog.dismiss();
            }
        });

    }

    private void createFailDialog() {
        falDialog = new Dialog(this);
        falDialog.setContentView(R.layout.custom_dialog_layout_2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            falDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        falDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        falDialog.setCancelable(false); //Optional
        falDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        Button Okay = falDialog.findViewById(R.id.btn_okay);
        Button Cancel = falDialog.findViewById(R.id.btn_cancel);

        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(HomeActivity.this, "Okay", Toast.LENGTH_SHORT).show();
                falDialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(HomeActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                falDialog.dismiss();
            }
        });

    }

    private void createAddDialog() {
        addDialog = new Dialog(this);
        addDialog.setContentView(R.layout.custom_dialog_addalert);
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
        EditText title = addDialog.findViewById(R.id.titleEdt);
        EditText description = addDialog.findViewById(R.id.AddDisc);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour=selectedHour;
                        minute=selectedMinute;
                        time.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));
                    }
                };
                int style = AlertDialog.THEME_HOLO_DARK;
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,style,onTimeSetListener,hour,minute,false);

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

                //Find time need to store
                Calendar rightNow = Calendar.getInstance();
                int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
                int currentMinute = rightNow.get(Calendar.MINUTE);

                int saveHour,saveMin;

                if(currentHour>hour){ saveHour=(24-currentHour)+hour; }
                else {saveHour=(hour-currentHour);}

                if(minute<currentMinute){ saveMin = (minute+60)-currentMinute; saveHour = saveHour-1; }
                else{ saveMin = minute-currentMinute; }

                if(saveHour==0){saveTime = ""+saveMin+"mins";}
                else {saveTime = ""+saveHour+"h "+saveMin+"mins";}

                //define title & description for save
                saveTitle = title.getText().toString();
                saveDescription = description.getText().toString();

//                DatabaseReference mDatabase;
//                mDatabase = FirebaseDatabase.getInstance().getReference();
//                mDatabase.child("Alert").child(saveTitle).child("time").setValue(saveTime);
//                mDatabase.child("Alert").child(saveTitle).child("description").setValue(saveDescription);
//                mDatabase.child("Alert").child(saveTitle).child("title").setValue(saveTitle);

                //call data saving methods
                Alert alt = new Alert(saveTitle,saveDescription,saveTime);
                dao.add(alt).addOnSuccessListener(suc ->
                {
                    progressDialog.dismiss();
                    //Toast.makeText(HomeActivity.this, "recode success", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(er ->{
                    progressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                });

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


    @Override
    protected void onResume() {
        super.onResume();
        getLatitude();
        getLongitude();

        checkSafe();

        //Toast.makeText(HomeActivity.this,"Value is: " + lat+lon,Toast.LENGTH_SHORT).show();
    }

    public void goNev(View view) {
        //Toast.makeText(HomeActivity.this,"Value is: " + lat+lon,Toast.LENGTH_SHORT).show();

        Intent in = new Intent(this,MapsActivity.class);
        in.putExtra("lat",lat);
        in.putExtra("lon",lon);
        startActivity(in);

        //need open google map app in phone
//        Uri gmmIntentUri = Uri.parse("geo:"+lat+","+lon);
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        if (mapIntent.resolveActivity(getPackageManager()) != null) {
//            startActivity(mapIntent);
//        }

    }

    private void getLongitude() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("GPSDATA").child("lng");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double value = dataSnapshot.getValue(Double.class);
                lon = value;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(HomeActivity.this,"Failed to read value.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLatitude() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("GPSDATA").child("lat");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Double value = dataSnapshot.getValue(Double.class);
                lat = value;
                //Toast.makeText(HomeActivity.this,"Value is: " + value,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(HomeActivity.this,"Failed to read value.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addAlert(View view) {

        createAddDialog();
        addDialog.show();

    }

    private void createpatDialog() {
        final int[] hour = new int[1];
        final int[] minute = new int[1];
        final String[] saveDate = new String[1];
        final DatePickerDialog[] datePickerDialog = new DatePickerDialog[1];

        patDialog = new Dialog(this);
        patDialog.setContentView(R.layout.custom_dialog_addpatient);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            patDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        }
        patDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        patDialog.setCancelable(false); //Optional
        patDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

        DAOAlert dao = new DAOAlert();

        Button Okay = patDialog.findViewById(R.id.btn_okay);
        Button Cancel = patDialog.findViewById(R.id.btn_cancel);
        Button time = patDialog.findViewById(R.id.time);
        Button date = patDialog.findViewById(R.id.date);
        EditText pname = patDialog.findViewById(R.id.pName);
        EditText age = patDialog.findViewById(R.id.Age);
        EditText dname = patDialog.findViewById(R.id.dName);
        EditText place = patDialog.findViewById(R.id.place);


        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("PatientDetail").child("pName").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    pname.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        mDatabase.child("PatientDetail").child("Age").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    age.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        mDatabase.child("PatientDetail").child("dName").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    dname.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        mDatabase.child("PatientDetail").child("place").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    place.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        mDatabase.child("PatientDetail").child("date").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    date.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        mDatabase.child("PatientDetail").child("time").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    time.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

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
                datePickerDialog[0] = new DatePickerDialog(HomeActivity.this,style,dateSetListener,year,month,day);

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
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

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
                String saveTimeDate =  time.getText().toString();
                String saveDate =  date.getText().toString();

                //Toast.makeText(SignUpActivity.this, "Data:"+savePname+saveTimeDate+saveDate[0], Toast.LENGTH_SHORT).show();

                DatabaseReference mDatabase;
                mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("PatientDetail").child("pName").setValue(savePname);
                mDatabase.child("PatientDetail").child("Age").setValue(saveAge);
                mDatabase.child("PatientDetail").child("dName").setValue(saveDName);
                mDatabase.child("PatientDetail").child("date").setValue(saveDate);
                mDatabase.child("PatientDetail").child("time").setValue(saveTimeDate);
                mDatabase.child("PatientDetail").child("place").setValue(savePlace).addOnSuccessListener(suc ->
                {
                    progressDialog.dismiss();
                }).addOnFailureListener(er ->{
                    progressDialog.dismiss();
                    Toast.makeText(HomeActivity.this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
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
                patDialog.dismiss();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Toast.makeText(HomeActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                patDialog.dismiss();
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

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("FoodDetail").child("Breakfast").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    btime.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        mDatabase.child("FoodDetail").child("Lunch").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    ltime.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        mDatabase.child("FoodDetail").child("Dinner").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    Dtime.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

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
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

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
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

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
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

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
//                String saveBrTime =  hour[0]+"h "+minute[0]+"min";
//                String saveluTime =  hour[1]+"h "+minute[1]+"min";
//                String saveDiTime =  hour[2]+"h "+minute[2]+"min";
                String saveBrTime = btime.getText().toString();
                String saveluTime = ltime.getText().toString();
                String saveDiTime = Dtime.getText().toString();


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
                    Toast.makeText(HomeActivity.this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
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

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("MedicineDetail").child("time1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    btime.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        mDatabase.child("MedicineDetail").child("time2").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    ltime.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        mDatabase.child("MedicineDetail").child("descrption1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    descrip1.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
        mDatabase.child("MedicineDetail").child("descrption2").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    descrip2.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });



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
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

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
                TimePickerDialog timePickerDialog = new TimePickerDialog(HomeActivity.this,style,onTimeSetListener, hour[0], minute[0],false);

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
                String saveFstTime =  btime.getText().toString();
                String saveSecTime =  ltime.getText().toString();

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
                    Toast.makeText(HomeActivity.this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
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
        createpatDialog();
        patDialog.show();
    }

    public void addFood(View view) {
        createFoodDialog();
        foodDialog.show();
    }

    public void addMedicine(View view) {
        createMedDialog();
        medDialog.show();
    }
}