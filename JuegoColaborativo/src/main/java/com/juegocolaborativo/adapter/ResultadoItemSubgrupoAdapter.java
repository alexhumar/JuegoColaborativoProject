package com.juegocolaborativo.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juegocolaborativo.R;
import com.juegocolaborativo.model.Resultado;

import java.util.List;

public class ResultadoItemSubgrupoAdapter extends BaseAdapter {

    private Context context;
    private List<Resultado> items;

    public ResultadoItemSubgrupoAdapter(Context context, List<Resultado> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (convertView == null) {
            /* Create a new view into the list. */
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item_subgrupo, parent, false);
        }
        /* Set data into the view. */
        TextView pieza = (TextView) rowView.findViewById(R.id.nombrePieza);
        TextView cumple = (TextView) rowView.findViewById(R.id.cumple);
        ImageView imagen = (ImageView) rowView.findViewById(R.id.correcta);
        Resultado item = this.items.get(position);
        pieza.setText(item.getPieza());
        cumple.setText(context.getString(R.string.list_cumple_resulado, item.getCumple() ? context.getString(R.string.dialog_si) : context.getString(R.string.dialog_no)));
        if (item.getCorrecta()){
            imagen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.check));
        }else{
            imagen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.uncheck));
        }

        return rowView;
    }
}
