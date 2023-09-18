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
import pe.carlos.undcbusestudiante.Class.Retorno;

public class RetornoAdapter extends RecyclerView.Adapter<RetornoAdapter.RetornoViewHolder>  {

    private List<Retorno> retornos = new ArrayList<>();

    public void setRetornos(List<Retorno> retornos) {
        this.retornos = retornos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RetornoAdapter.RetornoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_retorno, parent, false);
        return new RetornoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RetornoAdapter.RetornoViewHolder holder, int position) {
        Retorno retorno = retornos.get(position);

        // Vincula los datos de la retorno a los elementos de interfaz en el ViewHolder
        holder.paraderoRetornoTextView.setText(retorno.getParaderoretorno());
        holder.horarioRetornoTextView.setText(retorno.getHorarioretorno());
    }

    @Override
    public int getItemCount() {
        if (retornos != null) {
            return retornos.size();
        } else {
            return 0; // Otra acción apropiada si la lista es nula
        }
    }

    public class RetornoViewHolder extends RecyclerView.ViewHolder {
        TextView paraderoRetornoTextView;
        TextView horarioRetornoTextView;


        public RetornoViewHolder(View itemView) {
            super(itemView);

            // Inicializa elementos de interfaz aquí
            paraderoRetornoTextView = itemView.findViewById(R.id.ParaderoRetornoTextView);
            horarioRetornoTextView = itemView.findViewById(R.id.horarioRetornoTextView);

        }
    }
}