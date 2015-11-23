package com.juegocolaborativo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.juegocolaborativo.JuegoColaborativo;
import com.juegocolaborativo.R;
import com.juegocolaborativo.activity.MapActivity;
import com.juegocolaborativo.model.Consigna;
import com.juegocolaborativo.model.Subgrupo;
import com.juegocolaborativo.service.PoolServiceEstados;
import com.juegocolaborativo.soap.SoapManager;
import com.juegocolaborativo.task.WSTask;

import org.ksoap2.serialization.SoapObject;

public class ProximityIntentReceiver extends BroadcastReceiver {

    private JuegoColaborativo application;

    @Override
    public void onReceive(Context context, Intent intent) {

        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        if (entering) {
            if(intent.getAction().equals(MapActivity.PROX_ALERT_POI_SUBGRUPO)){
                /* El subgrupo avisa que ya llego a su poi y está en condiciones de comenzar el juego. */
                Subgrupo subgrupo = this.getApplication().getSubgrupo();
                subgrupo.setEstado(Subgrupo.ESTADO_INICIAL);
                String idSubgrupo = Integer.toString(subgrupo.getId());
                String idEstado = Integer.toString(subgrupo.getEstado());
                /* Ejecuto la tarea que cambia de estado al subgrupo. */
                WSTask cambiarEstadoSubgrupoTask = new WSTask();
                cambiarEstadoSubgrupoTask.setReferer(this);
                cambiarEstadoSubgrupoTask.setMethodName(SoapManager.METHOD_CAMBIAR_ESTADO_SUBGRUPO);
                cambiarEstadoSubgrupoTask.addStringParameter("idSubgrupo", idSubgrupo);
                cambiarEstadoSubgrupoTask.addStringParameter("idEstado", idEstado);
                cambiarEstadoSubgrupoTask.executeTask("completeCambiarEstadoSubgrupo", "errorCambiarEstadoSubgrupo");
            } else if(intent.getAction().equals(MapActivity.PROX_ALERT_POI_SIGUIENTE)){
                /* Avisa que ya llego al poi siguiente. */
                this.getApplication().enviarFinJuego();
            }
        }
        else {
            Log.e("location", "exiting");
        }
    }

    @SuppressWarnings("unused")
    public void completeCambiarEstadoSubgrupo(SoapObject result) {
        try{
            String idSubgrupo = Integer.toString(this.getApplication().getSubgrupo().getId());
            /* Ejecuto la tarea que obtiene la consulta del subgrupo. */
            WSTask getConsignaTask = new WSTask();
            getConsignaTask.setReferer(this);
            getConsignaTask.setMethodName(SoapManager.METHOD_GET_CONSIGNA);
            getConsignaTask.addStringParameter("idSubgrupo", idSubgrupo);
            getConsignaTask.executeTask("completeGetConsigna", "errorGetConsigna");
        }catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void errorCambiarEstadoSubgrupo(String failedMethod){
        this.getApplication().getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    @SuppressWarnings("unused")
    public void completeGetConsigna(SoapObject result) {
        try{
            /* El resultado trae el nombre y la consigna del grupo al cual pertenece el subgrupo. */
            Consigna consigna = new Consigna(result.getProperty("nombre").toString(),result.getProperty("descripcion").toString());
            this.getApplication().getSubgrupo().getGrupo().setNombre(result.getProperty("nombreGrupo").toString());
            this.getApplication().getSubgrupo().getGrupo().setConsigna(consigna);
            /* Genera la barrera para esperar a que todos los demas subgrupos lleguen a sus respectivas postas. */
            this.getApplication().getCurrentActivity().startService(new Intent(this.getApplication().getCurrentActivity(), PoolServiceEstados.class));
        }catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void errorGetConsigna(String failedMethod){
        this.getApplication().getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    public JuegoColaborativo getApplication() {
        return application;
    }

    public void setApplication(JuegoColaborativo application) {
        this.application = application;
    }
}
