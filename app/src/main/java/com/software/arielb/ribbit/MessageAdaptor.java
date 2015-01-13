package com.software.arielb.ribbit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by arielb on 1/12/2015.
 */
public class MessageAdaptor extends ArrayAdapter<ParseObject>{

    protected Context mContext;
    protected List<ParseObject> mMessages;

    public MessageAdaptor(Context context,List<ParseObject> messages){
        super(context,R.layout.message_item,messages);
        mContext=context;
        mMessages=messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        ParseObject message=mMessages.get(position);

        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)){
            holder.iconImageView.setImageResource(R.drawable.ic_action_picture);
        }else {
            holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
        }
        return convertView;
    }
    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;

    }
}
