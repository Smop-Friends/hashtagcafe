package sungshin.hashtagcafe;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class review_main extends AppCompatActivity {

    int PICK_IMAGE_MULTIPLE = 1;
    ImageButton addbtn,del;

    private ArrayList<review_photo> mArrayList;
    private review_photoAdapter mAdapter;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        String[] items = {"cozy","comfortable","pretty","cool"};

        MultiAutoCompleteTextView multi = (MultiAutoCompleteTextView)findViewById(R.id.SearchText);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,items);
        MultiAutoCompleteTextView.CommaTokenizer token = new MultiAutoCompleteTextView.CommaTokenizer();
        multi.setTokenizer(token);
        multi.setAdapter(adapter);

        addbtn = findViewById(R.id.picAddBtn);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();

        mAdapter = new review_photoAdapter(mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView pictureCount = (TextView)findViewById(R.id.picCount);
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            //다중선택일 경우 getClipData
            if (data.getClipData() != null) {

                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();

                for (int i = 0; i < cout; i++) {
                    if(review_photo.picCount == 3) return;
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    // reviewImg[i+picCount].setImageURI(imageurl);
                    review_photo data2 = new review_photo(imageurl);
                    mArrayList.add(data2); // RecyclerView의 마지막 줄에 삽입
                    mAdapter.notifyDataSetChanged();
                    review_photo.picCount++;
                }
                //  pictureCount.setText(picture.picCount+"/3");
                // count++;

            } else {
                //  Uri imageurl = data.getData();
                // mArrayUri.add(imageurl);
                // reviewImg1.setImageURI(mArrayUri.get(0));
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
}