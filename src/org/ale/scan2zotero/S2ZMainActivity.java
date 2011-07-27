package org.ale.scan2zotero;

import org.ale.scan2zotero.data.Account;
import org.ale.scan2zotero.data.BibItem;
import org.ale.scan2zotero.web.APIRequest;
import org.ale.scan2zotero.web.GoogleBooksAPIClient;
import org.ale.scan2zotero.web.RequestQueue;
import org.ale.scan2zotero.web.ZoteroAPIClient;
import org.ale.scan2zotero.web.APIRequest.APIResponse;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

public class S2ZMainActivity extends Activity {

    private static final String CLASS_TAG = S2ZMainActivity.class.getCanonicalName();

    private static final int RESULT_SCAN = 0;

    public static final String INTENT_EXTRA_ACCOUNT = "ACCOUNT";

    private ZoteroAPIClient mZAPI;
    private GoogleBooksAPIClient mBooksAPI;

    private RequestQueue mRequestQueue;
    private BibItemListAdapter mItemList;

    private AlertDialog mAlertDialog = null;

    private int mAccountId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.scan_isbn).setOnClickListener(scanIsbn);

        // Initialize Zotero API Client
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Account acct = (Account) extras.getParcelable(INTENT_EXTRA_ACCOUNT);
            mZAPI = new ZoteroAPIClient(mZoteroAPIHandler);
            mZAPI.setAccount(acct);
            mAccountId = acct.getDbId();
        }else{
            mZAPI = null;
            mAccountId = Account.NOT_IN_DATABASE;
        }

        mRequestQueue = RequestQueue.getInstance();

        // Initialize Google Books API Client
        mBooksAPI = new GoogleBooksAPIClient(mGoogleBooksHandler);

        mItemList = new BibItemListAdapter(S2ZMainActivity.this);
        ExpandableListView explv = (ExpandableListView)findViewById(R.id.bib_items);
        registerForContextMenu(explv);
        explv.setAdapter(mItemList);

        mItemList.fillFromDatabase(mAccountId);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mAlertDialog != null){
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mZAPI == null)
            logout();

        // Display any dialogs we were displaying before being destroyed
        switch(S2ZDialogs.displayedDialog) {
        case(S2ZDialogs.DIALOG_NO_DIALOG):
            break;
        case(S2ZDialogs.DIALOG_ZXING):
            mAlertDialog = S2ZDialogs.getZxingScanner(S2ZMainActivity.this);
            break;
        }
    }

    public void logout(){
        Intent intent = new Intent(S2ZMainActivity.this, S2ZLoginActivity.class);
        intent.putExtra(S2ZLoginActivity.INTENT_EXTRA_CLEAR_FIELDS, true);
        S2ZMainActivity.this.startActivity(intent);
        finish();
    }

    public void gotBibInfo(JSONObject info){
        // Fill in form from online info.
        BibItem item = new BibItem(BibItem.TYPE_BOOK, info, mAccountId);
        mItemList.addItem(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.ctx_collection:
            return true;
        case R.id.ctx_logout:
            logout();
            return true;
        case R.id.ctx_about:
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bib_item_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = 
            (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.ctx_edit:
            BibItem toEdit = (BibItem) mItemList.getGroup((int) info.id);
            Intent intent = new Intent(S2ZMainActivity.this, S2ZEditActivity.class);
            intent.putExtra(S2ZEditActivity.INTENT_EXTRA_BIBITEM, toEdit);
            return true;
        case R.id.ctx_delete:
            mItemList.deleteItem((int) info.id);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch(requestCode){
            case RESULT_SCAN:
                if (resultCode == RESULT_OK) {
                    String contents = intent.getStringExtra("SCAN_RESULT"); // The scanned ISBN
                    String format = intent.getStringExtra("SCAN_RESULT_FORMAT"); // "EAN 13"
                    handleBarcode(contents, format);
                }
                break;
        }
    }

    private void handleBarcode(String content, String format){
        switch(Util.parseBarcode(content, format)) {
            case(Util.SCAN_PARSE_ISBN):
                Log.d(CLASS_TAG, "Looking up ISBN:"+content);
                findViewById(R.id.bib_item_progress).setVisibility(View.VISIBLE);
                mBooksAPI.isbnLookup(content);
                break;
            case(Util.SCAN_PARSE_ISSN):
                Log.d(CLASS_TAG, "Looking up ISSN:"+content);
                break;
            default:
                Log.d(CLASS_TAG, "Could not handle code: "+content+" of type "+format);
                // XXX: Report scan failure
                break;
        }
    }

    private final Button.OnClickListener scanIsbn = new Button.OnClickListener() {
        public void onClick(View v) {
            try{
                Intent intent = new Intent(getString(R.string.zxing_intent_scan));
                intent.setPackage(getString(R.string.zxing_pkg));
                intent.putExtra("SCAN_MODE", "ONE_D_MODE");
                startActivityForResult(intent, RESULT_SCAN);
            } catch (ActivityNotFoundException e) {
                // Ask the user if we should install ZXing scanner
                S2ZDialogs.getZxingScanner(S2ZMainActivity.this);
            }
        }
    };

    private final Button.OnClickListener getGroups = new Button.OnClickListener() {
        public void onClick(View v) {
            Log.d(CLASS_TAG, "Starting get groups");
            mZAPI.getUsersGroups();
        }
    };

    private final Handler mZoteroAPIHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
            case APIRequest.START:
                break;
            case APIRequest.EXCEPTION:
                ((Exception)msg.obj).printStackTrace();
                break;
            case APIRequest.SUCCESS:
                APIResponse resp = (APIResponse)msg.obj;
                Log.d(CLASS_TAG, (String)resp.getData());
                break;
            case APIRequest.FINISH:
                mRequestQueue.taskComplete((APIRequest) msg.obj);
                break;
            }
        }
    };

    private final Handler mGoogleBooksHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what) {
            case APIRequest.START:
                ((ProgressBar)findViewById(R.id.bib_item_progress))
                                .setIndeterminate(false);
                ((ProgressBar)findViewById(R.id.bib_item_progress))
                                .incrementProgressBy(10);
                Log.d(CLASS_TAG, "START");
                break;
            case APIRequest.PROGRESS:
                ((ProgressBar)findViewById(R.id.bib_item_progress))
                                .incrementProgressBy(50);
                break;
            case APIRequest.EXCEPTION:
                Log.d(CLASS_TAG, "EXCEPTION");
                APIResponse rexp = (APIResponse)msg.obj;
                ((Exception)rexp.getData()).printStackTrace();
                break;
            case APIRequest.SUCCESS:
                ((ProgressBar)findViewById(R.id.bib_item_progress))
                                .incrementProgressBy(30);
                Log.d(CLASS_TAG, "SUCCESS");
                APIResponse resp = (APIResponse)msg.obj;
                String isbn = resp.getId();
                String jsonStr = (String) resp.getData();
                Log.d(CLASS_TAG, (String) resp.getData());
                JSONObject translated = GoogleBooksAPIClient
                                            .translateJsonResponse(isbn, jsonStr);
                gotBibInfo(translated);
                break;
            case APIRequest.FINISH:
                ((ProgressBar)findViewById(R.id.bib_item_progress))
                                .incrementProgressBy(10);
                Log.d(CLASS_TAG, "FINISHED");
                mRequestQueue.taskComplete((APIRequest) msg.obj);
                break;
            }
        }
    };
}
