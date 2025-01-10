package at.aau.ase.cl.api.model;

public class LoginResponse {
    public String token;
    public User user;

    public LoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
}