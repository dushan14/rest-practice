package com.spring.rest.Order;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class OrderController {

    private final OrderRepository repository;
    private final OrderResourceAssembler resourceAssembler;

    public OrderController(OrderRepository repository, OrderResourceAssembler resourceAssembler) {
        this.repository = repository;
        this.resourceAssembler = resourceAssembler;
    }

    @GetMapping(value = "/orders", produces = "application/json")
    Resources<Resource<Order>> all(){
        List<Resource<Order>> orders = repository.findAll().stream()
                .map(resourceAssembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(orders,
                linkTo(methodOn(OrderController.class).all()).withSelfRel());
    }

    @GetMapping(value = "/orders/{id}", produces = "application/json")
    Resource<Order> one(@PathVariable Long id){

        return resourceAssembler.toResource(repository.findById(id)
                .orElseThrow(()-> new OrderNotFoundException(id)));
    }

    @PostMapping("orders")
    ResponseEntity<?> newOrder(@RequestBody Order order) throws URISyntaxException {

        order.setStatus(Status.IN_PROGRESS);
        Resource<Order> orderResource = resourceAssembler.toResource(repository.save(order));

        return ResponseEntity
                .created(new URI(orderResource.getId().expand().getHref()))
                .body(orderResource);
                //or like this // .created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
                //.body(assembler.toResource(newOrder));
    }

    @DeleteMapping("/orders/{id}/cancel")
    ResponseEntity<?> cancel(@PathVariable Long id){

        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus()==Status.IN_PROGRESS){
            order.setStatus(Status.CANCELLED);
            return ResponseEntity.ok(resourceAssembler.toResource(repository.save(order)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method not allowed", "You can't cancel an order that is in the "+order.getStatus()+" status"));
    }

    @PutMapping("/orders/{id}/complete")
    ResponseEntity<?> complete(@PathVariable Long id){
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus()==Status.IN_PROGRESS){
            order.setStatus(Status.COMPLETED);
            return ResponseEntity.ok(resourceAssembler.toResource(repository.save(order)));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new VndErrors.VndError("Method not allowed", "You can't complete an order that is in the "+order.getStatus()+" status"));

    }

}
