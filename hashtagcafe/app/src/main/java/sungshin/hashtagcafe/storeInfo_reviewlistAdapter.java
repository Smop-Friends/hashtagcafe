package sungshin.hashtagcafe;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class storeInfo_reviewlistAdapter extends RecyclerView.Adapter<storeInfo_reviewlistAdapter.CustomViewHolder> {

    private ArrayList<storeInfo_reviewlist> mList;
    private Context mContext;

    public class CustomViewHolder extends RecyclerView.ViewHolder {  //아이템 뷰를 저장하는 뷰홀더 클래스.
        protected ImageView cafeimage;
        protected TextView hashtag;
        protected RatingBar ratingstar;
        protected TextView review;

        public CustomViewHolder(View view) {
            super(view);
            this.cafeimage = (ImageView) view.findViewById(R.id.cafeimage);
            this.hashtag = (TextView) view.findViewById(R.id.hashtag);
            this.ratingstar = (RatingBar) view.findViewById(R.id.ratingstar);
            this.review = (TextView) view.findViewById(R.id.review);
        }


    }

    public storeInfo_reviewlistAdapter(Context context, ArrayList<storeInfo_reviewlist> list) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {  // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_store_info_reviewlist, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {  //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.

        viewholder.cafeimage.setImageResource(mList.get(position).getCafeimage());
        viewholder.hashtag.setText(mList.get(position).getHashtag());
        viewholder.ratingstar.setRating(mList.get(position).getRating());
        viewholder.review.setText(mList.get(position).getReview());

        viewholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mList.get(position).userID;
                String cafename = mList.get(position).getCafeName();

                Intent intent = new Intent(mContext, reviewprofile_main.class);
                intent.putExtra("userID",id);
                intent.putExtra("cafename",cafename);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {   // 전체 데이터 갯수 리턴.
        return (null != mList ? mList.size() : 0);
    }

}