package bd.app.mypackage.regup;

//intro activity with bubt logo and app name

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    ImageView logo;
    TextView appname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = (ImageView) findViewById(R.id.startuplogo);
        appname = (TextView) findViewById(R.id.appname);

        //bottom to up animation for logo
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        //fade in animation for app name
        Animation fadein = AnimationUtils.loadAnimation(this,R.anim.fadein);
        logo.setAnimation(animation);
        appname.setAnimation(fadein);

        LogoLauncher l = new LogoLauncher();
        l.start();
    }

    //animation handling class
    private class LogoLauncher extends Thread{
        public void run(){
            //3.5 seconds delay
            try{
                sleep(3500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            //after 3.5 seconds LoginActivity opens
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
