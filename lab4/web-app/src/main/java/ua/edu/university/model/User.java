package ua.edu.university.model;

public class User {
    public Long id;
    public String firstName;
    public String lastName;
    public String email;

    public User() {
    }

    public User(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
