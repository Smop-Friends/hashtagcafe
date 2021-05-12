package sungshin.hashtagcafe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class review_photoAdapter extends RecyclerView.Adapter<review_photoAdapter.CustomViewHolder> {


    private ArrayList<review_photo> mList;

    public class CustomViewHolder extends RecyclerView.ViewHolder {  //아이템 뷰를 저장하는 뷰홀더 클래스.
        protected ImageView Pic;
        protected ImageButton del;
        public CustomViewHolder(View view) {
            super(view);
            this.Pic = (ImageView) view.findViewById(R.id.reviewPicture);
            this.del = (ImageButton)view.findViewById(R.id.pictureDelete);
        }


    }


    public review_photoAdapter(ArrayList<review_photo> list) {
        this.mList = list;
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {  // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_review_photoitem_list, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {  //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.

        viewholder.Pic.setImageURI(mList.get(position).getPic());

        viewholder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mList.size());
                review_photo.picCount--;
            }
        });
    }

    @Override
    public int getItemCount() {   // 전체 데이터 갯수 리턴.
        return (null != mList ? mList.size() : 0);
    }

}