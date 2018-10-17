package tech.honeysharma.techbmechat.onboarding;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

import tech.honeysharma.techbmechat.R;

public class BoardingActivity extends TutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFinishText("Verify email");

        addFragment(new Step.Builder().setTitle("Welcome to Techno Jam")
                .setContent("Techno jam is a chat app to help you learn technology")
                .setBackgroundColor(Color.parseColor("#232429")) // int background color
                .setDrawable(R.drawable.technojam) // int top drawable
                .setSummary("")
                .build());

        addFragment(new Step.Builder().setTitle("Verify your email")
                .setContent("Please verify your email first, before you can login")
                .setBackgroundColor(Color.parseColor("#FA5D0F")) // int background color
                .setDrawable(R.drawable.ic_email_verify) // int top drawable
                .setSummary("")
                .build());
    }

    @Override
    public void finishTutorial() {
        super.finishTutorial();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        startActivity(intent);
    }

    @Override
    public void setFinishText(String text) {
        super.setFinishText(text);
    }
}
