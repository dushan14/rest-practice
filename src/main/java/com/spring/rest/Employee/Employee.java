package com.spring.rest.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private @Id @GeneratedValue Long id;

    private String role;
    private String firstName;
    private String lastName;

    public Employee(String firstName, String lastName,String role) {
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Employee(String name,String role) {
        this.role = role;
        setName(name);
    }

    public void setName(String name){
       String[] split = name.split(" ");
       firstName=split[0];
       lastName=split[1];
   }

   public String getName(){
       return firstName+" "+lastName;
   }


}
