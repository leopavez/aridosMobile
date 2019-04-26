package com.ingenieriasantafe.leandro.aridosmobile;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OperadorSearchAdapter extends ArrayAdapter<Operadores>  {

    private Context context;
    private int LIMIT = 5;
    private List<Operadores> operadores;

    public OperadorSearchAdapter(Context context, List<Operadores> operadores){
        super(context, R.layout.operadores_search,operadores);
        this.context = context;
        this.operadores = operadores;
    }

    @Override
    public int getCount(){
        return Math.min(LIMIT, operadores.size());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.operadores_search,null);
        Operadores vh = operadores.get(position);
        TextView textviewpatente = view.findViewById(R.id.textviewOperadorSearch);
        textviewpatente.setText(vh.getNombre());
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new OperadorFilter(this,context);
    }

    private class OperadorFilter extends Filter{

        private OperadorSearchAdapter operadorSearchAdapter;
        private Context context;

        public OperadorFilter(OperadorSearchAdapter operadorSearchAdapter, Context context){
            super();
            this.operadorSearchAdapter = operadorSearchAdapter;
            this.context = context;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            operadorSearchAdapter.operadores.clear();
            FilterResults filterResults = new FilterResults();
            if (charSequence == null || charSequence.length() == 0){
                filterResults.values = new ArrayList<Vehiculos>();
                filterResults.count = 0;

            }else{
                DatabaseHelper db = new DatabaseHelper(context);
                List<Operadores> ope = db.searchOperador(charSequence.toString());
                filterResults.values = ope;
                filterResults.count = ope.size();

            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            operadorSearchAdapter.operadores.clear();
            if ((List<Vehiculos>)filterResults.values != null){
                operadorSearchAdapter.operadores.addAll((List<Operadores>)filterResults.values);
                operadorSearchAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Operadores operadores = (Operadores) resultValue;
            return operadores.getNombre();
        }
    }
}

