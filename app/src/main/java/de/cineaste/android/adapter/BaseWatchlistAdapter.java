package de.cineaste.android.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import de.cineaste.android.entity.Movie;

/**
 * Created by christianbraun on 16/03/16.
 */
public abstract class BaseWatchlistAdapter extends RecyclerView.Adapter< RecyclerView.ViewHolder> {

    protected List<Movie> dataset;
    protected List<Movie> filteredDataset;

    public void filter(String searchTerm){
        if(searchTerm != null && !searchTerm.isEmpty()){
            for(Movie currentMovie: dataset){
                String movieTitle = currentMovie.getTitle().toLowerCase();
                int index = filteredDataset.indexOf(currentMovie);
                if(!movieTitle.contains(searchTerm.toLowerCase())){
                    if(index != -1){
                        filteredDataset.remove(index);
                        notifyItemRemoved(index);
                    }
                }else{
                    if(index == -1){
                        filteredDataset.add(currentMovie);
                        notifyItemInserted(filteredDataset.size());
                    }
                }
            }
        } else{
            filteredDataset.clear();
            filteredDataset.addAll(dataset);
            notifyDataSetChanged();
        }
    }
}
