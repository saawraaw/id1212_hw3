/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.startup;
import common.FileCatalog;
import server.controller.Controller;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Sarah
 */
public class Server {
    private String fileCatalogName = FileCatalog.SERVER_NAME_IN_REGISTRY;

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startRMIServant();
            System.out.println("File server started.");
        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Failed to start file server.");
        }
    }

    private void startRMIServant() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller contr = new Controller();
        Naming.rebind(fileCatalogName, contr);
    }

    
}

