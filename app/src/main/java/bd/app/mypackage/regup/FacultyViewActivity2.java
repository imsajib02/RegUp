package bd.app.mypackage.regup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FacultyViewActivity2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String in_take, sec_tion, shi_ft, de_pt, given_date, seme_ster;
    int count = 0, REQUEST_STORAGE = 1;

    TextView intake, section, total;
    ListView listView;
    Button save;

    String COMMA_DELIMITER = ",";
    String NEW_LINE_SEPARATOR = "\n";
    String HEADER = "ID";

    boolean error_occurred;

    ArrayList<FacultyView2Item> adapterlist = new ArrayList<>();

    ArrayList<String> IDlist = new ArrayList<>();
    ArrayList<String> codelist = new ArrayList<>();

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_view2);

        intake = (TextView) findViewById(R.id.intake);
        section = (TextView) findViewById(R.id.section);
        total = (TextView) findViewById(R.id.total);
        listView = (ListView) findViewById(R.id.listview);
        save = (Button) findViewById(R.id.save);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        in_take = getIntent().getExtras().getString("intake");
        sec_tion = getIntent().getExtras().getString("section");
        shi_ft = getIntent().getExtras().getString("shift");

        given_date = HomeActivity.date;
        seme_ster = HomeActivity.semester;

        if(TextUtils.equals(shi_ft, "Day"))
        {
            de_pt = "CSE DAY";
        }
        else if(TextUtils.equals(shi_ft, "Eve"))
        {
            de_pt = "CSE EVE";
        }

        adapterlist.clear();


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


        firestore.collection("StudentData").document(de_pt)
                .collection("intake").document(in_take)
                .collection("section").document(sec_tion)
                .collection("id").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful())
                {
                    for (DocumentSnapshot document : task.getResult()) {

                        //student IDs
                        final String s = document.getId();

                        firestore.collection("StudentData").document(de_pt)
                                .collection("intake").document(in_take)
                                .collection("section").document(sec_tion)
                                .collection("id").document(s)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if(task.isSuccessful())
                                {
                                    DocumentSnapshot documentSnapshot = task.getResult();

                                    String registered_date = documentSnapshot.getString("date");

                                    if(registered_date != null)
                                    {
                                        if(registered_date.compareTo(given_date) == 0)
                                        {
                                            String name = documentSnapshot.getString("name");
                                            IDlist.add(s);

                                            adapterlist.add(new FacultyView2Item(name, s));
                                            count = count + 1;
                                        }
                                        else
                                        {
                                            //courses registered for previous semester
                                            //do nothing
                                        }
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


        //using 1.8 second delay
        //because it takes some time to fetch data from the server
        new CountDownTimer(2000, 1000) {
            public void onFinish() {

                FacultyView2Adapter facultyView2Adapter = new FacultyView2Adapter(getApplicationContext(),R.layout.faculty_list_view2_layout,adapterlist);
                listView.setAdapter(facultyView2Adapter);

                intake.setText("Intake: " +in_take+ " (" +shi_ft+ ")");
                section.setText("Section: " +sec_tion);
                total.setText("Number of student submitted: " +count);
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(FacultyViewActivity2.this, R.anim.blink_anim);
                save.startAnimation(animation);

                progressDialog.setMessage("Please wait..");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);

                //check for permission and save text file to external storage
                try {
                    if (ContextCompat.checkSelfPermission(FacultyViewActivity2.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(FacultyViewActivity2.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                    }

                    else
                    {
                        if(!IDlist.isEmpty())
                        {
                            //check if external storage is available
                            if(isExternalStorageWritable())
                            {
                                String albumName = "RegUP";
                                String fileName = seme_ster+ "_" +de_pt+ "_" +in_take+ "_" +sec_tion+ ".csv";

                                File folder = new File(Environment.getExternalStorageDirectory(), albumName);

                                if (!folder.exists())
                                {
                                    Log.e("msg", "Directory not created");
                                    folder.mkdirs();
                                }

                                final File file = new File(folder, fileName);

                                try
                                {
                                    final FileWriter writer = new FileWriter(file);

                                    //add headers to CSV file
                                    writer.append(NEW_LINE_SEPARATOR);
                                    writer.append("");
                                    writer.append(COMMA_DELIMITER);
                                    writer.append("Semester");
                                    writer.append(COMMA_DELIMITER);
                                    writer.append(seme_ster);
                                    writer.append(NEW_LINE_SEPARATOR);
                                    writer.append("");
                                    writer.append(COMMA_DELIMITER);
                                    writer.append("Department");
                                    writer.append(COMMA_DELIMITER);
                                    writer.append(de_pt);
                                    writer.append(NEW_LINE_SEPARATOR);
                                    writer.append("");
                                    writer.append(COMMA_DELIMITER);
                                    writer.append("Intake");
                                    writer.append(COMMA_DELIMITER);
                                    writer.append(in_take);
                                    writer.append(NEW_LINE_SEPARATOR);
                                    writer.append("");
                                    writer.append(COMMA_DELIMITER);
                                    writer.append("Section");
                                    writer.append(COMMA_DELIMITER);
                                    writer.append(sec_tion);
                                    writer.append(NEW_LINE_SEPARATOR);
                                    writer.append(NEW_LINE_SEPARATOR);
                                    writer.append(NEW_LINE_SEPARATOR);
                                    writer.append(NEW_LINE_SEPARATOR);
                                    writer.append("");
                                    writer.append(COMMA_DELIMITER);
                                    writer.append(HEADER);


                                    for(int i=0;i<IDlist.size();i++)
                                    {
                                        final String id = IDlist.get(i);
                                        codelist.clear();

                                        //add ID to CSV file
                                        writer.append(NEW_LINE_SEPARATOR);
                                        writer.append(NEW_LINE_SEPARATOR);
                                        writer.append("");
                                        writer.append(COMMA_DELIMITER);
                                        writer.append(id);
                                        writer.append(COMMA_DELIMITER);
                                        writer.append("");
                                        writer.append(COMMA_DELIMITER);

                                        firestore.collection("StudentData").document(de_pt)
                                                .collection("intake").document(in_take)
                                                .collection("section").document(sec_tion)
                                                .collection("id").document(id)
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if(task.isSuccessful())
                                                {
                                                    DocumentSnapshot documentSnapshot = task.getResult();

                                                    List<String> arr1 = (List<String>) documentSnapshot.get("course codes");
                                                    codelist.addAll(arr1);

                                                    for(int j=0;j<codelist.size();j++)
                                                    {
                                                        try
                                                        {
                                                            writer.append(codelist.get(j));
                                                            writer.append(COMMA_DELIMITER);
                                                        }
                                                        catch (Exception e)
                                                        {
                                                            e.printStackTrace();
                                                            error_occurred = true;
                                                        }
                                                    }
                                                }
                                                else
                                                {
                                                    //progressDialog.dismiss();
                                                    error_occurred = true;
                                                    //Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }

                                    new CountDownTimer(1800, 1000) {
                                        public void onFinish() {

                                            try
                                            {
                                                if(error_occurred)
                                                {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(FacultyViewActivity2.this, "Some error occurred! Please try again.", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    writer.flush();
                                                    writer.close();

                                                    progressDialog.dismiss();
                                                    Toast.makeText(FacultyViewActivity2.this, "File is saved to RegUP folder in external storage.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                                progressDialog.dismiss();
                                                Toast.makeText(FacultyViewActivity2.this, "Some error occurred! Please try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        public void onTick(long millisUntilFinished) {
                                            // millisUntilFinished    The amount of time until finished.
                                        }
                                    }.start();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(FacultyViewActivity2.this, "Some error occurred! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(FacultyViewActivity2.this, "External storage is not available.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(FacultyViewActivity2.this, "No data to save!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch (NullPointerException e){
                    e.printStackTrace();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_STORAGE)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Grant storage permission in settings.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            this.finish();
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

            Intent intent = new Intent(FacultyViewActivity2.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();

        } else if (id == R.id.menu_bvault) {

            Intent intent = new Intent(FacultyViewActivity2.this, BloodVaultActivity1.class);
            startActivity(intent);

        } else if (id == R.id.menu_mail_change) {

            Intent intent = new Intent(FacultyViewActivity2.this, ChangeEmailActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_pass_change) {

            Intent intent = new Intent(FacultyViewActivity2.this, ChangePasswordActivity.class);
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

                            Intent intent = new Intent(FacultyViewActivity2.this, LoginActivity.class);
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

            Intent intent = new Intent(FacultyViewActivity2.this, DeveloperActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
