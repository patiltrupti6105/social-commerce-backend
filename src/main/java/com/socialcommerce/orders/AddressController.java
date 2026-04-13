package com.socialcommerce.orders;

import com.socialcommerce.common.response.ApiResponse;
import com.socialcommerce.orders.dto.*;
import com.socialcommerce.orders.entity.Address;
import com.socialcommerce.orders.repository.AddressRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAddresses(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<AddressDTO> addresses = addressRepository.findByUserId(userId)
            .stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressDTO>> createAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAddressRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());

        if (request.getIsDefault()) {
            // Remove existing default
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(addr -> {
                    addr.setIsDefault(false);
                    addressRepository.save(addr);
                });
        }

        Address address = Address.builder()
            .userId(userId)
            .fullName(request.getFullName())
            .phone(request.getPhone())
            .addressLine1(request.getAddressLine1())
            .addressLine2(request.getAddressLine2())
            .city(request.getCity())
            .state(request.getState())
            .pincode(request.getPincode())
            .isDefault(request.getIsDefault())
            .build();

        return ResponseEntity.ok(ApiResponse.success(toDTO(addressRepository.save(address))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody CreateAddressRequest request) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getUserId().equals(userId)) throw new RuntimeException("Unauthorized");

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());
        address.setIsDefault(request.getIsDefault());

        return ResponseEntity.ok(ApiResponse.success(toDTO(addressRepository.save(address))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        if (!address.getUserId().equals(userId)) throw new RuntimeException("Unauthorized");
        addressRepository.delete(address);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private AddressDTO toDTO(Address a) {
        return AddressDTO.builder()
            .id(a.getId()).fullName(a.getFullName()).phone(a.getPhone())
            .addressLine1(a.getAddressLine1()).addressLine2(a.getAddressLine2())
            .city(a.getCity()).state(a.getState()).pincode(a.getPincode())
            .isDefault(a.getIsDefault()).build();
    }
}