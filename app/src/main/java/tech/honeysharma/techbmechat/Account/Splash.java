package tech.honeysharma.techbmechat.Account;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tech.honeysharma.techbmechat.R;

public class Splash extends AppCompatActivity {
 ConstraintLayout cons_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cons_back= (ConstraintLayout) findViewById(R.id.const_layout);
        cons_back.setBackgroundResource(R.drawable.splash_image);
        Thread thread =new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {}
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        thread.start();
    }
}
