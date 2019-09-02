package bd.app.mypackage.regup;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BloodVaultActivity1 extends AppCompatActivity {

    String value = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_vault1);

        try
        {
            value = getIntent().getExtras().getString("from");
        }
        catch (Exception e)
        {
            //
        }

        //4 second delay
        new CountDownTimer(4000, 1000) {
            public void onFinish() {

                Intent intent = new Intent(BloodVaultActivity1.this, BloodVaultActivity2.class);

                Bundle b = new Bundle();
                b.putString("from", value);
                intent.putExtras(b);

                startActivity(intent);
                finish();
            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
            }
        }.start();
    }
}
