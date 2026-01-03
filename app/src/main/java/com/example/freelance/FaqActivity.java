package com.example.freelance;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class FaqActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        MaterialToolbar tb = findViewById(R.id.toolbarFaq);
        tb.setNavigationOnClickListener(v -> finish());

        toggle(findViewById(R.id.q1), findViewById(R.id.a1));
        toggle(findViewById(R.id.q2), findViewById(R.id.a2));
        toggle(findViewById(R.id.q3), findViewById(R.id.a3));
    }

    private void toggle(TextView question, TextView answer) {
        question.setOnClickListener(v -> {
            if (answer.getVisibility() == View.VISIBLE) {
                answer.setVisibility(View.GONE);
            } else {
                answer.setVisibility(View.VISIBLE);
            }
        });
    }
}