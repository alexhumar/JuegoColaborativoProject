package com.juegocolaborativo;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.juegocolaborativo.activity.DefaultActivity;
import com.juegocolaborativo.activity.PiezaActivity;
import com.juegocolaborativo.activity.ResponderActivity;
import com.juegocolaborativo.activity.ResultadosActivity;
import com.juegocolaborativo.model.Consigna;
import com.juegocolaborativo.model.Consulta;
import com.juegocolaborativo.model.Coordenada;
import com.juegocolaborativo.model.PiezaARecolectar;
import com.juegocolaborativo.model.Poi;
import com.juegocolaborativo.model.Respuesta;
import com.juegocolaborativo.model.Resultado;
import com.juegocolaborativo.model.ResultadoFinal;
import com.juegocolaborativo.model.Subgrupo;
import com.juegocolaborativo.service.PoolServiceColaborativo;
import com.juegocolaborativo.service.PoolServiceEstados;
import com.juegocolaborativo.service.PoolServicePosta;
import com.juegocolaborativo.soap.SoapManager;
import com.juegocolaborativo.task.WSTask;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class JuegoColaborativo extends Application {

    private DefaultActivity currentActivity;
    private Subgrupo subgrupo;
    private PiezaARecolectar piezaActual;
    private TreeSet<ResultadoFinal> resultadoFinal = new TreeSet<>();
    private static JuegoColaborativo instance;

    public TreeSet<ResultadoFinal> getResultadoFinal() {
        return resultadoFinal;
    }

    public void setResultadoFinal(TreeSet<ResultadoFinal> resultadoFinal) {
        this.resultadoFinal = resultadoFinal;
    }

    public DefaultActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(DefaultActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public PiezaARecolectar getPiezaActual() {
        return piezaActual;
    }

    public void setPiezaActual(PiezaARecolectar piezaActual) {
        this.piezaActual = piezaActual;
    }

    public Subgrupo getSubgrupo() {
        return subgrupo;
    }

    public void setSubgrupo(Subgrupo subgrupo) {
        this.subgrupo = subgrupo;
    }

    public void esperarTurnoJuego() {
        String idSubgrupo = Integer.toString(getSubgrupo().getId());
        /* Ejecuto la tarea que cambia de estado al subgrupo. */
        WSTask esSubgrupoActualTask = new WSTask();
        esSubgrupoActualTask.setReferer(this);
        esSubgrupoActualTask.setMethodName(SoapManager.METHOD_ES_SUBGRUPO_ACTUAL);
        esSubgrupoActualTask.addStringParameter("idSubgrupo", idSubgrupo);
        esSubgrupoActualTask.executeTask("completeEsSubgrupoActual", "errorEsSubgrupoActual");
    }

    @SuppressWarnings("unused")
    public void completeEsSubgrupoActual(SoapObject result) {
        try{
            SoapPrimitive res = (SoapPrimitive) result.getProperty("valorInteger");
            int esSubgrupoActual = Integer.parseInt(res.toString());
            if (esSubgrupoActual == 1) {
                /* Deja de preguntar si es su turno. */
                this.getCurrentActivity().stopService(new Intent(getCurrentActivity(), PoolServicePosta.class));
                this.jugar();
            }
        }catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void errorEsSubgrupoActual(String failedMethod){
        this.getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    public void jugar() {
        this.getSubgrupo().setEstado(Subgrupo.ESTADO_JUGANDO);
        String idSubgrupo = Integer.toString(this.getSubgrupo().getId());
        String idEstado = Integer.toString(this.getSubgrupo().getEstado());
        /* Ejecuto la tarea que cambia de estado al subgrupo. */
        WSTask cambiarEstadoSubgrupoTask = new WSTask();
        cambiarEstadoSubgrupoTask.setReferer(this);
        cambiarEstadoSubgrupoTask.setMethodName(SoapManager.METHOD_CAMBIAR_ESTADO_SUBGRUPO);
        cambiarEstadoSubgrupoTask.addStringParameter("idSubgrupo", idSubgrupo);
        cambiarEstadoSubgrupoTask.addStringParameter("idEstado", idEstado);
        cambiarEstadoSubgrupoTask.executeTask("completeEnviarJugando", "errorEnviarJugando");
    }

    @SuppressWarnings("unused")
    public void completeEnviarJugando(SoapObject result) {
        try{
            this.comienzoJuego();
        }catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void errorEnviarJugando(String failedMethod){
        this.getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    public void enviarJugando(){
        /* Detengo el proximity alert del poi subgrupo y limpio el mapa (borro marker poi inicial). */
        //Quizas convendria chequear si ya esta jugando (por si le llega una consulta mientras esta en su poi. Ver OnResume de MapActivity)
        this.getCurrentActivity().removerPuntoInicial();
        this.getCurrentActivity().showProgressDialog(R.string.dialog_llegada_posta_asignada);
        /* Creo la lista de consultas vacias del subgrupo. */
        this.getSubgrupo().setIdsConsultasQueMeHicieron(new ArrayList<Integer>());
        /* Llamo al PoolServiceColaborativo para chequear si un subgrupo realizó una pregunta que debo responder. */
        this.getCurrentActivity().startService(new Intent(getCurrentActivity(), PoolServiceColaborativo.class));
        this.getCurrentActivity().stopService(new Intent(new Intent(getCurrentActivity(), PoolServiceEstados.class)));
        this.getCurrentActivity().startService(new Intent(getCurrentActivity(), PoolServicePosta.class));
    }

    public void esperarEstadoSubgrupos(){
        /* Es invocado desde el PoolServiceEstados */
        String idEstado = Integer.toString(this.getSubgrupo().getEstado());
        String idSubgrupo = Integer.toString(this.getSubgrupo().getId());
        WSTask esperarEstadoTask = new WSTask();
        esperarEstadoTask.setReferer(this);
        if (getSubgrupo().getEstado() == Subgrupo.ESTADO_FINAL){
            esperarEstadoTask.setMethodName(SoapManager.METHOD_ESPERAR_ESTADO_FINAL);
        }else{
            esperarEstadoTask.setMethodName(SoapManager.METHOD_ESPERAR_ESTADO_SUBGRUPOS);
            esperarEstadoTask.addStringParameter("idEstado", idEstado);
            esperarEstadoTask.addStringParameter("idSubgrupo", idSubgrupo);
        }
        esperarEstadoTask.executeTask("completeEsperarEstadoSubgrupos", "errorEsperarEstadoSubgrupos");
    }

    @SuppressWarnings("unused")
    public void completeEsperarEstadoSubgrupos(SoapObject result) {
        SoapPrimitive res = (SoapPrimitive) result.getProperty("valorInteger");
        int llegaronSubgrupos = Integer.parseInt(res.toString());
        if (llegaronSubgrupos == 1){
            this.getCurrentActivity().closeProgressDialog();
            /* Ahora dependendiendo del estado esperado, es el metodo que debo llamar despues de ejecutar la tarea. */
            if (getSubgrupo().getEstado() == Subgrupo.ESTADO_INICIAL){
                this.enviarJugando();
            }else if (getSubgrupo().getEstado() == Subgrupo.ESTADO_FINAL){
                this.finJuego();
            }
        }else{
            if(getSubgrupo().getEstado() == Subgrupo.ESTADO_INICIAL){
                this.getCurrentActivity().showProgressDialog(R.string.dialog_esperando_subgrupos);
            }else if (((getSubgrupo().getEstado() == Subgrupo.ESTADO_FINAL)) && (this.getCurrentActivity().getClass() != ResponderActivity.class)){
                /* Si estoy respondiendo una consulta, no mostrar el cartel. */
                if(!this.getCurrentActivity().isShowing(R.string.dialog_consulta_respondida_final)){
                    /* Si estoy mostrando el cartel de consulta respondida, no mostrar cartel de llegada a posta siguiente */
                    this.getCurrentActivity().showProgressDialog(R.string.dialog_llegada_posta_siguiente);
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public void errorEsperarEstadoSubgrupos(String failedMethod){
        this.getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    public void comienzoJuego() {
        try{
            this.getCurrentActivity().showDialogInfo(R.string.dialog_notificacion_turno);
            String idSubgrupo = Integer.toString(this.getSubgrupo().getId());
            WSTask getPiezaTask = new WSTask();
            getPiezaTask.setReferer(this);
            getPiezaTask.setMethodName(SoapManager.METHOD_GET_PIEZA_A_RECOLECTAR);
            getPiezaTask.addStringParameter("idSubgrupo", idSubgrupo);
            getPiezaTask.executeTask("completeGetPiezaARecolectar", "errorGetPiezaARecolectar");
        }catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void completeGetPiezaARecolectar(SoapObject result) {
        try{
            SoapObject poi = (SoapObject) result.getProperty("poi");
            int idPieza = Integer.parseInt(result.getProperty("id").toString());
            String nombre = result.getProperty("nombre").toString();
            String descripcion = result.getProperty("descripcion").toString();
            double latitud = Double.parseDouble(poi.getProperty("coordenadaY").toString());
            double longitud = Double.parseDouble(poi.getProperty("coordenadaX").toString());
            /* Creo la pieza a recolectar, la guardo en el poi de la posta del subgrupo. */
            PiezaARecolectar pieza = new PiezaARecolectar(idPieza,nombre,descripcion,new Poi(new Coordenada(latitud,longitud)),new ArrayList<Consigna>());
            this.getSubgrupo().getPosta().getPoi().setPieza(pieza);
            this.mostrarInfoPieza();
        }catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void errorGetPiezaARecolectar(String failedMethod){
        this.getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    public void mostrarInfoPieza(){
        this.setPiezaActual(this.getSubgrupo().getPosta().getPoi().getPieza());
        /* Inicio la activity que muestra la información de la pieza. */
        this.getCurrentActivity().startActivity(new Intent(this, PiezaActivity.class));
    }

    public void enviarFinJuego(Boolean... terminarJuego){
        /* Como java no soporta parametros opcionales, recibo parametros variables y verifico si fue enviado y si es true. */
        boolean terminar = (terminarJuego.length > 0 && terminarJuego[0]);
        if(terminar){
            this.getCurrentActivity().showDialogInfo(R.string.dialog_juego_terminado);
        }else{
            /* Detengo el proximity alert del poi final y limpio el mapa (borro marker poi final). */
            this.getCurrentActivity().removerPuntoFinal();
        }
        /* Aviso que ya terminé de jugar. */
        getSubgrupo().setEstado(Subgrupo.ESTADO_FINAL);
        String idSubgrupo = Integer.toString(this.getSubgrupo().getId());
        String idEstado = Integer.toString(this.getSubgrupo().getEstado());
        WSTask cambiarEstadoSubgrupoTask = new WSTask();
        cambiarEstadoSubgrupoTask.setReferer(this);
        cambiarEstadoSubgrupoTask.setMethodName(SoapManager.METHOD_CAMBIAR_ESTADO_SUBGRUPO);
        cambiarEstadoSubgrupoTask.addStringParameter("idSubgrupo", idSubgrupo);
        cambiarEstadoSubgrupoTask.addStringParameter("idEstado", idEstado);
        cambiarEstadoSubgrupoTask.executeTask("completeEnviarFinJuego", "errorEnviarFinJuego");
    }

    @SuppressWarnings("unused")
    public void completeEnviarFinJuego(SoapObject result) {
        try{
            /* Llamo al PoolServiceEstados para esperar a que todos terminen de jugar. */
            this.getCurrentActivity().startService(new Intent(getCurrentActivity(), PoolServiceEstados.class));
            /* Activo al Subgrupo siguiente. */
            String idSubgrupo = Integer.toString(this.getSubgrupo().getId());
            WSTask setPostaActualTask = new WSTask();
            setPostaActualTask.setReferer(this);
            setPostaActualTask.setMethodName(SoapManager.METHOD_SET_POSTA_ACTUAL);
            setPostaActualTask.addStringParameter("idSubgrupo", idSubgrupo);
            setPostaActualTask.executeTask("completeSetPostaActual", "errorSetPostaActual");
        }catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void errorEnviarFinJuego(String failedMethod){
        this.getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    @SuppressWarnings("unused")
    public void completeSetPostaActual(SoapObject result){
        //NO HACE NADA. EL CICLO SE CIERRA EN esperarEstadoSubgrupos() MEDIANTE PoolServiceEstados.
    }

    @SuppressWarnings("unused")
    public void errorSetPostaActual(String failedMethod){
        this.getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    public void finJuego(){
        this.getCurrentActivity().stopService(new Intent(new Intent(getCurrentActivity(), PoolServiceEstados.class)));
        this.getCurrentActivity().stopService(new Intent(new Intent(getCurrentActivity(), PoolServiceColaborativo.class)));
        WSTask getResultadosTask = new WSTask();
        getResultadosTask.setReferer(this);
        getResultadosTask.setMethodName(SoapManager.METHOD_GET_RESULTADOS);
        getResultadosTask.executeTask("completeGetResultados", "errorGetResultados");
    }

    @SuppressWarnings("unused")
    public void completeGetResultados(SoapObject result) {
        /* Inicializo el arreglo de resultados. */
        this.setResultadoFinal(new TreeSet<ResultadoFinal>());
        HashMap<String, Integer> resultadosParciales = new HashMap<>();
        try{
            for (int i = 0; i < result.getPropertyCount(); i++) {
                String nombreGrupo = ((SoapObject) result.getProperty(i)).getProperty("nombreGrupo").toString();
                int idSubgrupo = Integer.parseInt(((SoapObject) result.getProperty(i)).getProperty("idSubgrupo").toString());
                /* Respuesta del subgrupo. */
                int respuestaSubgrupo = Integer.parseInt(((SoapObject) result.getProperty(i)).getProperty("decisionFinalCumple").toString());
                /* Si es correcta o no la decision tomada (se calcula en el servidor). */
                int decisionCorrecta = Integer.parseInt(((SoapObject) result.getProperty(i)).getProperty("decisionCorrecta").toString());
                if(!resultadosParciales.containsKey(nombreGrupo)){
                    /* Si no existe lo inicializo. */
                    resultadosParciales.put(nombreGrupo, 0);
                }
                if(decisionCorrecta == 1){
                    /* Sumo 1 a las respuestas correctas. */
                    resultadosParciales.put(nombreGrupo, resultadosParciales.get(nombreGrupo) + 1);
                }
                /* Si son los resultados de mi subgrupo los guardo para mostrar el detalle. */
                if(idSubgrupo == this.getSubgrupo().getId()){
                    String nombrePieza = ((SoapObject) result.getProperty(i)).getProperty("nombrePieza").toString();
                    this.getSubgrupo().setResultado(new Resultado(nombrePieza, respuestaSubgrupo == 1, decisionCorrecta == 1));
                }
            }
            /* Recorro el hashmap auxiliar para genera la lista ordenada de resultados. */
            for (HashMap.Entry<String, Integer> entry : resultadosParciales.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                this.getResultadoFinal().add(new ResultadoFinal(key, value));
            }
            /* Inicio la activity que muestra los resultados finales. */
            this.getCurrentActivity().startActivity(new Intent(this, ResultadosActivity.class));
        } catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void errorGetResultados(String failedMethod){
        this.getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    public void esperarPreguntasSubgrupos(){
        /* Es invocado por el PoolServiceRespuestas para chequear si hay preguntas por responder. */
        String idSubgrupo = Integer.toString(this.getSubgrupo().getId());
        WSTask existePreguntaTask = new WSTask();
        existePreguntaTask.setReferer(this);
        existePreguntaTask.setMethodName(SoapManager.METHOD_EXISTE_PREGUNTA_SIN_RESPONDER);
        existePreguntaTask.addStringParameter("idSubgrupo", idSubgrupo);
        existePreguntaTask.executeTask("completeEsperarPreguntasSubgrupos", "errorEsperarPreguntasSubgrupos");
    }

    @SuppressWarnings("unused")
    public void completeEsperarPreguntasSubgrupos(SoapObject result) {
        try{
            /* Chequeo si el valor del resultado es positivo para levantar la pregunta y poder responderla. */
            SoapPrimitive res = (SoapPrimitive) result.getProperty("id");
            int idConsulta = Integer.parseInt(res.toString());
            if (idConsulta != -1){
                /* Chequeo si ya respondí esa consulta. */
                if ((this.getSubgrupo().getIdsConsultasQueMeHicieron().isEmpty()) || (!(this.getSubgrupo().getIdsConsultasQueMeHicieron()).contains(idConsulta))){
                    /* Agrego el id a las consultas que me hicieron. */
                    this.getSubgrupo().getIdsConsultasQueMeHicieron().add(idConsulta);
                    /* Creo el objeto Consulta y lo seteo a la consultaActual. */
                    SoapPrimitive res2 = (SoapPrimitive) result.getProperty("nombrePieza");
                    String nombrePieza = res2.toString();
                    SoapPrimitive res3 = (SoapPrimitive) result.getProperty("descripcionPieza");
                    String descripcionPieza = res3.toString();
                    SoapPrimitive res4 = (SoapPrimitive) result.getProperty("cumple");
                    int cumple = Integer.parseInt(res4.toString());
                    SoapPrimitive res5 = (SoapPrimitive) result.getProperty("justificacion");
                    String justificacion = res5.toString();
                    this.getSubgrupo().setConsultaActual(new Consulta(idConsulta,nombrePieza,descripcionPieza,cumple,justificacion));
                    /* Inicio la activity para responder la consulta. */
                    this.getCurrentActivity().startActivity(new Intent(this, ResponderActivity.class));
                }
            }
        } catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void errorEsperarPreguntasSubgrupos(String failedMethod){
        this.getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    public void esperarRespuestasSubgrupos(){
        /* Es invocado por el PoolServiceRespuestas para chequear si hay respuestas a una consulta realizada. */
        String idSubgrupo = Integer.toString(this.getSubgrupo().getId());
        WSTask existenRespuestasTask = new WSTask();
        existenRespuestasTask.setReferer(this);
        existenRespuestasTask.setMethodName(SoapManager.METHOD_EXISTEN_RESPUESTAS);
        existenRespuestasTask.addStringParameter("idSubgrupo", idSubgrupo);
        existenRespuestasTask.executeTask("completeEsperarRespuestasSubgrupos", "errorEsperarRespuestasSubgrupos");
    }

    @SuppressWarnings("unused")
    public void completeEsperarRespuestasSubgrupos(SoapObject result) {
        try{
            if (result.getPropertyCount() > this.getSubgrupo().getCantidadRespuestas()){
                HashMap<Integer, Respuesta> respuestas = this.getSubgrupo().getRespuestas();
                for (int i = 0; i < result.getPropertyCount(); i++) {
                    int idSubgrupoConsultado = Integer.parseInt(((SoapObject) result.getProperty(i)).getProperty("idSubgrupoConsultado").toString());
                    int acuerdoPropuesta = Integer.parseInt(((SoapObject) result.getProperty(i)).getProperty("acuerdoPropuesta").toString());
                    String justificacion = ((SoapObject) result.getProperty(i)).getProperty("justificacion").toString();
                    /* Busco en la lista de respuestas de la pieza actual del subgrupo y guardo la respuesta. */
                    respuestas.get(idSubgrupoConsultado).setCumple(acuerdoPropuesta);
                    respuestas.get(idSubgrupoConsultado).setJustificacion(justificacion);
                    this.getSubgrupo().setCantidadRespuestas(this.getSubgrupo().getCantidadRespuestas() + 1);
                }
                getCurrentActivity().getResultadosAdapter().notifyDataSetChanged();
            }
        } catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public void errorEsperarRespuestasSubgrupos(String failedMethod){
        this.getCurrentActivity().showDialogError(R.string.dialog_mensaje_error_tarea, failedMethod);
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static JuegoColaborativo getInstance() {
        return instance;
    }
}
