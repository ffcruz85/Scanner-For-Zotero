package org.ale.scan2zotero.data;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class BibItem implements BaseColumns, Parcelable {

    public static final String TBL_NAME = "bibinfo";

    public static final String COL_DATE = "date";
    public static final String COL_TYPE = "type";
    public static final String COL_JSON = "json";
    public static final String COL_ACCT = "acct";

    public static final int TYPE_ERROR = -1;
    public static final int TYPE_BOOK = 0;
    public static final int TYPE_JOURNAL = 1;
    public static final int TYPE_MAGAZINE = 2;

    public static final int NO_ID = -1;

    private int mId;
    private long mCreationDate;
    private int mType;
    private JSONObject mInfo;
    private int mAcctId;
    private int mSelected;
    
    private int mCachedNumAuthors = -1;
    private String mCachedAuthorString = null;
    private String mCachedTitleString = null;

    public BibItem(int id, long date, int type, JSONObject json, int acct){
        mId = id;
        mCreationDate = date;
        mType = type;
        mInfo = json;
        mSelected = 0;
        mAcctId = acct;
    }

    public BibItem(int type, JSONObject json, int acct){
        this(NO_ID, (new Date()).getTime(), type, json, acct);
    }

    public BibItem(Parcel p) throws JSONException{
        this(p.readInt(), // _ID
             p.readLong(), // Creation Date
             p.readInt(), // Type
             new JSONObject(p.readString()), //JSON String
             p.readInt());  // Account ID
        mSelected = p.readInt();
    }

    public static BibItem fromCursor(Cursor c){
        int id = c.getInt(Database.BIBINFO_ID_INDEX);
        long date = c.getLong(Database.BIBINFO_DATE_INDEX);
        int type = c.getInt(Database.BIBINFO_TYPE_INDEX);
        String json = c.getString(Database.BIBINFO_JSON_INDEX);
        int acct = c.getInt(Database.BIBINFO_ACCT_INDEX);

        JSONObject data;
        try {
            data = new JSONObject(json);
        } catch (JSONException e) {
            // XXX: Unparsable data in db, remove it and return null.
            data = new JSONObject();
        }
        
        return new BibItem(id,date,type,data,acct);
    }

    /* Parceling */
    public static final Creator<BibItem> CREATOR = new Creator<BibItem>() {
        public BibItem createFromParcel(Parcel in) {
            BibItem r;
            try {
                r = new BibItem(in);
            } catch (JSONException e) {
                r = new BibItem(TYPE_ERROR, new JSONObject(), Account.NOT_IN_DATABASE);
            }
            return r;
        }

        public BibItem[] newArray(int size) {
            return new BibItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(mId);
        p.writeLong(mCreationDate);
        p.writeInt(mType);
        p.writeString(mInfo.toString());
        p.writeInt(mAcctId);
        p.writeInt(mSelected); // must be last
    }

    /* Data access */
    public void setId(int id){
        mId = id;
    }

    public int getId(){
        return mId;
    }

    public JSONObject getSelectedInfo(){
        try {
            return mInfo.getJSONArray("items").getJSONObject(mSelected);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    /* Database */
    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(BibItem.COL_DATE, mCreationDate);
        values.put(BibItem.COL_TYPE, mType);
        values.put(BibItem.COL_JSON, mInfo.toString());
        values.put(BibItem.COL_ACCT, mAcctId);
        return values;
    }

    public void writeToDB(ContentResolver cr){
        ContentValues values = toContentValues();
        if(mId == NO_ID) {
            Uri row = cr.insert(Database.BIBINFO_URI, values);
            int id = Integer.parseInt(row.getLastPathSegment());
            setId(id);
        } else {
            cr.update(Database.BIBINFO_URI,
                      values, BibItem._ID+"=?",
                      new String[]{String.valueOf(mId)});
        }
    }

    /* Caching for textviews */
    public boolean hasCachedValues(){
        return (mCachedNumAuthors != -1) && (mCachedTitleString != null) 
                    && (mCachedAuthorString != null);
    }

    public void cacheForViews() {
        mCachedAuthorString = "";
        mCachedTitleString = "";
        mCachedNumAuthors = 0;

        JSONObject data = getSelectedInfo();
        mCachedTitleString = data.optString(ItemField.title);
        JSONArray creators = data.optJSONArray(ItemField.creators);
        if(creators != null){
            mCachedNumAuthors = creators.length();

            ArrayList<String> creatorNames = new ArrayList<String>(mCachedNumAuthors);
            try {
                for(int i=0; i<creators.length(); i++){
                    String name = ((JSONObject)creators.get(i)).optString(ItemField.Creator.name);
                    if(!TextUtils.isEmpty(name))
                        creatorNames.add(name);
                }
                mCachedAuthorString = TextUtils.join(", ", creatorNames);
            } catch (JSONException e) {
                mCachedAuthorString = "error";
            }
        }
    }
    public void clearCache(){
        mCachedNumAuthors = -1;
        mCachedAuthorString = null;
        mCachedTitleString = null;
    }
    public int getCachedNumAuthors(){
        return mCachedNumAuthors;
    }
    public String getCachedAuthorString(){
        return mCachedAuthorString;
    }
    public String getCachedTitleString(){
        return mCachedTitleString;
    }
}
