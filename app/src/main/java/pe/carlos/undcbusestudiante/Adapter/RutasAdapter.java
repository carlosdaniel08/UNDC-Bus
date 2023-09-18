package pe.carlos.undcbusestudiante.Adapter;

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
import pe.carlos.undcbusestudiante.Class.Ruta;
import pe.carlos.undcbusestudiante.Class.Salida;
import pe.carlos.undcbusestudiante.Class.Retorno;

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.RutaViewHolder> {

    private List<Ruta> rutas = new ArrayList<>();
    private List<Salida> salidas = new ArrayList<>(); // Agrega esta lista para el SalidaAdapter
    private List<Retorno> retornos = new ArrayList<>();

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

        // Configura recyclerViewRetornos con el adaptador RetornoAdapter
        RetornoAdapter retornoAdapter = new RetornoAdapter();
        retornoAdapter.setRetornos(ruta.getRetornos()); // Asegúrate de que esta lista no esté vacía
        holder.recyclerViewRetornos.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerViewRetornos.setAdapter(retornoAdapter);

    }

    @Override
    public int getItemCount() {
        return rutas.size();
    }

    public class RutaViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewSalidas;
        RecyclerView recyclerViewRetornos;
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
            recyclerViewRetornos = itemView.findViewById(R.id.recyclerViewRetornos);

        }
    }
}
