package sungshin.hashtagcafe;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class storeInfo_photoAdapter extends RecyclerView.Adapter<storeInfo_photoAdapter.CustomViewHolder>{

    private ArrayList<review_photo> mList;
    Context mContext;

    public class CustomViewHolder extends RecyclerView.ViewHolder {  //아이템 뷰를 저장하는 뷰홀더 클래스.
        protected ImageView Pic;
        protected ImageButton del;
        public CustomViewHolder(View view) {
            super(view);
            this.Pic = (ImageView) view.findViewById(R.id.reviewPicture2);
            this.del = (ImageButton)view.findViewById(R.id.pictureDelete2);
        }
    }


    public storeInfo_photoAdapter(Context context, ArrayList<review_photo> list) {
        this.mList = list;
        this.mContext = context;
    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {  // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.modi_photoitem, viewGroup, false);

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

            }
        });
    }

    @Override
    public int getItemCount() {   // 전체 데이터 갯수 리턴.
        return(null != mList ? mList.size() : 0) ;
    }

}

