package com.juegocolaborativo.task;

import android.os.AsyncTask;
import android.util.Log;

import com.juegocolaborativo.soap.SoapManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class WSTask extends AsyncTask<Void, Void, SoapObject> {

    private Object referer = null;
    private Method method = null;
    private String errorCallback = null;
    private Boolean primitive = true;
    private String methodName = null;

    public WSTask() {
        super();
        this.setParameters(new ArrayList<PropertyInfo>());
    }

    private ArrayList<PropertyInfo> parameters = null;

    public Object getReferer() {
        return referer;
    }

    public void setReferer(Object referer) {
        this.referer = referer;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getErrorCallback() {
        return errorCallback;
    }

    public void setErrorCallback(String errorCallback) {
        this.errorCallback = errorCallback;
    }

    public ArrayList<PropertyInfo> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<PropertyInfo> parameters) {
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String method_name) {
        this.methodName = method_name;
    }

    public Boolean getPrimitive() { return primitive; }

    public void setPrimitive(Boolean primitive) { this.primitive = primitive; }

    private void addParameter(String name, Object value, Object type) {
        PropertyInfo pi = new PropertyInfo();
        pi.setName(name);
        pi.setValue(value);
        pi.setType(type);
        this.getParameters().add(pi);
    }

    public void addStringParameter(String name, Object value) {
        this.addParameter(name, value, PropertyInfo.STRING_CLASS);
    }

    protected SoapObject doInBackground(Void... params) {

        try {

            SoapManager soapManager = new SoapManager();
            // Modelo el request
            SoapObject request = new SoapObject(soapManager.getNamespace(), this.getMethodName());

            // Paso parametros al WS
            for(PropertyInfo parameter : this.getParameters()) {
                request.addProperty(parameter);
            }

            // Modelo el Envelope (envoltura)
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);

            // Modelo el transporte
            HttpTransportSE transporte = new HttpTransportSE(soapManager.getUrl());

            // Llamada
            String theCall = soapManager.getNamespace() + "#" + this.getMethodName(); //Alex - prueba
            transporte.call(theCall, envelope);

            // Resultado
            SoapObject resultado;
            resultado = (SoapObject) envelope.getResponse();

            return resultado;

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR_IO", ' '+e.toString());
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", ' '+e.getMessage());
            return null;
        }
    }

    protected void onPostExecute(SoapObject result) {
        try {
            // Aca debería chequear si llegó bien
            if (result != null){
                (this.getMethod()).invoke(this.getReferer(), result);
            } else if (this.getErrorCallback() != null){
                Method errorMethod = this.getReferer().getClass().getMethod(this.getErrorCallback(), String.class);
                errorMethod.invoke(this.getReferer(), this.getMethodName());
            }
        } catch (Exception e) {
            Log.e("ERROR", "Error en invocación de método de callback: "+ this.getMethod() + " - ERROR: " + e.getStackTrace());
        }
    }

    public void executeTask(String methodCallback, String errorMethodCallback){
        try {
            this.setMethod(this.getReferer().getClass().getMethod(methodCallback, SoapObject.class));
            this.setErrorCallback(errorMethodCallback);
            this.execute();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
