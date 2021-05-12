// jenny
package sungshin.hashtagcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TagSearchList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_search_list);

        // get hashtag selected item from alert dialog
        hashtagSelected();

        // 전 페이지로 돌아가기 헤시태그 서치 페이지
        configureBackButton();

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
