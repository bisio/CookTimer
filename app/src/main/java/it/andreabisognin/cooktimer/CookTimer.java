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
import android.widget.Button;
import android.widget.TextView;

public class CookTimer extends ActionBarActivity {

    public final int COOK_TIME = 10;
    private TextView timerLabel;
    private Button button;
    private ICookServiceFunctions service = null;

    private ServiceConnection svcConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = (ICookServiceFunctions) binder;
            try {
                service.registerActivity(CookTimer.this,listener);
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

    private void updateTimer (long time) {
            timerLabel.setText(String.valueOf(time));
    }

    //private final String LOG_TAG = CookTimer.class.getCanonicalName();
    private final String LOG_TAG = "BISIO";
    private ICookListenerFunctions listener = new ICookListenerFunctions() {
        @Override
        public void setTimer(long time) {
            updateTimer(time);
            Log.i(LOG_TAG,"got from service: '" + time + "'");
        }

        @Override
        public void onFinish() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_timer);

        timerLabel = (TextView) findViewById(R.id.timer_label);
        button = (Button) findViewById(R.id.start_stop_button);
        Log.i(LOG_TAG,"before services");
        startService(new Intent(this,CookService.class));
        bindService(new Intent(this,CookService.class),svcConn,BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        service.unregisterActivity(this);
        unbindService(svcConn);
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
