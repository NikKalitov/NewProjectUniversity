package inm.project.pomodoro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Pomodoro extends AppCompatActivity {

    TextView tv_timer;
    Button btn_study, btn_relax, btn_long, btn_launch;
    int minutes, seconds, time_full, secs, check = 0;
    boolean click = false, reset = false;
    int phase = 1;

    Handler mainHandler = new Handler();

    volatile boolean stopThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        trash();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_study:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showAlertDialog(R.id.btn_study);
                                }
                            });
                        }
                    }).start();
                    break;
                case R.id.btn_relax:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showAlertDialog(R.id.btn_relax);
                                }
                            });
                        }
                    }).start();
                    break;
                case R.id.btn_long:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showAlertDialog(R.id.btn_long);
                                }
                            });
                        }
                    }).start();
                    break;
                case R.id.btn_launch:
                    if(!click){
                        startThreadNew();
                        click = true;
                        btn_launch.setText("стоп");
                    }
                    else{
                        stopThread();
                        click = false;
                        btn_launch.setText("запуск");
                    }
                    break;
            }
        }
    };

    public void trash(){
        tv_timer = findViewById(R.id.tv_timer);
        btn_study = findViewById(R.id.btn_study);
        btn_relax = findViewById(R.id.btn_relax);
        btn_long = findViewById(R.id.btn_long);
        btn_launch = findViewById(R.id.btn_launch);

        btn_study.setOnClickListener(onClickListener);
        btn_relax.setOnClickListener(onClickListener);
        btn_long.setOnClickListener(onClickListener);
        btn_launch.setOnClickListener(onClickListener);

        //minutes = 25;
        //seconds = 0;
        minutes = 0;
        seconds = 2;
        time_full = minutes * 60 + seconds;
        secs = time_full * 10;
    }

    public void startThreadNew() {
        if(reset){
            phase = 1;
        }
        stopThread = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < secs; i++) {
                    if(stopThread) {return;}
                    try {
                        check = check + 1;
                        Thread.sleep(100);
                        if(seconds / 10 == 0 && seconds != 0) {
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tv_timer.setText(minutes + ":0" + seconds);
                                }
                            });
                        }
                        else if(seconds == 0){
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tv_timer.setText(minutes + ":00");
                                }
                            });
                        }
                        else{
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tv_timer.setText(minutes + ":" + seconds);
                                }
                            });
                        }
                        //secs = secs - 1;
                        if(check % 10 == 0) {
                            time_full = time_full - 1;
                            minutes = time_full / 60;
                            seconds = time_full - minutes * 60;
                        }
                        if(minutes == 0 && seconds == 0){
                            phase = phase + 1;
                            phaseChoose(phase);
                            stopThread();
                            click = false;
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    btn_launch.setText("запуск");
                                }
                            });
                            reset = false;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopThread() {
        stopThread = true;
    }

    public void phaseChoose(int a){
        if(a % 2 != 0){
            //minutes = 25; seconds = 0;
            minutes = 0; seconds = 2;
        }
        if(a % 2 == 0 && a % 4 != 0){
            //minutes = 5; seconds = 0;
            minutes = 0; seconds = 3;
        }
        if(a % 8 == 0){
            //minutes = 15; seconds = 0;
            minutes = 0; seconds = 4;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                //tv_timer.setText(minutes + ":00");
                tv_timer.setText("0:" + seconds);
            }
        });
        time_full = minutes * 60 + seconds;
        secs = time_full * 10;
    }

    public void showAlertDialog(int id){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Прогресс");
        alert.setMessage("Вы точно хотите сбросить свой прогресс?\t " +
                "Дождитесь окончания таймера.");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetProgress(id);
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create().show();
    }

    public void resetProgress(int id){
        stopThread();
        switch(id){
            case R.id.btn_study:
                //minutes = 25; seconds = 0;
                minutes = 0; seconds = 2;
                break;
            case R.id.btn_relax:
                //minutes = 5; seconds = 0;
                minutes = 0; seconds = 3;
                reset = true;
                break;
            case R.id.btn_long:
                //minutes = 15; seconds = 0;
                minutes = 0; seconds = 4;
                reset = true;
                break;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                //tv_timer.setText(minutes + ":00");
                tv_timer.setText("0:" + seconds);
            }
        });
        time_full = minutes * 60 + seconds;
        secs = time_full * 10;
        click = false;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                btn_launch.setText("запуск");
            }
        });
    }
}