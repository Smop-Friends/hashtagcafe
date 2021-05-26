// jenny

package sungshin.hashtagcafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<User> userArrayList; // contains User class objects
    private OnItemClickListener mListener = null;

    // constructer
    public MyAdapter(Context context, ArrayList<User> userArrayList) {
        this.context = context;
        this.userArrayList = userArrayList;
    }

    // implement methods
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create the reference variable for the view
        // get layout from item.xml
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // get the object from our userArrayList
        User user = userArrayList.get(position);

        // overwrite with the data
        holder.cafeName.setText(user.cafeName);
        holder.cafeIntroduce.setText(user.cafeIntroduce);
        holder.checkList.setText(user.checkList);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    // inner class: refer to all the elements of the particular item //static 제거
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView cafeName, cafeIntroduce, checkList;
        Button btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        if(mListener != null){
                            mListener.onItemClick(v, position);
                        }
                    }
                }
            });

            cafeName = itemView.findViewById(R.id.tvCafeName);
            cafeIntroduce = itemView.findViewById(R.id.tvCafeIntroduce);
            checkList = itemView.findViewById(R.id.tvCheckList);
        }
    }

    // Search Functionality for RecyclerView
    public void filterList(ArrayList<User> filteredList){
        userArrayList = filteredList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
}
