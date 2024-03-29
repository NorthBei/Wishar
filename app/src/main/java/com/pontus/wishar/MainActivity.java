package com.pontus.wishar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pontus.wishar.data.DescCorr;
import com.pontus.wishar.data.HotSpotListBuilder;
import com.pontus.wishar.storage.db.WisharDB;
import com.pontus.wishar.view.adapter.HotSpotListAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        //The Ultimate JSON Library: JSON.simple vs GSON vs Jackson vs JSONP
        //http://blog.takipi.com/the-ultimate-json-library-json-simple-vs-gson-vs-jackson-vs-json/

        //How to Retrieve JSON Object from assets
        //https://www.youtube.com/watch?v=szO-OFCqF6k
        //or
        //Reading a JSON file from RAW in Android
        //https://www.youtube.com/watch?v=gnj-Df7QQHU&t=383s

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST)); //設定分割線

        Context context = getBaseContext();
        List<DescCorr> corrList = WisharDB.getDB(context).descCorrespDao().getDescCorrList();
        HotSpotListBuilder status = new HotSpotListBuilder(context, corrList);
        HotSpotListAdapter adapter = new HotSpotListAdapter(status);
        recyclerView.setAdapter(adapter);
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

        if (id == R.id.wifi_map) {
            startActivity(new Intent(this,MapsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
