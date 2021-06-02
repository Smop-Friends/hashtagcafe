package sungshin.hashtagcafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class reviewprofile_ImageSliderAdapter extends RecyclerView.Adapter<reviewprofile_ImageSliderAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> sliderImage;

    public reviewprofile_ImageSliderAdapter(Context context, ArrayList<String> sliderImage) {
        this.context = context;
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) { //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
        holder.bindSliderImage(sliderImage.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderImage.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {  //아이템 뷰를 저장하는 뷰홀더 클래스.

        private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) { // 생성자
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
        }

        public void bindSliderImage(String imageURL) { // 함수
            Glide.with(context)
                    .load(imageURL)
                    .into(mImageView);
        }
    }
}