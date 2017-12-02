/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.integration;

import common.AccountDTO;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import server.model.Account;
import server.model.Document;
import server.model.Holder;

/**
 *
 * @author Sarah
 */
public class FileCatalogDAO {
    private final EntityManagerFactory emFactory;
    private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();
    
    public FileCatalogDAO() {
        emFactory = Persistence.createEntityManagerFactory("FileCatalogPU");
    }
    
    public Account findAccountByUsername(String username, boolean endTransactionAfterSearching) {
        System.out.println("In findAccountByUsername");
        if (username == null) {
            System.out.println("UserName is Null");
            return null;
        }
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findAccountByName", Account.class).
                        setParameter("name", username).getSingleResult();
            } catch (NoResultException noSuchAccount) {
                System.out.println("No Account Found");
                return null;
            }
        } finally {
            if (endTransactionAfterSearching) {
                commitTransaction();
            }
        }  
    }
    
    public void createAccount(AccountDTO account) {
        try {
            EntityManager em = beginTransaction();
            System.out.println("Here");
            System.out.println("Account Username: " + account.getUserName());
            em.persist(account);
        } finally {
            commitTransaction();
        }
    }
    
    public Document findFile(String filename, boolean endTransactionAfterSearching) {
        if (filename == null) {
            return null;
        }
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findFileByName", Document.class).
                        setParameter("name", filename).getSingleResult();
            } catch (NoResultException noSuchFile) {
                return null;
            }
        } finally {
            if (endTransactionAfterSearching) {
                commitTransaction();
            }
        }  
    }
    
    public void createFile(Document file) {
        try {
            EntityManager em = beginTransaction();
            em.persist(file);
        } finally {
            commitTransaction();
        }
    }
    
    public void updateAccount() {
        commitTransaction();
    }
    
    
    
    private EntityManager beginTransaction() {
        EntityManager em = emFactory.createEntityManager();
        threadLocalEntityManager.set(em);
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        return em;
    }

    private void commitTransaction() {
        threadLocalEntityManager.get().getTransaction().commit();
    }
    
    public List<Document> findAllFiles() {
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findAllFiles", Document.class).getResultList();
            } catch (NoResultException noSuchAccount) {
                return new ArrayList<>();
            }
        } finally {
            commitTransaction();
        }
    }
    
    public void deleteFile(String filename) {
        try {
            EntityManager em = beginTransaction();
            em.createNamedQuery("deleteFileByName", Document.class).
                    setParameter("name", filename).executeUpdate();
        } finally {
            commitTransaction();
        }
    }
    
    public void deleteAllFiles(String username) {
        try {
            EntityManager em = beginTransaction();
            em.createNamedQuery("deleteAccountFiles", Document.class).
                    setParameter("username", username).executeUpdate();
        } finally {
            commitTransaction();
        }
    }
    
    public void deleteUser(String username) {
        try {
            EntityManager em = beginTransaction();
            em.createNamedQuery("deleteUser", Holder.class).
                    setParameter("name", username).executeUpdate();
        } finally {
            commitTransaction();
        }
    }
    
    public void deleteAccount(String holderName) {
        try {
            EntityManager em = beginTransaction();
            em.createNamedQuery("deleteAccountByName", Account.class).
                    setParameter("name", holderName).executeUpdate();
        } finally {
            commitTransaction();
        }
    }

}
