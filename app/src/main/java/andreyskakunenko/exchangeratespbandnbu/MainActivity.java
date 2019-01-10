package andreyskakunenko.exchangeratespbandnbu;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


import andreyskakunenko.exchangeratespbandnbu.Adapter.MyPbRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements MyPbRecyclerAdapter.Callbacks {
    Toolbar mToolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu_graph,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_graph) {
            // startService
            Intent intent = new Intent(MainActivity.this,WebGraph.class);
            intent.putExtra("webURL","https://hryvna.today/");
            startActivity(intent);
        }
        if(item.getItemId() == R.id.exit_app){
            finish();
        }
        return true;
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        //setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragmentPb = fm.findFragmentById(R.id.container_pb);
        if (fragmentPb == null) {
            fragmentPb = new PbFragment();
            fm.beginTransaction()
                    .add(R.id.container_pb, fragmentPb)
                    .commit();
           // fragmentPb.setRetainInstance(true);
        }
        Fragment fragmentNbu = fm.findFragmentById(R.id.container_nbu);
        if (fragmentNbu == null) {
            fragmentNbu = new NbuFragment();
            fm.beginTransaction()
                    .add(R.id.container_nbu, fragmentNbu)
                    .commit();
            //fragmentNbu.setRetainInstance(true);
        }

    }

    @Override
    public void onValutaSelected(String ccyPb) {
        NbuFragment nbuFragment = (NbuFragment) getSupportFragmentManager().findFragmentById(R.id.container_nbu);
        if (nbuFragment != null) {
            nbuFragment.detectValute(ccyPb);
        }
    }

}