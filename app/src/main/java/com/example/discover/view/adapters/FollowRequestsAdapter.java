package com.example.discover.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discover.R;
import com.example.discover.model.FollowRequestsModel;

import java.util.ArrayList;

/**
 * Adapter class for displaying follow requests in a RecyclerView.
 */
public class FollowRequestsAdapter extends RecyclerView.Adapter<FollowRequestsAdapter.ViewHolder> {

    private final ArrayList<FollowRequestsModel> followRequestsModelArrayList;
    private final Context context;

    /**
     * Constructor for the FollowRequestsAdapter.
     *
     * @param followRequestsModelArrayList The list of follow requests to be displayed.
     * @param context                      The context in which the adapter is used.
     */
    public FollowRequestsAdapter(ArrayList<FollowRequestsModel> followRequestsModelArrayList, Context context) {
        this.followRequestsModelArrayList = followRequestsModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.follow_requests_rv_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowRequestsModel model = followRequestsModelArrayList.get(position);
        bindFollowRequest(holder, model);
    }

    @Override
    public int getItemCount() {
        return followRequestsModelArrayList.size();
    }

    /**
     * Binds the follow request data to the ViewHolder.
     *
     * @param holder The ViewHolder to bind data to.
     * @param model  The follow request data to be displayed.
     */
    private void bindFollowRequest(@NonNull ViewHolder holder, FollowRequestsModel model) {
        holder.dUserProfileImage.setImageResource(model.getUserProfileImage());
        holder.dFollowRequestText.setText(model.getFollowRequestText());
    }

    /**
     * ViewHolder class for holding follow request views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView dUserProfileImage;
        private final TextView dFollowRequestText;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The view to be held by the ViewHolder.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dUserProfileImage = itemView.findViewById(R.id.userProfileFollowRequestsPageImageView);
            dFollowRequestText = itemView.findViewById(R.id.followRequestTextContainerTextView);
        }
    }
}
