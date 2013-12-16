package ifmo.mobdev.rssreader;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class DownloadXMLTask extends AsyncTask<String, Void, String> {
    private final RSSLoadCallback callback;
    private Exception cause;

    public DownloadXMLTask(RSSLoadCallback callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String xml = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
        } catch (UnsupportedEncodingException e) {
            Log.d("DownloadXMLTask", e.getLocalizedMessage());
            cause = e;
        } catch (ClientProtocolException e) {
            Log.d("DownloadXMLTask", e.getLocalizedMessage());
            cause = e;
        } catch (IOException e) {
            Log.d("DownloadXMLTask", e.getLocalizedMessage());
            cause = e;
        } catch (RuntimeException e) {
            Log.d("DownloadXMLTask", e.getLocalizedMessage());
            cause = e;
        }
        return xml;
    }

    @Override
    protected void onPostExecute(String xml) {
        if (cause != null) {
            callback.onRSSLoadFailed(cause);
        } else {
            callback.onRSSLoaded(xml);
        }
        Log.d("DownloadXMLTask", "xml downloaded");
    }
}

