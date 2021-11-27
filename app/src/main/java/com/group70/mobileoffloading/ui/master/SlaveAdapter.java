package com.group70.mobileoffloading.ui.master;

import android.app.Activity;
import android.location.Location;
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
            Slave s = slavesList.get(position);
            binding.name.setText(s.name);
            binding.battery.setText(String.format("%d%%", (int)s.battery));
            binding.coordinates.setText(formatLatLong(s.latitude, s.longitude));
            binding.llRoot.setOnClickListener(new View.OnClickListener() {
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

    private String formatLatLong(double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();

        if (latitude < 0) {
            builder.append("S ");
        } else {
            builder.append("N ");
        }

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        builder.append(" ");

        if (longitude < 0) {
            builder.append("W ");
        } else {
            builder.append("E ");
        }

        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        return builder.toString();
    }
}