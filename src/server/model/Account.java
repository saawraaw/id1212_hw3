/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model;

import common.AccountDTO;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

/**
 *
 * @author Sarah
 */

@NamedQueries({
    @NamedQuery(
        name = "deleteAccountByName",
        query = "DELETE FROM Account acct WHERE acct.holder.username LIKE :name"
    )
    ,
    @NamedQuery(
        name = "findAccountByName",
        query = "SELECT acct FROM Account acct WHERE acct.holder.username LIKE :name",
        lockMode = LockModeType.OPTIMISTIC
    )
    ,    

})

@Entity(name = "Account")
public class Account implements AccountDTO{
    @Id
    @Column(name = "accountId", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long accountId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(name = "holder", nullable = false)
    private Holder holder;
   

    @Column(name = "notifications", nullable = false)
    private final List<String> notifications = new ArrayList<>();
    
    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;
   
    public Account() {
        this(null);
    }
     
    public Account(Holder user) {
        this.holder = user ;
    }
    
    @Override
    public String getUserName() {
        return holder.getUserName();
    }
    @Override
    public String getUersPassword() {
        return holder.getPassword();
    }
    
    @Override
    public List<String> getNotifications(){
        return notifications ;
    }
    
    public void notify(String msg){
        notifications.add(msg);
    }
    
    public void clearNotifications (){
        notifications.clear();
    }
    
 
    
}
