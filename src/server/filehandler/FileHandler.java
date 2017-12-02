package server.filehandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sarah
 */
public class FileHandler {
    ServerSocket serverSocket ;
    private String basePath;
    public FileHandler(String basePath){
        this.basePath = basePath ;
        try {
           serverSocket = new ServerSocket(3333);
        } catch (IOException ex) {
            System.err.println("Could No Open Listening Socket");
        }
    }
    
    public void deleteFile(String owner, String filename){
        try {
            Files.deleteIfExists(Paths.get(basePath + owner + "\\" + filename));
        } catch (IOException ex) {
            System.err.println("Could Not Delete File");
            System.err.println(ex.getMessage());
        }
    }
    
    public void deleteAccount(String owner){
        try {
            File directory = new File(basePath + owner );
            System.out.println("List Of Files: " + directory.listFiles());
            for(File f: directory.listFiles()) 
                f.delete(); 
            Files.deleteIfExists(Paths.get(basePath + owner ));
        } catch (IOException ex) {
            System.err.println("Could Not Delete Account Directory");
            System.err.println(ex.getMessage());
        }
    }
    
    public void rcvFile(String owner, String filename){
        new Thread(new ReceiveFiles(owner, filename)).start();
    }
     
    public void sendFile(String owner, String filename){
        new Thread(new SendFiles(owner, filename)).start();
    }
    
    private class ReceiveFiles implements Runnable {
        private final String filename ;
        private final String owner;
        public ReceiveFiles(String owner, String filename){
            this.filename = filename ;
            this.owner = owner ; 
        } 

        
        @Override
        public void run(){
            Socket connectionSocket = null ;
            try {
            connectionSocket = serverSocket.accept();
            writeReceivedFiled(owner, filename , connectionSocket.getInputStream());
            } catch (IOException ex) {
                System.err.println("Could Not receive File");
                System.err.println(ex.getMessage());
            }   
            try {
                connectionSocket.close();
            } catch (IOException ex) {
                System.err.println("Could Not close socket");
                System.err.println(ex.getMessage());
            }   
        }
        
        private void writeReceivedFiled (String owner, String fileOutput, InputStream is) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] aByte = new byte[1];
            int bytesRead;
            int totalBytes =0;
            try {
                BufferedOutputStream bos = new BufferedOutputStream
                    (new FileOutputStream
                        ( basePath + owner + "\\" + fileOutput));
                bytesRead = is.read(aByte, 0, aByte.length);
                do {
                    totalBytes += bytesRead;
                    baos.write(aByte);
                    bytesRead = is.read(aByte);
                    //System.out.println("2: " + bytesRead);
                } while (bytesRead != -1);
                bos.write(baos.toByteArray());
                bos.flush();
                bos.close();
            } catch (FileNotFoundException ex) {
                System.err.println("Could Not Create/Update File");
                System.err.println(ex.getMessage());
            } catch (IOException ex) {
                System.err.println("Could Not Write To File");
            }
                
        
        }
    }
    
    private class SendFiles implements Runnable{
        private final String fileToSend;
        private final String owner;
        public SendFiles(String owner, String filename){
            fileToSend = filename ;
            this.owner = owner ;
            
        }
        @Override
        public void run(){
            byte[] bytes = readFile();
            try {
                Socket connectionSocket = serverSocket.accept();
                System.out.println("Connection Accepted");
                BufferedOutputStream outToClient = 
                        new BufferedOutputStream(connectionSocket.getOutputStream());
                outToClient.write(bytes, 0, bytes.length);
                System.out.println("Number of sent bytes" + bytes.length);
                outToClient.flush();
                outToClient.close();
                connectionSocket.close();
            } catch (IOException ex) {
                System.err.println("Could Not Send File");
                System.err.println(ex.getMessage());
  
            }   
        }
        
        private byte[] readFile() {
        byte[] bytearray =null ;
        try {
            File myFile = new File(basePath  + owner + "\\"+ fileToSend);
            bytearray = new byte[(int) myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            bis.read(bytearray, 0, bytearray.length);   
        } catch (FileNotFoundException ex) {
            System.err.println("File Does Not Exist");
            System.err.println(ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Could Not Read File");
            System.err.println(ex.getMessage());
        }
        return bytearray;
          
        }
    }
    

    

    

}
