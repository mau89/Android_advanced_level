package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.data.CityAdd;
import com.example.myapplication.data.VillageAdd;
import java.util.List;


public class CityAddAdapter extends RecyclerView.Adapter<CityAddAdapter.ViewHolder> {

  private static final int CITY_TYPE = 0;
  private static final int VILLAGE_TYPE = 1;

  private List<Object> items;
  private OnClickCity onClickCity;
  private OnClickDelButton onClickDelButton;
  private OnLongClickCity onLongClickCity;

  public void setOnClickDelButton(OnClickDelButton onClickDelButton) {
    this.onClickDelButton = onClickDelButton;
  }

  public void setOnLongClickCity(OnLongClickCity onLongClickCity) {
    this.onLongClickCity = onLongClickCity;
  }


  public void setOnClickCity(OnClickCity onClickCity) {
    this.onClickCity = onClickCity;
  }

  public CityAddAdapter(List<Object> items) {
    this.items = items;
  }

  public void removeItem(Object item) {
    int position = items.indexOf(item);
    if (position == -1) {
      return;
    }
    items.remove(position);
    notifyItemRemoved(position);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    if (viewType == CITY_TYPE) {
      return new CityViewHolder(layoutInflater.inflate(R.layout.item_city, parent, false));
    } else {
      return new VillageViewHolder(layoutInflater.inflate(R.layout.item_village, parent, false));
    }
  }


  @Override
  public int getItemViewType(int position) {
    if (items.get(position) instanceof CityAdd) {
      return CITY_TYPE;
    } else {
      return VILLAGE_TYPE;
    }
  }


  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Object item = items.get(position);
    holder.bindView(items.get(position));
    holder.itemView.setOnClickListener(v -> onClickCity.onClickCity(item));
    //holder.itemView.setOnClickListener(v -> onClickDelButton.onClickDelButton(item));
    holder.itemView.setOnLongClickListener(v -> {
      onLongClickCity.onLongClickCity(item);
      return true;
    });
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  public class CityViewHolder extends ViewHolder {

    TextView stringCity;
    TextView stringTemperature;

    public CityViewHolder(@NonNull View itemView) {
      super(itemView);
      stringCity = itemView.findViewById(R.id.text_view_city);
      stringTemperature = itemView.findViewById(R.id.text_view_city_temperature);
    }

    @Override
    public void bindView(Object element) {
      stringCity.setText(((CityAdd) element).getStringCity());
      stringTemperature.setText(((CityAdd) element).getStringTemperature());
    }
  }

  public class VillageViewHolder extends ViewHolder {

    TextView stringDay;
    TextView stringTemperature;

    public VillageViewHolder(@NonNull View itemView) {
      super(itemView);
      stringDay = itemView.findViewById(R.id.text_view_village);
      stringTemperature = itemView.findViewById(R.id.text_view_village_temperature);
    }

    @Override
    public void bindView(Object element) {
      stringDay.setText(((VillageAdd) element).getStringDay());
      stringTemperature.setText(((VillageAdd) element).getStringTemperature());
    }
  }

  public abstract class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(@NonNull View itemView) {
      super(itemView);
    }

    public abstract void bindView(Object element);
  }

  public interface OnClickCity {
    void onClickCity(Object objects);
  }

  public interface OnClickDelButton {
    void onClickDelButton(Object objects);
  }

  public interface OnLongClickCity {
    void onLongClickCity(Object objects);
  }
}
