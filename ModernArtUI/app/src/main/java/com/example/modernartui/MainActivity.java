package com.example.modernartui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    int buttonID[] = {R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4, R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8};
    int randRed, randBlue, randGreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views in activity
        final SeekBar skBr = (SeekBar) findViewById(R.id.colorSlider);
        final Button[] buttons = new Button[buttonID.length];

        for(int i = 0; i < buttons.length; i++){
            // generate random color for button
            randRed     = new Random().nextInt(256);
            randBlue    = new Random().nextInt(256);
            randGreen   = new Random().nextInt(256);

            buttons[i] = (Button) findViewById(buttonID[i]);
            buttons[i].setBackgroundColor(Color.rgb(randRed, randGreen, randBlue));
            buttons[i].setTag(new int[]{randRed, randGreen, randBlue});
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button disBtn = (Button) view;
                    String btnText = disBtn.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q="+btnText));
                    startActivity(intent);
                }
            });
        }

        // set seekbar listener
        skBr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                for (Button btn: buttons) {
                    //btn.setText(String.valueOf(seekBar.getProgress()));

                    // get color from tag
                    int rgb[] = (int[]) btn.getTag();

                    // change color
                    btn.setBackgroundColor(changeColor(rgb, i, btn));

                }
            }

            private int changeColor(int rgb[], int add, Button btn){
                add = (add * 2);
                int r = (rgb[0] + add >= 256) ? 255 : rgb[0] + add;
                int g = (rgb[1] + add >= 256) ? 255 : rgb[1] + add;
                int b = (rgb[2] + add >= 256) ? 255 : rgb[2] + add;

                btn.setText(String.format("rgb(%d, %d, %d)", r, g, b));

                return Color.rgb(r, g, b);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private PopupWindow popupWindow;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.reloadItem){
            recreate();
            return true;
        }else if(item.getItemId() == R.id.aboutItem){
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_menu, null);

            final Button learnMore = (Button) popupView.findViewById(R.id.learnMoreButton);
            learnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String facebookUrl = "https://github.com/thanhtung4work/Android-Final";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(facebookUrl));
                    startActivity(intent);
                }
            });

            final Button closePopUp = (Button) popupView.findViewById(R.id.closePopUp);
            closePopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                }
            });

            // create the popup window
            int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            popupWindow = new PopupWindow(popupView, width, height, focusable);

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken
            View view = findViewById(R.id.mainLayout);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            // dismiss the popup window when touched
            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }
}