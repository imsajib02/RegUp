package bd.app.mypackage.regup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class NotifyAboutVerificationActivity extends AppCompatActivity {

    TextView msg;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_about_verification);

        msg = (TextView) findViewById(R.id.msg);
        button = (Button) findViewById(R.id.button);

        int value = getIntent().getExtras().getInt("value");

        if(value == 0)
        {
            msg.setText("A verification mail has been sent to your email address. " +
                    "Please verify your email in order to login.");
        }
        else if (value == 1)
        {
            msg.setText("A verification mail has been sent to your new email address. " +
                    "Please verify your new email and login.");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(NotifyAboutVerificationActivity.this, R.anim.blink_anim);
                button.startAnimation(animation);

                Intent intent = new Intent(NotifyAboutVerificationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onBackPressed(){
        //
        Intent intent = new Intent(NotifyAboutVerificationActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}
