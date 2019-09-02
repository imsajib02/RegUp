package bd.app.mypackage.regup;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String name, id, dept, intake, sec, mail, type;
    public static String date, semester;

    TextView username, idd, sec_tion, int_ake, department, showtext;
    Button com_reg;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        username = (TextView) findViewById(R.id.name);
        idd = (TextView) findViewById(R.id.idd);
        sec_tion = (TextView) findViewById(R.id.section);
        int_ake = (TextView) findViewById(R.id.intake);
        department = (TextView) findViewById(R.id.dept);
        showtext = (TextView) findViewById(R.id.showtext);
        com_reg = (Button) findViewById(R.id.com_reg);

        //getting user id
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userid = user.getUid();
        com_reg.setVisibility(View.INVISIBLE);

        //getting our user information from database
        firestore.collection("User").document(userid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    type = documentSnapshot.getString("type");

                    if(TextUtils.equals(type, "student"))
                    {
                        name = documentSnapshot.getString("name");
                        id = documentSnapshot.getString("id");
                        dept = documentSnapshot.getString("dept");
                        intake = documentSnapshot.getString("intake");
                        sec = documentSnapshot.getString("section");
                        mail = documentSnapshot.getString("mail");

                        username.setText("Name: " +name);
                        idd.setText("ID: " +id);
                        int_ake.setText("Intake: " +intake);
                        sec_tion.setText("Section: " +sec);

                        if(dept.contains("CSE") || dept.contains("CSIT"))
                        {
                            department.setText("Department: Computer Science & Engineering");
                        }
                        else if(dept.contains("EEE"))
                        {
                            department.setText("Department: Electrical & Electronic Engineering");
                        }
                        else if(dept.contains("ECO"))
                        {
                            department.setText("Department: Economics");
                        }
                        else if(dept.contains("EDE"))
                        {
                            department.setText("Department: Environmental Science");
                        }
                        else if(dept.contains("TEXTILE"))
                        {
                            department.setText("Department: Textile Engineering");
                        }
                        else if(dept.contains("ARCHITECHTURE"))
                        {
                            department.setText("Department: Architechture");
                        }
                        else if(dept.contains("LLB") || dept.contains("LLM"))
                        {
                            department.setText("Department: Law & Justice");
                        }
                        else if(dept.contains("ENGLISH") || dept.contains("ELT"))
                        {
                            department.setText("Department: English");
                        }
                        else if(dept.contains("MATH"))
                        {
                            department.setText("Department: Mathematics and Statistics");
                        }
                        else if(dept.contains("BBA") || dept.contains("MBA") || dept.contains("MBM"))
                        {
                            department.setText("Department: Business Management");
                        }

                        final String date_in_student = documentSnapshot.getString("date");

                        if(TextUtils.equals(dept, "CSE DAY") || TextUtils.equals(dept, "CSE EVE"))
                        {
                            //checking if registration is open or close
                            firestore.collection("Admin").document("reg_status")
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot documentSnapshot = task.getResult();

                                        //getting the last intake which is approved for registration
                                        //specific to the users department
                                        String last_intake = documentSnapshot.getString(dept);

                                        if(Integer.parseInt(intake) < Integer.parseInt(last_intake))
                                        {
                                            //users intake is not approved for registration
                                            showtext.setText("Your intake is considered as graduated. " +
                                                    "Course submission through RegUp is not permitted.");
                                        }
                                        else
                                        {
                                            //users intake is approved for registration
                                            date = documentSnapshot.getString("date");

                                            if(date_in_student != null)
                                            {
                                                if(date_in_student.compareTo(date) == 0)
                                                {
                                                    //student has already completed course registration
                                                    //for new semester
                                                    semester = documentSnapshot.getString("semester");

                                                    showtext.setText("You have already submitted courses for " +semester+ ". " +
                                                            "To make any change click update.");
                                                    com_reg.setText("Update");
                                                    com_reg.setVisibility(View.VISIBLE);
                                                }
                                                else
                                                {
                                                    //student has not completed course registration
                                                    //for new semester
                                                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                                    Calendar c = Calendar.getInstance();
                                                    String currentdate = dateFormat.format(c.getTime());

                                                    semester = documentSnapshot.getString("semester");

                                                    Date date1 = new Date();
                                                    Date date2 = new Date();;

                                                    try {

                                                        date1 = dateFormat.parse(date);
                                                        date2 = dateFormat.parse(currentdate);

                                                        if (date1.after(date2) || date1.equals(date2))
                                                        {
                                                            //registration is open
                                                            showtext.setText("Course submission is open for " + semester + " till " + date + ".");
                                                            com_reg.setVisibility(View.VISIBLE);
                                                        }
                                                        else if (date1.before(date2))
                                                        {
                                                            //registration is close
                                                            showtext.setText("Course submission is close for now.");
                                                        }

                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            else if(date_in_student == null)
                                            {
                                                //student has not yet completed course registration
                                                //for any semester
                                                //new user
                                                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                                Calendar c = Calendar.getInstance();
                                                String currentdate = dateFormat.format(c.getTime());

                                                semester = documentSnapshot.getString("semester");

                                                Date date1 = new Date();
                                                Date date2 = new Date();;

                                                try {

                                                    date1 = dateFormat.parse(date);
                                                    date2 = dateFormat.parse(currentdate);

                                                    if (date1.after(date2) || date1.equals(date2))
                                                    {
                                                        //registration is open
                                                        showtext.setText("Course submission is open for " + semester + " till " + date + ".");
                                                        com_reg.setVisibility(View.VISIBLE);
                                                    }
                                                    else if (date1.before(date2))
                                                    {
                                                        //registration is close
                                                        showtext.setText("Course submission is close for now.");
                                                    }

                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
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
                        else
                        {
                            //currently course registration is not open for other departments
                            showtext.setText("Currently course submission is open for only CSE department.");
                        }

                    }

                    else if(TextUtils.equals(type, "faculty"))
                    {
                        username.setText("Name: " +documentSnapshot.getString("name"));
                        idd.setText("Designation: " +documentSnapshot.getString("post"));

                        if(documentSnapshot.getString("dept").contains("CSE"))
                        {
                            int_ake.setText("Department: Computer Science & Engineering");
                        }

                        firestore.collection("Admin").document("reg_status")
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if(task.isSuccessful())
                                {
                                    DocumentSnapshot documentSnapshot = task.getResult();

                                    semester = documentSnapshot.getString("semester");
                                    date = documentSnapshot.getString("date");

                                    showtext.setText("View data for " +semester+ ".");
                                    com_reg.setText("View");
                                    com_reg.setVisibility(View.VISIBLE);
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

        com_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.blink_anim);
                com_reg.startAnimation(animation);

                if(TextUtils.equals(type, "student"))
                {
                    Intent intent = new Intent(HomeActivity.this, CourseSubmissionActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(TextUtils.equals(type, "faculty"))
                {
                    Intent intent = new Intent(HomeActivity.this, FacultyViewActivity1.class);
                    startActivity(intent);
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("You are about to logout!")
                    .setCancelable(false)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            firebaseAuth.signOut();
                            Toast.makeText(getApplicationContext(), "You are logged out!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finishAffinity();
                        }
                    })

                    .setNegativeButton("Cancel", null)
                    .show();

            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
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

            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();

        } else if (id == R.id.menu_bvault) {

            Intent intent = new Intent(HomeActivity.this, BloodVaultActivity1.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Bundle b = new Bundle();
            b.putString("from", "home");
            intent.putExtras(b);

            startActivity(intent);
            finishAffinity();

        } else if (id == R.id.menu_mail_change) {

            Intent intent = new Intent(HomeActivity.this, ChangeEmailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Bundle b = new Bundle();
            b.putString("from", "home");
            intent.putExtras(b);

            startActivity(intent);
            finishAffinity();

        } else if (id == R.id.menu_pass_change) {

            Intent intent = new Intent(HomeActivity.this, ChangePasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Bundle b = new Bundle();
            b.putString("from", "home");
            intent.putExtras(b);

            startActivity(intent);
            finishAffinity();

        } else if (id == R.id.menu_logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("You are about to logout!")
                    .setCancelable(false)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            firebaseAuth.signOut();
                            Toast.makeText(getApplicationContext(), "You are logged out!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
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

            Intent intent = new Intent(HomeActivity.this, DeveloperActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Bundle b = new Bundle();
            b.putString("from", "home");
            intent.putExtras(b);

            startActivity(intent);
            finishAffinity();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

        //End of navigation section
    }
}
