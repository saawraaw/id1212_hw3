/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

/**
 *
 * @author Sarah
 */
public class WrongCommandException extends Exception{
    public WrongCommandException(String reason) {
        super(reason);
    }

    public WrongCommandException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
