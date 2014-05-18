package com.app.nuevoproyecto.nuevoproyecto;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



public class MainActivity extends ActionBarActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {

    TextView mainTextView;
    Button mainButton, quitButton, playButton, pauseButton, stopButton;
    EditText mainEditText;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Access the TextView defined in layout XML
        // and then set its text
        mainTextView = (TextView) findViewById(R.id.main_textview);
        mainTextView.setText("Hello World!");

        // 2. Access the Button defined in layout XML
        // and listen for it here
        mainButton = (Button) findViewById(R.id.main_button);
        mainButton.setOnClickListener(this);

        // 3. Access the EditText defined in layout XML
        // and listen when its clicked
        mainEditText = (EditText) findViewById(R.id.main_edittext);
        mainEditText.setOnClickListener(this);

        // 4. Add listener to the quit button
        quitButton = (Button) findViewById(R.id.quit_button);
        quitButton.setOnClickListener(this);

        // 5. Add listeners to the play, pause and stop buttons
        playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(this);

        pauseButton = (Button) findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(this);

        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(this);

        // 6. Prepare song to be played, better done in a new method with try-catch for errors
        setupSong();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        // Gets texts from mainEditTexts and substitutes text on mainTextView
        if(view == mainButton) {
            mainTextView.setText("Hello " + mainEditText.getText().toString() + "!");
        }
        //Clears the field
        else if(view == mainEditText){
            mainEditText.setText("");
        }
        // Quit button to fuck the App
        else if(view == quitButton) {
            // Show pop-up to confirm exit
            FragmentManager fragmentManager = getSupportFragmentManager();
            QuitDialogFragment quitDialogFragment = new QuitDialogFragment();
            quitDialogFragment.show(fragmentManager, "tagAlerta");
        }
        else if(view == playButton) {
            play();
        }

        else if(view == pauseButton) {
            pause();
        }

        else if(view == stopButton) {
            stop();
        }
    }

    // This listener will call the stop() method when it detects the song has finished.
    // It allows multiple reproductions of the song.
    public void onCompletion(MediaPlayer mp) {
        stop();
    }

    // Load the song into the MediaPlayer object with a try-catch for errors.
    private void loadClip() {
        try {
            mp=MediaPlayer.create(this, R.raw.song);
            mp.setOnCompletionListener(this);
        }
        catch (Throwable t) {
            showError(t);
        }
    }


    // Play and pause methods, action buttons are enabled or disabled depending
    // on the state of the reproduction.
    private void play() {
        mp.start();
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);
    }

    private void pause() {
        mp.pause();

        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    // After calling MediaPlayer.stop, prepare song again and set the pointer at the start
    // so it can be reproduced again if needed.
    private void stop() {
        mp.stop();
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        try {
            mp.prepare();
            mp.seekTo(0);
            playButton.setEnabled(true);
        }
        catch (Throwable t) {
            showError(t);
        }
    }

    // Handles the start state, loading the song and the initial state of buttons
    private void setupSong() {
        loadClip();
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    // If an error is caught, show an alert dialog 
    private void showError(Throwable t) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder
                .setTitle("Exception!")
                .setMessage(t.toString())
                .setPositiveButton("OK", null)
                .show();
    }
}
