package com.enseirb.alemoreau.nalp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {

    Graph mGraph;
    private boolean _score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        _score = false;
        final Button button_easy = (Button) findViewById(R.id.button_easy);
        button_easy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                Bundle b = new Bundle();
                b.putInt("nZones", 20);
                b.putInt("nCouleurs", 4);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        final Button button_medium = (Button) findViewById(R.id.button_medium);
        button_medium.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                Bundle b = new Bundle();
                b.putInt("nZones", 50);
                b.putInt("nCouleurs", 4);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        final Button button_hard = (Button) findViewById(R.id.button_hard);
        button_hard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                Bundle b = new Bundle();
                b.putInt("nZones", 100);
                b.putInt("nCouleurs", 4);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        final Button button_score = (Button) findViewById(R.id.button_score);
        button_score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (_score){
                    button_easy.setClickable(true); button_easy.setText("Easy");
                    button_medium.setClickable(true); button_medium.setText("Medium");
                    button_hard.setClickable(true);  button_hard.setText("Hard");
                    button_score.setText("Score");
                }
                else{
                    SharedPreferences prefs = getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
                    button_easy.setClickable(false); button_easy.setText("Easy : " + prefs.getInt("easy", 0));
                    button_medium.setClickable(false); button_medium.setText("Medium : " +  prefs.getInt("medium", 0));
                    button_hard.setClickable(false);  button_hard.setText("Hard : " +  prefs.getInt("hard", 0));
                    button_score.setText("Play !");
                }
                    _score = !_score;

            }
        });

        //Bitmap bitmap =  Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
