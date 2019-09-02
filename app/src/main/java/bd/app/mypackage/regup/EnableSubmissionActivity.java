package bd.app.mypackage.regup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EnableSubmissionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private EditText registrationlastdate;
    private Spinner sp1;
    private Button activate, pick, extend;

    private int sem_year, sem_month, sem_day;
    private String registrationdate, semester;

    private String[] sem = {"SELECT SEMESTER  - - -", "Fall", "Summer", "Spring"};

    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_submission);

        registrationlastdate = (EditText) findViewById(R.id.date);
        pick = (Button) findViewById(R.id.datepicker);
        activate = (Button) findViewById(R.id.status);
        extend = (Button) findViewById(R.id.extend);
        sp1 = (Spinner) findViewById(R.id.spinner_semester);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        registrationlastdate.setEnabled(false);

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(EnableSubmissionActivity.this, R.anim.blink_anim);
                pick.startAnimation(animation);

                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "date picker");
                datepicker.setCancelable(false);
            }
        });

        final ArrayAdapter<String> ar = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,sem){

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Disable the first item of Spinner
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                if(position == 0)
                {
                    //Set the disabled item text color
                    tv.setTextColor(Color.GRAY);
                }
                else
                {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };

        ar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(ar);


        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0)
                {
                    semester = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                semester = null;
            }
        });


        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Animation animation = AnimationUtils.loadAnimation(EnableSubmissionActivity.this, R.anim.blink_anim);
                activate.startAnimation(animation);

                if(sp1.getSelectedItem() == "Fall")
                {
                    semester = "Fall, " + sem_year + "-" +(sem_year+1);
                }
                else if(sp1.getSelectedItem() == "Summer")
                {
                    semester = "Summer, " + sem_year;
                }
                else if(sp1.getSelectedItem() == "Spring")
                {
                    semester = "Spring, " + sem_year;
                }

                if(TextUtils.isEmpty(registrationdate))
                {
                    if(semester == null)
                    {
                        Toast.makeText(getApplicationContext(), "Pick a date and choose semester.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Pick a date.", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    if(semester == null)
                    {
                        Toast.makeText(getApplicationContext(), "Choose semester.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Calendar c = Calendar.getInstance();
                        final String currentdate = dateFormat.format(c.getTime());

                        final String date = sem_day+ "-" +(sem_month+1)+ "-" +sem_year;

                        Date date1, date2 = new Date();

                        try {

                            date1 = dateFormat.parse(date);
                            date2 = dateFormat.parse(currentdate);

                            if (date1.before(date2) || date1.equals(date2))
                            {
                                Toast.makeText(getApplicationContext(), "Pick a date after today.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                firestore.collection("Admin").document("reg_status")
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if(task.isSuccessful())
                                        {
                                            final DocumentSnapshot documentSnapshot = task.getResult();

                                            String fetched_semester = documentSnapshot.getString("semester");
                                            String fetched_date = documentSnapshot.getString("date");

                                            if(TextUtils.equals(semester, fetched_semester))
                                            {
                                                Date date3, date4 = new Date();

                                                try
                                                {
                                                    date3 = dateFormat.parse(currentdate);
                                                    date4 = dateFormat.parse(fetched_date);

                                                    if(date4.after(date3) || date4.equals(date3))
                                                    {
                                                        Toast.makeText(getApplicationContext(), "Course submission is already open for " +semester+ ".", Toast.LENGTH_LONG).show();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(getApplicationContext(), "Course submission was open for " +semester+ " and is closed now.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                                catch (ParseException e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else
                                            {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                                                builder.setMessage("Open course submission for " +semester+ " till " +registrationdate+ "?")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                                progressDialog.setMessage("Please wait..");
                                                                progressDialog.show();
                                                                progressDialog.setCanceledOnTouchOutside(false);
                                                                progressDialog.setCancelable(false);

                                                                String CSE_DAY = documentSnapshot.getString("CSE DAY");
                                                                String CSE_EVE = documentSnapshot.getString("CSE EVE");

                                                                final Map<String, Object> data = new HashMap<>();
                                                                data.put("semester", semester);
                                                                data.put("date", sem_day+ "-" +(sem_month+1)+ "-" +sem_year);
                                                                data.put("CSE DAY", String.valueOf(Integer.parseInt(CSE_DAY) + 1));
                                                                data.put("CSE EVE", String.valueOf(Integer.parseInt(CSE_EVE) + 1));

                                                                firestore.collection("Admin").document("reg_status")
                                                                        .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful())
                                                                        {
                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(getApplicationContext(), "Submission is open for " +semester+ " till " +registrationdate+ ".", Toast.LENGTH_LONG).show();
                                                                        }
                                                                        else
                                                                        {
                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        })

                                                        .setNegativeButton("No", null)
                                                        .show();

                                                Dialog dialog = builder.create();
                                                dialog.setCanceledOnTouchOutside(false);
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                            semester = null;
                        }
                    }
                }
            }
        });


        extend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Animation animation = AnimationUtils.loadAnimation(EnableSubmissionActivity.this, R.anim.blink_anim);
                extend.startAnimation(animation);

                if(TextUtils.isEmpty(registrationdate))
                {
                    Toast.makeText(getApplicationContext(), "Pick a date!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    final Map<String, Object> data = new HashMap<>();
                    data.put("date" ,sem_day+ "-" +(sem_month+1)+ "-" +sem_year);
                    final String date = sem_day+ "-" +(sem_month+1)+ "-" +sem_year;

                    firestore.collection("Admin").document("reg_status")
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful())
                            {
                                DocumentSnapshot documentSnapshot = task.getResult();

                                final String fetched_date = documentSnapshot.getString("date");
                                semester = documentSnapshot.getString("semester");

                                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                Calendar c = Calendar.getInstance();
                                final String currentdate = dateFormat.format(c.getTime());

                                Date date1, date2, date3 = new Date();

                                try {

                                    date1 = dateFormat.parse(date);
                                    date2 = dateFormat.parse(fetched_date);
                                    date3 = dateFormat.parse(currentdate);

                                    if (date1.before(date3) || date1.equals(date3))
                                    {
                                        Toast.makeText(getApplicationContext(), "Pick a date after today.", Toast.LENGTH_LONG).show();
                                        semester = null;
                                    }
                                    else
                                    {
                                        if (date1.before(date2) || date1.equals(date2))
                                        {
                                            Toast.makeText(getApplicationContext(), "Pick a date after " +fetched_date+ ".", Toast.LENGTH_LONG).show();
                                            semester = null;
                                        }
                                        else if (date1.after(date2))
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                                            builder.setMessage("Extend course submission date from " +fetched_date+ " to " +date+ " for " +semester+ "?")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            progressDialog.setMessage("Please wait..");
                                                            progressDialog.show();
                                                            progressDialog.setCanceledOnTouchOutside(false);
                                                            progressDialog.setCancelable(false);

                                                            firestore.collection("Admin").document("reg_status").update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful())
                                                                    {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(getApplicationContext(), "Course submission date is extended to " +date+ " for " +semester+ ".", Toast.LENGTH_LONG).show();
                                                                        semester = null;
                                                                    }
                                                                    else
                                                                    {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                        semester = null;
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    })

                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            semester = null;
                                                        }
                                                    })
                                                    .show();

                                            Dialog dialog = builder.create();
                                            dialog.setCanceledOnTouchOutside(false);
                                        }
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    semester = null;
                                }
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int date) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, date);
        sem_year = year;
        sem_month = month;
        sem_day = date;

        registrationdate = DateFormat.getDateInstance().format(c.getTime());
        registrationlastdate.setText(registrationdate);
    }

    @Override
    public void onBackPressed() {

        this.finish();
    }
}
