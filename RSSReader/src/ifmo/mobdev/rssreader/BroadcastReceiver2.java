package ifmo.mobdev.rssreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;

public class BroadcastReceiver2 extends BroadcastReceiver {
    ImageView pict;
    ListView lv;
    private ArrayList<HashMap<String, String>> items;

    public BroadcastReceiver2() {
        super();
    }

    public BroadcastReceiver2(ImageView p, ListView l) {
        pict = p;
        lv = l;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String xml = intent.getStringExtra("xml");
        if (xml != null) {
            SAXXMLParser parser = new SAXXMLParser(xml);
            items = parser.parse();

            if (items == null) {
                pict.setImageResource(R.drawable.wrongurl);
                Toast toast = Toast.makeText(context, "Wrong URL", 3000);
                toast.show();
            } else {
                pict.setVisibility(View.INVISIBLE);

                //----  AddToListView()
                ListAdapter adapter = new SimpleAdapter(context, items, R.layout.list_item,
                        new String[] {MyActivity2.TITLE, MyActivity2.LINK, MyActivity2.DESCR, MyActivity2.DATE},
                        new int [] {R.id.title, R.id.link, R.id.descr, R.id.date});
                lv.setAdapter(adapter);
                lv.deferNotifyDataSetChanged();
                //----


                Intent intentResponse = new Intent();
                intentResponse.setAction(MyActivity2.RSS_LOADED);
                intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
                intentResponse.putExtra("xml", xml);
                context.sendBroadcast(intentResponse);
            }
        } else {
            pict.setImageResource(R.drawable.wrongurl);
            Toast toast = Toast.makeText(context, "Wrong URL", 3000);
            toast.show();
        }
    }
}
