package ifmo.mobdev.rssreader;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.*;

public class MyActivity2DB extends Activity {
    public static final String TITLE = "title";
    public static final String LINK = "link";
    public static final String DESCR = "description";
    public static final String DATE = "pubDate";
    public static final String RSS_LOADED = "rssloaded";
    ImageView pict;
    ImageButton update;
    TextView feed_name;
    BroadcastReceiver2DB myBroadcastReceiver2DB;
    String url;
    long feed_id;
    private RSSDBAdapter mDbHelper;
    Cursor artCursor;
    ListView lv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.w2);
        initialise();

        int titleind = artCursor.getColumnIndex(RSSDBAdapter.KEY_TITLE);
        try {
            artCursor.moveToNext();
            String t = artCursor.getString(titleind);
        } catch (Exception e) {
            pict.setVisibility(View.VISIBLE);
            Intent intentMyIntentService = new Intent(MyActivity2DB.this, MyIntentService.class);
            intentMyIntentService.putExtra("url", url);
            intentMyIntentService.putExtra("id", Long.toString(feed_id));
            intentMyIntentService.putExtra("screen", "YES");
            startService(intentMyIntentService);
        }

        myBroadcastReceiver2DB = new BroadcastReceiver2DB(pict, lv);
        IntentFilter intentFilter2 = new IntentFilter(BroadcastReceiver2DB.ACTION_MYBROADCASTRECEIVER2);
        intentFilter2.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver2DB, intentFilter2);
    }

    private void initialise() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        url = extras.getString("url");
        pict = (ImageView) findViewById(R.id.imageView);
        feed_name = (TextView) findViewById(R.id.w2_feed);
        update = (ImageButton) findViewById(R.id.reloadBut);
        pict.setVisibility(View.INVISIBLE);
        mDbHelper = new RSSDBAdapter(this);
        mDbHelper.open();

        feed_id = mDbHelper.getFeedIdByURL(url);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pict.setVisibility(View.VISIBLE);
                Intent intentMyIntentService = new Intent(MyActivity2DB.this, MyIntentService.class);
                intentMyIntentService.putExtra("id", Long.toString(feed_id));
                intentMyIntentService.putExtra("url", url);
                intentMyIntentService.putExtra("screen", "YES");
                startService(intentMyIntentService);
            }
        });
        //Intent intentMyIntentService = new Intent(this, MyIntentService.class);
        //intentMyIntentService.putExtra("id", Integer.toString(feed_id));
        //startService(intentMyIntentService.putExtra("url", url));

        feed_name.setText(mDbHelper.getFeedNameByID(feed_id));

        lv = (ListView) findViewById(R.id.listView);
        artCursor = mDbHelper.fetchOneChannel(feed_id);
        startManagingCursor(artCursor);
        String[] from = new String[]{RSSDBAdapter.KEY_TITLE, RSSDBAdapter.KEY_LINK, RSSDBAdapter.KEY_DATE};

        int[] to = new int[]{R.id.title, R.id.link, R.id.date};

        SimpleCursorAdapter artAdapter = new SimpleCursorAdapter(this, R.layout.list_item_w2, artCursor, from, to);
        lv.setAdapter(artAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView titleView = (TextView) view.findViewById(R.id.title);
                TextView linkView =  (TextView) view.findViewById(R.id.link);
                TextView dateView = (TextView) view.findViewById(R.id.date);
                String title = titleView.getText().toString();
                String link = linkView.getText().toString();
                //String descr = ((TextView) view.findViewById(R.id.descr)).getText().toString();
                String date = (dateView.getText().toString());

                Intent intent = new Intent(MyActivity2DB.this, MyActivity3DB.class);
                intent.putExtra("title", title);
                intent.putExtra("link", link);
                //intent.putExtra("descr", descr);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myBroadcastReceiver2DB);
        super.onDestroy();
    }


}

