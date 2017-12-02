/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

/**
 *
 * @author Sarah
 */
@NamedQueries ({
    @NamedQuery(
        name = "findFileByName",
        query = "SELECT foundFile FROM Document foundFile WHERE foundFile.filename LIKE :name",
        lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
        name = "findAllFiles",
        query = "SELECT file FROM Document file",
        lockMode = LockModeType.OPTIMISTIC
    ),
    @NamedQuery(
        name = "deleteFileByName",
        query = "DELETE FROM Document file WHERE file.filename LIKE :name"
    )
    ,
    @NamedQuery(
        name = "deleteAccountFiles",
        query = "DELETE FROM Document file WHERE file.owner.holder.username LIKE :username"

    )
    ,
})
@Entity(name ="Document")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "subtype_id", discriminatorType = DiscriminatorType.STRING)
public abstract class Document implements Serializable{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long FileId;

    @Column(name = "filename", nullable = false)
    private String filename;
    
    @Column(name = "size", nullable = false)
    private double size;
    
    @Column(name="subtype_id")
    protected String subTypeId;

    public String getSubTypeId() {
        return subTypeId;
    
    }
    
    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private Account owner;

    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;
    
    public Document(){
        this(null,null,null,0);
    }
    public Document(String filename, Account owner, String type, double size){
        this.filename = filename ;
        this.owner = owner ;
        this.subTypeId = type;
        this.size = size ;
    }
    
    public String getDocName(){
        return filename ;
    }
    
    public String getOwnerName(){
        return owner.getUserName();
    }
    
    public double getSize(){
        return size;
    }
    
    public void setSize(double size){
        this.size =  size;
    }

    
}
