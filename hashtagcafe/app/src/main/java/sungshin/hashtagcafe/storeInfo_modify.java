package sungshin.hashtagcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class storeInfo_modify extends AppCompatActivity {

    TextView checkCovid,checkInterior,checkCafe,checkMenu;
    Boolean textCovidClick=true,textInteriorClick=true,textCafeClick=true,textMenuClick=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info_modify);

         checkCovid=(TextView)findViewById(R.id.checkCovid);
         checkInterior=(TextView)findViewById(R.id.checkInterior);
         checkCafe=(TextView)findViewById(R.id.checkCafe);
         checkMenu=(TextView)findViewById(R.id.checkMenu);


        CheckBox [] checkBoxCovids=new CheckBox[10];
        CheckBox [] checkBoxInteriors=new CheckBox[10];
        CheckBox [] checkBoxCafes=new CheckBox[10];
        CheckBox [] checkBoxMenus=new CheckBox[10];

        Integer[] checkBoxCovidId={R.id.check_covid19_1,R.id.check_covid19_2,R.id.check_covid19_3,
                R.id.check_covid19_4,R.id.check_covid19_5};
        Integer[] checkBoxInteriorId={R.id.check_interior_1,R.id.check_interior_2,R.id.check_interior_3,R.id.check_interior_4,
                R.id.check_interior_5,R.id.check_interior_6,R.id.check_interior_7,R.id.check_interior_8,R.id.check_interior_9};
        Integer[] checkBoxCafeId={R.id.check_cafe_1,R.id.check_cafe_2,R.id.check_cafe_3,R.id.check_cafe_4,R.id.check_cafe_5,
                R.id.check_cafe_6,R.id.check_cafe_7,R.id.check_cafe_8};
        Integer[] checkBoxMenuId={R.id.check_menu_1,R.id.check_menu_2,R.id.check_menu_3,R.id.check_menu_4,R.id.check_menu_5,
                R.id.check_menu_6,R.id.check_menu_7,R.id.check_menu_8};


// 체크박스 연결
        for (int i = 0; i < checkBoxCovidId.length; i++) {
            checkBoxCovids[i] = (CheckBox) findViewById(checkBoxCovidId[i]);
        }
        for (int i = 0; i < checkBoxInteriorId.length; i++) {
            checkBoxInteriors[i] = (CheckBox) findViewById(checkBoxInteriorId[i]);
        }
        for (int i = 0; i < checkBoxCafeId.length; i++) {
            checkBoxCafes[i] = (CheckBox) findViewById(checkBoxCafeId[i]);
        }
        for (int i = 0; i < checkBoxMenuId.length; i++) {
            checkBoxMenus[i] = (CheckBox) findViewById(checkBoxMenuId[i]);
        }
        checkCovid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textCovidClick)
                {
                    textCovidClick=!textCovidClick;
                    for(int i=0;i<checkBoxCovidId.length;i++)
                    {
                    checkBoxCovids[i].setVisibility(View .VISIBLE);
                    }
                }
                else
                {
                    textCovidClick=!textCovidClick;
                    for(int i=0;i<checkBoxCovidId.length;i++)
                    {
                        checkBoxCovids[i].setVisibility(View .GONE);
                    }
                }

            }
        });
        checkInterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textInteriorClick)
                {
                    textInteriorClick=!textInteriorClick;
                    for(int i=0;i<checkBoxInteriorId.length;i++)
                    {
                        checkBoxInteriors[i].setVisibility(View .VISIBLE);
                    }
                }
                else
                {
                    textInteriorClick=!textInteriorClick;
                    for(int i=0;i<checkBoxInteriorId.length;i++)
                    {
                        checkBoxInteriors[i].setVisibility(View .GONE);
                    }
                }

            }
        });
        checkCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textCafeClick)
                {
                    textCafeClick=!textCafeClick;
                    for(int i=0;i<checkBoxCafeId.length;i++)
                    {
                        checkBoxCafes[i].setVisibility(View .VISIBLE);
                    }
                }
                else
                {
                    textCafeClick=!textCafeClick;
                    for(int i=0;i<checkBoxCafeId.length;i++)
                    {
                        checkBoxCafes[i].setVisibility(View .GONE);
                    }
                }

            }
        });
        checkMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textMenuClick)
                {
                    textMenuClick=!textMenuClick;
                    for(int i=0;i<checkBoxMenuId.length;i++)
                    {
                        checkBoxMenus[i].setVisibility(View .VISIBLE);
                    }
                }
                else
                {
                    textMenuClick=!textMenuClick;
                    for(int i=0;i<checkBoxMenuId.length;i++)
                    {
                        checkBoxMenus[i].setVisibility(View .GONE);
                    }
                }

            }
        });
    }
}