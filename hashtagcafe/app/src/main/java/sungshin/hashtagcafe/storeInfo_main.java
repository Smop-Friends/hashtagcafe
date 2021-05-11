package sungshin.hashtagcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class storeInfo_main extends AppCompatActivity {


    ImageButton mapBtn,modifyBtn,keep;
    Boolean keepClick=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info_main);

        mapBtn= (ImageButton) findViewById(R.id.map);
        modifyBtn= (ImageButton)findViewById(R.id.modify);
        keep= (ImageButton)findViewById(R.id.keep);

        mapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), storeInfo_map.class);
                startActivity(intent);
            }
        });

        modifyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), storeInfo_modify.class);
                //Intent intent = new Intent(getApplicationContext(), storeInfo_modify.class);
                startActivity(intent);
            }
        });

        keep.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //setIcon emptyHeart
                if(keepClick)
                {
                    keepClick=!keepClick;
                    keep.setImageResource(R.drawable.emptyheart);
                }
                else
                {
                    keepClick=!keepClick;
                    keep.setImageResource(R.drawable.fullheart);
                }
            }});
    }
}