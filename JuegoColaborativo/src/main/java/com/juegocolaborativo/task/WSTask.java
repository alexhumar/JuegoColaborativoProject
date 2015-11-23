package com.juegocolaborativo.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.juegocolaborativo.R;
import com.juegocolaborativo.soap.SoapManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class WSTask extends AsyncTask<Void, Void, SoapObject> {

    private Object referer = null;
    private Method method = null;
    private String errorCallback = null;
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
            /* Modelo el request. */
            SoapObject request = new SoapObject(soapManager.getNamespace(), this.getMethodName());
            /* Paso parametros al WS. */
            for(PropertyInfo parameter : this.getParameters()) {
                request.addProperty(parameter);
            }
            /* Modelo el Envelope (envoltura). */
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            /* Modelo el transporte. */
            HttpTransportSE transporte = new HttpTransportSE(soapManager.getUrl());
            /* Llamada. */
            String theCall = soapManager.getNamespace() + "#" + this.getMethodName();
            transporte.call(theCall, envelope);
            /* Resultado. */
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
            if (result != null){
                (this.getMethod()).invoke(this.getReferer(), result);
            } else if (this.getErrorCallback() != null){
                Method errorMethod = this.getReferer().getClass().getMethod(this.getErrorCallback(), String.class);
                errorMethod.invoke(this.getReferer(), this.getMethodName());
            }
        } catch (Exception e) {
            Context context = (Context)this.getReferer();
            Log.e("ERROR", context.getString(R.string.log_callback_error, this.getMethod(), Arrays.toString(e.getStackTrace())));
        }
    }

    public void executeTask(String methodCallback, String errorMethodCallback){
        try {
            this.setMethod(this.getReferer().getClass().getMethod(methodCallback, SoapObject.class));
            this.setErrorCallback(errorMethodCallback);
            this.execute();
        } catch (SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
