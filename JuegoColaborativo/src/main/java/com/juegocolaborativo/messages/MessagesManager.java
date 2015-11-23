package com.juegocolaborativo.messages;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.juegocolaborativo.R;

public class MessagesManager {
    private AlertDialog cartelMostrandose;

    private Integer idCartelMostrandose;

    private ProgressDialog progressDialogMostrandose;

    private Integer idProgressDialogMostrandose;

    private Activity activity;

    public MessagesManager(Activity activity) {
        this.setActivity(activity);
    }

    public AlertDialog getCartelMostrandose() {
        return cartelMostrandose;
    }

    public void setCartelMostrandose(AlertDialog cartelMostrandose) {
        this.cartelMostrandose = cartelMostrandose;
    }

    public Integer getIdCartelMostrandose() {
        return idCartelMostrandose;
    }

    public void setIdCartelMostrandose(Integer idCartelMostrandose) {
        this.idCartelMostrandose = idCartelMostrandose;
    }

    public ProgressDialog getProgressDialogMostrandose() {
        return progressDialogMostrandose;
    }

    public void setProgressDialogMostrandose(ProgressDialog progressDialogMostrandose) {
        this.progressDialogMostrandose = progressDialogMostrandose;
    }

    public Integer getIdProgressDialogMostrandose() {
        return idProgressDialogMostrandose;
    }

    public void setIdProgressDialogMostrandose(Integer idProgressDialogMostrandose) {
        this.idProgressDialogMostrandose = idProgressDialogMostrandose;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void closeProgressDialog(){
        if(this.getProgressDialogMostrandose() != null){
            this.getProgressDialogMostrandose().cancel();
            this.setIdProgressDialogMostrandose(null);
            this.setProgressDialogMostrandose(null);
        }
    }

    public void closeDialog(){
        if (this.getCartelMostrandose() != null){
            this.getCartelMostrandose().cancel();
            this.setCartelMostrandose(null);
            this.setIdCartelMostrandose(null);
        }
    }

    public void showProgressDialog(int idString){

        if((this.getProgressDialogMostrandose() == null) || (this.getIdProgressDialogMostrandose() != idString)){
            String msg = this.getActivity().getString(idString);

            this.closeDialog();
            this.closeProgressDialog();

            ProgressDialog pd = new ProgressDialog(this.getActivity());
            pd.setCancelable(false);
            pd.setMessage(msg);
            pd.show();
            this.setProgressDialogMostrandose(pd);
            this.setIdProgressDialogMostrandose(idString);
        }
    }

    private void showDialog(int idContent, int idTitle, int idButton, String... mensajes){
        if((this.getCartelMostrandose() == null) || (this.getIdCartelMostrandose() != idContent)){
            this.closeDialog();
            this.closeProgressDialog();

            String msj = (mensajes.length == 0) ? this.getActivity().getString(idContent) : mensajes[0];
            String titulo = this.getActivity().getString(idTitle);
            String boton = this.getActivity().getString(idButton);

            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder
                    .setTitle(titulo)
                    .setMessage(msj)
                    .setPositiveButton(boton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            this.setCartelMostrandose(alert);
            this.setIdCartelMostrandose(idContent);
            alert.show();
        }
    }

    public void showDialogError(int idContent, String param){
        String mensaje = this.getActivity().getString(idContent, param);
        this.showDialog(idContent, R.string.dialog_error, R.string.button_cerrar, mensaje);
    }

    public void showDialogInfo(int idContent){
        this.showDialog(idContent, R.string.dialog_info, R.string.button_aceptar);
    }

}
