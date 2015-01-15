package it.andreabisognin.cooktimer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    private  long timeStep = 10;
    private long cookTime=0;
    private long lastSetTime=0;
    private boolean newInstance = false;


    private ServiceConnection svcConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = (ICookServiceFunctions) binder;
            try {
                service.registerActivity(CookTimer.this,listener);
                initUIHandlers();
                initUI();
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

    private void initUI(){
        if (!service.isTimerRunning() && newInstance) {
            cookTime = lastSetTime;
            updateTimer(cookTime);
        }
        if (service.isTimerRunning()) {
            timerRunning = true;
            setUITimerStarted();
        }
    }

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
                service.stopAlarm();
                service.resetAlarm();
            }
        });

        incTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookTime += timeStep;
                updateTimer(cookTime);
                lastSetTime = cookTime;
            }
        });

        decTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookTime = (cookTime - timeStep) > 0? cookTime - timeStep : 0;
                updateTimer(cookTime);
                lastSetTime = cookTime;
            }
        });

        timerLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i(LOG_TAG,"clicked timerLabel!");
                service.stopAlarm();
            }
        });

    }

    private void startTimer() {
        Log.i(LOG_TAG, "starting the timer in service");
        service.startTimer(cookTime);
        timerRunning = true;
        setUITimerStarted();
    }

    private void setUITimerStarted() {
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
            //service.startAlarm();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_timer);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        lastSetTime = preferences.getLong(getString(R.string.saved_last_set_time),0);


        timerLabel = (TextView) findViewById(R.id.timer_label);



        button = (Button) findViewById(R.id.start_stop_button);
        resetButton = (Button) findViewById(R.id.reset_button);

        incTimeButton = (Button) findViewById(R.id.increase_time_button);
        decTimeButton = (Button) findViewById(R.id.decrease_time_button);

        startService(new Intent(this, CookService.class));
        bindService(new Intent(this, CookService.class), svcConn, BIND_AUTO_CREATE);


        if (savedInstanceState == null) {
            newInstance = true;
        }

        if (timerRunning)
            button.setText(R.string.stop);
        else
            button.setText(R.string.start);

        bindNumPadButtons();
/*
        TextView one = (TextView) findViewById(R.id.button1);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookTime = Long.valueOf(((TextView) v).getText().toString());
                updateTimer(cookTime);
            }
        });
*/

    }


    private static void applyListener(View child, View.OnClickListener listener) {
        if (child == null)
            return;

        if (child instanceof ViewGroup) {
            applyListener((ViewGroup) child, listener);
        }
        else if (child != null) {
               if(child instanceof TextView) {
                   child.setOnClickListener(listener);
               }
         }
    }

    private static void applyListener(ViewGroup parent, View.OnClickListener listener) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup) {
                applyListener((ViewGroup) child, listener);
            } else {
                applyListener(child, listener);
            }
        }
    }

    private void bindNumPadButtons() {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookTime = Long.valueOf(((TextView) v).getText().toString());
                updateTimer(cookTime);
            }
        };
        View numpad = findViewById(R.id.numpad);

        applyListener(numpad,listener);
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
        outState.putLong(COOK_TIME, cookTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.unregisterActivity(this);
        unbindService(svcConn);
        Log.i(LOG_TAG, "Service unbound");
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(getString(R.string.saved_last_set_time), lastSetTime);
        editor.commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        timeStep  = Long.valueOf(sp.getString(getString(R.string.pref_timestep_key),"10"));
        Log.i(LOG_TAG,"timeStep is "+timeStep);
        if (service.isTimerRunning()) {
            Log.i(LOG_TAG, "In restart The timer is running");
            setUITimerStarted();
        }
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
            startActivity(new Intent(this,CookPrefs.class));
        }

        if (id == R.id.action_kill_service) {
            if (service != null) {
                stopService(new Intent(this,CookService.class));
            }

        }


        return super.onOptionsItemSelected(item);
    }
}
