package sungshin.hashtagcafe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class cafesrch extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<User> userArrayList;  // store the whole cafe data
    MyAdapter myAdapter;
    FirebaseFirestore db;
    ProgressDialog progressDialog;
    String srchs;

    EditText srchresult;
    ImageButton srchresultbtn;
    //Button btn;
    ArrayList<User> filteredList;
    ImageButton noresultadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srch);

        srchresult = (EditText) findViewById(R.id.srchresult);
        srchresultbtn = (ImageButton) findViewById(R.id.srchresultbtn);

        Intent intent = getIntent();
        srchs = intent.getStringExtra("srchcafename");
        srchresult.setText(srchs);

        noresultadd = (ImageButton) findViewById(R.id.noresultadd);
        noresultadd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                srchs = srchresult.getText().toString();

                Intent intent = new Intent(getApplicationContext(), storeInfo_main.class);
                intent.putExtra("cafename", srchs);
                startActivity(intent);
                Log.v("태그", srchs);
            }
        });

        srchresultbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                srchs = srchresult.getText().toString();

                // show progress dialog bar while fetching data from firestore
                progressDialog();

                // build recycler view
                buildRecyclerView();

                // get the data from the firestore
                getResult();

            };
        });

        // show progress dialog bar while fetching data from firestore
        progressDialog();

        // build recycler view
        buildRecyclerView();

        // get the data from the firestore
        getResult();
    }

    // show progress dialog bar while fetching data from firestore
    private void progressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();
    }

    // build recycler view
    private void buildRecyclerView() {
        // firesotre recyclerview
        recyclerView = findViewById(R.id.recyclerview2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initialize firebase firestore database, arraylist, myadapter
        db = FirebaseFirestore.getInstance();  // get instance from the firestore
        userArrayList = new ArrayList<User>();
        myAdapter = new MyAdapter(cafesrch.this, userArrayList);

        // set the adapter to the recyclerview
        recyclerView.removeAllViewsInLayout();
        recyclerView.setAdapter(myAdapter);

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
    };

    // get the data from the firestore
    private void getResult() {
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
                        if (error != null) {

                            // progress dialog
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }

                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

                        // get data correctly
                        for (DocumentChange dc : value.getDocumentChanges()) {

                            // call whenever data is added
                            // and fetch all the data from the firestore and put the data to our User Arraylist
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                userArrayList.add(dc.getDocument().toObject(User.class));
                            }

                            // call
                            filterRecyclerView(srchs);

                            // not using: used before filtering recyclerview
                            // myAdapter.notifyDataSetChanged();

                            // progress dialog
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        if(filteredList.size() == 0){
                            Toast.makeText(getApplicationContext(), "검색 결과가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                            noresultadd.setVisibility(View.VISIBLE);
                        }
                        else{
                            noresultadd.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    };

    // Search Functionality for RecyclerView
    public void filterRecyclerView(String cn) {
        filteredList = new ArrayList<>();
        //띄어쓰기 무시 위해
        String[] searchboxarray = cn.split(" ");

        for (User item : userArrayList){
            Boolean contained = true;
            for (String s : searchboxarray){
                Log.v("태그", s);
                // 카페 리스트에 중복으로 appear 해서 && !filteredList.contains(item) 추가함
                if (!(item.cafeName.toLowerCase().contains(s.toLowerCase()))  || filteredList.contains(item)){
                    contained = false;
                }
            }
            if(contained == true){
                filteredList.add(item);
                Log.v("태그", "" + filteredList.size());
            }

        }

        // jenny added 0526
        // paste the filtered list into filterList in MyAdapter
        myAdapter.filterList(filteredList);

//        myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                User item = filteredList.get(position);
//
//                String s = item.cafeName;
//                Intent intent = new Intent(getApplicationContext(), storeInfo_main.class);
//                intent.putExtra("cafename", s);
//                startActivity(intent);
//                Log.v("태그", s);
//            }
//        });

    }
}