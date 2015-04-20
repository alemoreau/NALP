package com.enseirb.alemoreau.nalp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;


public class GameActivity extends ActionBarActivity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }*/
    Graph mGraph;
    private boolean mFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        mFirst = true;
        int nZones = b.getInt("nZones");
        int nCouleurs = b.getInt("nCouleurs");
        mGraph = new Graph(this, null, nZones, nCouleurs);
        //mGraph.setBackgroundColor(Color.WHITE);
        setContentView(mGraph);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean b = super.dispatchTouchEvent(ev);
        this.setTitle("NALP - " + mGraph.complexity() + " : " + mGraph.getScore() + " steps");
        if (mGraph.isComplete() && !b){
            //setting preferences
            SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            if (prefs.getInt(mGraph.complexity(), Integer.MAX_VALUE) > mGraph.getScore()) {
                editor.putInt(mGraph.complexity(), mGraph.getScore());
                editor.commit();
            }

            if (mFirst) {
                mFirst = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Congratulation, you won in " + mGraph.getScore() + " steps.")
                        .setTitle("You won !");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        return b;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_settings){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.help)
                    .setTitle("Help ?");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

