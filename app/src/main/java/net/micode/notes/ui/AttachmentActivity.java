package net.micode.notes.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import net.micode.notes.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

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
    private DynamicListView dynamicListView;
    private ArrayList<Attachment> arrayList;
    AlphaInAnimationAdapter animationAdapter;
    AttachmentAdapter attachmentAdapter;

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


         arrayList = new ArrayList<Attachment>();
//        arrayList1.add(new Attachment(R.drawable.clock, "1111"));
//        arrayList1.add(new Attachment(R.drawable.clock, "2222"));
//        arrayList1.add(new Attachment(R.drawable.clock, "2222"));
//        ArrayList<String> arrayList = new ArrayList<String>();


         //遍历select结果，构造arraylist
         if(cursor!= null){
             while(cursor.moveToNext()){
                 String content = cursor.getString(cursor.getColumnIndex("content"));
                 Log.d(TAG, "onCreate: "+ content);
                 String name = content.substring(content.lastIndexOf("/") + 1, content.length());


                 int pic_id;
                 /* 取得扩展名 */
                 String end = content.substring(content.lastIndexOf(".") + 1, content.length()).toLowerCase(Locale.getDefault());
                /* 依扩展名的类型决定MimeType */
                 if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
                     pic_id = R.drawable.attachment_audio;
                 } else if (end.equals("3gp") || end.equals("mp4")) {
                     pic_id = R.drawable.attachment_video;
                 } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
                     pic_id = R.drawable.attachment_photo;
//                 } else if (end.equals("apk")) {
//                     return getApkFileIntent(filePath);
//                 } else if (end.equals("ppt")) {
//                     return getPptFileIntent(filePath);
//                 } else if (end.equals("xls")) {
//                     return getExcelFileIntent(filePath);
//                 } else if (end.equals("doc")) {
//                     return getWordFileIntent(filePath);
//                 } else if (end.equals("pdf")) {
//                     return getPdfFileIntent(filePath);
//                 } else if (end.equals("chm")) {
//                     return getChmFileIntent(filePath);
//                 } else if (end.equals("txt")) {
//                     return getTextFileIntent(filePath, false);
                 } else {
                     pic_id = R.drawable.attachment_file;
                 }

                 arrayList.add(new Attachment(pic_id, content));
             }
         }



        dynamicListView = (DynamicListView) findViewById(R.id.dynamiclistview);
        dynamicListView.enableDragAndDrop();

//        String [] ss = new String[]{"111","222","333"};
//        MyAdapter<String> myAdapter = new MyAdapter<String>(
//                AttachmentActivity.this,
//                android.R.layout.simple_list_item_1,
//                arrayList
//
//        );
//        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(
//                AttachmentActivity.this,
//                android.R.layout.simple_list_item_1,
//                ss
//        );





        attachmentAdapter = new AttachmentAdapter(
                AttachmentActivity.this,
                R.layout.attachment_item,
                arrayList
        );

//        ListView listView = (ListView) findViewById(R.id.attachment_listView);
//        listView.setAdapter(attachmentAdapter);
//
        animationAdapter = new AlphaInAnimationAdapter(attachmentAdapter);
        animationAdapter.setAbsListView(dynamicListView);
        dynamicListView.setAdapter(animationAdapter);
//        dynamicListView.enableDragAndDrop();
//        dynamicListView.setAdapter(myAdapter);
//        dynamicListView.setAdapter(arrayAdapter);
//        dynamicListView.insert(1,"1");



        //点击附件事件
        dynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Attachment attachment = arrayList.get(position);
                String path = attachment.getContent();
                Intent intent = OpenFileUtil.openFile(path);
                Log.d(TAG, "onItemClick: path = " + path);
//                intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
                startActivity(intent);
            }
        });



        //点击长按删除事件
        dynamicListView.setLongClickable(true);
        dynamicListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AttachmentActivity.this);
                dialog.setTitle("删除");
                final String content = arrayList.get(i).getContent();
                String name = content.substring(content.lastIndexOf("/") + 1, content.length());
                dialog.setMessage("确认删除此条附件？\n" + name);
                dialog.setCancelable(true);
                final int index = i ;
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //从数据库中删除
                        contentResolver.delete(
                                URI_TABLE_DATA,
                                //"note_id=" + NOTE_ID + " and mime_type=\"" + MIME_TYPE_ATTACHMENT + "\"",
                                "content=?",
                                new String[]{content}
                                );
                        //从arraylist中删除并更新ui
                        arrayList.remove(index);
                        attachmentAdapter.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //不做动作
                    }
                });
                dialog.show();
                return true;
            }
        });


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

//            String path = uri.getPath();
            String path = getPath(this, uri);
            Log.d(TAG, path);



//            boolean isExist = false;
            //检测获取得uri有无和已存在得uri冲突
            for(Attachment attachment:arrayList){
                if(attachment.getContent().equals(path)){
                    Toast.makeText(this,"附件已经添加过", Toast.LENGTH_LONG).show();
                    //isExist = true;
                    return;
                }
            }


            //更新data数据库，把uri塞进去，如果附件的新添加的
            ContentValues contentValues = new ContentValues();
            contentValues.put("mime_type",MIME_TYPE_ATTACHMENT);
            contentValues.put("note_id",NOTE_ID);
            contentValues.put("content",path);
            contentResolver.insert(URI_TABLE_DATA,contentValues);



            //更新ui,如果附件是新添加的
//            onCreate();
//          构造新的Attachment结构体
            /* 取得扩展名 */
            int pic_id;
            String end = path.substring(path.lastIndexOf(".") + 1, path.length()).toLowerCase(Locale.getDefault());
                /* 依扩展名的类型决定MimeType */
            if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
                pic_id = R.drawable.attachment_audio;
            } else if (end.equals("3gp") || end.equals("mp4")) {
                pic_id = R.drawable.attachment_video;
            } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
                pic_id = R.drawable.attachment_photo;
//                 } else if (end.equals("apk")) {
//                     return getApkFileIntent(filePath);
//                 } else if (end.equals("ppt")) {
//                     return getPptFileIntent(filePath);
//                 } else if (end.equals("xls")) {
//                     return getExcelFileIntent(filePath);
//                 } else if (end.equals("doc")) {
//                     return getWordFileIntent(filePath);
//                 } else if (end.equals("pdf")) {
//                     return getPdfFileIntent(filePath);
//                 } else if (end.equals("chm")) {
//                     return getChmFileIntent(filePath);
//                 } else if (end.equals("txt")) {
//                     return getTextFileIntent(filePath, false);
            } else {
                pic_id = R.drawable.attachment_file;
            }

            arrayList.add(new Attachment(pic_id, path));
            attachmentAdapter.notifyDataSetChanged();
//            animationAdapter.notifyDataSetChanged();
//            Intent intent = new Intent(AttachmentActivity.this,AttachmentActivity.class);
////            Intent intent = new Intent(NoteEditActivity.this, AttachmentActivity.class);
//            intent.putExtra("note_id", NOTE_ID);
//            startActivity(intent);


        }
    }




    //获取所得文件的绝对路径
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
//                Log.i(TAG,"isExternalStorageDocument***"+uri.toString());
//                Log.i(TAG,"docId***"+docId);
//                以下是打印示例：
//                isExternalStorageDocument***content://com.android.externalstorage.documents/document/primary%3ATset%2FROC2018421103253.wav
//                docId***primary:Test/ROC2018421103253.wav
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
//                Log.i(TAG,"isDownloadsDocument***"+uri.toString());
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
//                Log.i(TAG,"isMediaDocument***"+uri.toString());
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"content***"+uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"file***"+uri.toString());
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


}

class OpenFileUtil {

    public static Intent openFile(String filePath) {

        File file = new File(filePath);
        if (!file.exists())
            return null;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase(Locale.getDefault());
        /* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            return getAudioFileIntent(filePath);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            return getVideoFileIntent(filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") || end.equals("jpeg") || end.equals("bmp")) {
            return getImageFileIntent(filePath);
        } else if (end.equals("apk")) {
            return getApkFileIntent(filePath);
        } else if (end.equals("ppt")) {
            return getPptFileIntent(filePath);
        } else if (end.equals("xls")) {
            return getExcelFileIntent(filePath);
        } else if (end.equals("doc")) {
            return getWordFileIntent(filePath);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(filePath);
        } else if (end.equals("chm")) {
            return getChmFileIntent(filePath);
        } else if (end.equals("txt")) {
            return getTextFileIntent(filePath, false);
        } else {
            return getAllIntent(filePath);
        }
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    // Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    // Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    // Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    // Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {

        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    // Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    // Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    // Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    // Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    // Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    // Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (paramBoolean) {
            Uri uri1 = Uri.parse(param);
            intent.setDataAndType(uri1, "text/plain");
        } else {
            Uri uri2 = Uri.fromFile(new File(param));
            intent.setDataAndType(uri2, "text/plain");
        }
        return intent;
    }

    // Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

}
