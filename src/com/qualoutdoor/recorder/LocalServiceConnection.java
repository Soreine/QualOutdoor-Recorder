package com.qualoutdoor.recorder;

import java.util.LinkedList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * This ServiceConnection is meant to be used for connecting a component to
 * services within the same process. Indeed we cast IBinder to LocalBinder.
 */
public class LocalServiceConnection<S> implements ServiceProvider<S>,
        ServiceConnection {
    /** The class of the Service S */
    private final Class<S> serviceClass;

    /** A reference to the connected service */
    private S service;
    /** Indicate if the service is bound or not */
    private boolean isAvailable = false;
    /** The collection of the registered ServiceListener */
    private List<ServiceListener<S>> listeners = new LinkedList<ServiceListener<S>>();
    /** Indicates if this ServiceConnection is bound to a service or not */
    private boolean isBound = false;

    /**
     * Create a new LocalServiceConnection, we provide the service class for
     * later use (@see bindToService method).
     */
    public LocalServiceConnection(Class<S> serviceType) {
        this.serviceClass = serviceType;
    }

    /**
     * Bind the given context to a Service S through this ServiceConnection.
     * 
     * @param context
     *            The context to bind to the service through this
     *            ServiceConnection
     * @return True if the Service has bound successfully
     */
    public boolean bindToService(Context context) {
        // Create an intent toward Service S
        Intent intent = new Intent(context, serviceClass);
        // Bind the context to the Service through this ServiceConnection
        isBound = context.bindService(intent, this, Context.BIND_AUTO_CREATE);
        return isBound;
    }

    /** Unbind this ServiceConnection if it was bound */
    public void unbindService(Context context) {
        if (isBound) {
            // Unbind the context
            context.unbindService(this);
            // We are no longer bound
            isBound = false;
        }
    }

    @Override
    public boolean isAvailable() {
        return isAvailable;
    }

    /** Indicates wether this ServiceConnection is bound to a service */
    public boolean isBound() {
        return isBound;
    }

    @Override
    public S getService() throws ServiceNotBoundException {
        if (isAvailable) {
            // The service is available and can be safely returned
            return service;
        } else {
            // We have no current access to the service
            throw new ServiceNotBoundException();
        }
    };

    @Override
    public void onServiceDisconnected(ComponentName serviceName) {
        // We are not bound to the service anymore
        isAvailable = false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onServiceConnected(ComponentName serviceName, IBinder binder) {
        try {
            // Cast the IBinder to a LocalBinder
            service = ((LocalBinder<S>) binder).getService();
        } catch (ClassCastException exc) {
            throw new ClassCastException(
                    "The bound service must provide the same LocalBinder<S> as declared.");
        }
        // We have successfully bound to the service
        isAvailable = true;
        // Notifify the listeners
        notifyListeners();

    }

    @Override
    public void register(ServiceListener<S> listener) {
        // Add the listener to the list of listeners
        listeners.add(listener);
        // If the service is available already, notify now
        if (isAvailable)
            listener.onServiceAvailable(service);
    }

    @Override
    public void unregister(ServiceListener<S> listener) {
        // Remove the listener from the list
        listeners.remove(listener);
    }

    private void notifyListeners() {
        // Read through the listeners list
        for (ServiceListener<S> listener : listeners) {
            // Notify each
            listener.onServiceAvailable(service);
        }
    }
}
