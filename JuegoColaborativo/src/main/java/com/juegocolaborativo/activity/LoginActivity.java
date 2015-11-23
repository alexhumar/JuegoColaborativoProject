package com.juegocolaborativo.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.juegocolaborativo.JuegoColaborativo;
import com.juegocolaborativo.R;
import com.juegocolaborativo.model.Coordenada;
import com.juegocolaborativo.model.Grupo;
import com.juegocolaborativo.model.Poi;
import com.juegocolaborativo.model.Posta;
import com.juegocolaborativo.model.Subgrupo;
import com.juegocolaborativo.soap.SoapManager;
import com.juegocolaborativo.task.WSTask;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.ArrayList;

public class LoginActivity extends DefaultActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /* Le agrega al FrameLayout de activity_login.xml el fragment declarado en esta clase. */
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

            final View button = rootView.findViewById(R.id.button_login);
            button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(((TextView) rootView.findViewById(R.id.subgrupo)).getText().toString().trim().length() == 0){
                            String mensaje = getActivity().getString(R.string.toast_ingresar_subgrupo);
                            Toast toast = Toast.makeText(rootView.getContext(), mensaje, Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            ((LoginActivity) getActivity()).showProgressDialog(R.string.dialog_verificando);
                            /* Obtengo el subgrupo de la vista. */
                            String nombreSubgrupo = ((TextView) rootView.findViewById(R.id.subgrupo)).getText().toString();
                            /* Password por ahora no utilizamos. */
                            //String password = ((TextView) rootView.findViewById(R.id.password)).getText().toString();
                            /* Instancio la clase que ejecuta el web service. */
                            WSTask loginTask = new WSTask();
                            loginTask.setReferer(getActivity());
                            loginTask.setMethodName(SoapManager.METHOD_LOGIN);
                            loginTask.addStringParameter("nombreSubgrupo", nombreSubgrupo);
                            loginTask.executeTask("completeLoginTask", "errorLoginTask");
                        }
                    }
                }
            );

            return rootView;
        }
    }

    @SuppressWarnings("unused")
    public void completeLoginTask(SoapObject result) {
        SoapPrimitive res = (SoapPrimitive) result.getProperty("valorInteger");
        int idSubgrupo = Integer.parseInt(res.toString());
        if(idSubgrupo == -1){
            this.showDialogInfo(R.string.dialog_subgrupo_incorrecto);
        } else {
            ((JuegoColaborativo) getApplication()).setSubgrupo(new Subgrupo(idSubgrupo));
            this.closeProgressDialog();
            this.showProgressDialog(R.string.dialog_obteniendo_poi);
            WSTask puntoInicialTask = new WSTask();
            puntoInicialTask.setReferer(this);
            puntoInicialTask.setMethodName(SoapManager.METHOD_PUNTO_INICIAL);
            puntoInicialTask.addStringParameter("idSubgrupo", res.toString());
            puntoInicialTask.executeTask("completePuntoInicial", "errorPuntoInicial");
        }
    }

    @SuppressWarnings("unused")
    public void completePuntoInicial(SoapObject result) {
        int idxPoiSubgrupo = 0;
        int idxPoiSiguiente = 1;
        double latitud = Double.parseDouble(((SoapObject) result.getProperty(idxPoiSubgrupo)).getProperty("coordenadaY").toString());
        double longitud = Double.parseDouble(((SoapObject) result.getProperty(idxPoiSubgrupo)).getProperty("coordenadaX").toString());
        Poi poiSubgrupo = new Poi(new Coordenada(latitud, longitud));
        latitud = Double.parseDouble(((SoapObject) result.getProperty(idxPoiSiguiente)).getProperty("coordenadaY").toString());
        longitud = Double.parseDouble(((SoapObject) result.getProperty(idxPoiSiguiente)).getProperty("coordenadaX").toString());
        Poi poiSiguiente = new Poi(new Coordenada(latitud, longitud));
        Posta postaSiguiente = new Posta(null, poiSiguiente);
        Posta postaSubgrupo = new Posta(postaSiguiente,poiSubgrupo);
        ((JuegoColaborativo) getApplication()).getSubgrupo().setPosta(postaSubgrupo);
        String idSubgrupo = Integer.toString(((JuegoColaborativo) getApplication()).getSubgrupo().getId());
        WSTask subgruposTask = new WSTask();
        subgruposTask.setReferer(this);
        subgruposTask.setMethodName(SoapManager.METHOD_GET_SUBGRUPOS);
        subgruposTask.addStringParameter("idSubgrupo", idSubgrupo);
        subgruposTask.executeTask("completeGetSubgrupos", "errorGetSubgrupos");
    }

    @SuppressWarnings("unused")
    public void errorLoginTask(String failedMethod){
        showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    @SuppressWarnings("unused")
    public void errorPuntoInicial(String failedMethod){
        showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    @SuppressWarnings("unused")
    public void completeGetSubgrupos(SoapObject result) {
        /* Se carga la coleccion de subgrupos excluyendo al subgrupo del dispositivo y se invoca a MapActivity */
        try{
            ((JuegoColaborativo) getApplication()).getSubgrupo().setGrupo(new Grupo());
            ((JuegoColaborativo) getApplication()).getSubgrupo().getGrupo().setSubgrupos(new ArrayList<Subgrupo>());
            for (int i = 0; i < result.getPropertyCount(); i++) {
                int idSubgrupo = Integer.parseInt(((SoapObject) result.getProperty(i)).getProperty("id").toString());
                if (idSubgrupo != ((JuegoColaborativo) getApplication()).getSubgrupo().getId()) {
                    String nombreSubgrupo = ((SoapObject) result.getProperty(i)).getProperty("nombre").toString();
                    Subgrupo subgrupo = new Subgrupo(idSubgrupo);
                    subgrupo.setNombre(nombreSubgrupo);
                    ((JuegoColaborativo) getApplication()).getSubgrupo().getGrupo().getSubgrupos().add(subgrupo);
                }
            }
            this.startActivity(new Intent(this, MapActivity.class));
        } catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void errorGetSubgrupos(String failedMethod){
        showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }
}
