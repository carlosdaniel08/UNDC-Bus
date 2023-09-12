package pe.carlos.undcbusestudiante.Adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pe.carlos.undcbusestudiante.R;
import pe.carlos.undcbusestudiante.Ruta;
import pe.carlos.undcbusestudiante.Salida;

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.RutaViewHolder> {

    private List<Ruta> rutas = new ArrayList<>();
    private List<Salida> salidas = new ArrayList<>(); // Agrega esta lista para el SalidaAdapter


    public void setRutas(List<Ruta> rutas) {
        this.rutas = rutas;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RutaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ruta, parent, false);
        return new RutaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutaViewHolder holder, int position) {
        Ruta ruta = rutas.get(position);

        // Vincula los datos de la ruta a los elementos de interfaz en el ViewHolder
        holder.nombreTextView.setText(ruta.getNombre());
        holder.turnoTextView.setText(ruta.getTurno());
        holder.puntoRecojoTextView.setText(ruta.getPuntoRecojo());

        // Configura recyclerViewSalidas con el adaptador SalidaAdapter
        SalidaAdapter salidaAdapter = new SalidaAdapter();
        salidaAdapter.setSalidas(ruta.getSalidas()); // Asegúrate de que esta lista no esté vacía
        holder.recyclerViewSalidas.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerViewSalidas.setAdapter(salidaAdapter);

    }

    @Override
    public int getItemCount() {
        return rutas.size();
    }

    public class RutaViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewSalidas;
        TextView nombreTextView;
        TextView turnoTextView;
        TextView puntoRecojoTextView;

        public RutaViewHolder(View itemView) {
            super(itemView);

            // Inicializa elementos de interfaz aquí
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            turnoTextView = itemView.findViewById(R.id.turnoTextView);
            puntoRecojoTextView = itemView.findViewById(R.id.puntoRecojoTextView);
            recyclerViewSalidas = itemView.findViewById(R.id.recyclerViewSalidas); // Asegúrate de tener este ID en tu diseño XML

        }
    }
}
