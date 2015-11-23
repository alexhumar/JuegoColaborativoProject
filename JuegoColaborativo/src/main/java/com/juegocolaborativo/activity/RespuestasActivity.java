package com.juegocolaborativo.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.juegocolaborativo.JuegoColaborativo;
import com.juegocolaborativo.R;
import com.juegocolaborativo.adapter.ResultadosAdapter;
import com.juegocolaborativo.model.Respuesta;

import java.util.ArrayList;

public class RespuestasActivity extends DefaultActivity {

    private ListView respuestasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuestas);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.pieza, menu);
        return true;
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_respuestas, container, false);
            /* Por cada subgrupo conocido generar una entrada en la lista. */
            JuegoColaborativo app = (JuegoColaborativo) getActivity().getApplication();
            /* Creo el adapter custom) y lo asocio con el layout custom. */
            RespuestasActivity currentActivity = ((RespuestasActivity) this.getActivity());
            currentActivity.setRespuestasView((ListView) rootView.findViewById(R.id.listaRespuestas));
            ArrayList<Respuesta> arrayRespuestas = new ArrayList<>(app.getSubgrupo().getRespuestas().values());
            currentActivity.setResultadosAdapter(new ResultadosAdapter(this.getActivity(), arrayRespuestas));
            currentActivity.getRespuestasView().setAdapter(currentActivity.getResultadosAdapter());
            final View buttonTomarDecision = rootView.findViewById(R.id.tomarDecision);
            buttonTomarDecision.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    }
            );

            return rootView;
        }
    }

    @Override
    protected void onResume() {
        enviarMsjConsultaRespondida();
        super.onResume();
    }

    public ListView getRespuestasView() {
        return respuestasView;
    }

    public void setRespuestasView(ListView respuestasView) {
        this.respuestasView = respuestasView;
    }
}
