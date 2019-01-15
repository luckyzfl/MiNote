package net.micode.notes.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import net.micode.notes.R;

/**
 * Created by feilong zuo on 19/1/15.
 */

public class AttachmentActivity extends Activity{
    private final String TAG = "Note attachment";
    private final String MIME_TYPE_ATTACHMENT = "vnd.android.cursor.item/attachment_note";
    private final String URI_STRING_TABLE_DATA = "content://micode_notes/data";
    private ContentResolver contentResolver;
    private Uri URI_TABLE_DATA;
    private Long NOTE_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attachment_layout);
        NOTE_ID = getIntent().getLongExtra("note_id",0);
        Log.d(TAG, NOTE_ID.toString());
        Toast.makeText(this, NOTE_ID.toString() , Toast.LENGTH_LONG);
        contentResolver = getContentResolver();
        URI_TABLE_DATA = Uri.parse(URI_STRING_TABLE_DATA);

//        ContentValues contentValues = new ContentValues();
//        contentValues.put("mime_type",MIME_TYPE_ATTACHMENT);
//        contentValues.put("note_id",NOTE_ID);
//        contentResolver.insert(URI_TABLE_DATA,contentValues);

        final FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.attachment_add_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });

        //where id = NOTE_ID and mime_type = MIME_TYPE_ATTACHMENT
         Cursor cursor = contentResolver.query(
                 URI_TABLE_DATA,
                 new String[]{"content"},
                 "note_id=" + NOTE_ID + " and mime_type=\"" + MIME_TYPE_ATTACHMENT + "\"",
                 null,
                 null );

         if(cursor!= null){
             while(cursor.moveToNext()){
                 String content = cursor.getString(cursor.getColumnIndex("content"));
                 Log.d(TAG, "onCreate: "+ content);
             }
         }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
//            String[] proj = {MediaStore.Images.Media.DATA};
//            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
//            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            actualimagecursor.moveToFirst();
//            String img_path = actualimagecursor.getString(actual_image_column_index);
//            File file = new File(img_path);
//            Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_SHORT).show();
//            Log.d(TAG, img_path);
            String path = uri.getPath();
            Log.d(TAG, path);

            ContentValues contentValues = new ContentValues();
            contentValues.put("mime_type",MIME_TYPE_ATTACHMENT);
            contentValues.put("note_id",NOTE_ID);
            contentValues.put("content",path);
            contentResolver.insert(URI_TABLE_DATA,contentValues);

        }
    }


}
