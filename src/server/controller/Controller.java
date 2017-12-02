/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;

import common.AccountDTO;
import common.FileCatalog;
import java.io.File;
import server.integration.FileCatalogDAO;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import server.model.Holder;
import server.model.Account;
import server.model.DbException;
import server.model.Document;
import server.model.PrivateDocument;
import server.model.PublicDocument;
import server.filehandler.FileHandler;

/**
 *
 * @author Sarah
 */
public class Controller extends UnicastRemoteObject implements FileCatalog {
    private final FileCatalogDAO fileCatalogDb;
    private final String basePath = "C:\\Users\\Sarah\\Documents\\NetBeansProjects\\FileCatalog\\Database\\server\\";
    private final FileHandler fileHandler  =new FileHandler(basePath );

    public Controller() throws RemoteException {
        super();
        fileCatalogDb = new FileCatalogDAO();
    }
    
    @Override
    public AccountDTO createAccount(String username, String password) throws DbException {
        if (fileCatalogDb.findAccountByUsername(username, true) != null) {
            throw new DbException("Username \"" + username + "\" already exists");
        }
        Account acc = new Account(new Holder(username,password));
        fileCatalogDb.createAccount(acc);
        creatDirectoryForClient(username);
        return acc;

    }
    
    private void creatDirectoryForClient(String username){
        File file = new File (basePath + username);
        if(!file.exists())
            file.mkdirs();
    }
    
    @Override
    public AccountDTO login(String username, String password) throws  DbException {
        System.out.println("In login method in controller");
        Account acct = fileCatalogDb.findAccountByUsername(username, true);
        //System.out.println("Account Found: " + acct.getUserName());
        if(acct == null){
            throw new DbException("Account Not Found");
        }
        String passwordInDb = acct.getUersPassword();
        if (!passwordInDb.equals(password)) {
            throw new DbException("Wrong Password");
        }
        System.out.println("Before Returning Account: " + acct.getUserName());
        return acct;

    }
    

    @Override
    public PublicDocument uploadNewPublicFile(AccountDTO owner, String filename,
            boolean writable, boolean notify, double size) throws DbException {
        checkForExistingFile(filename);
        PublicDocument publicFile = new PublicDocument((Account) owner , filename , writable, notify, size);
        addFile(owner, publicFile);
        return publicFile;
    }

    @Override
    public PrivateDocument uploadNewPrivateFile(AccountDTO owner, String filename, double size) throws DbException {
        checkForExistingFile(filename);
        PrivateDocument privateFile = new PrivateDocument((Account)owner , filename, size);
        addFile(owner, privateFile);
        return privateFile;
    }
    
    private void checkForExistingFile(String filename)throws DbException{
        if (fileCatalogDb.findFile(filename, true) != null) {
            throw new DbException("File \"" + filename + "\" already exists");
        }
    }
    
    private void addFile(AccountDTO acc, Document newFile){
        System.out.println("In Add file method");
        fileHandler.rcvFile(acc.getUserName(), newFile.getDocName());
        System.out.println("File Received");
        fileCatalogDb.createFile(newFile);
        //Account acct = fileCatalogDb.findAccountByUsername(acc.getUserName(), true);
        //acct.addFile(newFile);
        //fileCatalogDb.updateAccount();
    }
    
    @Override
    public void download(String filename, AccountDTO acct) throws DbException{
        Document file = findFile (filename);
        if(!fileBelongsToUser(file, acct)){
            PublicDocument publicFile = (PublicDocument) file;
            if(publicFile.shouldNotify()){
                String msg = "User \"" + acct.getUserName() + "\" has downloaded " + file.getDocName();
                notifyUser(file.getOwnerName(),msg);
            }
        }
        fileHandler.sendFile(file.getOwnerName(),filename);
    }
    
    
    @Override
    public void update(String filename, AccountDTO acct, double size) throws DbException{
        Document file = fileCatalogDb.findFile(filename, false);
        if(fileBelongsToUser(file,acct)){
            fileHandler.rcvFile(acct.getUserName(), file.getDocName());
            file.setSize(size);
            fileCatalogDb.updateAccount();
        } else {
            updatePublicFile(acct, (PublicDocument)file, size);
        }
    }
    
    private void updatePublicFile(AccountDTO acct, PublicDocument publicFile, double size)throws DbException{
            if(publicFile.isWritable()){
                fileHandler.rcvFile(publicFile.getOwnerName(), publicFile.getDocName());
                publicFile.setSize(size);
                fileCatalogDb.updateAccount();
                if(publicFile.shouldNotify()){
                    System.out.println("Changes Must Be Notified");
                    String msg = "User \"" + acct.getUserName() + "\" has updated " + publicFile.getDocName();
                    notifyUser(publicFile.getOwnerName(),msg);
                }
            }  
            else {
                fileCatalogDb.updateAccount();
                throw new DbException("This is a read-only public file");
            }
    }

    
    private boolean fileBelongsToUser(Document file, AccountDTO acct)throws DbException{
        boolean fileBelongsToUser = true ;
        if(!file.getOwnerName().equals(acct.getUserName())){
            fileBelongsToUser = false;
            if(file.getSubTypeId().equalsIgnoreCase("private"))
                throw new DbException("This is a private file of another user");
        }
        return fileBelongsToUser ;
    }
        
    
    private Document findFile (String filename) throws DbException {
        Document file = fileCatalogDb.findFile(filename, true);
        if (file == null) {
            throw new DbException("File \"" + filename + "\" not found");
        }
        return file;
    }
    
    
    public void clearNotifications(AccountDTO acct){
        Account acc = fileCatalogDb.findAccountByUsername(acct.getUserName(), false);
        acc.clearNotifications();
        fileCatalogDb.updateAccount();
    }
    
    private void notifyUser (String username, String msg){
        System.out.println("In notifyUser method");
        Account acc = fileCatalogDb.findAccountByUsername(username, false);
        System.out.println("Size before notifying: " + acc.getNotifications().size());
        acc.notify(msg);
        fileCatalogDb.updateAccount();
        System.out.println("Size After notifying: " + acc.getNotifications().size());
    }
    
    @Override
    public List<String> readNotifications(AccountDTO acct){
        Account acc = fileCatalogDb.findAccountByUsername(acct.getUserName(), true);
        List<String> notifications = acc.getNotifications();
        clearNotifications(acct);
        return notifications;
    }
    
    @Override
    public List<String> listFiles(AccountDTO acc) throws DbException{
        List <Document> files = fileCatalogDb.findAllFiles();
        if(files.isEmpty())
            throw new DbException("There are no files in the database");
        List<String> listOfFiles = new ArrayList<>();
        System.out.println("Size of all found files " + files.size());
        for (int i=0 ; i<files.size(); i++){
            String s = fileInfo(acc.getUserName(), files.get(i));
            if(s!=null)
                listOfFiles.add(s);
        }
        if(listOfFiles.isEmpty())
            throw new DbException("No files could be listed");
        return listOfFiles;
    }
    
    private String fileInfo(String username, Document file){
        System.out.println("Creating File Info");
        System.out.println("Filename" + file.getDocName());
        String s = "File Name: " + file.getDocName() + "\t\tSize(bytes): " + file.getSize() + "\t\tType: ";
        if(file.getSubTypeId().equalsIgnoreCase("private")){
            if(file.getOwnerName().equals(username))
                return  s+ "Private" ;
        } else {
            PublicDocument publicFile = (PublicDocument) file;
            s = s + "Public\t\tIs Writable: " + publicFile.isWritable();
            return s;
        }
        return null;
    }
    
    @Override
    public void deleteFile(String filename, AccountDTO acct) throws DbException{
        Document file = findFile (filename);
        if(!fileBelongsToUser(file, acct)){
            PublicDocument publicFile = (PublicDocument) file;
            if(publicFile.shouldNotify()){
                String msg = "User \"" + acct.getUserName() + "\" has deleted " + file.getDocName();
                notifyUser(file.getOwnerName(),msg);
            }
        }
        fileCatalogDb.deleteFile(filename);
        fileHandler.deleteFile(filename, acct.getUserName());
    }
    
    @Override
    public void deleteAccount(String holderName) throws DbException {
        try {
            fileCatalogDb.deleteAllFiles(holderName);
            fileCatalogDb.deleteAccount(holderName);
            fileCatalogDb.deleteUser(holderName);
            fileHandler.deleteAccount(holderName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(e.getCause());
            throw new DbException("Could not delete account for: " + holderName);
            
        }
    }
}
