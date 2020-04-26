package com.irl.primefinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Application to count odd numbers and calculating if the number is a prime.
 * <p>
 * Main Activity. Called when application is created.
 */
public class MainActivity extends AppCompatActivity implements Runnable {

    private static final String TAG = "MainActivity";

    private Button button;
    private TextView textView;
    private Thread t;
    private long candidate;
    private boolean counting = false;

    /** View call. Sets activity, variables and listeners. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPrime();
        t = new Thread(this);
        t.start();

        textView = findViewById(R.id.textView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopCounting();
            }
        });
    }

    /**
     * Saves and displays the latest found primenumber and
     * updates the candidate for testing.
     */
    private void startCounter() {
        if (isPrime(candidate)) {
            Log.d(TAG, candidate + "is a prime.");
            savePrime();
            updateCount(candidate);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        candidate += 2;
    }

    /** Computes if the parameter is a prime number */
    private boolean isPrime(long candidate) {
        long sqrt = (long) Math.sqrt(candidate);
        for (long i = 3; i <= sqrt; i += 2)
            if (candidate % i == 0)
                return false;
        return true;
    }

    /** Switches the boolean controlling the thread loop */
    private void startStopCounting() {
        if (counting)
            setButtonText("Start");
        else
            setButtonText("Stop");
        counting = !counting;
    }

    /** Saves 'candidate' to shared preferences. */
    private void savePrime() {
        String fileName = "savedPrime";
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(fileName, candidate);
        editor.commit();
    }

    /** Loads the latest found prime to start from */
    private void loadPrime() {
        String fileName = "savedPrime";
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        candidate = sharedPref.getLong(fileName, 1);
    }

    /** Updates textview */
    private void updateCount(long candidate) {
        textView.setText("" + candidate);
    }

    /** Updates button text */
    private void setButtonText(String text) {
        button.setText(text);
    }

    /** Run method. Starts/Pauses the counter depending on 'counting' boolean */
    @Override
    public void run() {
        while (true) {
            while (counting)
                startCounter();
        }
    }
}
