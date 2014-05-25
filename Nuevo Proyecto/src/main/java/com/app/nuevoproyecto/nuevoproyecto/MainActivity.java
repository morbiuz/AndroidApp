package com.app.nuevoproyecto.nuevoproyecto;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends ActionBarActivity implements MediaPlayer.OnCompletionListener {

    TextView mainTextView;
    Button mainButton, quitButton, playButton, pauseButton, stopButton, selectButton;
    EditText mainEditText;
    MediaPlayer mp;
    // Intents use an int number as identification so on return the activity recognizes it
    private static final int FIND_SONG_INTENT = 10;

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
        mainButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
               mainTextView.setText("Hello " + mainEditText.getText().toString() + "!");
            }
        });

        // 3. Access the EditText defined in layout XML
        // and listen when its clicked
        mainEditText = (EditText) findViewById(R.id.main_edittext);
        mainEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mainEditText.setText("");
            }
        });

        // 4. Add listener to the quit button
        quitButton = (Button) findViewById(R.id.quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Show pop-up to confirm exit
                FragmentManager fragmentManager = getSupportFragmentManager();
                QuitDialogFragment quitDialogFragment = new QuitDialogFragment();
                quitDialogFragment.show(fragmentManager, "tagAlerta");
            }
        });

        // 5. Add listeners to the play, pause and stop buttons
        playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Plays song
                play();
            }
        });

        pauseButton = (Button) findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Pauses song
                pause();
            }
        });

        stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Stops song
                stop();
            }
        });

        // 6. Prepare song to be played, better done in a new method with try-catch for errors
        setupSong();

        // 7. Add select button listener
        selectButton = (Button) findViewById(R.id.select_button);
        selectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //stops and releases current song
                //needs a try-catch so it doesn't throw an error when mp is not initialized
                try {
                    stop();
                    mp.release();
                }catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                //calls a new song selection
                launchMusicPlayer(view);
            }
        });
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

    // Starts user's choice of music player for song selection.
    public void launchMusicPlayer(View view) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
        // Tells the intent to get an external content URI, and the type of file it needs to
        // look for is audio/mp3
        intent.setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,"audio/mp3");
        // Starts the activity with the predefined number ID (FIND_SONG_INTENT)
        startActivityForResult(intent, FIND_SONG_INTENT);
    }

    // Called after returning from music selection app
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == FIND_SONG_INTENT) && (resultCode == RESULT_OK)) {
            Uri musicURI = data.getData(); //Gets obtained URI for the file
            try {
                // Reinitializes the MediaPlayer object and opens the selected song
                mp = new MediaPlayer();
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(getApplicationContext(), musicURI);
                mp.prepare();
                playButton.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast toast = Toast.makeText(this, R.string.selection_cancel,
                    Toast.LENGTH_SHORT);
            toast.show();
            playButton.setEnabled(false);
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
