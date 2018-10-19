package tech.honeysharma.techbmechat.Chat;


import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import tech.honeysharma.techbmechat.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
ListView ListViewRequest;
 FirebaseDatabase mref;
    ArrayList<String> listofreq=null;
    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_requests, container, false);
	ListViewRequest= (ListView) view.findViewById(R.id.Req_frag_list);
        mref= FirebaseDatabase.getInstance();
        listofreq=new ArrayList<String>();
        req();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    public void req()
   {
        DatabaseReference  mDb_ref=mref.getReference("notifications");
        mDb_ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = (String) dataSnapshot.getValue();
                listofreq.add(key);
                // get subchild of this key and then its sub child "type" and "from"
                // find the key of "from" in "Users" node and get its name.
                ListViewRequest.setAdapter(new Myadapter(getActivity(),listofreq));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
   private class Myadapter extends BaseAdapter{
         Context context=null;
         ArrayList<String> list_of_req=null;
        public Myadapter(Context context,ArrayList<String> list_of_req){
          this.context=context;
          this.list_of_req=list_of_req;
        }

       @Override
       public View getView(int i, View view, ViewGroup viewGroup) {
            String user=list_of_req.get(i);
           LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.ticket_request,null);
           TextView tv=(TextView) view.findViewById(R.id.tv_name_ticket_req);
           tv.setText(user);
           Button btn_reject=(Button) view.findViewById(R.id.btn_reject);
           Button btn_accept=(Button) view.findViewById(R.id.btn_accept);
           btn_accept.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   //when user ACCEPT request
               }
           });
           btn_reject.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   //when user REJECT request

               }
           });

           return view;
       }

       @Override
       public int getCount() {
           return list_of_req.size();
       }

       @Override
       public Object getItem(int i) {
           return list_of_req.get(i);
       }

       @Override
       public long getItemId(int i) {
           return i;
       }

   }

