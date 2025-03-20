package org.example.modelo;
public class Person {
    private int idPerson;
    private String firstName;
    private String lastName;
    private String direction;
    private String role;
    private String password;

    public Person() {
    }
    public Person(int idPerson, String firstName, String lastName, String direction, String role, String password) {
        this.idPerson=idPerson;
        this.firstName = firstName;
        this.lastName = lastName;
        this.direction = direction;
        this.role = role;
        this.password = password;
    }

    public Person(String firstName, String lastName, String direction, String role, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.direction = direction;
        this.role = role;
        this.password = password;
    }

    public int getIdPerson() {
        return idPerson;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDirection() {
        return direction;
    }

    public void setIdPerson(int idPerson) {this.idPerson= idPerson;}

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPassword(String passsword) {
        this.password = passsword;
    }

    @Override
    public String toString() {
        return "Person{" +
                "idPerson=" + idPerson +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", direction='" + direction + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

