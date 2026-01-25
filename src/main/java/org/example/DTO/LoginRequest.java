package org.example.DTO;

public class LoginRequest {
    private String email;
    private String password;
    private boolean rememberEmail; // 쿠키로 이메일 기억

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isRememberEmail() { return rememberEmail; }
    public void setRememberEmail(boolean rememberEmail) { this.rememberEmail = rememberEmail; }
}
