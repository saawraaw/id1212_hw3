/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author Sarah
 */
@Entity(name = "PrivateDocument")
@DiscriminatorValue("PRIVATE")
public class PrivateDocument extends Document {
    
    public PrivateDocument(){
        this(null,null,0);
    }
    public PrivateDocument (Account owner, String filename, double size){
        super(filename , owner,"PRIVATE", size);
    }
}
