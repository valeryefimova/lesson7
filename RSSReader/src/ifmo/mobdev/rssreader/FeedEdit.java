package ifmo.mobdev.rssreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FeedEdit extends Activity {
    private RSSDBAdapter mDbHelper;
    Button ok;
    EditText txtedn, txtedurl;
    long feed_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_feed);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        feed_id = Long.parseLong(extras.getString("feed_id"));

        ok = (Button) findViewById(R.id.button);
        txtedn = (EditText) findViewById(R.id.edtn);
        txtedurl = (EditText) findViewById(R.id.edtr);

        mDbHelper = new RSSDBAdapter(this);
        mDbHelper.open();

        txtedn.setText(mDbHelper.getFeedNameByID(feed_id));
        txtedurl.setText(mDbHelper.getFeedURLByID(feed_id));

        ok.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = txtedn.getText().toString();
                String url = txtedurl.getText().toString();
                mDbHelper.updateFeed(feed_id, url, name);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
