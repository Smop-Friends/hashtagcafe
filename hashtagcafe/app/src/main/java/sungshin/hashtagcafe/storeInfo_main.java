package sungshin.hashtagcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class storeInfo_main extends AppCompatActivity {


    Button mapBtn,modifyBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info_main);

        mapBtn= (Button)findViewById(R.id.map);
        mapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), storeInfo_map.class);
                startActivity(intent);
            }
        });
        modifyBtn= (Button)findViewById(R.id.modify);
        modifyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), storeInfo_modify.class);
                startActivity(intent);
            }
        });
    }
}