package bd.app.mypackage.regup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class RegistrationUserTypeActivity extends AppCompatActivity {

    Button faculty, student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_user_type);

        faculty = (Button) findViewById(R.id.faculty);
        student = (Button) findViewById(R.id.student);

        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(RegistrationUserTypeActivity.this, R.anim.blink_anim);
                faculty.startAnimation(animation);

                Intent intent = new Intent(RegistrationUserTypeActivity.this, FacultyVerificationActivity.class);
                startActivity(intent);
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(RegistrationUserTypeActivity.this, R.anim.blink_anim);
                student.startAnimation(animation);

                Intent intent = new Intent(RegistrationUserTypeActivity.this, StudentVerificationActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed(){
        //goes to Login activity
        this.finish();
    }
}
