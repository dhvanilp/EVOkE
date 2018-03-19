package com.example.dhp.codefundo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;

public class MainActivity extends AppCompatActivity {

    static final String SERVER_HOST = "https://southeastasia.api.cognitive.microsoft.com/face/v1.0";
    static final String SUBSCRIPTION_KEY = "eb5c5e259ead4741b0e2792b17fbc98c";
    private static FaceServiceClient faceServiceClient;
    ListView simpleList;
    private String[] a, b;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        boolean state = isNetworkAvailable();
        if (state) {
            faceServiceClient = new FaceServiceRestClient(SERVER_HOST, SUBSCRIPTION_KEY);
            // specify an adapter (see also next example)

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build();
            StrictMode.setThreadPolicy(policy);

            try {
                com.microsoft.projectoxford.face.contract.PersonGroup[] all_person_groups = faceServiceClient.getPersonGroups();

                a = new String[all_person_groups.length];
                b = new String[all_person_groups.length];
                int i = 0;
                for (com.microsoft.projectoxford.face.contract.PersonGroup p : all_person_groups) {
                    a[i] = (i + 1) + ".)" + p.personGroupId;
                    b[i] = p.personGroupId;
                    Log.d("names:", a[i]);
                    i++;
                }

                simpleList = findViewById(R.id.simpleListView);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview, R.id.textView, a);
                simpleList.setAdapter(arrayAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }

            simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    Intent intent = new Intent(getApplicationContext(), PersonGroup.class);
                    String msg = b[position];
                    Log.d("passedstring", msg);
                    intent.putExtra("groupId", msg);
                    startActivity(intent);
                }
            });

            final Button createperson = (Button) findViewById(R.id.createPersongroup);
            createperson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), CreateGroup.class);
                    startActivity(i);
                }
            });
        } else {
            new android.app.AlertDialog.Builder(this).setTitle("No internet")
                    .setMessage("Check your internet connection and try again!")
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}
