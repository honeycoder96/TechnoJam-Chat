package tech.honeysharma.techbmechat.Chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import tech.honeysharma.techbmechat.R;

/**
 * Created by Honey Sharma on 26/2/18.
 */

public class UsersViewHolder extends RecyclerView.ViewHolder {

    View mView;
    TextView userNameView;
    TextView userStatusView;
    CircleImageView userImageView;


    public UsersViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

    }

    public void setDisplayName(String name){
        userNameView = (TextView) mView.findViewById(R.id.user_single_name);
        userNameView.setText(name);

    }

    public void setUserStatus(String status){

        userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
        userStatusView.setText(status);
    }

    public void setUserImage(String thumb_image, Context ctx){
        userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
        Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

    }

    public Pair[] getPairs(){
        Pair[] pairs=new Pair[3];
        pairs[0]=new Pair<View,String>(userImageView,"imageTransition");
        pairs[1]=new Pair<View,String>(userNameView,"nameTransition");
        pairs[2]=new Pair<View,String>(userStatusView,"statusTransition");

        return pairs;
    }
}
