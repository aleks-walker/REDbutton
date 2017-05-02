package kg.kloop.android.redbutton.groups;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alexwalker.sendsmsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Approve extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private ListView listView;
    private ArrayList<String> requests;
    ArrayAdapter<String> adapter;
    TextView info;
    String userIdForChange;
    String reqId;
    String groupName;
    private static final String TAG = "myLog";

    private HashMap<String, Request> requestsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);
        init();



    }

    private void init(){
        groupName = getIntent().getStringExtra("groupName");

        info = (TextView) findViewById(R.id.infotext);
        listView  = (ListView) findViewById(R.id.listView);

        requestsList = new HashMap<>();



        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference().child("Groups").child(groupName).child("requests");

        requests = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, requests);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = ((TextView)view).getText().toString();

                for (HashMap.Entry<String, Request> entry: requestsList.entrySet()){
                    Request req = entry.getValue();
                    if (req.getUserName().equals(userName)){
                        Log.d(TAG , "count : " + Integer.toString(req.getAgreeCount()));
                        userIdForChange = req.getUserId();
                        reqId = entry.getKey();
                        int newCount = req.getAgreeCount() + 1;

                        Log.d(TAG, "new count: " + Integer.toString(newCount));
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/"+reqId+"/agreeCount", newCount );
                        childUpdates.put("/"+reqId + "/approved/"+ FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
                        ref.updateChildren(childUpdates);
                        Toast.makeText(Approve.this, "Вы одобрили заявку", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request request = dataSnapshot.getValue(Request.class);
//                info.setText("");
//                info.append("key: " + dataSnapshot.getKey() + "\n");
//                info .append("userId: " + request.getUserId() + "\n");

                if (!isRequestAlreadyApproved(dataSnapshot)) {

                    requests.add(request.getUserName());
                    requestsList.put(dataSnapshot.getKey(), request);

                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "requestsList size: " + Integer.toString(requestsList.size()));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (!isRequestAlreadyApproved(dataSnapshot)){

                    Log.d(TAG, "Changed, but was not approved ");
                    Request request = dataSnapshot.getValue(Request.class);
                    requestsList.put(dataSnapshot.getKey(), request);
                    Log.d(TAG, "requests size: " + Integer.toString(requestsList.size()));
                } else {
                    deleteRequestFromList(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
               deleteRequestFromList(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean isRequestAlreadyApproved(DataSnapshot dataSnapshot){
        boolean isAlreadyApproved = false;
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        for (DataSnapshot child: dataSnapshot.getChildren()){
            if (child.getKey().equals("approved")){
                //Map of users who approved this request
                Map<String, Boolean> users = (HashMap<String, Boolean>) child.getValue();
                //Looking for current user's Id in users
                for (String userId: users.keySet()){
                    if (userId.equals(currentUserId)){
                        isAlreadyApproved = true;
                        break;
                    }
                }
            }
        }

        return isAlreadyApproved;
    }

    private void deleteRequestFromList(DataSnapshot dataSnapshot){
        Request request = dataSnapshot.getValue(Request.class);
        String removedUserName = request.getUserName();
        if (requests.contains(removedUserName)){
            requests.remove(removedUserName);
            adapter.notifyDataSetChanged();
        }
        if (requestsList.containsKey(dataSnapshot.getKey())) {
            requestsList.remove(dataSnapshot.getKey());
            Log.d(TAG, "requestsList size: " + Integer.toString(requestsList.size()));
        }
    }
}
