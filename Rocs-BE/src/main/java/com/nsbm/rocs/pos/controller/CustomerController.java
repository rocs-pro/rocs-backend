package com.nsbm.rocs.pos.controller;

import com.nsbm.rocs.common.response.ApiResponse;
import com.nsbm.rocs.entity.pos.Customer;
import com.nsbm.rocs.pos.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pos/customers")
@CrossOrigin
public class CustomerController {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Customer>> getCustomerByPhone(@RequestParam String phone) {
        return customerRepository.findByPhone(phone)
                .map(customer -> new ResponseEntity<>(
                        ApiResponse.success("Customer found", customer),
                        HttpStatus.OK
                ))
                .orElse(new ResponseEntity<>(
                        ApiResponse.error("Customer not found"),
                        HttpStatus.NOT_FOUND
                ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Customer>> createCustomer(@RequestBody Customer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            return new ResponseEntity<>(
                    ApiResponse.error("Name is required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
             return new ResponseEntity<>(
                    ApiResponse.error("Phone number is required"),
                    HttpStatus.BAD_REQUEST
            );
        }

        if (customerRepository.existsByPhone(customer.getPhone())) {
            return new ResponseEntity<>(
                    ApiResponse.error("Customer with this phone number already exists"),
                    HttpStatus.CONFLICT
            );
        }

        Customer saved = customerRepository.save(customer);
        return new ResponseEntity<>(
                ApiResponse.success("Customer created", saved),
                HttpStatus.CREATED
        );
    }
}

