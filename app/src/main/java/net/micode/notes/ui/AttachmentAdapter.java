package net.micode.notes.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.micode.notes.R;

import java.util.List;

/**
 * Created by pc on 19/1/15.
 */

public class AttachmentAdapter extends ArrayAdapter<Attachment>{
    private int resourceID;
    public AttachmentAdapter(Context context, int textViewResourceId, List<Attachment> objects) {
        super(context, textViewResourceId, objects);
        resourceID = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        Attachment attachment = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
        ImageView attachmentImage = (ImageView) view.findViewById(R.id.attachment_image);
        TextView textView = (TextView) view.findViewById(R.id.attachment_content);
        attachmentImage.setImageResource(attachment.getImageID());
        String path = attachment.getContent();
        String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
        //Log.d(TAG, fileName);
        textView.setText(fileName);
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}


class Attachment{
    private int imageID;
    private String content;
    public Attachment(int imageID,String content){
        this.imageID=imageID;
        this.content=content;
    }
    public int getImageID(){
        return imageID;
    }
    public String getContent(){
        return content;
    }
}