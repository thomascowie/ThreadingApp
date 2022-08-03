package com.example.group12_inclass06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
Cowie_InClass06
MainActivity.java
Thomas Cowie
 */
public class MainActivity extends AppCompatActivity {
    //Fields
    TextView textViewComplexityTimes;
    TextView textViewAverage;
    TextView textViewProgress;
    SeekBar seekBarComplexity;
    ProgressBar progressBar;
    Handler handler;
    Button buttonGenerate;
    String messageText;
    String progress;
    ArrayList<Double> numbers;
    ListView listView;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Main Activity");  //Set title
        numbers = new ArrayList<>();

        //Find the elements to modify
        seekBarComplexity = findViewById(R.id.seekBarComplexity);
        textViewComplexityTimes = findViewById(R.id.textViewComplexityTimes);
        buttonGenerate = findViewById(R.id.buttonGenerate);
        textViewAverage = findViewById(R.id.textViewAverage);
        textViewProgress = findViewById(R.id.textViewProgress);
        progressBar = findViewById(R.id.progressBarWorkDone);
        listView = findViewById(R.id.listViewWork);
        adapter = new ArrayAdapter(this, R.layout.work_layout, R.id.textViewNumber, numbers);
        listView.setAdapter(adapter);

        //Set initial state
        progress = Integer.valueOf(seekBarComplexity.getProgress()).toString();
        textViewComplexityTimes.setText(progress + " Times");

        //On Seekbar Update
        seekBarComplexity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = String.valueOf(seekBar.getProgress());   //Update progress value
                textViewComplexityTimes.setText(i + " Times");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Handler Setup
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.getData().containsKey("status")) {
                    messageText = message.getData().getString("status");    //Get the status
                    numbers.add(message.getData().getDouble("number")); //Get the number

                    Log.d("Handler", "handleMessage: " + messageText);

                    //Update Progresses
                    progressBar.setProgress(Integer.parseInt(messageText) + 1);
                    textViewProgress.setText(numbers.size() + " / " + progress);
                    double avg;
                    double sum = 0;
                    for (double number : numbers) {
                        sum = sum + number;
                    }
                    avg = sum / numbers.size();
                    textViewAverage.setText("Average: " + Double.toString(avg));
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        //On Generate Button Tap
        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExecutorService taskPool = Executors.newFixedThreadPool(2); //Thread Pool Declaration of Size 2
                taskPool.execute(new DoWork(Integer.parseInt(progress)));
                progressBar.setMax(Integer.parseInt(progress));
                progressBar.setVisibility(View.VISIBLE);
                textViewProgress.setText("0 / " + progress);
                textViewProgress.setVisibility(View.VISIBLE);
                textViewAverage.setVisibility(View.VISIBLE);
            }
        });

    }
    class DoWork implements Runnable {
        //Fields
        private ArrayList<Double> numbers;
        private int runTimes;

        //Constructor
        public DoWork(int timesToRun) {
            this.numbers = new ArrayList<>();
            this.runTimes = timesToRun;
        }

        //Run Method to do the work
        @Override
        public void run() {
            //Background work
            for (int i = 0; i < runTimes; i++) {
                this.numbers.add(HeavyWork.getNumber());
                sendMsg(String.valueOf(i), numbers.get(i));
                Log.d("Test", "run: " + numbers.get(i) + " " + i);
            }
        }

        private void sendMsg(String messageStr, double number) {
            Bundle bundle = new Bundle();
            bundle.putString("status", messageStr);
            bundle.putDouble("number", number);
            Message message = new Message();
            message.setData(bundle);
            handler.sendMessage((message));
        }
    }
}
