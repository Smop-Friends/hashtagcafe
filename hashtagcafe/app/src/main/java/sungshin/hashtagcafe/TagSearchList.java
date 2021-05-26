// jenny
package sungshin.hashtagcafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TagSearchList extends AppCompatActivity {

    // jenny 0525
    // pass hashtag selected item to next activity from alert dialog
    public static final String GOTO_INFOPAGE = "sungshin.hashtagcafe.GOTO_INFOPAGE";

    // firesotre recyclerview
    RecyclerView recyclerView;
    ArrayList<User> userArrayList;  // store the whole cafe data
    MyAdapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_search_list);

        // show progress dialog bar while fetching data from firestore
        progressDialog();

        // build recycler view
        buildRecyclerView();

        // get the data from the firestore
        EventChangeListener();

        // before firestore
        // get hashtag selected item from alert dialog
        hashtagSelected();

        // 전 페이지로 돌아가기 헤시태그 서치 페이지
        configureBackButton();

        // jenny 0525
        // 상세보기 버튼
        // gotoInfoButton();

        /*
        Button gotoButton = (Button) findViewById(R.id.gotoInfoBtn);
        gotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item = "hi";

                Intent intent = new Intent(TagSearchList.this, storeInfo_main.class);
                // pass hashtag selected item to next activity from alert dialog
                intent.putExtra(GOTO_INFOPAGE, item);
                startActivity(intent);
            }
        });

         */

    }

    // jenny 0525
    // 상세보기 버튼
    /*private void gotoInfoButton() {
        Button gotoButton = (Button) findViewById(R.id.gotoInfoBtn);
        gotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference docRef = db.collection("cafe").document("cafeName");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()){
                                // Log.d("LOGGER", "DocumentSnapshot data: " + document.getData().toString());

                                String item = document.getData().toString();

                                // 상세보기 버튼
                                Intent intent = new Intent(TagSearchList.this, storeInfo_main.class);
                                // pass hashtag selected item to next activity from alert dialog
                                intent.putExtra(GOTO_INFOPAGE, item);
                                startActivity(intent);

                            } else {
                                Log.d("LOGGER", "No such document");
                            }

                        } else{
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }

                    }
                });

            }
        });
    }*/

    // show progress dialog bar while fetching data from firestore
    private void progressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data…");
        progressDialog.show();
    }

    // build recycler view
    private void buildRecyclerView() {
        // firesotre recyclerview
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize firebase firestore database, arraylist, myadapter
        db = FirebaseFirestore.getInstance();  // get instance from the firestore
        userArrayList = new ArrayList<User>();
        myAdapter = new MyAdapter(TagSearchList.this, userArrayList);

        // set the adapter to the recyclerview
        recyclerView.setAdapter(myAdapter);

        // jenny 0526 start
        // switch to yejin cafe info page
        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                User item = userArrayList.get(position);

                String s = item.cafeName;
                Intent intent = new Intent(getApplicationContext(), storeInfo_main.class);
                intent.putExtra("cafename", s);
                startActivity(intent);
                Log.v("태그", s);
            }
        });
        // jenny 0526 end

    }

    // Search Functionality for RecyclerView
    public void filterRecyclerView(String searchboxtext) {
        ArrayList<User> filteredList = new ArrayList<>();

        // split the text from the searchbox hashtag
        String[] searchboxarray = searchboxtext.split("/");

        for (String s : searchboxarray){

            for (User item : userArrayList){
                // 카페 리스트에 중복으로 appear 해서 && !filteredList.contains(item) 추가함
                if (item.checkList.toLowerCase().contains(s.toLowerCase()) && !filteredList.contains(item)){
                    filteredList.add(item);
                }
            }

        }

        /* // not using
        for (User item : userArrayList){
            if (item.checkList.toLowerCase().contains(searchboxtext.toLowerCase())){
                filteredList.add(item);
            }
        }
        // not using
         */

        // paste the filtered list into filterList in MyAdapter
        myAdapter.filterList(filteredList);

        // jenny 0526 start
        // switch to yejin cafe info page
        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                User item = filteredList.get(position);

                String s = item.cafeName;
                Intent intent = new Intent(getApplicationContext(), storeInfo_main.class);
                intent.putExtra("cafename", s);
                startActivity(intent);
                Log.v("태그", s);
            }
        });
        // jenny 0526 end

    }

    // get the data from the firestore
    private void EventChangeListener() {
        // name of the collection is "cafe" in firestore database
        // order the list by cafeName in ascending order
        db.collection("cafe").orderBy("cafeName", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        // callback method: when new data is added, modified or removed the data
                        // or run the application and attach this listener for the first time
                        // in this four cases, this will get the data from the firebase

                        // if there is an error
                        if (error != null){

                            // progress dialog
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }

                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

                        // get data correctly
                        for (DocumentChange dc : value.getDocumentChanges()){

                            // call whenever data is added
                            // and fetch all the data from the firestore and put the data to our User Arraylist
                            if (dc.getType() == DocumentChange.Type.ADDED){
                                userArrayList.add(dc.getDocument().toObject(User.class));
                            }

                            // Search Functionality for RecyclerView
                            Intent intent = getIntent();
                            String itemSelected = intent.getStringExtra(map.ITEM_SELECTED);
                            // call
                            filterRecyclerView(itemSelected);

                            // not using: used before filtering recyclerview
                            // myAdapter.notifyDataSetChanged();

                            // progress dialog
                            if (progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                        }

                    }
                });

    }

    // get hashtag selected item from alert dialog
    private void hashtagSelected(){
        Intent intent = getIntent();
        String itemSelected = intent.getStringExtra(map.ITEM_SELECTED);

        TextView htselected = findViewById(R.id.TVselected);
        htselected.setText(itemSelected);

        // Making TextView scrollable horizontal, when text overflows
        htselected.setSelected(true);
    }

    // 전 페이지로 돌아가기 헤시태그 서치 페이지
    private void configureBackButton() {
        Button backButton = (Button) findViewById(R.id.BtnGoBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}