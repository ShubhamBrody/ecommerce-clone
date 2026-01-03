package com.ecommerce.project.controller;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.service.AddressService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddresses(@Valid @RequestBody AddressDTO address){
        User user = authUtil.loggedInUser();
        AddressDTO createdAddress = addressService.createAddress(address, user);
        return ResponseEntity.ok().body(createdAddress);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses(){
        List<AddressDTO> addresses = addressService.getAllAddresses();
        return ResponseEntity.ok().body(addresses);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable("addressId") Long addressId){
        AddressDTO address = addressService.getAddressById(addressId);
        return ResponseEntity.ok().body(address);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressesByUser(){
        User user = authUtil.loggedInUser();
        List<AddressDTO> addresses = addressService.getAllAddressesByUser(user);
        return ResponseEntity.ok().body(addresses);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@RequestBody AddressDTO address,
                                                 @PathVariable("addressId") Long addressId){
        AddressDTO updatedAddress = addressService.updateAddress(address, addressId);
        return ResponseEntity.ok().body(updatedAddress);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable("addressId") Long addressId){
        String status = addressService.deleteAddress(addressId);
        return ResponseEntity.ok().body(status);
    }
}
