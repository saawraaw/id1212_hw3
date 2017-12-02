/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

/**
 *
 * @author Sarah
 */
@NamedQueries({
    @NamedQuery(
        name = "deleteUser",
        query = "DELETE FROM Holder ownerr WHERE ownerr.username LIKE :name"
    )
    ,
})
@Entity(name = "Holder")
public class Holder implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long holderId;

    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;

    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;

    public Holder() {
        this(null,null);
    }

    public Holder(String name, String password) {
        this.username = name;
        this.password = password ;
    }

    public String getUserName() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    
}