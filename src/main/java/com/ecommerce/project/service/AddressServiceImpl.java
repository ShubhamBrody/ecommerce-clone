package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Address;
import com.ecommerce.project.model.User;
import com.ecommerce.project.payload.AddressDTO;
import com.ecommerce.project.repositories.AddressRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createAddress(AddressDTO address, User user) {
        Address newAddress = modelMapper.map(address, Address.class);
        List<Address> addressList = user.getAddresses();
        addressList.add(newAddress);
        user.setAddresses(addressList);

        newAddress.setUser(user);
        Address savedAddress = addressRepository.save(newAddress);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream().map(
                address -> modelMapper.map(address, AddressDTO.class)
        ).toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Address", "address_id", addressId));

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddressesByUser(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream().map(
                address -> modelMapper.map(address, AddressDTO.class)
        ).toList();
    }

    @Override
    public AddressDTO updateAddress(AddressDTO address, Long addressId) {
        Address oldAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "address_id", addressId));

        oldAddress.setStreet(address.getStreet());
        oldAddress.setCity(address.getCity());
        oldAddress.setState(address.getState());
        oldAddress.setBuildingName(address.getBuildingName());
        oldAddress.setCountry(address.getCountry());
        oldAddress.setPincode(address.getPincode());

        Address updatedAddress = addressRepository.save(oldAddress);

        User user = oldAddress.getUser();
        user.getAddresses().removeIf(userAddress -> userAddress.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    @Transactional
    public String deleteAddress(Long addressId) {
        Address oldAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "address_id", addressId));

        User user = oldAddress.getUser();
        user.getAddresses().removeIf(userAddress -> userAddress.getAddressId().equals(addressId));
        addressRepository.delete(oldAddress);
        userRepository.save(user);

        return "Address with addressId " + addressId + " has been deleted";
    }
}
