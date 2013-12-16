package ifmo.mobdev.rssreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class BroadcastReceiver3DB extends BroadcastReceiver {
    public static final String ACTION_MyBroadcastReceiver3DB = "startAlarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Cursor cursor = null;
        try {
            RSSDBAdapter mDbHelper = new RSSDBAdapter(context);
            mDbHelper.open();
            cursor = mDbHelper.fetchAllFeeds();
            int index = cursor.getColumnIndex(RSSDBAdapter.KEY_ROWID);
            while(cursor.moveToNext()) {
                long id = cursor.getLong(index);
                Intent intentMyIntentService = new Intent(context, MyIntentService.class);
                intentMyIntentService.putExtra("url", mDbHelper.getFeedURLByID(id));
                intentMyIntentService.putExtra("id", Long.toString(id));
                intentMyIntentService.putExtra("screen", "NO");
                context.startService(intentMyIntentService);
            }
        } catch (Exception e) {

        } finally {
            cursor.close();
        }
    }
}
