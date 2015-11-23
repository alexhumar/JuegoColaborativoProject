package com.juegocolaborativo.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.juegocolaborativo.JuegoColaborativo;
import com.juegocolaborativo.R;
import com.juegocolaborativo.soap.SoapManager;
import com.juegocolaborativo.task.WSTask;

import org.ksoap2.serialization.SoapObject;

public class ResponderActivity extends DefaultActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.responder, menu);
        return true;
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_responder, container, false);
            TextView tituloPieza = (TextView) rootView.findViewById(R.id.tituloPieza);
            TextView descripcionPieza = (TextView) rootView.findViewById(R.id.descripcionPieza);
            JuegoColaborativo app = (JuegoColaborativo) getActivity().getApplication();
            tituloPieza.setText(app.getSubgrupo().getConsultaActual().getNombrePieza());
            descripcionPieza.setText(app.getSubgrupo().getConsultaActual().getDescripcionPieza());
            TextView tituloConsigna = (TextView) rootView.findViewById(R.id.tituloConsigna);
            TextView descripcionConsigna = (TextView) rootView.findViewById(R.id.descripcionConsigna);
            tituloConsigna.setText(app.getSubgrupo().getGrupo().getConsigna().getNombre());
            descripcionConsigna.setText(app.getSubgrupo().getGrupo().getConsigna().getDescripcion());
            TextView cumpleConsulta = (TextView) rootView.findViewById(R.id.cumpleConsulta);
            cumpleConsulta.setText(getActivity().getString(R.string.form_cumpleconsulta_respuesta, (app.getSubgrupo().getConsultaActual().getCumple() == 0) ? "No" : "Sí"));
            TextView descripcionConsulta = (TextView) rootView.findViewById(R.id.descripcionConsulta);
            descripcionConsulta.setText(getActivity().getString(R.string.form_descripcionconsulta_respuesta, app.getSubgrupo().getConsultaActual().getJustificacion()));
            final View buttonEnviar = rootView.findViewById(R.id.button_responder);
            buttonEnviar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(((TextView) rootView.findViewById(R.id.justificacion)).getText().toString().trim().length() == 0){
                            Toast toast = Toast.makeText(rootView.getContext(), getActivity().getString(R.string.toast_justificacion_respuesta), Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            JuegoColaborativo app = (JuegoColaborativo) getActivity().getApplication();
                            ToggleButton respuesta = (ToggleButton) rootView.findViewById(R.id.respuestaConsulta);
                            ((ResponderActivity) getActivity()).showProgressDialog(R.string.dialog_enviando_respuesta);
                            int idConsulta = app.getSubgrupo().getConsultaActual().getId();
                            int acuerdo = respuesta.isChecked() ? 1 : 0;
                            String justificacion = ((TextView) rootView.findViewById(R.id.justificacion)).getText().toString();
                            app.getSubgrupo().getConsultaActual().setRespondida(1);
                            WSTask guardarRespuestaTask = new WSTask();
                            guardarRespuestaTask.setReferer(getActivity());
                            guardarRespuestaTask.setMethodName(SoapManager.METHOD_GUARDAR_RESPUESTA);
                            guardarRespuestaTask.addStringParameter("idConsulta", Integer.toString(idConsulta));
                            guardarRespuestaTask.addStringParameter("idSubgrupoConsultado", Integer.toString(app.getSubgrupo().getId()));
                            guardarRespuestaTask.addStringParameter("acuerdo", Integer.toString(acuerdo));
                            guardarRespuestaTask.addStringParameter("justificacion", justificacion);
                            guardarRespuestaTask.executeTask("completeGuardarRespuesta", "errorGuardarRespuesta");
                        }
                    }
                }
            );

            return rootView;
        }
    }

    @SuppressWarnings("unused")
    public void completeGuardarRespuesta(SoapObject result) {
        this.finish();
    }

    @SuppressWarnings("unused")
    public void errorGuardarRespuesta(String failedMethod){
        showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }
}