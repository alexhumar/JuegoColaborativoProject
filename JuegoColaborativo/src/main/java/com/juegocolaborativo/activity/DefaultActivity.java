package com.juegocolaborativo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.juegocolaborativo.JuegoColaborativo;
import com.juegocolaborativo.R;
import com.juegocolaborativo.adapter.ResultadosAdapter;
import com.juegocolaborativo.messages.MessagesManager;

public class DefaultActivity extends Activity {

    private ResultadosAdapter resultadosAdapter;
    private MessagesManager messagesManager;

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public void setMessagesManager(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }

    public void showProgressDialog(int idString){
        this.getMessagesManager().showProgressDialog(idString);
    }

    public void closeProgressDialog(){
        this.getMessagesManager().closeProgressDialog();
    }

    /**
     * Muestra un error generico
     */
    public void showDialogError(int idString, String param) {
        this.getMessagesManager().showDialogError(idString, param);
    }

    /**
     * Muestra un dialogo de informacion
     */
    public void showDialogInfo(int idString) {
        this.getMessagesManager().showDialogInfo(idString);
    }

    /**
     * Retorna si un determinado id de string se esta mostrando en un process dialog
     */
    public boolean isShowing(Integer idString){
        return (idString.equals(this.getMessagesManager().getIdProgressDialogMostrandose()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setMessagesManager(new MessagesManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Seteo la activity que se está ejecutando en la aplicación. */
        ((JuegoColaborativo) getApplication()).setCurrentActivity(this);
    }

    @Override
    public void onBackPressed() {
        //return;
    }

    public void removerPuntoInicial(){}

    //public void addPiezasARecolectarToMap(PiezaARecolectar pieza){}

    public void removeProximityAlert(String intentAction){}

    //public void markerVisitado(Integer id){}

    public void removerPuntoFinal(){}

    public void enviarMsjConsultaRespondida(){
        /* Si vuelvo de responder una consulta, mostrar mensaje. */
        if ((((JuegoColaborativo) getApplication()).getSubgrupo().getConsultaActual() != null) && (((JuegoColaborativo) getApplication()).getSubgrupo().getConsultaActual().getRespondida() == 1)){
            /* Borro la consulta actual porque ya fue respondida. */
            ((JuegoColaborativo) getApplication()).getSubgrupo().setConsultaActual(null);
            showDialogInfo(R.string.dialog_respuesta_enviada);
        }
    }

    public ResultadosAdapter getResultadosAdapter() {
        return resultadosAdapter;
    }

    public void setResultadosAdapter(ResultadosAdapter resultadosAdapter) {
        this.resultadosAdapter = resultadosAdapter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here. The action bar will
           automatically handle clicks on the Home/Up button, so long
           as you specify a parent activity in AndroidManifest.xml. */
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SetPreferenceActivity.class);
                startActivityForResult(i, 1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
