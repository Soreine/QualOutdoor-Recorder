package com.qualoutdoor.recorder;

/**
 * A component implementing this interface is able to provide a reference to a
 * Service of type 'S'.
 */
public interface ServiceProvider<S> {

    /**
     * Get a reference to the service this component provides. Throws an
     * exception if the service is not available.
     */
    S getService() throws ServiceNotBoundException;

    /** Indicate whether the service is currently available */
    boolean isAvailable();

    /** Register to be informed when the service becomes available */
    void register(ServiceListener<S> listener);
    
    /** Unregister the given listener */
    void unregister(ServiceListener<S> listener);

    
    /**
     * This runtime exception is thrown when one tries to access a service while
     * not available.
     */
    public class ServiceNotBoundException extends RuntimeException {
        /**
         * Generated serial version UID
         */
        private static final long serialVersionUID = 7232920276486060679L;

    }

}
