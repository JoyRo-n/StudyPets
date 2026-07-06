package edu.uph.m24si2.studypets.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m24si2.studypets.R;
import edu.uph.m24si2.studypets.room.UserEntity;

// Pola persis MahasiswaAdapter dari materi dosen
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<UserEntity> list;
    onDeleteClick listener;

    public interface onDeleteClick {
        void onDelete(UserEntity user);
    }

    public UserAdapter(List<UserEntity> list, onDeleteClick listener) {
        this.list     = list;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txvUsername;
        TextView txvPassword;
        TextView txvId;
        TextView txvPetType;
        Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            txvUsername = itemView.findViewById(R.id.txvUsername);
            txvPassword = itemView.findViewById(R.id.txvPassword);
            txvId       = itemView.findViewById(R.id.txvId);
            txvPetType  = itemView.findViewById(R.id.txvPetType);
            btnDelete   = itemView.findViewById(R.id.btnDelete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserEntity u = list.get(position);
        holder.txvUsername.setText(u.username);
        holder.txvPassword.setText(u.password);
        holder.txvId.setText("ID: " + u.id);
        holder.txvPetType.setText("Pet: " + u.petType);
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(u));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // setData() — pola dari materi dosen
    public void setData(List<UserEntity> data) {
        list = data;
        notifyDataSetChanged();
    }
}
