package com.spring.rest.Employee;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class EmployeeController {

    private final EmployeeRepository repository;
    private final EmployeeResourceAssembler resourceAssembler;

    public EmployeeController(EmployeeRepository repository, EmployeeResourceAssembler resourceAssembler) {
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }


    @GetMapping(value = "/employees",produces = "application/json")
    Resources<Resource<Employee>> all(){

        List<Resource<Employee>> employees = repository.findAll().stream()
                .map(resourceAssembler::toResource)
                .collect (Collectors.toList());

        return new Resources<>(employees,
                linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    @PostMapping("/employees")
    ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) throws URISyntaxException {

        Resource<Employee> employeeResource = resourceAssembler.toResource(repository.save(newEmployee));

        // HTTP 201 Created
        return ResponseEntity
                .created(new URI(employeeResource.getId().expand().getHref()))
                .body(employeeResource);

    }

    @GetMapping(value = "/employees/{id}",produces = "application/json")
    Resource<Employee> one(@PathVariable Long id){

        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return resourceAssembler.toResource(employee);

    }

    @PutMapping("/employees/{id}")
    ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee,@PathVariable Long id) throws URISyntaxException {

        Employee updatedEmployee = repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });

        Resource<Employee> employeeResource= resourceAssembler.toResource(updatedEmployee);

        // HTTP 201 Created
        return ResponseEntity
                .created(new URI(employeeResource.getId().expand().getHref()))
                .body(employeeResource);
    }

    @DeleteMapping("/employees/{id}")
    ResponseEntity<?> deleteEmployee(@PathVariable Long id){

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
