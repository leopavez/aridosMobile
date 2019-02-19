package com.example.leandro.aridosmobile;

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

public class PatentesSearchAdapter extends ArrayAdapter<Vehiculos> {

    private Context context;
    private int LIMIT = 5;
    private List<Vehiculos> vehiculos;

    public PatentesSearchAdapter(Context context, List<Vehiculos> vehiculos){
        super(context, R.layout.pantentes_search,vehiculos);
        this.context = context;
        this.vehiculos = vehiculos;
    }

    @Override
    public int getCount(){
        return Math.min(LIMIT, vehiculos.size());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.pantentes_search,null);
        Vehiculos vh = vehiculos.get(position);
        TextView textviewpatente = view.findViewById(R.id.textviewPatenteSearch);
        textviewpatente.setText(vh.getPatente());
        TextView textviewm3 = view.findViewById(R.id.textViewM3Search);
        textviewm3.setText(vh.getM3());
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new PatentFilter(this,context);
    }

    private class PatentFilter extends Filter{

        private PatentesSearchAdapter patentesSearchAdapter;
        private Context context;

        public PatentFilter(PatentesSearchAdapter patentesSearchAdapter, Context context){
            super();
            this.patentesSearchAdapter = patentesSearchAdapter;
            this.context = context;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            patentesSearchAdapter.vehiculos.clear();
            FilterResults filterResults = new FilterResults();
            if (charSequence == null || charSequence.length() == 0){
                filterResults.values = new ArrayList<Vehiculos>();
                filterResults.count = 0;

            }else{
                DatabaseHelper db = new DatabaseHelper(context);
                List<Vehiculos> patents = db.search(charSequence.toString());
                filterResults.values = patents;
                filterResults.count = patents.size();

            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            patentesSearchAdapter.vehiculos.clear();
            if ((List<Vehiculos>)filterResults.values != null){
                patentesSearchAdapter.vehiculos.addAll((List<Vehiculos>)filterResults.values);
                patentesSearchAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Vehiculos vehiculos = (Vehiculos) resultValue;
            return vehiculos.getPatente();
        }
    }
}
