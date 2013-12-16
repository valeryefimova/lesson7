package ifmo.mobdev.rssreader;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.*;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.CalendarContract;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyActivity1DB extends Activity implements TextView.OnEditorActionListener{

    private static final int DROP_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int RENAME_ID = Menu.FIRST + 2;
    private static final int ACTIVITY_CREATE = 0;
    private static final long TWENTY_MINUTES = 1000 * 60 * 15;
    ImageButton show;
    ImageView title;
    EditText edtxt;
    String url;
    String name;
    public ArrayList<String> feeds;
    private RSSDBAdapter mDbHelper;
    ListView lv;
    BroadcastReceiver3DB myBroadcastReceiver3DB;
    static AlarmManager am;
    PendingIntent pi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.w1);

        mDbHelper = new RSSDBAdapter(this);
        mDbHelper.open();
        //mDbHelper.drop();

        mDbHelper.createFeed("http://bash.im/rss", "Bash");
        mDbHelper.createFeed("http://lenta.ru/rss", "Lenta");
        mDbHelper.createFeed("http://stackoverflow.com/feeds", "StackOverflow");
        mDbHelper.createFeed("http://habrahabr.ru/rss/hubs/", "Habr");
        mDbHelper.createFeed("http://vesti.ru/vesti.rss", "Vesti");
        mDbHelper.createFeed("http://st.kinopoisk.ru/rss/news.rss", "Kinopoisk");

        initialiseViews();
        addDBFeedsView();
        registerForContextMenu(lv);
        receiverAndAlarm();
    }

    private void receiverAndAlarm() {
        myBroadcastReceiver3DB = new BroadcastReceiver3DB();
        IntentFilter intentFilter3 = new IntentFilter(BroadcastReceiver3DB.ACTION_MyBroadcastReceiver3DB);
        intentFilter3.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver3DB, intentFilter3);

        Intent in = new Intent(BroadcastReceiver3DB.ACTION_MyBroadcastReceiver3DB);
        //in.putExtra("url", url);
        pi = PendingIntent.getBroadcast(MyActivity1DB.this, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        am.cancel(pi);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + TWENTY_MINUTES * 3, TWENTY_MINUTES * 3, pi);
    }

    private void initialiseViews() {
        show = (ImageButton) findViewById(R.id.show);
        edtxt = (EditText) findViewById(R.id.editText);
        title = (ImageView) findViewById(R.id.imgViewTitle);
        lv = (ListView) findViewById(R.id.listViewW1);

        edtxt.setOnEditorActionListener(this);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.title_anim);
        title.startAnimation(animation);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = edtxt.getText().toString();
                int pr = url.indexOf(' ');
                if (pr == -1) {
                    Toast toast = Toast.makeText(MyActivity1DB.this, "Wrong format!", 3000);
                    toast.show();
                }   else {
                    name = url.substring(0, pr);
                    url = url.substring(pr + 1);
                    url = url.trim();
                }

                try {
                    URL Url = new URL(url);

                    if ((mDbHelper.getFeedIdByName(name) == -1) && (mDbHelper.getFeedIdByURL(url) == -1)) {
                        mDbHelper.createFeed(url, name);
                    }

                    addDBFeedsView();

                    Intent intent = new Intent(MyActivity1DB.this, MyActivity2DB.class);
                    intent.putExtra("url", url);
                    intent.putExtra("name", name);

                    startActivity(intent);

                } catch (MalformedURLException e) {
                    Toast toast = Toast.makeText(MyActivity1DB.this, "Wrong URL!", 3000);
                    toast.show();
                }
            }
        });
    }

    private void addDBFeedsView() {
        Cursor feedsCursor = mDbHelper.fetchAllFeeds();
        startManagingCursor(feedsCursor);

        String[] from = new String[]{RSSDBAdapter.KEY_NAME, RSSDBAdapter.KEY_URL};

        int[] to = new int[]{R.id.tw1, R.id.tw2};

        SimpleCursorAdapter feedsAdapter = new SimpleCursorAdapter(this, R.layout.list_item_w1, feedsCursor, from, to);
        lv.setAdapter(feedsAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String lstname = ((TextView) view.findViewById(R.id.tw1)).getText().toString();
                String lsturl = ((TextView) view.findViewById(R.id.tw2)).getText().toString();
                try {
                    URL Url = new URL(lsturl);

                    Intent intent = new Intent(MyActivity1DB.this, MyActivity2DB.class);
                    intent.putExtra("url", lsturl);
                    intent.putExtra("name", lstname);

                    startActivity(intent);

                } catch (MalformedURLException e) {
                    Toast toast = Toast.makeText(MyActivity1DB.this, "Wrong URL!", 3000);
                    toast.show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(myBroadcastReceiver3DB);
        super.onDestroy();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        menu.add(0, RENAME_ID, 0, R.string.menu_rename);
        menu.add(0, DROP_ID, 0, R.string.menu_drop);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case DELETE_ID:
                mDbHelper.deleteFeed(info.id);
                addDBFeedsView();
                return true;
            case RENAME_ID:
                renameFeed(info.id);
                return true;
            case DROP_ID:
                dropFeeds();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    public void renameFeed(long id) {
        Intent i = new Intent(this, FeedEdit.class);
        i.putExtra("feed_id", Long.toString(id));
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    public void dropFeeds() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.menu_sure);
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDbHelper.drop();
                addDBFeedsView();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        addDBFeedsView();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            url = edtxt.getText().toString();
            int pr = url.indexOf(' ');
            if (pr == -1) {
                Toast toast = Toast.makeText(MyActivity1DB.this, "Wrong format!", 3000);
                toast.show();
            }   else {
                name = url.substring(0, pr);
                url = url.substring(pr + 1);
                url = url.trim();
            }

            try {
                URL Url = new URL(url);

                if ((mDbHelper.getFeedIdByName(name) == -1) && (mDbHelper.getFeedIdByURL(url) == -1)) {
                    mDbHelper.createFeed(url, name);
                }

                addDBFeedsView();

                Intent intent = new Intent(MyActivity1DB.this, MyActivity2DB.class);
                intent.putExtra("url", url);
                intent.putExtra("name", name);

                startActivity(intent);

            } catch (MalformedURLException e) {
                Toast toast = Toast.makeText(MyActivity1DB.this, "Wrong URL!", 3000);
                toast.show();
            }
            return true;
        }
        return false;
    }

}

