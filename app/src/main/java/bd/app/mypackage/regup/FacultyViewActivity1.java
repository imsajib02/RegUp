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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FacultyViewActivity1 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ListView listView;

    public static String[] day;
    public static String[] eve;

    ArrayList<FacultyView1Item> adapterlist = new ArrayList<>();

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_view1);

        listView = (ListView) findViewById(R.id.listview);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


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

        //getting user id
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userid = user.getUid();

        firestore.collection("User").document(userid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    List<String> arr = (List<String>) documentSnapshot.get("day");
                    day = arr.toArray(new String[arr.size()]);

                    List<String> arr1 = (List<String>) documentSnapshot.get("eve");
                    eve = arr1.toArray(new String[arr1.size()]);

                    calladapter();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Start of navigation section
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
        new CountDownTimer(1600, 1000) {
            public void onFinish() {

                adapterlist.clear();

                try
                {
                    if(day.length == 0)
                    {
                        //no intake list for day shift

                        if(eve.length == 0)
                        {
                            //no intake list for evening shift
                        }
                        else if(eve.length > 0)
                        {
                            for (int i=0;i<eve.length;i++)
                            {
                                String s = eve[i];
                                int p = s.indexOf("-");

                                String intake = s.substring(0,p);
                                String section = s.substring(p+1,s.length());

                                adapterlist.add(new FacultyView1Item(intake, section, "Eve"));
                            }
                        }
                    }
                    else if(day.length > 0)
                    {
                        for (int i=0;i<day.length;i++)
                        {
                            String s = day[i];
                            int p = s.indexOf("-");

                            String intake = s.substring(0,p);
                            String section = s.substring(p+1,s.length());

                            adapterlist.add(new FacultyView1Item(intake, section, "Day"));
                        }

                        if(eve.length == 0)
                        {
                            //no intake list for evening shift
                        }
                        else if(eve.length > 0)
                        {
                            for (int i=0;i<eve.length;i++)
                            {
                                String s = eve[i];
                                int p = s.indexOf("-");

                                String intake = s.substring(0,p);
                                String section = s.substring(p+1,s.length());

                                adapterlist.add(new FacultyView1Item(intake, section, "Eve"));
                            }
                        }
                    }

                    FacultyView1Adapter facultyView1Adapter = new FacultyView1Adapter(getApplicationContext(),R.layout.faculty_list_view1_layout,adapterlist);
                    listView.setAdapter(facultyView1Adapter);
                }
                catch (Exception e)
                {
                    //do nothing
                }
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

            this.finish();

        } else if (id == R.id.menu_bvault) {

            Intent intent = new Intent(FacultyViewActivity1.this, BloodVaultActivity1.class);
            startActivity(intent);

        } else if (id == R.id.menu_mail_change) {

            Intent intent = new Intent(FacultyViewActivity1.this, ChangeEmailActivity.class);
            startActivity(intent);

        } else if (id == R.id.menu_pass_change) {

            Intent intent = new Intent(FacultyViewActivity1.this, ChangePasswordActivity.class);
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

                            Intent intent = new Intent(FacultyViewActivity1.this,LoginActivity.class);
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

            Intent intent = new Intent(FacultyViewActivity1.this, DeveloperActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
