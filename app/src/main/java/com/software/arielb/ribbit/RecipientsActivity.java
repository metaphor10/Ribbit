package com.software.arielb.ribbit;

import android.app.AlertDialog;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends ActionBarActivity {
    protected ListView mListview;
    public static final String TAG=RecipientsActivity.class.getSimpleName();
    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected ParseUser mCurrentUser;
    protected MenuItem mSendMenuItem;
    protected Uri mMediaUri;
    protected String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_recipients);
        mListview = (ListView) findViewById(R.id.receipientsListView);
        mListview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mListview.getCheckedItemCount()>0){
                    mSendMenuItem.setVisible(true);
                }else {
                    mSendMenuItem.setVisible(false);
                }



            }
        });

        mMediaUri=getIntent().getData();
        mFileType=getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
    }
   @Override
    public void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
       setSupportProgressBarIndeterminateVisibility(true);
        ParseQuery<ParseUser> query=mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {
                setSupportProgressBarIndeterminateVisibility(false);
                if(e==null) {
                    mFriends = friends;
                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        usernames[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecipientsActivity.this, android.R.layout.simple_list_item_checked, usernames);
                    //mListview.setListAdapter(adapter);
                    mListview.setAdapter(adapter);
                }else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder= new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem=menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.action_send:
                ParseObject message=createMessage();
                if(message==null){
                    //error
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.error_selecting_file))
                            .setTitle(getString(R.string.error_selecting_file_title))
                    .setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }else {
                    send(message);
                    finish();
                }

              return true;
        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
    protected ParseObject createMessage(){
        ParseObject message= new ParseObject(ParseConstants.CLASS_MESSAGES);
        message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
        message.put(ParseConstants.KEY_RECIPIENTS_IDS,getRecipientsIds());
        message.put(ParseConstants.KEY_FILE_TYPE,mFileType);

        byte[] fileBytes=FileHelper.getByteArrayFromFile(this,mMediaUri);
        if(fileBytes==null){
            return null;
        }else {
            if(mFileType.equals(ParseConstants.TYPE_IMAGE)){
                fileBytes=FileHelper.reduceImageForUpload(fileBytes);
            }else {

            }
            String fileName=FileHelper.getFileName(this,mMediaUri,mFileType);
            ParseFile file=new ParseFile(fileName,fileBytes);
            message.put(ParseConstants.KEY_FILE,file);
            return message;
        }

    }
    protected ArrayList<String> getRecipientsIds(){
        ArrayList<String> recipientsIds=new ArrayList<String>();
        for (int i=0;i<mListview.getCount();i++){
            if(mListview.isItemChecked(i)){
                recipientsIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientsIds;
    }
    protected void send(ParseObject message){
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    Toast.makeText(RecipientsActivity.this,getString(R.string.success_message),Toast.LENGTH_LONG);
                }else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(RecipientsActivity.this);
                    builder.setMessage(getString(R.string.error_send_message))
                            .setTitle(getString(R.string.error_selecting_file_title))
                            .setPositiveButton(android.R.string.ok,null);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            }
        });
    }
}
