package kg.kloop.android.redbutton.groups;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.alexwalker.sendsmsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MembersTab extends Fragment {

    private static final String TAG = "MembersTab log";
    private ListView membersListView;
    private ArrayList<Member> members;
    MembersListAdapter adapter;
    private String userId;
    private String userName;
    DatabaseReference groupref;
    String groupName;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_members_tab, container, false);

        init();

        groupref.child(GroupDefaults.membersChild).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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

        return v;
    }

    private void init(){
        groupName = ((GroupActivity) v.getContext()).groupName;
        Log.d(TAG, groupName);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        membersListView = (ListView) v.findViewById(R.id.groupMembersListView);
        members = new ArrayList<>();
        adapter = new MembersListAdapter(v.getContext(), members);
        membersListView.setAdapter(adapter);
        groupref = FirebaseDatabase.getInstance().getReference().child(GroupDefaults.groupsBranch).child(groupName);

    }

}
