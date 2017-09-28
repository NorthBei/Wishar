package com.pontus.wishar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.pontus.wishar.data.HotSpot;
import com.pontus.wishar.storage.AssetsStorage;
import com.pontus.wishar.view.adapter.HotSpotListAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String WIFI_DEF_JSON = "wifi_def.json";
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.fab) FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, DebugActivity.class));
            }
        });

        //The Ultimate JSON Library: JSON.simple vs GSON vs Jackson vs JSONP
        //http://blog.takipi.com/the-ultimate-json-library-json-simple-vs-gson-vs-jackson-vs-json/

        //How to Retrieve JSON Object from assets
        //https://www.youtube.com/watch?v=szO-OFCqF6k
        //or
        //Reading a JSON file from RAW in Android
        //https://www.youtube.com/watch?v=gnj-Df7QQHU&t=383s

        ArrayList<HotSpot> hotSpotList = parseGson();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST)); //設定分割線
        if (hotSpotList != null) {
            HotSpotListAdapter adapter = new HotSpotListAdapter(hotSpotList);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private ArrayList<HotSpot> parseGson() {
        Type type = new TypeToken<ArrayList<HotSpot>>() {}.getType();
        AssetsStorage as = new AssetsStorage(getBaseContext());
        ArrayList<HotSpot> list = as.readFileToJson(WIFI_DEF_JSON, type);

        return list;
    }
}
