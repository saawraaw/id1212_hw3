/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author Sarah
 */
@Entity(name = "PublicDocument")
@DiscriminatorValue("PUBLIC")
public class PublicDocument extends Document{
   @Column(name = "writable", nullable = false)
    private boolean writable;
    
    @Column(name = "notify", nullable = false)
    private boolean notify;
    
    
    public PublicDocument(){
        this(null,null,false,false,0);
    }
    
    public PublicDocument(Account owner, String filename, boolean writable, boolean notify, double size){
        super(filename, owner, "PUBLIC", size);
        this.writable = writable ; 
        this.notify = notify ;
    }
    
    public boolean isWritable(){
        return writable;
    }
    
    public boolean shouldNotify(){
        return notify;
    }
}
