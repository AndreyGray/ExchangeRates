package andreyskakunenko.exchangeratespbandnbu;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


import andreyskakunenko.exchangeratespbandnbu.Adapter.MyPbRecyclerAdapter;

public class MainActivity extends AppCompatActivity implements MyPbRecyclerAdapter.Callbacks {
    ImageButton webView;

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        //setContentView(R.layout.activity_main);
        webView =findViewById(R.id.laga);
        webView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detail = new Intent(MainActivity.this,WebGraph.class);
                detail.putExtra("webURL","https://hryvna.today/");
                startActivity(detail);

            }
        });

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
            fragmentNbu.setRetainInstance(true);
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