/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import client.filehandler.FileHandler;
import client.view.UserInterface;
import client.view.Command;
import common.FileCatalog;
import common.AccountDTO;
import java.rmi.RemoteException;
import java.util.List;
import server.model.DbException;


public class Controller {
    private FileCatalog fileCatalog;
    private boolean receivingCmds = false;
    private boolean loggedIn = false ;
    AccountDTO acc = null;
    private final String basePath = "C:\\Users\\Sarah\\Documents\\NetBeansProjects\\FileCatalog\\Database\\client\\";
    private final FileHandler fileHandler= new FileHandler (basePath);
    private final UserInterface cmdLine = new UserInterface();
    
    
    public void start(FileCatalog fileCatalog) {
        this.fileCatalog = fileCatalog;
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        interpret();
    }
    
    public void interpret() {
        UserInterface.println("Enter HELP to see all possible commands");
        while (receivingCmds) {
            try {
                switch (cmdLine.getCmd(UserInterface.readNextLine())) {
                    case HELP:
                        help();
                        break;
                    case QUIT:
                        quit();
                        break;
                    case REGISTER:
                        acc = createAccount();
                        fileHandler.creatDirectoryForClient(acc.getUserName());
                        break;
                    case UNREGISTER:
                        deleteAccount();
                        break;
                    case LOGIN:
                        acc = login();
                        break;
                    case LOGOUT:
                        logout();
                        break;
                    case UPLOAD:
                        upload();
                        break;
                    case DOWNLOAD:
                        download();
                        break;
                    case UPDATE:
                        update();
                        break;
                    case NOTIFY:
                        readNotifications();
                        break;
                    case LIST:
                        listAllFiles();
                        break;
                    case DELETE:
                        deleteFile();
                        break;
                    default:
                        UserInterface.println("illegal command");
                }
            } catch (WrongCommandException | RemoteException | DbException e) {
                UserInterface.println("Operation failed");
                UserInterface.println(e.getMessage());
            }
        }
    }
    
    private void help(){
        for (Command command : Command.values()) {
            if (command == Command.ILLEGAL_COMMAND) {
                continue;
            }
            UserInterface.println(command.toString().toLowerCase());
        }
    }
    
    private void quit(){
        receivingCmds = false;
        loggedIn = false ;
        acc = null;
    }

    private AccountDTO createAccount() throws RemoteException, DbException, WrongCommandException {
        checkLogggedOut();
        UserInterface.println("Choose A Username:");
        String username = UserInterface.readNextLine();
        UserInterface.println("Choose A Password:");
        String password = UserInterface.readNextLine();
        AccountDTO acct = fileCatalog.createAccount(username, password);
        loginCommand(username, password);
        return acct;    
    }
    
    private AccountDTO login() throws RemoteException, DbException, WrongCommandException{
        checkLogggedOut();
        UserInterface.println("Enter Your Username:");
        String username = UserInterface.readNextLine();
        UserInterface.println("Enter Your Password:");
        String password = UserInterface.readNextLine();
        AccountDTO acct = fileCatalog.login(username, password);
        loginCommand(username, password);
        return acct;     
    }
    
    private void loginCommand(String username, String password) throws RemoteException, DbException{
        UserInterface.println("Welcome " + username);
        loggedIn = true ;
    }
    
    private void logout() throws RemoteException, DbException, WrongCommandException {
        checkLogggedIn();
        UserInterface.println("Logging Out...");
        loggedIn = false ;
        acc = null;
    }
    
    
    private void deleteAccount() throws RemoteException, DbException, WrongCommandException{
        checkLogggedIn();
        fileCatalog.deleteAccount(acc.getUserName());
        UserInterface.println("Logging Out...");
        loggedIn = false ;
        acc = null;
    }
    
    private void upload() throws RemoteException, DbException, WrongCommandException{
        checkLogggedIn();
        //System.out.println("Acct Username: " + acc.getUserName());
        UserInterface.println("Enter Filename:");
        String filename = UserInterface.readNextLine();
        byte[] bytes = fileHandler.readFile(acc.getUserName(),filename);
        UserInterface.println("Do you want to make this file public or private?");
        String filetype = UserInterface.readNextLine();
        if (filetype.equalsIgnoreCase("public"))
            createPublicFile(filename, bytes.length);
        else if (filetype.equalsIgnoreCase("private"))
            fileCatalog.uploadNewPrivateFile(acc, filename, bytes.length);
        else 
            throw new WrongCommandException("Type of File is Wrong");
        fileHandler.sendFile(bytes);
        UserInterface.println("File Uploaded Successfully");
    }
    
    private void createPublicFile(String filename, double size) 
            throws RemoteException, DbException, WrongCommandException{
        UserInterface.println("Do you want to make this file writable for other users? "
                + "Enter Yes or No");
        String input = UserInterface.readNextLine();
        int writable = (input.equalsIgnoreCase("yes")) ? 1 : 
                (input.equalsIgnoreCase("no")) ? 0 : -1 ;
        UserInterface.println("Do you want to be notified if another "
                + "user downloads/makes changes to this file? Enter Yes or No");
        input = UserInterface.readNextLine();
        int notify = (input.equalsIgnoreCase("yes")) ? 1 : 
                (input.equalsIgnoreCase("no")) ? 0 : -1 ;
        if ((writable==-1) || (notify==-1)){
            throw new WrongCommandException("Wrong Configuration For File");
        }
        else {
            fileCatalog.uploadNewPublicFile(acc, filename, writable==1, notify==1, size);
        }  
    }
    
    private void checkLogggedIn () throws WrongCommandException {
        if (!loggedIn)
            throw new WrongCommandException("You Must Be Logged In"
                    + " For This Operation");
    }
    
    private void checkLogggedOut () throws WrongCommandException {
        if (loggedIn)
            throw new WrongCommandException("Logout To Perform This Operation");
    }

    
    private void download() throws RemoteException, DbException, WrongCommandException{
        checkLogggedIn();
        //System.out.println("Acct Username: " + acc.getUserName());
        UserInterface.println("Enter Filename:");
        String filename = UserInterface.readNextLine();
        fileCatalog.download(filename, acc);
        fileHandler.receiveFile(acc.getUserName(),filename);
        UserInterface.println("File Downloaded Successfully");
    }
    
    
    private void update()throws RemoteException, DbException, WrongCommandException{
        checkLogggedIn();
        UserInterface.println("Enter Filename:");
        String filename =UserInterface.readNextLine();
        byte [] bytes = fileHandler.readFile(acc.getUserName(),filename);
        fileCatalog.update(filename, acc, bytes.length);
        fileHandler.sendFile(bytes);
        UserInterface.println("File Updated Successfully");        
    }
    
    private void readNotifications()throws RemoteException, DbException, WrongCommandException{
        checkLogggedIn();
        List<String> notifications = fileCatalog.readNotifications(acc);
        //System.out.println("Size of notifications: " + notifications.size());
        if (notifications.isEmpty()){
            UserInterface.println("You have no new notifications"); 
            return ;
        }
        printList(notifications);
    }
    
    private void printList(List<String> list){
        for (int i=0; i<list.size() ; i++) {
            UserInterface.println(list.get(i));
        }
    }
    
    private void listAllFiles()throws RemoteException, DbException, WrongCommandException{
        checkLogggedIn();
        List<String> filesInfo = fileCatalog.listFiles(acc);
        printList(filesInfo);
    }
    
    private void deleteFile()throws RemoteException, DbException, WrongCommandException{
        checkLogggedIn();
        //System.out.println("Acct Username: " + acc.getUserName());
        UserInterface.println("Enter Filename:");
        String filename = UserInterface.readNextLine();
        fileCatalog.deleteFile(filename, acc);
        UserInterface.println("File Deleted Successfully");
    }   
}
