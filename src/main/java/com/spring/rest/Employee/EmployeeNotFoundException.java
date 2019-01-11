package com.spring.rest.Employee;

public class EmployeeNotFoundException extends RuntimeException {

    EmployeeNotFoundException(Long id){
        super("Could not find employee " + id);
    }
}
