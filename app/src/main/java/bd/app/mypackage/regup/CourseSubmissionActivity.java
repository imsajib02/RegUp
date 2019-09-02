package bd.app.mypackage.regup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseSubmissionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String id, dept, intake, sec, date, mytext = "Submit";

    private Spinner sp1, sp2;
    private AutoCompleteTextView autoComplete;
    private Button reguler, major, irreguler, submit;
    public static TextView totalcourse, totalcredit;
    public static ListView listView;

    private int a = 0, i = 0, j = 0, update_flag = 0;
    double total_credit = 0.0;

    private String[] reg = {"SELECT REGULER COURSE  - - -", "1st semester", "2nd semester", "3rd semester", "4th semester",
            "5th semester", "6th semester", "7th semester", "8th semester", "9th semester", "10th semester", "11th semester", "12th semester"};

    private String[] maj = {"SELECT MAJOR  - - -", "Artificial Intelligence", "Software", "Networking"};

    ArrayList<CourseItem> adapterlist = new ArrayList<>();
    public static ArrayList<String> finallist = new ArrayList<>();

    public static ArrayList<String> codelist = new ArrayList<>();
    public static ArrayList<String> namelist = new ArrayList<>();
    public static ArrayList<String> creditlist = new ArrayList<>();
    public static ArrayList<String> typelist = new ArrayList<>();

    public static ArrayList<String> allcodelist = new ArrayList<>();
    public static ArrayList<String> allnamelist = new ArrayList<>();
    public static ArrayList<String> allcreditlist = new ArrayList<>();

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_submission);

        sp1 = (Spinner) findViewById(R.id.spinner_reguler);
        sp2 = (Spinner) findViewById(R.id.spinner_major);
        autoComplete = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        reguler = (Button) findViewById(R.id.reguler);
        major = (Button) findViewById(R.id.major);
        irreguler = (Button) findViewById(R.id.irreguler);
        totalcourse = (TextView) findViewById(R.id.totalcourse);
        totalcredit = (TextView) findViewById(R.id.totalcredit);
        submit = (Button) findViewById(R.id.submit);
        listView = (ListView) findViewById(R.id.listview);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        adapterlist.clear();
        codelist.clear();
        namelist.clear();
        creditlist.clear();
        typelist.clear();

        sp2.setEnabled(false);
        major.setEnabled(false);
        autoComplete.setEnabled(false);

        date = HomeActivity.date;

        //getting user id
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String userid = user.getUid();

        //getting our user information from database
        firestore.collection("User").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    id = documentSnapshot.getString("id");
                    dept = documentSnapshot.getString("dept");
                    intake = documentSnapshot.getString("intake");
                    sec = documentSnapshot.getString("section");

                    try {

                        List<String> arr1 = (List<String>) documentSnapshot.get("course codes");
                        codelist.addAll(arr1);

                        List<String> arr2 = (List<String>) documentSnapshot.get("course names");
                        namelist.addAll(arr2);

                        List<String> arr3 = (List<String>) documentSnapshot.get("course credits");
                        creditlist.addAll(arr3);

                        List<String> arr4 = (List<String>) documentSnapshot.get("course types");
                        typelist.addAll(arr4);

                        update_flag = 1;
                        submit.setText("UPDATE");
                        mytext = "Update";

                    } catch (Exception e)
                    {
                        update_flag = 0;
                        mytext = "Submit";
                        e.printStackTrace();
                    }

                    total_credit = 0.0;
                    adapterlist.clear();

                    for(int i=0;i<codelist.size();i++)
                    {
                        total_credit = total_credit + Double.parseDouble(creditlist.get(i));
                        adapterlist.add(new CourseItem(codelist.get(i), namelist.get(i), creditlist.get(i), typelist.get(i)));
                    }

                    StudentAdapter studentAdapter = new StudentAdapter(getApplicationContext(),R.layout.course_submission_list_view_layout,adapterlist);
                    listView.setAdapter(studentAdapter);

                    totalcourse.setText("Total Course: " +codelist.size());
                    totalcredit.setText("Total Credit: " +total_credit);
                }

                else
                {
                    Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });


        final ArrayAdapter<String> ar = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,reg){

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

                if(position > 10)
                {
                    sp2.setEnabled(true);
                    major.setEnabled(true);
                    reguler.setEnabled(false);

                    final ArrayAdapter<String> ar = new ArrayAdapter<String>(CourseSubmissionActivity.this, android.R.layout.simple_spinner_item,maj){

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
                    sp2.setAdapter(ar);
                }
                else if(position <= 10)
                {
                    sp2.setEnabled(false);
                    major.setEnabled(false);
                    reguler.setEnabled(true );
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

                                firestore.collection("CourseList").document("department")
                                        .collection("CSE").document("semester")
                                        .collection(num).document(s)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful())
                                        {
                                            DocumentSnapshot documentSnapshot = task.getResult();

                                            //code is already in the arraylist
                                            if(allcodelist.indexOf(s) != -1)
                                            {
                                                //do nothing
                                            }
                                            //code is not in the arraylist
                                            else
                                            {
                                                allcodelist.add(s);
                                                allnamelist.add(documentSnapshot.getString("name"));
                                                allcreditlist.add(documentSnapshot.getString("credit"));
                                            }
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            //handle table view
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

                                    firestore.collection("CourseList").document("department")
                                            .collection("CSE").document("semester")
                                            .collection(num1).document("major")
                                            .collection(num2).document(s)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful())
                                            {
                                                DocumentSnapshot documentSnapshot = task.getResult();

                                                //code is already in the arraylist
                                                if(allcodelist.indexOf(s) != -1)
                                                {
                                                    //do nothing
                                                }
                                                //code is not in the arraylist
                                                else
                                                {
                                                    allcodelist.add(s);
                                                    allnamelist.add(documentSnapshot.getString("name"));
                                                    allcreditlist.add(documentSnapshot.getString("credit"));
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
                            else
                            {
                                Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }

        //auto complete view setup for irregular courses
        //using 1.5 second delay
        //because it takes some time to fetch data from the server
        new CountDownTimer(1500, 1000) {
            public void onFinish() {

                autoComplete.setEnabled(true);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, allcodelist);
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                autoComplete.setAdapter(adapter);
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();



        reguler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(CourseSubmissionActivity.this, R.anim.blink_anim);
                reguler.startAnimation(animation);

                final String position = String.valueOf(sp1.getSelectedItemPosition());

                firestore.collection("CourseList").document("department")
                        .collection("CSE").document("semester")
                        .collection(position).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot document : task.getResult()) {

                                final String s = document.getId();

                                firestore.collection("CourseList").document("department")
                                        .collection("CSE").document("semester")
                                        .collection(position).document(s)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful())
                                        {
                                            DocumentSnapshot documentSnapshot = task.getResult();

                                            //code is already in the arraylist
                                            if(codelist.indexOf(s) != -1)
                                            {
                                                //do nothing
                                            }
                                            //code is not in the arraylist
                                            else
                                            {
                                                codelist.add(s);
                                                namelist.add(documentSnapshot.getString("name"));
                                                creditlist.add(documentSnapshot.getString("credit"));
                                                typelist.add("Reguler");
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
                        else
                        {
                            Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //handle listview
                //using 1.7 second delay
                //because it takes some time to fetch data from the server
                new CountDownTimer(1700, 1000) {
                    public void onFinish() {

                        total_credit = 0.0;
                        adapterlist.clear();
                        int exceedflag = 0;
                        int removal_count = 0;

                        for(int i=0;i<codelist.size();i++)
                        {
                            total_credit = total_credit + Double.parseDouble(creditlist.get(i));

                            if(total_credit > 20.0)
                            {
                                exceedflag = 1;
                                total_credit = total_credit - Double.parseDouble(creditlist.get(i));
                                removal_count = removal_count + 1;

                                codelist.remove(i);
                                namelist.remove(i);
                                creditlist.remove(i);
                                typelist.remove(i);
                                i = i - 1;
                            }
                            else
                            {
                                adapterlist.add(new CourseItem(codelist.get(i), namelist.get(i), creditlist.get(i), typelist.get(i)));
                            }
                        }

                        if(exceedflag == 1)
                        {
                            Toast.makeText(getApplicationContext(), "   Credit exceeded!\n" +removal_count+ " course not added.", Toast.LENGTH_SHORT).show();
                        }

                        StudentAdapter studentAdapter = new StudentAdapter(getApplicationContext(),R.layout.course_submission_list_view_layout,adapterlist);
                        listView.setAdapter(studentAdapter);

                        totalcourse.setText("Total Course: " +codelist.size());
                        totalcredit.setText("Total Credit: " +total_credit);
                    }

                    public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();

            }
        });

        major.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(CourseSubmissionActivity.this, R.anim.blink_anim);
                major.startAnimation(animation);

                final String sem_position = Integer.toString(sp1.getSelectedItemPosition());
                final String position_of_major = Integer.toString(sp2.getSelectedItemPosition());

                firestore.collection("CourseList").document("department")
                        .collection("CSE").document("semester")
                        .collection(sem_position).document("major")
                        .collection(position_of_major).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot document : task.getResult()) {

                                final String s = document.getId();

                                firestore.collection("CourseList").document("department")
                                        .collection("CSE").document("semester")
                                        .collection(sem_position).document("major")
                                        .collection(position_of_major).document(s)
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful())
                                        {
                                            DocumentSnapshot documentSnapshot = task.getResult();

                                            //code is already in the arraylist
                                            if(codelist.indexOf(s) != -1)
                                            {
                                                //do nothing
                                            }
                                            //code is not in the arraylist
                                            else
                                            {
                                                codelist.add(s);
                                                namelist.add(documentSnapshot.getString("name"));
                                                creditlist.add(documentSnapshot.getString("credit"));
                                                typelist.add("Reguler");
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
                        else
                        {
                            Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //handle listview
                //using 1.7 second delay
                //because it takes some time to fetch data from the server
                new CountDownTimer(1700, 1000) {
                    public void onFinish() {

                        total_credit = 0.0;
                        adapterlist.clear();
                        int exceedflag = 0;
                        int removal_count = 0;

                        for(int i=0;i<codelist.size();i++)
                        {
                            total_credit = total_credit + Double.parseDouble(creditlist.get(i));

                            if(total_credit > 20.0)
                            {
                                exceedflag = 1;
                                total_credit = total_credit - Double.parseDouble(creditlist.get(i));
                                removal_count = removal_count + 1;

                                codelist.remove(i);
                                namelist.remove(i);
                                creditlist.remove(i);
                                typelist.remove(i);
                                i = i - 1;
                            }
                            else
                            {
                                adapterlist.add(new CourseItem(codelist.get(i), namelist.get(i), creditlist.get(i), typelist.get(i)));
                            }
                        }

                        if(exceedflag == 1)
                        {
                            Toast.makeText(getApplicationContext(), "   Credit exceeded!\n" +removal_count+ " course not added.", Toast.LENGTH_SHORT).show();
                        }

                        StudentAdapter studentAdapter = new StudentAdapter(getApplicationContext(),R.layout.course_submission_list_view_layout,adapterlist);
                        listView.setAdapter(studentAdapter);

                        totalcourse.setText("Total Course: " +codelist.size());
                        totalcredit.setText("Total Credit: " +total_credit);
                    }

                    public void onTick(long millisUntilFinished) {
                        // millisUntilFinished    The amount of time until finished.
                    }
                }.start();
            }
        });


        irreguler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(CourseSubmissionActivity.this, R.anim.blink_anim);
                irreguler.startAnimation(animation);

                String code = autoComplete.getText().toString();
                code = code.toUpperCase();

                if(allcodelist.contains(code))
                {
                    int pos = allcodelist.indexOf(code);

                    //code is already in the arraylist
                    if(codelist.indexOf(code) != -1)
                    {
                        //do nothing
                    }
                    //code is not in the arraylist
                    else
                    {
                        codelist.add(code);
                        namelist.add(allnamelist.get(pos));
                        creditlist.add(allcreditlist.get(pos));
                        typelist.add("Irreguler");
                    }

                    //handle listview
                    adapterlist.clear();
                    total_credit = 0.0;
                    int exceedflag = 0;

                    for(int i=0;i<codelist.size();i++)
                    {
                        total_credit = total_credit + Double.parseDouble(creditlist.get(i));

                        if(total_credit > 20.0)
                        {
                            exceedflag = 1;
                            total_credit = total_credit - Double.parseDouble(creditlist.get(i));

                            codelist.remove(i);
                            namelist.remove(i);
                            creditlist.remove(i);
                            typelist.remove(i);
                            i = i - 1;
                        }
                        else
                        {
                            adapterlist.add(new CourseItem(codelist.get(i), namelist.get(i), creditlist.get(i), typelist.get(i)));
                        }
                    }

                    if(exceedflag == 1)
                    {
                        Toast.makeText(getApplicationContext(), " Credit exceeded!\nCourse not added.", Toast.LENGTH_SHORT).show();
                    }

                    StudentAdapter studentAdapter = new StudentAdapter(getApplicationContext(),R.layout.course_submission_list_view_layout,adapterlist);
                    listView.setAdapter(studentAdapter);

                    totalcourse.setText("Total Course: " +codelist.size());
                    totalcredit.setText("Total Credit: " +total_credit);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Select a valid course code from suggestion.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(CourseSubmissionActivity.this, R.anim.blink_anim);
                submit.startAnimation(animation);

                if(codelist.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "No course is selected!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder.setMessage(mytext+ " courses for " +HomeActivity.semester + "?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, final int i) {

                                    progressDialog.setMessage("Please wait..");
                                    progressDialog.show();
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.setCancelable(false);

                                    final Map<String, Object> data = new HashMap<>();
                                    data.put("course codes",codelist);
                                    data.put("course names",namelist);
                                    data.put("course credits",creditlist);
                                    data.put("course types",typelist);
                                    data.put("date", date);

                                    //saving to StudentData
                                    firestore.collection("StudentData").document(dept)
                                            .collection("intake").document(intake)
                                            .collection("section").document(sec)
                                            .collection("id").document(id).update(data)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful())
                                                    {
                                                        firestore.collection("User").document(userid).update(data)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        if(task.isSuccessful())
                                                                        {
                                                                            if(update_flag == 0)
                                                                            {
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(getApplicationContext(), "Course submission is successful for " +HomeActivity.semester+ ".", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            else if(update_flag == 1)
                                                                            {
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(getApplicationContext(), "Course submission is successfully updated for " +HomeActivity.semester+ ".", Toast.LENGTH_SHORT).show();
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
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            Intent intent = new Intent(CourseSubmissionActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_home) {

            Intent intent = new Intent(CourseSubmissionActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();

        } else if (id == R.id.menu_bvault) {

            Intent intent = new Intent(CourseSubmissionActivity.this, BloodVaultActivity1.class);
            startActivity(intent);

        } else if (id == R.id.menu_mail_change) {

            Intent intent = new Intent(CourseSubmissionActivity.this, ChangeEmailActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_pass_change) {

            Intent intent = new Intent(CourseSubmissionActivity.this, ChangePasswordActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("You are about to logout!")
                    .setCancelable(false)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            firebaseAuth.signOut();
                            Toast.makeText(getApplicationContext(), "You are logged out!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(CourseSubmissionActivity.this,LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finishAffinity();
                        }
                    })

                    .setNegativeButton("Cancel", null)
                    .show();

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);

        } else if (id == R.id.menu_about) {

            Intent intent = new Intent(CourseSubmissionActivity.this, DeveloperActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
