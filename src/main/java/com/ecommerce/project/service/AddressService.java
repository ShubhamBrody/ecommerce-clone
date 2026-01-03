package com.ecommerce.project.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO address, User user);
    List<AddressDTO> getAllAddresses();
    AddressDTO getAddressById(Long addressId);
    List<AddressDTO> getAllAddressesByUser(User user);
    AddressDTO updateAddress(AddressDTO address, Long addressId);
    String deleteAddress(Long addressId);
}
