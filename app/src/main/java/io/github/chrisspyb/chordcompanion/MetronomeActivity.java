package io.github.chrisspyb.chordcompanion;


import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MetronomeActivity extends AppCompatActivity {
    private boolean playing = false;
    private boolean autoplay = false;
    private int metronome_bpm = 100;
    private static int BPM_MAX = 240;
    private static int BPM_MIN = 40;
    private static SoundPool soundPool;
    private Timer timer = new Timer("MetronomeTimer",true);
    private TimerTask tickTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metronome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Populate Sound Pool
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,100);
        soundPool.load(this, R.raw.tick,1);
        SeekBar bpmBar = (SeekBar) findViewById(R.id.seekBar_bpm);

        // Config SeekBar
        final TextView bpmTextView = (TextView) findViewById(R.id.textView_bpm);
        bpmBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                metronome_bpm = BPM_MIN + progress*(BPM_MAX-BPM_MIN)/100 ;
                bpmTextView.setText(metronome_bpm+" BPM");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // pause ticking
                if (playing){
                    stopMetronome();
                    autoplay = true;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // restart ticking
                if (autoplay){
                    startMetronome(metronome_bpm);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMetronome();
    }

    public void startMetronome(int bpm){
        if (playing) return;
        playing = true;
        autoplay = false;
        long delay = 60000/bpm; // ms
        tickTask = new TimerTask(){
            @Override
            public void run(){
                soundPool.play(1,1f,1f,1,0,1f);
            }
        };
        timer.scheduleAtFixedRate(tickTask,delay,delay);
    }

    public void stopMetronome(){
        if (!playing) return;
        playing = false;
        tickTask.cancel();
    }
    public void clickPlay(View view){
        if (playing){
            stopMetronome();
        }
        else{
            startMetronome(metronome_bpm);
        }
    }
}
