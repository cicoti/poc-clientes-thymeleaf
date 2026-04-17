package br.com.ctech.pocclientes.dto;

public class CadastroLoginForm {

    private String username;
    private String email;
    private String password;
    private String confirmacaoPassword;

    public CadastroLoginForm() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmacaoPassword() {
        return confirmacaoPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmacaoPassword(String confirmacaoPassword) {
        this.confirmacaoPassword = confirmacaoPassword;
    }
}