package com.evasler.clientapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ArrayList<TextView> feedbackTexts;
    Button getResultsBt, openMapBt, settingsBt, newCheckinBt;
    EditText minLatET, maxLatET, minLongET, maxLongET, minDurationET, maxDurationET, amountOfTopResultsET;
    SharedPreferences mapperPrefs, generalPrefs;
    TextView reducerFeedback;
    LinearLayout feedbackLayout;
    LinearLayout.LayoutParams textParams;
    boolean reducerDone;
    boolean mapperDone[];
    int amountOfMappers;
    public static List<UUID> myIds;
    holderClass holder = new holderClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myIds =new ArrayList<UUID>();

        mapperPrefs = getSharedPreferences("mapperIPs", MODE_PRIVATE);
        generalPrefs = getSharedPreferences("preferences", MODE_PRIVATE);

        feedbackTexts = new ArrayList<>();

        amountOfMappers = generalPrefs.getInt("amountOfMappers", 0);

        mapperDone = new boolean[amountOfMappers];

        for (int i = 0; i < amountOfMappers; i++) {

            mapperDone[i] = false;
        }
        reducerDone = false;


        reducerFeedback = (TextView) findViewById(R.id.reducerFeedback);

        feedbackLayout = (LinearLayout) findViewById(R.id.feedbackLayout);
        textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.gravity = Gravity.CENTER_HORIZONTAL;

        updateFeedbackLayout();

        minLatET = (EditText) findViewById(R.id.minLat);
        maxLatET = (EditText) findViewById(R.id.maxLat);
        minLongET = (EditText) findViewById(R.id.minLong);
        maxLongET = (EditText) findViewById(R.id.maxLong);
        minDurationET = (EditText) findViewById(R.id.minDuration);
        maxDurationET = (EditText) findViewById(R.id.maxDuration);
        amountOfTopResultsET = (EditText) findViewById(R.id.amountOfTopResults);

        getResultsBt = (Button) findViewById(R.id.getResults);
        openMapBt = (Button) findViewById(R.id.openMap);
        settingsBt = (Button) findViewById(R.id.settingsBt);
        newCheckinBt= (Button) findViewById(R.id.insertBt);
        newCheckinBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i= new Intent(MainActivity.this, CheckinNew.class);
                startActivity(i);
            }
        });
        getResultsBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                getResults();
            }
        });

        settingsBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openSettings();
            }
        });
        openMapBt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                openMap();
            }
        });


    }
    protected void openMap(){
        Intent i = new Intent(this, MapsActivity.class);
        i.putExtra("holderClass",holder);
        startActivity(i);
    }
    @Override
    protected void onResume() {

        super.onResume();
        clearFeedbackLayout();
        updateFeedbackLayout();
    }

    class mapParams {

        String IP;
        int port;
        int ID;
        ClientRequest cr;

        mapParams(String IP, int port, int ID, ClientRequest cr) {

            this.IP = IP;
            this.port = port;
            this.ID = ID;
            this.cr = cr;
        }
    }

    class reduceParams {

        String IP;
        int port;
        int kTop;
        List<AsyncTask> mapperTasks;

        reduceParams(String IP, int port, int kTop, List<AsyncTask> mapperTasks) {

            this.IP = IP;
            this.port = port;
            this.kTop = kTop;
            this.mapperTasks = mapperTasks;
        }
    }

    private void openSettings() {

        Intent i = new Intent(this, settings.class);
        startActivity(i);
    }

    private void updateFeedbackLayout() {

        amountOfMappers = generalPrefs.getInt("amountOfMappers", 0);

        for (int i = 0; i < amountOfMappers ; i++) {

            feedbackTexts.add(new TextView(this));
            feedbackTexts.get(i).setText(String.format("Mapper %d: Waiting...", i + 1));
            feedbackTexts.get(i).setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9.6f, getResources().getDisplayMetrics()));

            assert feedbackLayout != null;
            feedbackLayout.addView(feedbackTexts.get(i), textParams);
        }
    }

    private void clearFeedbackLayout() {

        feedbackLayout.removeAllViews();
        feedbackTexts.clear();
    }

    private void getResults() {
        openMapBt.getBackground().clearColorFilter();
        amountOfMappers = generalPrefs.getInt("amountOfMappers", 1);
        myIds.clear();
        float minLatitude = Float.parseFloat(minLatET.getText().toString());
        float maxLatitude = Float.parseFloat(maxLatET.getText().toString());
        float minLongtitude = Float.parseFloat(minLongET.getText().toString());
        float maxLongtitude = Float.parseFloat(maxLongET.getText().toString());
        String dateFrom = minDurationET.getText().toString();
        String dateTo = maxDurationET.getText().toString();


        float division = Math.abs(maxLatitude - minLatitude) / (float) amountOfMappers;
        mapParams[] mapParameters = new mapParams[amountOfMappers];

        String IP;
        int port;
        int ID;

        List<AsyncTask> mapperTasks = Collections.synchronizedList(new ArrayList<AsyncTask>());
        for(int i=0;i<amountOfMappers;i++){

            IP = mapperPrefs.getString("mapperIp" + i, null);
            port = Integer.parseInt(mapperPrefs.getString("mapperPort" + i, null));
            ID = i;

            ClientRequest cr = new ClientRequest(minLongtitude, minLatitude + i*division, maxLongtitude, maxLatitude - (amountOfMappers - i - 1) * division, dateFrom, dateTo);

            Log.d("Debug", IP);
            Log.d("Debug", String.valueOf(port));
            Log.d("Debug", String.valueOf(ID));

            mapParameters[i] = new mapParams(IP, port, ID, cr);
            initSocketMap initiateMap = new initSocketMap();
            mapperTasks.add(initiateMap.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mapParameters[i]));
        }

        IP = generalPrefs.getString("reducerIp", null);
        port = 3000;
        int kTop = Integer.parseInt(amountOfTopResultsET.getText().toString());

        reduceParams reduceParameters = new reduceParams(IP, port, kTop, mapperTasks);

        initSocketReduce initiateReduce = new initSocketReduce();
       // initiateReduce.execute(reduceParameters);
        initiateReduce.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,reduceParameters);
    }

    class initSocketMap extends AsyncTask<mapParams, String, String> {

        int ID;

        @Override
        protected String doInBackground(mapParams... params) {

            String IP = params[0].IP;
            int port = params[0].port;
            ID = params[0].ID;
            ClientRequest cr = params[0].cr;

            Socket socket1 = null;
            ObjectInputStream in = null;
            ObjectOutputStream out = null;

            try {

                socket1 = new Socket(IP, port);

                out = new ObjectOutputStream(socket1.getOutputStream());
                out.writeObject(cr);
                in = new ObjectInputStream(socket1.getInputStream());
                myIds.add((UUID) in.readObject());
            }
            catch(IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket1.close();
                } catch (IOException e)	{
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            feedbackTexts.get(ID).setText(String.format("Mapper %d: Done!", ID));
            mapperDone[ID] = false;
            openMapBt.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }

        //most likely won't need to write this unless we want to do something relevant to the UI
        //buffering icon
        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
            feedbackTexts.get(ID).setText(String.format("Mapper %d: Processing...", ID));
        }
    }

    class initSocketReduce extends AsyncTask<reduceParams, String, Set<ClientResult>> {

        @Override
        protected  void onPreExecute() {


        }

        @Override
        protected Set<ClientResult> doInBackground(reduceParams... params) {

            String IP = params[0].IP;
            int port = params[0].port;
            int kTop = params[0].kTop;

            for (AsyncTask mapperTask : params[0].mapperTasks) {
                try {
                    mapperTask.get();// this blocks the reducer until all mappers are done
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            Socket socket=null;
            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            Set<ClientResult> reduced = null;

            try {

                socket = new Socket(IP, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                out.writeObject(myIds);
                out.writeObject(new Integer(kTop));
                reduced = (Set<ClientResult>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.getOutputStream().flush();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return reduced;
        }

        @Override
        protected void onPostExecute(Set<ClientResult> s) {

            super.onPostExecute(s);
            holder.setTopResults(s);

            reducerDone = true;
            reducerFeedback.setText("Reducer: Done!");
        }

        //most likely won't need to write this unless we want to do something relevant to the UI
        //buffering icon
        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
            reducerFeedback.setText("Reducer: Processing...");
        }
    }
}
