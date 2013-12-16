package ifmo.mobdev.rssreader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyActivity1 extends Activity{

    ImageButton show;
    ImageView title;
    EditText edtxt;
    String url;
    public ArrayList<String> feeds;
    BroadcastReceiver1 myBroadcastReceiver;

    public void addFeedsView() {
        ListView lv = (ListView) findViewById(R.id.listViewW1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.w1_list_item, feeds);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                url = ((TextView) view.findViewById(R.id.w1TxtView)).getText().toString();
                try {
                    URL Url = new URL(url);

                    Intent intent = new Intent(MyActivity1.this, MyActivity2.class);
                    intent.putExtra("url", url);

                    startActivity(intent);

                } catch (MalformedURLException e) {
                    Toast toast = Toast.makeText(MyActivity1.this, "Wrong URL!", 3000);
                    toast.show();
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.w1);

        show = (ImageButton) findViewById(R.id.show);
        edtxt = (EditText) findViewById(R.id.editText);
        title = (ImageView) findViewById(R.id.imgViewTitle);

        feeds = new ArrayList<String>();
        feeds.add("http://bash.im/rss");
        feeds.add("http://lenta.ru/rss");
        feeds.add("http://stackoverflow.com/feeds");
        addFeedsView();

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.title_anim);
        title.startAnimation(animation);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = edtxt.getText().toString();

                addFeedsView();
                try {
                    URL Url = new URL(url);

                    Intent intent = new Intent(MyActivity1.this, MyActivity2.class);
                    intent.putExtra("url", url);

                    startActivity(intent);

                } catch (MalformedURLException e) {
                    Toast toast = Toast.makeText(MyActivity1.this, "Wrong URL!", 3000);
                    toast.show();
                }
            }
        });

        myBroadcastReceiver = new BroadcastReceiver1();

        IntentFilter intentFilter = new IntentFilter(MyActivity2.RSS_LOADED);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
       unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
    }

    public class BroadcastReceiver1 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (feeds.indexOf(url) == -1) {
                 feeds.add(url);
                 addFeedsView();
            }
        }
    }
}
