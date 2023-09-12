package pe.carlos.undcbusestudiante.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pe.carlos.undcbusestudiante.R;
import pe.carlos.undcbusestudiante.Salida;

public class SalidaAdapter extends RecyclerView.Adapter<SalidaAdapter.SalidaViewHolder>  {

    private List<Salida> salidas = new ArrayList<>();

    public void setSalidas(List<Salida> salidas) {
        this.salidas = salidas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SalidaAdapter.SalidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salida, parent, false);
        return new SalidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalidaAdapter.SalidaViewHolder holder, int position) {
        Salida salida = salidas.get(position);

        // Vincula los datos de la salida a los elementos de interfaz en el ViewHolder
        holder.paraderoSalidaTextView.setText(salida.getParaderosalida());
        holder.horarioSalidaTextView.setText(salida.getHorariosalida());
    }

    @Override
    public int getItemCount() {
        return salidas.size();
    }

    public class SalidaViewHolder extends RecyclerView.ViewHolder {
        TextView paraderoSalidaTextView;
        TextView horarioSalidaTextView;


        public SalidaViewHolder(View itemView) {
            super(itemView);

            // Inicializa elementos de interfaz aquí
            paraderoSalidaTextView = itemView.findViewById(R.id.ParaderoSalidaTextView);
            horarioSalidaTextView = itemView.findViewById(R.id.horarioSalidaTextView);

        }
    }
}

