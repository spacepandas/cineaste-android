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
    protected String oldSearchTerm;

    public void filter(String searchTerm){
        if(filteredDataset == null)
            return;

        if(searchTerm != null && !searchTerm.isEmpty()){
            oldSearchTerm = searchTerm;
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
            searchTerm = null;
            filteredDataset.clear();
            filteredDataset.addAll(dataset);
            notifyDataSetChanged();
        }
    }

    public int getTotalItemCount(){
        return dataset.size();
    }
}
