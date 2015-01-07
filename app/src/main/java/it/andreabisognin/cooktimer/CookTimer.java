package it.andreabisognin.cooktimer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CookTimer extends ActionBarActivity {

    private TextView timerLabel;
    private Button button;
    private Button incTimeButton;
    private Button decTimeButton;
    private Button resetButton;
    private ICookServiceFunctions service = null;
    private final String TIMER_RUNNING="timer_running";
    private final String COOK_TIME = "cook_time";
    private boolean timerRunning = false;
    private final String LOG_TAG = "BISIO";
    private final long TIME_STEP = 10;
    private long cookTime=0;
    private long lastSetTime=0;


    private ServiceConnection svcConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = (ICookServiceFunctions) binder;
            try {
                service.registerActivity(CookTimer.this,listener);
                initUIHandlers();
                Log.i(LOG_TAG,"Service Bound!");
            } catch (Throwable t) {
                Log.e(LOG_TAG,"Could not bind service");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };

    private void initUIHandlers() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    stopTimer();
                    } else {
                    if (cookTime == 0)
                        return;
                    startTimer();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTimer(lastSetTime);
            }
        });

        incTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookTime += TIME_STEP;
                updateTimer(cookTime);
            }
        });

        decTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookTime = (cookTime - TIME_STEP) > 0? cookTime -TIME_STEP : 0;
                updateTimer(cookTime);
            }
        });

        timerLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG,"clicked timerLabel!");
                service.stopAlarm();
            }
        });

    }

    private void startTimer() {
        Log.i(LOG_TAG, "starting the timer in service");
        service.startTimer(cookTime);
        timerRunning = true;
        incTimeButton.setEnabled(false);
        decTimeButton.setEnabled(false);
        button.setText(getString(R.string.stop));
        resetButton.setEnabled(false);
    }

    private void stopTimer() {
        Log.i(LOG_TAG, "stopping the timer in service");
        service.stopTimer();
        timerRunning = false;
        button.setText(getString(R.string.start));
        incTimeButton.setEnabled(true);
        decTimeButton.setEnabled(true);
        resetButton.setEnabled(true);
    }

    private void updateTimer (long time) {
            cookTime = time;
            timerLabel.setText(Utility.secondsToPrettyTime(time));
    }

    //private final String LOG_TAG = CookTimer.class.getCanonicalName();

    private ICookListenerFunctions listener = new ICookListenerFunctions() {
        @Override
        public void setTimer(long time) {
            updateTimer(time);
        }

        @Override
        public void onFinish() {
            cookTime = 0;
            timerRunning=false;
            timerLabel.setText(getString(R.string.done));
            button.setText(getString(R.string.start));
            incTimeButton.setEnabled(true);
            decTimeButton.setEnabled(true);
            resetButton.setEnabled(true);
            service.startAlarm();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_timer);

        timerLabel = (TextView) findViewById(R.id.timer_label);
        if (savedInstanceState == null)
            timerLabel.setText(Utility.secondsToPrettyTime(cookTime));
        button = (Button) findViewById(R.id.start_stop_button);
        resetButton = (Button) findViewById(R.id.reset_button);

        incTimeButton = (Button) findViewById(R.id.increase_time_button);
        decTimeButton = (Button) findViewById(R.id.decrease_time_button);
        if (timerRunning) {
            button.setText(getString(R.string.stop));
        } else {
            button.setText(getString(R.string.start));
        }
        startService(new Intent(this, CookService.class));
        bindService(new Intent(this,CookService.class),svcConn,BIND_AUTO_CREATE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        timerRunning = savedInstanceState.getBoolean(TIMER_RUNNING);
        cookTime = savedInstanceState.getLong(COOK_TIME);
        if (timerRunning) {
            button.setText(R.string.stop);
            incTimeButton.setEnabled(false);
            decTimeButton.setEnabled(false);
        }
        updateTimer(cookTime);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TIMER_RUNNING,timerRunning);
        outState.putLong(COOK_TIME,cookTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.unregisterActivity(this);
        unbindService(svcConn);
        Log.i(LOG_TAG,"Service unbound");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cook_timer, menu);
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
