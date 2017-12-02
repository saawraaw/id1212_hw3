/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.filehandler;

import client.controller.WrongCommandException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 *
 * @author Sarah
 */
public class FileHandler {
    String basePath ;
    
    public FileHandler (String basePath){
        this.basePath = basePath;
    }
    
    public void creatDirectoryForClient(String username){
        File file = new File (basePath + username);
        if(!file.exists())
            file.mkdirs();
    }
    
    public byte[] readFile(String username, String fileToSend) throws WrongCommandException {
        byte[] bytearray ;
        try {
            File myFile = new File(basePath  + username + "\\"+ fileToSend);
            bytearray = new byte[(int) myFile.length()];
            //System.out.println("File path: " + myFile.getAbsolutePath());
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytearray, 0, bytearray.length);   
        } catch (FileNotFoundException ex) {
            throw new WrongCommandException ("File Does Not Exist");
        } catch (IOException ex) {
            throw new WrongCommandException ("Could Not Read File");
        }
        return bytearray;
          
    }
    
    public void sendFile(byte[] bytes) throws WrongCommandException {
        Socket clientSocket ;
        try {
            clientSocket = new Socket( "localhost" , 3333 );
            BufferedOutputStream outToServer = 
                    new BufferedOutputStream(clientSocket.getOutputStream());
            outToServer.write(bytes, 0, bytes.length);
            outToServer.flush();
            outToServer.close();
            clientSocket.close();
        } catch (IOException ex) {
            throw new WrongCommandException("Could Not Send File To The Server");
        }
    }
    
    private void writeReceivedFiled (String username, String fileOutput, InputStream is)
            throws WrongCommandException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] aByte = new byte[1];
        int bytesRead;
        String path = basePath + username + "\\" + fileOutput ;
        try {
            BufferedOutputStream bos = 
                    new BufferedOutputStream(new FileOutputStream( path ));
            bytesRead = is.read(aByte, 0, aByte.length);
            do {
                baos.write(aByte);
                bytesRead = is.read(aByte);
            } while (bytesRead != -1);
            bos.write(baos.toByteArray());
            bos.flush();
            bos.close();
        } catch (FileNotFoundException ex) {
            throw new WrongCommandException("Could Not Create/Update File");
        } catch (IOException ex) {
            throw new WrongCommandException("Could Not Write To File");
        }         
        
    }
    
    
    public void receiveFile(String username, String filename) throws WrongCommandException {
        Socket clientSocket;
        try {
            clientSocket = new Socket( "localhost" , 3333 );
            writeReceivedFiled(username, filename , clientSocket.getInputStream());
            clientSocket.close();
        } catch (IOException ex) {
            throw new WrongCommandException("Could not receive file from server");
        }
       
    }
    
    
}
