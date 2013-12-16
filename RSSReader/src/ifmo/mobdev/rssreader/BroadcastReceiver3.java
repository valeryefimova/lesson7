package ifmo.mobdev.rssreader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class BroadcastReceiver3 extends BroadcastReceiver {
    String url;
    ImageView pict;

    public BroadcastReceiver3() {
        super();
    }

    public BroadcastReceiver3(String url, ImageView p) {
        super();
        this.url = url;
        pict = p;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (pict != null) {
            pict.setVisibility(View.VISIBLE);
            pict.setImageResource(R.drawable.loading);
        }
        Bundle extras = intent.getExtras();
        url = extras.getString("url");
        Intent intentMyIntentService = new Intent(context, MyIntentService.class);
        intentMyIntentService.putExtra("url", url);
        context.startService(new Intent(context, MyIntentService.class).putExtra("url", url));
        Toast toast = Toast.makeText(context, "RELOAD", 500);
        toast.show();
    }
}
