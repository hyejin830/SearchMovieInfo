package jeong.hyejin.searchmovieinfo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private ArrayList<MovieInfoItem> mMovieInfoItemArrayList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    MovieInfoAdapter(Context context, ArrayList<MovieInfoItem> movieInfoItemArrayList){
        mContext = context;
        mMovieInfoItemArrayList = movieInfoItemArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_row,parent,false);
        return new MovieInfoHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MovieInfoItem currentItem = mMovieInfoItemArrayList.get(position);

        String imageUrl = currentItem.getImageUrl();
        String subject = currentItem.getTitle();
        float user_rate = (float)currentItem.getUserRating()/(float)2;
        String pub_date = currentItem.getPubDate();
        String director = currentItem.getDirector();
        String actors = currentItem.getActors();

        MovieInfoHolder movieInfoHolder = (MovieInfoHolder) holder;
        movieInfoHolder.tvTitle.setText(Html.fromHtml(subject));
        movieInfoHolder.rbRate.setRating(user_rate);
        movieInfoHolder.tvYear.setText(pub_date);
        movieInfoHolder.tvDirector.setText(director);
        movieInfoHolder.tvActor.setText(actors);
        if(!imageUrl.isEmpty()){
            Picasso.with(mContext).load(imageUrl).fit().centerInside().into(movieInfoHolder.ivImageUrl);
        }else{
           movieInfoHolder.ivImageUrl.setImageResource(android.R.color.transparent);
        }
    }

    public void removeAllItems(){
        mMovieInfoItemArrayList.clear();
    }

    @Override
    public int getItemCount() {
        return mMovieInfoItemArrayList.size();
    }

    public class MovieInfoHolder extends RecyclerView.ViewHolder{
        public ImageView ivImageUrl;
        public TextView tvTitle;
        public RatingBar rbRate;
        public TextView tvYear;
        public TextView tvDirector;
        public TextView tvActor;

        public MovieInfoHolder(View view) {
            super(view);
            ivImageUrl = view.findViewById(R.id.thumbnail);
            tvTitle = view.findViewById(R.id.subject);
            rbRate = view.findViewById(R.id.rate);
            tvYear = view.findViewById(R.id.year);
            tvDirector = view.findViewById(R.id.director);
            tvActor = view.findViewById(R.id.actors);

            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }


}
