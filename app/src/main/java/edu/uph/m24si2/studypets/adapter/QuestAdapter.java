package edu.uph.m24si2.studypets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uph.m24si2.studypets.R;
import edu.uph.m24si2.studypets.model.Quest;

// QuestAdapter — menampilkan daftar quest di RecyclerView
// Pola sesuai materi dosen: ada setData() untuk refresh data
public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    // Interface untuk event complete dan delete — dikirim ke Activity
    public interface OnQuestCompleteListener {
        void onComplete(Quest quest);
    }

    public interface OnQuestDeleteListener {
        void onDelete(Quest quest);
    }

    public interface OnSubmitBuktiListener {
        void onSubmitBukti(Quest quest);
    }

    private Context context;
    private List<Quest> questList;
    private OnQuestCompleteListener completeListener;
    private OnQuestDeleteListener deleteListener;
    private OnSubmitBuktiListener submitBuktiListener;

    public QuestAdapter(Context context, List<Quest> questList,
                        OnQuestCompleteListener completeListener,
                        OnQuestDeleteListener deleteListener) {
        this.context          = context;
        this.questList        = questList;
        this.completeListener = completeListener;
        this.deleteListener   = deleteListener;
    }

    public void setSubmitBuktiListener(OnSubmitBuktiListener listener) {
        this.submitBuktiListener = listener;
    }

    // setData() — method untuk update data dari luar adapter
    // Pola dari materi dosen: adapter.setData(db.mahasiswaDAO().getMahasiswa())
    public void setData(List<Quest> data) {
        this.questList = data;
        notifyDataSetChanged(); // beritahu RecyclerView ada perubahan data
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quest, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        Quest quest = questList.get(position);

        holder.tvTitle.setText(quest.getTitle());
        holder.tvSubject.setText(quest.getSubject());
        holder.tvDeadline.setText("📅 " + quest.getDeadline());
        holder.tvDifficulty.setText(quest.getDifficulty());

        // Label reward berbeda sesuai tipe quest
        if (quest.isFromAdmin()) {
            holder.tvReward.setText("👑 Admin Quest  |  ⚡ " + quest.getXpReward() + " XP  🪙 " + quest.getCoinReward());
            holder.tvReward.setTextColor(0xFFFF9800);
        } else {
            holder.tvReward.setText("📝 Quest Pribadi  |  Tanpa Reward");
            holder.tvReward.setTextColor(0xFF9E9E9E);
        }

        // Warna badge kesulitan
        if (quest.getDifficulty().equalsIgnoreCase("easy")) {
            holder.tvDifficulty.setBackgroundResource(R.drawable.bg_badge_easy);
        } else if (quest.getDifficulty().equalsIgnoreCase("hard")) {
            holder.tvDifficulty.setBackgroundResource(R.drawable.bg_badge_hard);
        } else {
            holder.tvDifficulty.setBackgroundResource(R.drawable.bg_badge_medium);
        }

        boolean isPending   = quest.getStatus().equals("pending");
        boolean isSubmitted = quest.getStatus().equals("submitted");

        // Banner menunggu review
        holder.tvStatusSubmitted.setVisibility(isSubmitted ? View.VISIBLE : View.GONE);

        if (quest.isFromAdmin()) {
            // Quest admin: tombol "Submit Bukti" saat pending, sembunyikan saat submitted
            if (isPending && submitBuktiListener != null) {
                holder.btnComplete.setVisibility(View.VISIBLE);
                holder.btnComplete.setText("📷 Submit Bukti");
                holder.btnComplete.setOnClickListener(v -> submitBuktiListener.onSubmitBukti(quest));
            } else {
                holder.btnComplete.setVisibility(View.GONE);
            }
            // Quest admin tidak bisa dihapus user
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            // Quest pribadi: tombol complete biasa
            if (isPending && completeListener != null) {
                holder.btnComplete.setVisibility(View.VISIBLE);
                holder.btnComplete.setText("✅ Selesai");
                holder.btnComplete.setOnClickListener(v -> completeListener.onComplete(quest));
            } else {
                holder.btnComplete.setVisibility(View.GONE);
            }
            // Quest pribadi bisa dihapus
            if (deleteListener != null) {
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(quest));
            } else {
                holder.btnDelete.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    // ViewHolder menyimpan referensi ke komponen layout satu item quest
    static class QuestViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubject, tvDeadline, tvReward, tvDifficulty, tvStatusSubmitted;
        Button btnComplete, btnDelete;

        QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle           = itemView.findViewById(R.id.tv_quest_title);
            tvSubject         = itemView.findViewById(R.id.tv_quest_subject);
            tvDeadline        = itemView.findViewById(R.id.tv_quest_deadline);
            tvReward          = itemView.findViewById(R.id.tv_quest_reward);
            tvDifficulty      = itemView.findViewById(R.id.tv_quest_difficulty);
            tvStatusSubmitted = itemView.findViewById(R.id.tv_quest_status_submitted);
            btnComplete       = itemView.findViewById(R.id.btn_complete_quest);
            btnDelete         = itemView.findViewById(R.id.btn_delete_quest);
        }
    }
}
