package andreyskakunenko.exchangeratespbandnbu.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import andreyskakunenko.exchangeratespbandnbu.Model.PbResult;
import andreyskakunenko.exchangeratespbandnbu.R;

class PbViewHolder extends RecyclerView.ViewHolder {
    TextView ccy,buy,sale;
    CardView mCardView;

    public PbViewHolder(@NonNull View itemView) {
        super(itemView);
        ccy = itemView.findViewById(R.id.valuta_pb);
        buy = itemView.findViewById(R.id.valuta_buy_pb);
        sale = itemView.findViewById(R.id.valuta_sale_pb);
        mCardView = itemView.findViewById(R.id.pb_item);
    }
}


public class MyPbRecyclerAdapter extends RecyclerView.Adapter<PbViewHolder> {

    private Context mContext;
    private List<PbResult> results;
    private Callbacks mCallbacks;
    int selectedPos = 8;//


    public MyPbRecyclerAdapter(Context context, List<PbResult> results) {
        mContext = context;
        this.results = results;
    }

    public void setOnClickedListener(Callbacks callbacks) {
        this.mCallbacks = callbacks;
    }


    public interface Callbacks {
        void onValutaSelected(String url);
    }

    @NonNull
    @Override
    public PbViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pb_cource_item,parent,false);
        return new PbViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PbViewHolder holder, int i) {

        holder.itemView.setSelected(selectedPos == i);//
        holder.itemView.setBackgroundColor(selectedPos == i ? Color.GREEN : Color.TRANSPARENT);//
        String ccystr = results.get(i).getResultCcy();
        if((results.get(i).getResultCcy()).compareTo("BTC")==0)ccystr = "BTCto$";
        holder.ccy.setText(ccystr);
        holder.buy.setText(results.get(i).getResultBuy());
        holder.sale.setText(results.get(i).getResultSale());
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selectedPos);//
                selectedPos= holder.getAdapterPosition();//
                notifyItemChanged(selectedPos);//
                String str = results.get(holder.getAdapterPosition()).getResultCcy();
                if((results.get(holder.getAdapterPosition()).getResultCcy()).compareTo("RUR")==0)str = "RUB";
                mCallbacks.onValutaSelected(str);

            }
        });

    }


    @Override
    public int getItemCount() {
        return results.size();
    }
}


