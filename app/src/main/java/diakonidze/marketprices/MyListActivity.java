package diakonidze.marketprices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import diakonidze.marketprices.customViews.MyListItemView;
import diakonidze.marketprices.database.DBManager;
import diakonidze.marketprices.util.GlobalConstants;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import diakonidze.marketprices.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyListActivity extends AppCompatActivity {

    private Context mContext = MyListActivity.this;
    private static final String TAG = "My_List_Activity";

    // wigdets
    private LinearLayout uncheckedConteiner, checkedConteiner;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("key", 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DBManager.initialaize(mContext);
        DBManager.openWritable();
        DBManager.saveMyList(GlobalConstants.MY_SHOPING_LIST);
        DBManager.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        init_components();

//        if (savedInstanceState == null) {
        Log.d(TAG, "shopListSize = " + GlobalConstants.MY_SHOPING_LIST.size());
        for (int i = 0; i < GlobalConstants.MY_SHOPING_LIST.size(); i++) {
            if (GlobalConstants.MY_SHOPING_LIST.get(i).getChecked()) {
                MyListItemView itemView = new MyListItemView(mContext, GlobalConstants.MY_SHOPING_LIST.get(i), true, uncheckedConteiner, checkedConteiner);
                checkedConteiner.addView(itemView);
            } else {
                MyListItemView itemView = new MyListItemView(mContext, GlobalConstants.MY_SHOPING_LIST.get(i), false, uncheckedConteiner, checkedConteiner);
                uncheckedConteiner.addView(itemView);
            }
        }
//        }


    }

    private void init_components() {


        uncheckedConteiner = findViewById(R.id.unchecked_item_conteiner);
        checkedConteiner = findViewById(R.id.checked_item_conteiner);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.bnm_promotion:
                        Intent intent1 = new Intent(mContext, MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.bnm_search:
                        Intent intent2 = new Intent(mContext, SearchActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.bnm_add:
                        Intent intent3 = new Intent(mContext, AddActivity.class);
                        startActivity(intent3);
                        break;
                    case R.id.bnm_mylist:

                        break;
                    case R.id.bnm_user:
                        Intent intent5 = new Intent(mContext, PersonalActivity.class);
                        startActivity(intent5);
                        break;
                }
                overridePendingTransition(0, 0);
                return false;
            }
        });

        Menu bottomMenu = bottomNavigationView.getMenu();
        MenuItem menuItem = bottomMenu.getItem(3);
        menuItem.setChecked(true);
    }
}