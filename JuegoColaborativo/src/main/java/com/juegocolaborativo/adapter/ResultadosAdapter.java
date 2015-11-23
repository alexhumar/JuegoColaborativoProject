package com.juegocolaborativo.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juegocolaborativo.R;
import com.juegocolaborativo.model.Respuesta;

import java.util.ArrayList;

public class ResultadosAdapter extends ArrayAdapter<Respuesta> {

    private ArrayList<Respuesta> respuestas;
    private final Activity context;

    public ResultadosAdapter(Activity context, ArrayList<Respuesta> list) {
        super(context, R.layout.rowlayout, list);
        this.context = context;
        this.respuestas = list;
    }

    static class ViewHolder {
        protected TextView nombre;
        protected TextView respuesta;
        protected ImageView imagen;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LinearLayout root = (LinearLayout)context.findViewById(R.id.rowrespuestaslayout);
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.rowlayout, root);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.nombre = (TextView) view.findViewById(R.id.label);
            viewHolder.respuesta = (TextView) view.findViewById(R.id.content);
            viewHolder.imagen = (ImageView) view.findViewById(R.id.icon);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.nombre.setText(respuestas.get(position).getSubgrupo().getNombre());
        holder.respuesta.setText(((respuestas.get(position).getJustificacion()) == null) ? context.getString(R.string.item_respuesta_resulado) : respuestas.get(position).getJustificacion());
        if ((respuestas.get(position).getJustificacion()) == null) {
            holder.imagen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_launcher));
        }else{
            if (respuestas.get(position).getCumple() == 1){
                holder.imagen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.check));
            }else{
                holder.imagen.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.uncheck));
            }
        }

        return view;
    }
}
