package de.cineaste.android.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import de.cineaste.android.entity.Movie;

public abstract class BaseWatchlistAdapter extends RecyclerView.Adapter< RecyclerView.ViewHolder> {

    List<Movie> dataset;
    List<Movie> filteredDataset;
    String oldSearchTerm;

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
                        int location = indexInAlphabeticalOrder(currentMovie, filteredDataset);
                        filteredDataset.add(location, currentMovie);
                        notifyItemInserted(location);
                    }
                }
            }
        } else{
            filteredDataset.clear();
            filteredDataset.addAll(dataset);
            notifyDataSetChanged();
        }
    }

    int indexInAlphabeticalOrder(Movie movie, List<Movie> movies){
        for(int i = 0; i < movies.size(); ++i){
            if(movie.compareTo(movies.get(i)) <= 0){
                return i;
            }
        }
        return movies.size();
    }

    public int getDatasetSize() {
        return dataset.size();
    }
}