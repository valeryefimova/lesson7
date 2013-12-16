package ifmo.mobdev.rssreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RSSDBAdapter {
    public static final String KEY_TITLE = "title";
    public static final String KEY_BODY = "body";
    public static final String KEY_ROWID = "_id";
    public static String DROP = " DROP TABLE IF EXISTS ";

    private static final String TAG = "RSSDBAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "rssdata";

    //----- feeds ----
    private static final String FEEDS_DATABASE_TABLE = "feeds";
    public static final String KEY_URL = "url";
    public static final String KEY_NAME = "name";
    private static final int DATABASE_VERSION = 2;
    private static final String FEEDS_DATABASE_CREATE =
            "create table " + FEEDS_DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_URL + " text not null unique, " + KEY_NAME + " text not null);";
    //----articles-----
    private static final String ARTICLES_DATABASE_TABLE = "articles";
    public static final String KEY_FEED_ID = "feed_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_LINK = "link";
    private static final String ARTICLES_DATABASE_CREATE =
            "create table " + ARTICLES_DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_FEED_ID + " integer not null, " + KEY_DATE + " text not null, " + KEY_TITLE + " text not null, "
                    + KEY_LINK + " text not null unique);";

    //----content------
    private static final String CONTENT_DATABASE_TABLE = "content";
    public static final String KEY_ARTICLE_ID = "article_id";
    public static final String KEY_CONTENT = "content";
    private static final String CONTENT_DATABASE_CREATE =
            "create table " + CONTENT_DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_ARTICLE_ID + " integer not null, " + KEY_CONTENT + " text not null unique);";


    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(FEEDS_DATABASE_CREATE);
            db.execSQL(ARTICLES_DATABASE_CREATE);
            db.execSQL(CONTENT_DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(DROP + FEEDS_DATABASE_TABLE);
            db.execSQL(DROP + ARTICLES_DATABASE_TABLE);
            db.execSQL(DROP + CONTENT_DATABASE_TABLE);
            onCreate(db);
        }
    }


    public RSSDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public RSSDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        try {
            mDb = mDbHelper.getWritableDatabase();
        } catch (Exception e) {
            try {
                mDb = mDbHelper.getReadableDatabase();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void drop() {
        mDb.execSQL(DROP + FEEDS_DATABASE_TABLE);
        mDb.execSQL(DROP + ARTICLES_DATABASE_TABLE);
        mDb.execSQL(DROP + CONTENT_DATABASE_TABLE);
        mDb.execSQL(FEEDS_DATABASE_CREATE);
        mDb.execSQL(ARTICLES_DATABASE_CREATE);
        mDb.execSQL(CONTENT_DATABASE_CREATE);
    }
    public long createFeed(String url, String name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_URL, url);
        initialValues.put(KEY_NAME, name);

        return mDb.insert(FEEDS_DATABASE_TABLE, null, initialValues);
    }

    public long createArticle(long feed_id, String title, String date, String link) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FEED_ID, feed_id);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_LINK, link);

        return mDb.insert(ARTICLES_DATABASE_TABLE, null, initialValues);
    }

    public int getArticleIdByTitle(String title) {
        if (title == null) return -1;
        Cursor cursor = mDb.query(ARTICLES_DATABASE_TABLE, new String[] {KEY_ROWID},
                KEY_TITLE + "=?", new String[] {title}, null, null, null, null);
        int index = cursor.getColumnIndex(KEY_ROWID);
        cursor.moveToNext();
        int a = -1;
        try {
            a = cursor.getInt(index);
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return a;
    }

    public long createContent(int article_id, String content) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ARTICLE_ID, article_id);
        initialValues.put(KEY_CONTENT, content);

        return mDb.insert(CONTENT_DATABASE_TABLE, null, initialValues);
    }

    public long getFeedIdByName(String name) {
        if (name == null) return -1;
        Cursor cursor = mDb.query(FEEDS_DATABASE_TABLE, new String[] {KEY_ROWID},
                KEY_NAME + "=?", new String[] {name}, null, null, null, null);
        int index = cursor.getColumnIndex(KEY_ROWID);
        cursor.moveToNext();
        long a = -1;
        try {
            a = cursor.getLong(index);
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return a;
    }

    public int getFeedIdByURL(String url) {
        if (url == null) return -1;
        Cursor cursor = mDb.query(FEEDS_DATABASE_TABLE, new String[] {KEY_ROWID},
                KEY_URL + "=?", new String[] {url}, null, null, null, null);
        int index = cursor.getColumnIndex(KEY_ROWID);
        cursor.moveToNext();
        int a = -1;
        try {
            a = cursor.getInt(index);
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return a;
    }

    public String getFeedURLByID(long id) {
        Cursor cursor = mDb.query(FEEDS_DATABASE_TABLE, new String[] {KEY_URL},
                KEY_ROWID + "=" + id, null, null, null, null);
        int index = cursor.getColumnIndex(KEY_URL);
        cursor.moveToNext();
        String a = null;
        try {
            a = cursor.getString(index);
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return a;
    }

    public String getFeedNameByID(long id) {
        Cursor cursor = mDb.query(FEEDS_DATABASE_TABLE, new String[] {KEY_NAME},
                KEY_ROWID + "=" + id, null, null, null, null);
        int index = cursor.getColumnIndex(KEY_NAME);
        cursor.moveToNext();
        String a = null;
        try {
            a = cursor.getString(index);
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return a;
    }

    public boolean deleteFeed(long rowId) {
        Cursor cursor = fetchOneChannel(rowId);
        int index = cursor.getColumnIndex(KEY_ROWID);
        long a;
        try {
            while(cursor.moveToNext()) {
                a = cursor.getLong(index);
                deleteContent(a);
            }
        } catch (Exception e) {

        }
        cursor.close();

        mDb.delete(ARTICLES_DATABASE_TABLE, KEY_FEED_ID + "=" + rowId, null);
        return mDb.delete(FEEDS_DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean deleteContent(long rowId) {
        return mDb.delete(CONTENT_DATABASE_TABLE, KEY_ARTICLE_ID + "=" + rowId, null) > 0;
    }

    public boolean deleteArticle(long rowId) {
        return mDb.delete(ARTICLES_DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public void deleteOver300Articles(long feed_id) {
        Cursor cursor = fetchOneChannel(feed_id);
        int index = cursor.getColumnIndex(KEY_ROWID);
        long a;
        int i = 0;
        try {
            while(cursor.moveToNext()) {
                i++;
                if (i > 300) {
                    a = cursor.getLong(index);
                    deleteContent(a);
                    deleteArticle(a);
                }
            }
        } catch (Exception e) {

        }
        cursor.close();
    }

    public Cursor fetchAllFeeds() {
        return mDb.query(FEEDS_DATABASE_TABLE, new String[] {KEY_ROWID, KEY_URL,
                KEY_NAME}, null, null, null, null, null);
    }

    public Cursor fetchAllArticles() {
        return mDb.query(ARTICLES_DATABASE_TABLE, new String[] {KEY_ROWID, KEY_FEED_ID,
                KEY_TITLE, KEY_DATE, KEY_LINK}, null, null, null, null, null);
    }

    public Cursor fetchOneChannel(long chID) {
        Cursor mCursor = mDb.query(ARTICLES_DATABASE_TABLE, new String[] {KEY_ROWID, KEY_FEED_ID, KEY_DATE, KEY_TITLE, KEY_LINK},
                KEY_FEED_ID + "=" + chID, null, null, null, null);
        return mCursor;
    }

    public Cursor fetchContent(long article_id) {
        Cursor mCursor = mDb.query(CONTENT_DATABASE_TABLE, new String[] {KEY_ROWID, KEY_ARTICLE_ID, KEY_CONTENT},
                KEY_ARTICLE_ID + "=" + article_id, null, null, null, null);
        return mCursor;
    }

    public boolean updateFeed(long rowId, String url, String name) {
        ContentValues args = new ContentValues();
        args.put(KEY_URL, url);
        args.put(KEY_NAME, name);

        return mDb.update(FEEDS_DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
