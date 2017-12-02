/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.startup;
import common.FileCatalog;
import client.controller.Controller;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 *
 * @author Sarah
 */
public class Main {
    
     public static void main(String[] args) {
        try {
            FileCatalog fileCatalog = (FileCatalog) Naming.lookup(FileCatalog.SERVER_NAME_IN_REGISTRY);
            new Controller().start(fileCatalog);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            System.out.println("Could not start file client.");
        }
    }

}
