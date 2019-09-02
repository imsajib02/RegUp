package bd.app.mypackage.regup;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FacultyViewActivity3 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView sname, sid, totalcourse, totalcredit;
    ListView listView;

    int total_course = 0;
    double total_credit = 0.0;

    String stu_id, stu_name, stu_intake, stu_section, stu_dept;

    String[] codearray, namearray, creditarray, typearray;

    ArrayList<FacultyView3Item> adapterlist = new ArrayList<>();

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_view3);

        sname = (TextView) findViewById(R.id.name);
        sid = (TextView) findViewById(R.id.id);
        totalcourse = (TextView) findViewById(R.id.totalcourse);
        totalcredit = (TextView) findViewById(R.id.totalcredit);
        listView = (ListView) findViewById(R.id.listview);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        stu_id = getIntent().getExtras().getString("id");
        stu_intake = getIntent().getExtras().getString("intake");
        stu_section = getIntent().getExtras().getString("section");
        stu_dept = getIntent().getExtras().getString("dept");


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


        firestore.collection("StudentData").document(stu_dept)
                .collection("intake").document(stu_intake)
                .collection("section").document(stu_section)
                .collection("id").document(stu_id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    stu_name = documentSnapshot.getString("name");

                    List<String> arr1 = (List<String>) documentSnapshot.get("course codes");
                    codearray = arr1.toArray(new String[arr1.size()]);

                    List<String> arr2 = (List<String>) documentSnapshot.get("course names");
                    namearray = arr2.toArray(new String[arr2.size()]);

                    List<String> arr3 = (List<String>) documentSnapshot.get("course credits");
                    creditarray = arr3.toArray(new String[arr3.size()]);

                    List<String> arr4 = (List<String>) documentSnapshot.get("course types");
                    typearray = arr4.toArray(new String[arr4.size()]);

                    calladapter();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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


    public void calladapter()
    {
        //using 1.5 second delay
        //because it takes some time to fetch data from the server
        new CountDownTimer(1500, 1000) {
            public void onFinish() {

                adapterlist.clear();

                for(int i=0;i<codearray.length;i++)
                {
                    total_course = total_course + 1;
                    total_credit = total_credit + Double.parseDouble(creditarray[i]);

                    adapterlist.add(new FacultyView3Item(codearray[i], namearray[i], creditarray[i], typearray[i]));
                }

                FacultyView3Adapter facultyView3Adapter = new FacultyView3Adapter(getApplicationContext(),R.layout.faculty_list_view3_layout,adapterlist);
                listView.setAdapter(facultyView3Adapter);

                sname.setText("Name: " +stu_name);
                sid.setText("ID: " +stu_id);
                totalcredit.setText("Total Credit: " +total_credit);
                totalcourse.setText("Number of Course Taken: " +total_course);
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();
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

            Intent intent = new Intent(FacultyViewActivity3.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();

        } else if (id == R.id.menu_bvault) {

            Intent intent = new Intent(FacultyViewActivity3.this, BloodVaultActivity1.class);
            startActivity(intent);

        } else if (id == R.id.menu_mail_change) {

            Intent intent = new Intent(FacultyViewActivity3.this, ChangeEmailActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_pass_change) {

            Intent intent = new Intent(FacultyViewActivity3.this, ChangePasswordActivity.class);
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

                            Intent intent = new Intent(FacultyViewActivity3.this, LoginActivity.class);
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

            Intent intent = new Intent(FacultyViewActivity3.this, DeveloperActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
