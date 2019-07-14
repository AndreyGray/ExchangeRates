package andreyskakunenko.exchangeratespbandnbu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import andreyskakunenko.exchangeratespbandnbu.Adapter.MyNbuRecyclerAdapter;
import andreyskakunenko.exchangeratespbandnbu.Model.NbuToday;
import andreyskakunenko.exchangeratespbandnbu.Retrofit.MyAPI;
import andreyskakunenko.exchangeratespbandnbu.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class NbuFragment extends Fragment {
    private static final String URL_NBU = "https://bank.gov.ua/NBUStatService/v1/";
    private static final String DIALOG_DATE = "Dialog date";

    View view;
    ImageButton mDateButton;
    TextView mCurrentDate;
    String currentDate;
    List<NbuToday> mList;

    MyAPI mAPI;
    RecyclerView mRecyclerView;
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    Calendar mDate=Calendar.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Retrofit retrofit = RetrofitClient.getInstance(URL_NBU);
        mAPI = retrofit.create(MyAPI.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentDate=mDate.get(Calendar.DAY_OF_MONTH)+"."
                +(mDate.get(Calendar.MONTH)+1)+"."
                +mDate.get(Calendar.YEAR);
        view = inflater.inflate(R.layout.fragment_nbu, container, false);
        mDateButton = view.findViewById(R.id.nbu_dateButton);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(new Date());
                dialog.setTargetFragment(NbuFragment.this,0);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mCurrentDate = view.findViewById(R.id.nbu_currentDate);
        mCurrentDate.setText(currentDate);
        mRecyclerView = view.findViewById(R.id.recycler_view_nbu);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchData(currentDate);

        return view;
    }

    private void fetchData(String date) {
        mCompositeDisposable.add(mAPI.getNbuResults(urlCreator(date))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<NbuToday>>() {
                    @Override
                    public void accept(List<NbuToday> nbuResults) throws Exception {
                        displayData(nbuResults);
                        mList=nbuResults;
                    }
                })
        );

    }

    private String urlCreator(String date) {
        String str = date.substring(0,date.length()-5);
        String pickedDate;
        int d = str.indexOf(".");
        if(str.substring(d+1,str.length()).length()==2){
            pickedDate=str.substring(d+1,str.length())+str.substring(0,d);
        }else {
            pickedDate="0"+str.substring(d+1,str.length())+str.substring(0,d);
        }


        return URL_NBU+"statdirectory/exchange?date=2018"+pickedDate+"&json";
    }

    private void displayData(List<NbuToday> nbuResults) {
        MyNbuRecyclerAdapter adapter = new MyNbuRecyclerAdapter(getContext(),nbuResults);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent pickedDate) {
        if(resultCode!=Activity.RESULT_OK){
            return;
        }
        int REQUEST_DATE = 0;
        if (requestCode == REQUEST_DATE) {
            currentDate=pickedDate.getSerializableExtra(DatePickerFragment.EXTRA_DATE).toString();
            mCurrentDate.setText(currentDate);
            fetchData(currentDate);
        }
    }


    @SuppressLint("ResourceAsColor")
    public  void detectValute(String ccy){
        for(int i=0;i<mList.size();i++){
            if((mList.get(i).getCc()).compareTo(ccy)==0){
               //mRecyclerView.smoothScrollToPosition(i); // with animation

                ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(i,0);
                ((MyNbuRecyclerAdapter)mRecyclerView.getAdapter()).updateHighlightPosition(i);

                break;
            }
        }

    }
}
