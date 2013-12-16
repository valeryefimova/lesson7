package ifmo.mobdev.rssreader;

public interface RSSLoadCallback {
    public void onRSSLoaded(String res);
    public void onRSSLoadFailed(Exception e);
}
