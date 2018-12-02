package diakonidze.marketprices;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import diakonidze.marketprices.util.BottomNavigationViewHelper;
import diakonidze.marketprices.util.Constants;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Context mContext = MainActivity.this;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (Constants.COMPLITE_INITIAL_DOWNLOADS) {
                    switch (menuItem.getItemId()) {
                        case R.id.bnm_promotion:

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
                            Intent intent4 = new Intent(mContext, MyListActivity.class);
                            startActivity(intent4);
                            break;
                        case R.id.bnm_user:
                            Intent intent5 = new Intent(mContext, PersonalActivity.class);
                            startActivity(intent5);
                            break;
                    }
                    overridePendingTransition(0, 0);
                } else {
                    Constants.showtext(mContext, "weit for inialization");
                }

                //Sibling transitions
                return false;
            }
        });

        Menu bottomMenu = bottomNavigationView.getMenu();
        MenuItem menuItem = bottomMenu.getItem(0);
        menuItem.setChecked(true);

        if (!Constants.COMPLITE_INITIAL_DOWNLOADS) {
            long time= System.currentTimeMillis();
            Log.d(TAG, " Time value in millisecinds "+time);

            Constants.fill_prodList(mContext);
        }
    }

    private void showText(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}
