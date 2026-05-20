package e0bmanager.dto;

import com.google.gson.annotations.SerializedName;

public class UserDTO {
    private Long id;
    private String username;
    private String phone;
    private String cccd;
    private String avatar;
    @SerializedName("fullname")
    private String fullname;
    private String email;
    private String password;
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {this.username = username;}
    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}
    public String getCccd() {return cccd;}
    public void setCccd(String cccd) {this.cccd = cccd;}
    public String getAvatar() {return avatar;}
    public void setAvatar(String avatar) {this.avatar = avatar;}
    public String getFullName() {return fullname;}
    public void setFullName(String fullName) {this.fullname = fullName;}
    public String getEmail() {return email;}
    public void setEmail(String Email) {this.email = Email;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
}
