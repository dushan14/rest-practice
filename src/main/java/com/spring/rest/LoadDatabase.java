package com.spring.rest;

import com.spring.rest.Employee.Employee;
import com.spring.rest.Employee.EmployeeRepository;
import com.spring.rest.Order.Order;
import com.spring.rest.Order.OrderRepository;
import com.spring.rest.Order.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

    @Bean
    CommandLineRunner initDatabaseEmployee(EmployeeRepository repository){
        return args -> {
            log.info("Preloading "+repository.save(new Employee("Bilbo Baggins", "burglar")));
            log.info("Preloading "+repository.save(new Employee("Frodo Baggins", "thief")));
        };
    }

    @Bean
    CommandLineRunner initDatabaseOrders(OrderRepository repository){
        repository.save(new Order("MacBook Pro", Status.COMPLETED));
        repository.save(new Order("iPhone", Status.IN_PROGRESS));

        return args -> {
            repository.findAll().forEach(order -> {
                log.info("Preloaded " + order);
            });
        };
    }
}
