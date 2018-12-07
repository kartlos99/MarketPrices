package diakonidze.marketprices;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import androidx.appcompat.app.AppCompatActivity;
import diakonidze.marketprices.util.GlobalConstants;
import diakonidze.marketprices.util.NetService;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Context mContext = MainActivity.this;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Chip chip = findViewById(R.id.chip8);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout linearLayout = findViewById(R.id.linear_container);

            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (GlobalConstants.COMPLITE_INITIAL_DOWNLOADS) {
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
                    GlobalConstants.showtext(mContext, "weit for inialization");
                }

                //Sibling transitions
                return false;
            }
        });

        Menu bottomMenu = bottomNavigationView.getMenu();
        MenuItem menuItem = bottomMenu.getItem(0);
        menuItem.setChecked(true);

        if (!GlobalConstants.COMPLITE_INITIAL_DOWNLOADS) {
            long time= System.currentTimeMillis();
            Log.d(TAG, " Time value in millisecinds "+time);

            NetService ns = new NetService(mContext);
            ns.fill_prodList();
        }
    }

    private void showText(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}
