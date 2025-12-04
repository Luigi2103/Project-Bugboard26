package it.unina.bugboard.DTO;

public class RichiestaLogin {
    private String username;
    private String password;
    private String modalita;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getModalita() {
        return modalita;
    }
    public void setModalita(String modalita) {
        this.modalita = modalita;
    }
}
