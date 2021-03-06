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

import andreyskakunenko.exchangeratespbandnbu.Model.NbuToday;
import andreyskakunenko.exchangeratespbandnbu.R;

import static java.lang.Math.round;

class NbuViewHolder extends RecyclerView.ViewHolder{
    TextView textCcy,uanPrice,unitCcy;
   public CardView nbuCard;


    public NbuViewHolder(@NonNull View itemView) {
        super(itemView);
        textCcy = itemView.findViewById(R.id.ccy_name_ru);
        uanPrice = itemView.findViewById(R.id.uan_price);
        unitCcy = itemView.findViewById(R.id.ccy_name_en);
        nbuCard = itemView.findViewById(R.id.card_nbu);
    }
}


public class MyNbuRecyclerAdapter extends RecyclerView.Adapter<NbuViewHolder> {
    private Context mContext;
    private List<NbuToday> results;
    public int updatingPos = -1;//


    public MyNbuRecyclerAdapter(Context context, List<NbuToday> results) {
        mContext = context;
        this.results = results;
    }

    @NonNull
    @Override
    public NbuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.nbu_cource_item,viewGroup,false);
        return new NbuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NbuViewHolder holder, int i) {

        if (updatingPos == i){
            holder.itemView.setBackgroundColor(/*updatingPos == i ?*/Color.GREEN /*: Color.TRANSPARENT*/);
        }else {

            if (round(i % 2) == 0) {
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackground));
                //holder.nbuCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorBackground));
            } else {
                holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundW));
                //holder.nbuCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorBackgroundW));
            }
        }
        holder.unitCcy.setText(results.get(i).getCc());
        holder.textCcy.setText(results.get(i).getTxt());
        holder.uanPrice.setText(results.get(i).getRate()+"");

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void updateHighlightPosition(int position){
        notifyItemChanged(position);
        updatingPos = position;
        notifyItemChanged(position);
    }
}
