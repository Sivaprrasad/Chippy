package com.example.chippy;

import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    GameEngine chippySpaceship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);

        final MediaPlayer catsoundmp = MediaPlayer.create(this, R.raw.sample);

        // Get size of the screen
                Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Initialize the GameEngine object
        // Pass it the screen size (height & width)
        chippySpaceship = new GameEngine(this, size.x, size.y);

        // Make GameEngine the view of the Activity
        setContentView(chippySpaceship);
    }

    // Android Lifecycle function
    @Override
    protected void onResume() {
        super.onResume();
        chippySpaceship.startGame();
    }

    // Stop the thread in snakeEngine
    @Override
    protected void onPause() {
        super.onPause();
        chippySpaceship.pauseGame();
    }
}