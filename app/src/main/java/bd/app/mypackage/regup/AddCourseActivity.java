package bd.app.mypackage.regup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddCourseActivity extends AppCompatActivity {

    EditText code, name;
    Spinner sp_credit, sp_dept, sp_semester, sp_major;
    Button add;

    int major_flag = 0, i = 0, j = 0;

    String ccode, ccname, ccredit, deptname, cre_pos, dept_pos, sem_pos, maj_pos;

    private String[] semester = {"SELECT SEMESTER  - - -", "1st semester", "2nd semester", "3rd semester", "4th semester",
            "5th semester", "6th semester", "7th semester", "8th semester", "9th semester", "10th semester", "11th semester", "12th semester"};

    private String[] major = {"SELECT MAJOR  - - -", "Artificial Intelligence", "Software", "Networking"};

    private String[] dept = {"SELECT DEPARTMENT  - - -", "EEE", "CSE", "TEXTILE", "LAW", "ENGLISH", "ECONOMICS", "BBA"};

    private String[] credit = {"SELECT CREDIT  - - -", "0.75", "1.00", "1.50", "2.00", "3.00", "4.00", "5.00"};

    ArrayList<String> allcodelist = new ArrayList<>();
    ArrayList<String> corres_sem_list = new ArrayList<>();

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        code = (EditText) findViewById(R.id.code);
        name = (EditText) findViewById(R.id.name);
        sp_credit = (Spinner) findViewById(R.id.sp_credit);
        sp_dept = (Spinner) findViewById(R.id.sp_dept);
        sp_semester = (Spinner) findViewById(R.id.sp_semester);
        sp_major = (Spinner) findViewById(R.id.sp_major);
        add = (Button) findViewById(R.id.add);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        sp_dept.setEnabled(false);
        sp_semester.setEnabled(false);
        sp_major.setEnabled(false);
        add.setEnabled(false);

        ArrayAdapter<String> ar = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, credit){

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
        sp_credit.setAdapter(ar);


        sp_credit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                cre_pos = String.valueOf(position);

                if(position > 0)
                {
                    sp_dept.setEnabled(true);
                }
                else
                {
                    sp_dept.setEnabled(false);
                    sp_semester.setEnabled(false);
                    sp_major.setEnabled(false);
                    add.setEnabled(false);
                    major_flag = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> ar1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dept){

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

        ar1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_dept.setAdapter(ar1);


        sp_dept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                dept_pos = String.valueOf(position);

                if(position > 0 && TextUtils.equals(sp_dept.getItemAtPosition(position).toString(), "CSE"))
                {
                    sp_semester.setEnabled(true);

                    if(Integer.parseInt(sem_pos) > 0 && Integer.parseInt(sem_pos) <= 10)
                    {
                        sp_major.setEnabled(false);
                        add.setEnabled(true);
                        major_flag = 0;
                    }
                    else if(Integer.parseInt(sem_pos) > 10)
                    {
                        sp_major.setEnabled(true);

                        if(Integer.parseInt(maj_pos) > 0)
                        {
                            add.setEnabled(true);
                            major_flag = 1;
                        }
                        else
                        {
                            add.setEnabled(false);
                            major_flag = 0;
                        }
                    }
                }
                else
                {
                    sp_semester.setEnabled(false);
                    sp_major.setEnabled(false);
                    add.setEnabled(false);
                    major_flag = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> ar2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, semester){

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

        ar2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_semester.setAdapter(ar2);


        sp_semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                sem_pos = String.valueOf(position);

                if(position > 0 && position <=10)
                {
                    add.setEnabled(true);
                    sp_major.setEnabled(false);
                    major_flag = 0;
                }
                else if(position > 10)
                {
                    sp_major.setEnabled(true);
                    add.setEnabled(false);

                    if(Integer.parseInt(maj_pos) > 0)
                    {
                        add.setEnabled(true);
                        major_flag = 1;
                    }
                    else
                    {
                        add.setEnabled(false);
                        major_flag = 0;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> ar3 = new ArrayAdapter<String>(AddCourseActivity.this, android.R.layout.simple_spinner_item, major){

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

        ar3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_major.setAdapter(ar3);


        sp_major.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                maj_pos = String.valueOf(position);

                if(position > 0)
                {
                    major_flag = 1;
                    add.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //get all course code with corresponding name and credit
        for(i=1;i<=12;i++)
        {
            //1 to 10th semester
            if(i<=10)
            {
                final String num = String.valueOf(i);

                firestore.collection("CourseList").document("department")
                        .collection("CSE").document("semester")
                        .collection(num).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot document : task.getResult()) {

                                final String s = document.getId();
                                allcodelist.add(s);
                                corres_sem_list.add(num);
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            //11th to 12th semester (major)
            else if(i>10)
            {
                final String num1 = String.valueOf(i);

                for(j=1;j<=3;j++)
                {
                    final String num2 = String.valueOf(j);

                    firestore.collection("CourseList").document("department")
                            .collection("CSE").document("semester")
                            .collection(num1).document("major")
                            .collection(num2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful())
                            {
                                for (DocumentSnapshot document : task.getResult()) {

                                    final String s = document.getId();
                                    allcodelist.add(s);
                                    corres_sem_list.add(num1);
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Animation animation = AnimationUtils.loadAnimation(AddCourseActivity.this, R.anim.blink_anim);
                add.startAnimation(animation);

                ccode = code.getText().toString().toUpperCase();
                ccname = name.getText().toString();
                ccredit = sp_credit.getSelectedItem().toString();
                deptname = sp_dept.getSelectedItem().toString();

                if(major_flag == 0)
                {
                    if (TextUtils.isEmpty(ccode) || (TextUtils.isEmpty(ccname))
                            || TextUtils.equals(cre_pos, "0") || TextUtils.equals(dept_pos, "0")
                            || TextUtils.equals(sem_pos, "0"))
                    {
                        Toast.makeText(getApplicationContext(), "Please fill up missing information.", Toast.LENGTH_SHORT).show();
                    }

                    else
                    {
                        new CountDownTimer(1500, 1000) {
                            public void onFinish() {

                                //semester is between 1 to 10

                                if(allcodelist.contains(ccode))
                                {
                                    int p = allcodelist.indexOf(ccode);
                                    int n = Integer.parseInt(corres_sem_list.get(p));
                                    String ss;

                                    if(n == 1)
                                    {
                                        ss = "1st";
                                    }
                                    else if(n == 2)
                                    {
                                        ss = "2nd";
                                    }
                                    else if(n == 3)
                                    {
                                        ss = "3rd";
                                    }
                                    else
                                    {
                                        ss = "" +n+ "th";
                                    }

                                    Toast.makeText(getApplicationContext(), "" +ccode+ " is already in " +ss+ " semester.", Toast.LENGTH_LONG).show();
                                }

                                else
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                                    builder.setMessage("Add '" +ccname+ "' in semester " +sem_pos+ " for " +deptname+ " department.")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    progressDialog.setMessage("Please wait..");
                                                    progressDialog.show();
                                                    progressDialog.setCanceledOnTouchOutside(false);
                                                    progressDialog.setCancelable(false);

                                                    final Map<String, Object> data = new HashMap<>();
                                                    data.put("name", ccname);
                                                    data.put("credit", ccredit);

                                                    firestore.collection("CourseList").document("department")
                                                            .collection(deptname).document("semester")
                                                            .collection(sem_pos).document(ccode)
                                                            .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful())
                                                            {
                                                                allcodelist.add(ccode);
                                                                corres_sem_list.add(sem_pos);

                                                                progressDialog.dismiss();
                                                                Toast.makeText(getApplicationContext(), "Course added successfully!", Toast.LENGTH_LONG).show();
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
                            public void onTick(long millisUntilFinished) {
                                // millisUntilFinished    The amount of time until finished.
                            }
                        }.start();
                    }
                }

                else if(major_flag == 1)
                {
                    if (TextUtils.isEmpty(ccode) || (TextUtils.isEmpty(ccname))
                            || TextUtils.equals(cre_pos, "0") || TextUtils.equals(dept_pos, "0")
                            || TextUtils.equals(sem_pos, "0") || TextUtils.equals(maj_pos, "0"))
                    {
                        Toast.makeText(getApplicationContext(), "Please fill up missing information.", Toast.LENGTH_SHORT).show();
                    }

                    else
                    {
                        new CountDownTimer(1500, 1000) {
                            public void onFinish() {

                                //semester is between 11 to 12

                                if(allcodelist.contains(ccode))
                                {
                                    int p = allcodelist.indexOf(ccode);
                                    int n = Integer.parseInt(corres_sem_list.get(p));
                                    String ss;

                                    if(n == 1)
                                    {
                                        ss = "1st";
                                    }
                                    else if(n == 2)
                                    {
                                        ss = "2nd";
                                    }
                                    else if(n == 3)
                                    {
                                        ss = "3rd";
                                    }
                                    else
                                    {
                                        ss = "" +n+ "th";
                                    }

                                    Toast.makeText(getApplicationContext(), "" +ccode+ " is already in " +ss+ " semester.", Toast.LENGTH_LONG).show();
                                }

                                else
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                                    builder.setMessage("Add '" +ccname+ "' for " +major[Integer.parseInt(maj_pos)]+ " in semester " +sem_pos+ " for " +deptname+ " department.")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                    progressDialog.setMessage("Please wait..");
                                                    progressDialog.show();
                                                    progressDialog.setCanceledOnTouchOutside(false);
                                                    progressDialog.setCancelable(false);

                                                    final Map<String, Object> data = new HashMap<>();
                                                    data.put("name", ccname);
                                                    data.put("credit", ccredit);

                                                    firestore.collection("CourseList").document("department")
                                                            .collection(deptname).document("semester")
                                                            .collection(sem_pos).document("major")
                                                            .collection(maj_pos).document(ccode)
                                                            .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful())
                                                            {
                                                                allcodelist.add(ccode);
                                                                corres_sem_list.add(sem_pos);

                                                                progressDialog.dismiss();
                                                                Toast.makeText(getApplicationContext(), "Course added successfully!", Toast.LENGTH_LONG).show();
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
                            public void onTick(long millisUntilFinished) {
                                // millisUntilFinished    The amount of time until finished.
                            }
                        }.start();
                    }
                }

            }
        });
    }


    @Override
    public void onBackPressed() {

        allcodelist.clear();
        corres_sem_list.clear();
        this.finish();
    }
}
