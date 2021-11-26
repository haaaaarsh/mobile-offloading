package com.group70.mobileoffloading.ui.master;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.group70.mobileoffloading.R;
import com.group70.mobileoffloading.data.Slave;
import com.group70.mobileoffloading.databinding.ItemSlaveBinding;

import java.util.ArrayList;
import java.util.List;

public class SlaveAdapter extends RecyclerView.Adapter<SlaveAdapter.ViewHolder> {
    private List<Slave> slavesList;
    private Activity context;
    private LayoutInflater inflater;
    private SlaveClickListener slaveClickListener;

    public SlaveAdapter(Activity context, SlaveClickListener slaveClickListener) {
        this.context = context;
        this.slavesList = new ArrayList<>();
        this.slaveClickListener = slaveClickListener;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSlaveBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_slave, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return slavesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemSlaveBinding binding;

        ViewHolder(ItemSlaveBinding bind) {
            super(bind.getRoot());
            this.binding = bind;
        }

        void bindData(final int position) {
            binding.name.setText(slavesList.get(position).name);
            binding.rlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    slaveClickListener.onSlaveClick(slavesList.get(position));
                }
            });
        }
    }

    public List<Slave> getList() {
        return this.slavesList;
    }

    public void addAll(List<Slave> list) {
        this.slavesList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearAll() {
        this.slavesList.clear();
        notifyDataSetChanged();
    }

    public void add(Slave s) {
        this.slavesList.add(s);
        notifyDataSetChanged();
    }

    public void remove(Slave s) {
        this.slavesList.remove(s);
        notifyDataSetChanged();
    }

    public interface SlaveClickListener {
        public void onSlaveClick(Slave slave);
    }
}