/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import server.model.DbException;
import server.model.PrivateDocument;
import server.model.PublicDocument;


/**
 *
 * @author Sarah
 */
public interface FileCatalog extends Remote {
    
    public static final String SERVER_NAME_IN_REGISTRY = "server";
    
    public AccountDTO createAccount(String username, String password) 
            throws RemoteException, DbException;
    public AccountDTO login(String username, String password)
            throws RemoteException, DbException;
    public PublicDocument uploadNewPublicFile(AccountDTO owner, String fileName, 
            boolean writable, boolean notify, double size) throws RemoteException, DbException;
    public PrivateDocument uploadNewPrivateFile(AccountDTO acct, String fileName, double size)
            throws RemoteException, DbException;
    public void download(String filename, AccountDTO acct) 
            throws RemoteException, DbException;
    public void update(String filename, AccountDTO acct, double size) 
            throws RemoteException, DbException;
    public List<String> readNotifications(AccountDTO acct) throws RemoteException;
    public List<String> listFiles(AccountDTO acc) throws RemoteException, DbException;
    public void deleteFile(String filename, AccountDTO acct) throws RemoteException, DbException;
    public void deleteAccount(String holderName) throws RemoteException, DbException;

    
   
  
    
    
}
