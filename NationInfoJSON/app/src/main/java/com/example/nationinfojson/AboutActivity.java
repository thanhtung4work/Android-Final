package com.example.nationinfojson;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView meme = (TextView) findViewById(R.id.memeView);
        meme.setText("A: Làm\nN: Deadline\nD: Muốn\nR: Rớt\nO: Nước\nI: Mắt\nD: \uD83D\uDE2D");

        TextView credit = (TextView) findViewById(R.id.creditView);
        credit.setText(
                String.format("Universal Image Loader: %n%s%n %nDevelopers: %n%s %n%s %n%s",
                        "https://github.com/nostra13/Android-Universal-Image-Loader",
                        "Tran Thanh Tung \n(thủ khoa)",
                        "Nguyen Tran Van Vu \n(thợ săn học bổng)",
                        "Pham Tran Khoi \n(ông trùm bảo mật)")
        );
    }
}