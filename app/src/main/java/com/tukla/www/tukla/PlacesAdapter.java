package com.tukla.www.tukla;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Lenovo on 10/23/2017.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder > {

    private List<Places> myPlacesList;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView primaryText,secText,distText;

        public MyViewHolder(View itemView) {
            super( itemView );

            primaryText=(TextView) itemView.findViewById( R.id.primaryText );
            secText=(TextView) itemView.findViewById( R.id.addressDescription );
            distText=(TextView) itemView.findViewById( R.id.distance );
        }
    }

    public PlacesAdapter(List<Places>myPlacesList){
        this.myPlacesList=myPlacesList;
    }


    @Override
    public PlacesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from( parent.getContext())
                .inflate(R.layout.place_autocomplete_adapter,parent,false );

        return new PlacesAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(PlacesAdapter.MyViewHolder holder, int position) {
        Places places =myPlacesList.get( position );
        holder.primaryText.setText( places.getPrimaryText() );
        holder.secText.setText( places.getAddressDescription() );
        holder.distText.setText( places.getDistance() );


    }

    @Override
    public int getItemCount() {
        return myPlacesList.size();
    }
}
