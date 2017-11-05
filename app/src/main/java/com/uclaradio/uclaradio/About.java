package com.uclaradio.uclaradio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.view.View;
import android.net.Uri;

public class About extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageButton fbk = (ImageButton) findViewById(R.id.button1);
        fbk.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                Uri uri = Uri.parse("https://www.facebook.com/UCLARadio/?fref=ts");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });

        ImageButton igm = (ImageButton) findViewById(R.id.button2);
        igm.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                Uri uri = Uri.parse("https://www.instagram.com/uclaradio/");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });

        ImageButton twr = (ImageButton) findViewById(R.id.button3);
        twr.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                Uri uri = Uri.parse("https://twitter.com/UCLAradio");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });

        ImageButton tbr = (ImageButton) findViewById(R.id.button4);
        tbr.setOnClickListener(new OnClickListener() {
            public void onClick(View v){
                Uri uri = Uri.parse("http://uclaradio.tumblr.com/");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
            }
        });
    }
}
