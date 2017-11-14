package de.cineaste.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import de.cineaste.android.Constants;
import de.cineaste.android.R;
import de.cineaste.android.database.NearbyMessageHandler;
import de.cineaste.android.entity.MatchingResult;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

	private final NearbyMessageHandler handler;

	private final List<MatchingResult> results;
	private final OnMovieSelectListener listener;
	private Context context;
	private final int rowLayout;

	public interface OnMovieSelectListener {
		void onMovieSelectListener(int position);
	}

	public ResultAdapter(
			List<MatchingResult> results,
			OnMovieSelectListener listener) {
		this.results = results;
		this.rowLayout = R.layout.card_result;
		this.listener = listener;
		handler = NearbyMessageHandler.getInstance();
	}

	@Override
	public int getItemCount() {
		return results == null ? 0 : results.size();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		context = parent.getContext();
		View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		holder.assignData(results.get(position), handler.getSize());
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		final ImageView moviePoster;
		final Button watchedButton;
		final TextView title, counter;

		public ViewHolder(final View itemView) {
			super(itemView);
			title = itemView.findViewById(R.id.movie_title);
			counter = itemView.findViewById(R.id.movie_counter_tv);
			watchedButton = itemView.findViewById(R.id.watched_button);
			moviePoster = itemView.findViewById(R.id.movie_poster_image_view);
		}

		public void assignData(MatchingResult matchingResult, int resultCounter) {
			String posterUri =
					Constants.POSTER_URI_SMALL
							.replace("<posterName>", matchingResult.getPosterPath() != null ?
									matchingResult.getPosterPath() : "/")
							.replace("<API_KEY>", context.getString(R.string.movieKey));
			Picasso.with(context)
					.load(Uri.parse(posterUri))
					.resize(222, 334)
					.error(R.drawable.placeholder_poster)
					.into(moviePoster);
			watchedButton.setOnClickListener(this);
			title.setText(matchingResult.getTitle());
			counter.setText(
					String.format(Locale.getDefault(), "%d/%d", matchingResult.getCounter(), resultCounter)
			);
		}

		@Override
		public void onClick(View v) {
			int position = getAdapterPosition();
			if (listener != null)
				listener.onMovieSelectListener(position);
			results.remove(position);
			notifyItemRemoved(position);
		}
	}
}
