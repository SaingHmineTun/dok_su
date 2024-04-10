package it.saimao.doksu.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.MessageFormat;

import it.saimao.doksu.R;

/* renamed from: it.saimao.doksu.adapters.MyListAdapter */
public class DokSuAdapter extends RecyclerView.Adapter<DokSuViewHolder> {
    private final String[] titles;
    private final DokSuItemClickListener listener;

    public DokSuAdapter(String[] titles, DokSuItemClickListener listener) {
        this.titles = titles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DokSuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doksu, parent, false);
        return new DokSuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DokSuViewHolder holder, int position) {
        String title = titles[position];
        holder.tvTitle.setText(MessageFormat.format("({0}) {1}", position + 1, title));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(position + 1));
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }
}

class DokSuViewHolder extends RecyclerView.ViewHolder {
    TextView tvTitle;

    public DokSuViewHolder(View view) {
        super(view);
        tvTitle = view.findViewById(R.id.txtTitle);

    }
}
