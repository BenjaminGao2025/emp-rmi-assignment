package com.example.server;

import com.example.rmi.EmpService;
import com.example.rmi.EmpServiceImpl;

import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

/**
 * Bootstraps the RMI registry and published service.
 */
public final class ServerMain {
    public static final String SERVICE_NAME = "EmpService";

    private ServerMain() {
    }

    public static void main(String[] args) throws Exception {
        String host = envOrDefault("RMI_HOST", "rmi-server");
        int registryPort = Integer.parseInt(envOrDefault("RMI_REGISTRY_PORT", "1099"));
        int objectPort = Integer.parseInt(envOrDefault("RMI_OBJECT_PORT", "2001"));
        String dbPath = envOrDefault("EMP_DB_PATH", Path.of("data", "runtime", "CSCI7785_database.db").toString());

        ServerHandle handle = startServer(host, registryPort, objectPort, dbPath);
        Runtime.getRuntime().addShutdownHook(new Thread(handle::closeQuietly));
        Thread.currentThread().join();
    }

    public static ServerHandle startServer(String host, int registryPort, int objectPort, String dbPath)
            throws RemoteException, SQLException {
        System.setProperty("java.rmi.server.hostname", host);

        Registry registry = LocateRegistry.createRegistry(registryPort);
        EmpServiceImpl service = new EmpServiceImpl(dbPath);
        EmpService stub = (EmpService) UnicastRemoteObject.exportObject(service, objectPort);
        registry.rebind(SERVICE_NAME, stub);

        return new ServerHandle(registry, service);
    }

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value != null && !value.isBlank()) {
            return value;
        }
        return defaultValue;
    }

    public static final class ServerHandle implements AutoCloseable {
        private final Registry registry;
        private final Remote service;

        private ServerHandle(Registry registry, Remote service) {
            this.registry = registry;
            this.service = service;
        }

        @Override
        public void close() {
            closeQuietly();
        }

        private void closeQuietly() {
            try {
                registry.unbind(SERVICE_NAME);
            } catch (RemoteException | NotBoundException ignored) {
            }

            try {
                UnicastRemoteObject.unexportObject(service, true);
            } catch (RemoteException ignored) {
            }

            try {
                UnicastRemoteObject.unexportObject(registry, true);
            } catch (RemoteException ignored) {
            }
        }
    }
}
