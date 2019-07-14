package andreyskakunenko.exchangeratespbandnbu;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import andreyskakunenko.exchangeratespbandnbu.Model.NbuToday;
import andreyskakunenko.exchangeratespbandnbu.Retrofit.MyAPI;
import andreyskakunenko.exchangeratespbandnbu.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ChartActivity extends AppCompatActivity {

    MyAPI mAPI;
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    Calendar calendar;
    double dollar;
    static List<Double> mList = new ArrayList<>();


    LineChart mChart;
    ArrayList<Entry> yValueGreen= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        if (ConnectionChecker.isNetworkAvailable(getApplicationContext())){
            Retrofit retrofit = RetrofitClient.getInstance("https://bank.gov.ua/NBUStatService/v1/statdirectory/");
            mAPI = retrofit.create(MyAPI.class);
        }else{
            Toast.makeText(this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
        }

        mChart = findViewById(R.id.chart);

        XAxis xAxis;
        xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f,10f,0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);

        YAxis yAxis;
        yAxis = mChart.getAxisLeft();
        yAxis.enableGridDashedLine(10f,10f,0f);
        mChart.getAxisRight().setEnabled(false);

        setDataCart(7);

        mChart.animateX(1000);
    }

    private void setDataCart(int count) {

        for (int i=0;i<count;i++) {
            if(mList.size()<count) {
                fetchData(giveDateDay(i));
            }
        }
        //SystemClock.sleep(2000);
        for (int i=0;i<mList.size();i++) {

            //float dollar = (float) mList.get(i).doubleValue();

            yValueGreen.add(new Entry(giveDateDay(i), (float) mList.get(i).doubleValue()));
        }

        LineDataSet setChart;
        setChart = new LineDataSet(yValueGreen,"Chart of dependence rate  the UAN to USD");
        setChart.setColor(Color.GREEN);
        LineData data = new LineData(setChart);
        mChart.setData(data);
    }

    private int giveDateDay(int count){
        calendar = Calendar.getInstance();
        calendar.getTime();
        return calendar.get(Calendar.DAY_OF_MONTH)-(7-count);
    }



    private String urlForRequestCreator(int dateDay){
        calendar = Calendar.getInstance();
        calendar.getTime();
        return
                "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?valcode=usd&date="+
                calendar.get(Calendar.YEAR)+"0"+
                (calendar.get(Calendar.MONTH)+1)+
                dateDay+
                "&json";
    }



    private void fetchData(int dateDay) {
        mCompositeDisposable.add(mAPI.getNbuResults(urlForRequestCreator(dateDay))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NbuToday>>() {
                    @Override
                    public void accept(List<NbuToday> nbuResults) throws Exception {

                        dollar = nbuResults.get(0).getRate();
                        mList.add(dollar);
                    }
                })
        );

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mCompositeDisposable!=null){mCompositeDisposable.clear();}
    }
}
