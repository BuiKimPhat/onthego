package com.leobkdn.onthego.ui.intro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leobkdn.onthego.R;

import java.util.List;

// Working with UI
public class IntroAdapter extends RecyclerView.Adapter<IntroAdapter.IntroViewHolder> {
    // List of introItems
    private List<IntroItem> introItemList;
    //constructor of adapter, passing introItemList as parameter of class
    public IntroAdapter(List<IntroItem> introItemList) {
        this.introItemList = introItemList;
    }

    //  managing index of indicators
    @NonNull
    @Override
    public IntroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IntroViewHolder(
                //Transform xml file into View in java, and maybe bind to parent
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.intro_item_container, parent, false
                )
        );
    }
    // pass introItemList[position]'s data to introViewHolder
    @Override
    public void onBindViewHolder(@NonNull IntroViewHolder holder, int position) {
        holder.setIntroData(introItemList.get(position));
    }
    //size of introItemList
    @Override
    public int getItemCount() {
        return introItemList.size();
    }

    //each UI View of intro
    class IntroViewHolder extends RecyclerView.ViewHolder {
        private TextView introTitle;
        private TextView introDescription;
        private ImageView introImage;

        //constructor of holder, holder attributes connect to UI View
        IntroViewHolder(@NonNull View itemView) {
            super(itemView);
            introTitle = itemView.findViewById(R.id.introTitle);
            introDescription = itemView.findViewById(R.id.introDescription);
            introImage = itemView.findViewById(R.id.introImage);
        }
        //edit values in UI View
        void setIntroData(IntroItem introItem){
            introTitle.setText(introItem.getTitle());
            introDescription.setText(introItem.getDescription());
            introImage.setImageResource(introItem.getImage());
        }
    }
}
