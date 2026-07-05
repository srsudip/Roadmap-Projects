package com.moviereservation.model;

public class User {
    private final int id;
    private final String username;
    private final String passwordHash;
    private final String fullName;
    private final String role;

    public User(int id, String username, String passwordHash, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
    }

    public User(int id, String username, String fullName, String role) {
        this(id, username, null, fullName, role);
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public boolean isAdmin() { return "ADMIN".equals(role); }

    @Override
    public String toString() { return fullName + " (@" + username + ")"; }
}
