package bd.app.mypackage.regup;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class DeveloperActivity extends AppCompatActivity {

    ImageView fb_sajib, fb_antu, fb_tariqul, insta_sajib, insta_antu, insta_tariqul;

    String value = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        fb_sajib = (ImageView) findViewById(R.id.fb_sajib);
        fb_antu = (ImageView) findViewById(R.id.fb_antu);
        fb_tariqul = (ImageView) findViewById(R.id.fb_tariqul);
        insta_sajib = (ImageView) findViewById(R.id.insta_sajib);
        insta_antu = (ImageView) findViewById(R.id.insta_antu);
        insta_tariqul = (ImageView) findViewById(R.id.insta_tariqul);

        try
        {
            value = getIntent().getExtras().getString("from");
        }
        catch (Exception e)
        {
            //
        }


        fb_sajib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(DeveloperActivity.this, R.anim.blink_anim);
                fb_sajib.startAnimation(animation);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/imsajib02"));
                startActivity(browserIntent);
            }
        });


        insta_sajib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(DeveloperActivity.this, R.anim.blink_anim);
                insta_sajib.startAnimation(animation);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/imsajib2"));
                startActivity(browserIntent);
            }
        });


        fb_tariqul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(DeveloperActivity.this, R.anim.blink_anim);
                fb_tariqul.startAnimation(animation);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tariqul4911"));
                startActivity(browserIntent);
            }
        });


        insta_tariqul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(DeveloperActivity.this, R.anim.blink_anim);
                insta_tariqul.startAnimation(animation);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/taricool"));
                startActivity(browserIntent);
            }
        });


        fb_antu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(DeveloperActivity.this, R.anim.blink_anim);
                fb_antu.startAnimation(animation);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Anuvhob.Roy"));
                startActivity(browserIntent);
            }
        });


        insta_antu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation animation = AnimationUtils.loadAnimation(DeveloperActivity.this, R.anim.blink_anim);
                insta_antu.startAnimation(animation);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/anuvhob"));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(TextUtils.equals(value, "home"))
        {
            Intent intent = new Intent(DeveloperActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();
        }
        else
        {
            this.finish();
        }
    }
}
