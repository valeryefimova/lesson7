package ifmo.mobdev.rssreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;

public class BroadcastReceiver2DB extends BroadcastReceiver {
    ImageView pict;
    ListView lv;
    public static final String ACTION_MYBROADCASTRECEIVER2 = "ifmo.mobdev.rssreader.broadcastreceiver2.RESPONSE";
    //private ArrayList<HashMap<String, String>> items;
    private RSSDBAdapter mDbHelper;

    public BroadcastReceiver2DB() {
        super();
    }

    public BroadcastReceiver2DB(ImageView p, ListView l) {
        pict = p;
        lv = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mDbHelper = new RSSDBAdapter(context);
        mDbHelper.open();
        String xml = intent.getStringExtra("xml");
        String items = intent.getStringExtra("items");
        String screen = intent.getStringExtra("screen");
        long feed_id = Long.parseLong(intent.getStringExtra("id"));
        if (xml.equals(MyIntentService.RSS_LOAD_OK) && screen.equals("YES")) {
            if (items.equals(MyIntentService.ITEMS_PARSE_BAD)) {
                pict.setImageResource(R.drawable.wrongurl);
                Toast toast = Toast.makeText(context, "Wrong URL", 3000);
                toast.show();
            } else if (items.equals(MyIntentService.ITEMS_PARSE_OK)) {
                pict.setVisibility(View.INVISIBLE);

                //show loaded information
                show(context, feed_id);

                Toast toast = Toast.makeText(context, "RSS loaded", 3000);
                toast.show();
                Intent intentResponse = new Intent();
                intentResponse.setAction(MyActivity2DB.RSS_LOADED);
                intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                intentResponse.putExtra("xml", xml);
                context.sendBroadcast(intentResponse);
            }
        } else if (xml.equals(MyIntentService.RSS_LOAD_BAD) && screen.equals("YES")) {
            pict.setVisibility(View.INVISIBLE);
            show(context, feed_id);
            //pict.setImageResource(R.drawable.wrongurl);
            Toast toast = Toast.makeText(context, "Wrong URL or not connection", 3000);
            toast.show();
        }
    }

    private void show(Context context, long feed_id) {
        Cursor artCursor = mDbHelper.fetchOneChannel(feed_id);
        String[] from = new String[]{RSSDBAdapter.KEY_TITLE, RSSDBAdapter.KEY_LINK, RSSDBAdapter.KEY_DATE};

        int[] to = new int[]{R.id.title, R.id.link, R.id.date};

        SimpleCursorAdapter artAdapter = new SimpleCursorAdapter(context, R.layout.list_item_w2, artCursor, from, to);
        lv.setAdapter(artAdapter);
    }

}

