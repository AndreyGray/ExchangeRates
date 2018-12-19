package andreyskakunenko.exchangeratespbandnbu;

import android.app.Activity;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import andreyskakunenko.exchangeratespbandnbu.Adapter.MyPbRecyclerAdapter;
import andreyskakunenko.exchangeratespbandnbu.Model.PbAndNduArchive;
import andreyskakunenko.exchangeratespbandnbu.Model.PbResult;
import andreyskakunenko.exchangeratespbandnbu.Model.PbToday;
import andreyskakunenko.exchangeratespbandnbu.Retrofit.MyAPI;
import andreyskakunenko.exchangeratespbandnbu.Retrofit.RetrofitClient;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class PbFragment extends Fragment {
    private static final String URL_PB = "https://api.privatbank.ua/p24api/";
    private static final String DIALOG_DATE = "Dialog date";
    List<PbResult> mResults;

    View view;
    ImageButton mDateButton;
    TextView mCurrentDate;
    String currentDate;

    MyAPI mAPI;
    RecyclerView mRecyclerView;
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    Calendar mDate=Calendar.getInstance();
    private int REQUEST_DATE = 0;

    private MyPbRecyclerAdapter.Callbacks mCallbacks;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Retrofit retrofit = RetrofitClient.getInstance(URL_PB);
        mAPI = retrofit.create(MyAPI.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        currentDate=mDate.get(Calendar.DAY_OF_MONTH)+"."
                +(mDate.get(Calendar.MONTH)+1)+"."
                +mDate.get(Calendar.YEAR);
        view = inflater.inflate(R.layout.fragment_pb, container, false);
        mDateButton = view.findViewById(R.id.dateButton);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(new Date());
                dialog.setTargetFragment(PbFragment.this,0);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mCurrentDate = view.findViewById(R.id.currentDate);
        mCurrentDate.setText(currentDate);
        mRecyclerView = view.findViewById(R.id.recycler_view_pb);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchData();//поставить условие что если дата отличается  выполнять другой запрос


        return view;
    }

    private void fetchData() {
        mCompositeDisposable.add(mAPI.getResults()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<PbToday>>() {
                    @Override
                    public void accept(List<PbToday> results) throws Exception {
                        mResults=new ArrayList<>();
                        PbResult mRes;
                        for(int i=0;i<results.size();i++){
                            mRes = new PbResult(results.get(i).getCcy(),results.get(i).getBuy(),results.get(i).getSale());
                            mResults.add(mRes);
                        }
                        displayData(mResults);
                    }
                })
        );

    }
    private void displayData(List<PbResult> results) {
        MyPbRecyclerAdapter adapter = new MyPbRecyclerAdapter(getContext(),results);
        adapter.setOnClickedListener(mCallbacks);
        mRecyclerView.setAdapter(adapter);
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
        if (requestCode == REQUEST_DATE) {
            currentDate=pickedDate.getSerializableExtra(DatePickerFragment.EXTRA_DATE).toString();
            mCurrentDate.setText(currentDate);
            fetchNewDate(currentDate);
        }
    }

    private void fetchNewDate(String currentDate) {
        String urlRequest ="https://api.privatbank.ua/p24api/exchange_rates?json&date="+currentDate;
        mCompositeDisposable.add(mAPI.getArchiveResults(urlRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PbAndNduArchive>() {
                    @Override
                    public void accept(PbAndNduArchive archiveToDate) throws Exception {
                        mResults=new ArrayList<>();
                        PbResult mRes;
                        for(int i=0;i<archiveToDate.getExchangeRate().size();i++){
                            if(archiveToDate.getExchangeRate().get(i).getSaleRate()!=null){
                                mRes = new PbResult(archiveToDate.getExchangeRate().get(i).getCurrency(),
                                        archiveToDate.getExchangeRate().get(i).getPurchaseRate().toString(),
                                        archiveToDate.getExchangeRate().get(i).getSaleRate().toString());
                                mResults.add(mRes);
                            }

                        }
                        displayData(mResults);
                    }
                })
        );
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (MyPbRecyclerAdapter.Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


}
