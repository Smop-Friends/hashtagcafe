package sungshin.hashtagcafe;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;


public class review_photoAdapter extends RecyclerView.Adapter<review_photoAdapter.CustomViewHolder> {


    private ArrayList<review_photo> mList;
    Context mContext;
    View mainpicCount;
    TextView picCountnum;

    public class CustomViewHolder extends RecyclerView.ViewHolder {  //아이템 뷰를 저장하는 뷰홀더 클래스.
        protected ImageView Pic;
        protected ImageButton del;
        public CustomViewHolder(View view) {
            super(view);
            this.Pic = (ImageView) view.findViewById(R.id.reviewPicture);
            this.del = (ImageButton)view.findViewById(R.id.pictureDelete);
            picCountnum = (TextView)mainpicCount.findViewById(R.id.picCount);

        }
    }


    public review_photoAdapter(View view, Context context, ArrayList<review_photo> list) {
        this.mainpicCount = view;
        this.mList = list;
        this.mContext = context;
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
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),mList.get(position).getPic());
            viewholder.Pic.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
        viewholder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mList.size());
                review_photo.picCount--;

                picCountnum.setText(review_photo.picCount+"/4");
            }
        });
    }

    @Override
    public int getItemCount() {   // 전체 데이터 갯수 리턴.
        return (null != mList ? mList.size() : 0);
    }

}