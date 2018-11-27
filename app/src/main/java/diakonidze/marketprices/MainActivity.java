package diakonidze.marketprices;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Context mContext = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.bnm_promotion:
                        showText("aeeee");
                        break;
                    case R.id.bnm_search:
                        showText("search");
                        break;
                    case R.id.bnm_add:
                        showText("aeedd");
                        break;
                    case R.id.bnm_mylist:
                        showText("list");
                        break;
                    case R.id.bnm_user:
                        showText("user");
                        break;
                }


                return true;
            }
        });

    }

    private void showText(String text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}
