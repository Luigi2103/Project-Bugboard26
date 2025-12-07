package it.unina.bugboard.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Utente")
public class User extends BasicUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idutente")
    private Long id;

    @Column(nullable = false, name = "mail")
    private String email;

    @Column(nullable = false, name = "isadmin")
    private boolean isAdmin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User(String username, String passwordHash, String email, boolean isAdmin) {
        super(username, passwordHash);
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
