package user.management.dtos;

import user.management.model.Role;

import java.util.List;

public class AuthToken {

    private Long userId;

    private String token;

    private List<Role> roles;

    public AuthToken() {

    }

    public AuthToken(String token, List<Role> roles, Long userId) {
        this.userId = userId;
        this.token = token;
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}