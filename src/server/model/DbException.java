/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

/**
 *
 * @author Sarah
 */
public class DbException extends Exception{
    public DbException(String reason) {
        super(reason);
    }

    public DbException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
