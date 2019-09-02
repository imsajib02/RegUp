package bd.app.mypackage.regup;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminActivity extends AppCompatActivity {

    Button update_registration, add_course, remove_course, add_faculty, generate_passcode, check_validity, remove_faculty,
            update_faculty_supervision, change_password;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        update_registration = (Button) findViewById(R.id.reg_status);
        add_course = (Button) findViewById(R.id.add_course);
        remove_course = (Button) findViewById(R.id.remove_course);
        add_faculty = (Button) findViewById(R.id.add_faculty);
        generate_passcode = (Button) findViewById(R.id.generate_code);
        check_validity = (Button) findViewById(R.id.check_validity);
        remove_faculty = (Button) findViewById(R.id.remove_faculty);
        update_faculty_supervision = (Button) findViewById(R.id.update_faculty_supervision);
        change_password = (Button) findViewById(R.id.change_password);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        update_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AdminActivity.this, R.anim.blink_anim);
                update_registration.startAnimation(animation);

                Intent intent = new Intent(AdminActivity.this, EnableSubmissionActivity.class);
                startActivity(intent);
            }
        });

        add_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AdminActivity.this, R.anim.blink_anim);
                add_course.startAnimation(animation);

                Intent intent = new Intent(AdminActivity.this, AddCourseActivity.class);
                startActivity(intent);
            }
        });

        remove_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AdminActivity.this, R.anim.blink_anim);
                remove_course.startAnimation(animation);

                Intent intent = new Intent(AdminActivity.this, RemoveCourseActivity.class);
                startActivity(intent);
            }
        });

        add_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AdminActivity.this, R.anim.blink_anim);
                add_faculty.startAnimation(animation);

                Intent intent = new Intent(AdminActivity.this, AddFacultyActivity.class);
                startActivity(intent);
            }
        });

        generate_passcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AdminActivity.this, R.anim.blink_anim);
                generate_passcode.startAnimation(animation);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://generator.voucherify.io"));
                startActivity(browserIntent);
            }
        });

        check_validity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AdminActivity.this, R.anim.blink_anim);
                check_validity.startAnimation(animation);

                Intent intent = new Intent(AdminActivity.this, CheckPasscodeValidityActivity.class);
                startActivity(intent);
            }
        });

        remove_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AdminActivity.this, R.anim.blink_anim);
                remove_faculty.startAnimation(animation);

                Intent intent = new Intent(AdminActivity.this, RemoveFacultyActivity.class);
                startActivity(intent);
            }
        });

        update_faculty_supervision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AdminActivity.this, R.anim.blink_anim);
                update_faculty_supervision.startAnimation(animation);

                Intent intent = new Intent(AdminActivity.this, UpdateSupervisionActivity.class);
                startActivity(intent);
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(AdminActivity.this, R.anim.blink_anim);
                change_password.startAnimation(animation);

                Intent intent = new Intent(AdminActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }


    //creating the action bar with home button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.logout_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //home button click listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.logoutbutton:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("You are about to logout!")
                        .setCancelable(false)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                firebaseAuth.signOut();
                                Toast.makeText(getApplicationContext(), "You are logged out!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finishAffinity();
                            }
                        })

                        .setNegativeButton("Cancel", null)
                        .show();

                Dialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("You are about to logout!")
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        firebaseAuth.signOut();
                        Toast.makeText(getApplicationContext(), "You are logged out!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
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
