package com.p1.nomnom.address.service;

import com.p1.nomnom.address.dto.request.AddressRequestDto;
import com.p1.nomnom.address.dto.response.AddressResponseDto;
import com.p1.nomnom.address.entity.Address;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.address.repository.AddressRepository;
import com.p1.nomnom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    // 주소 목록 조회
    public List<AddressResponseDto> getAddressList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream()
                .map(addr -> new AddressResponseDto(addr.getId(), addr.getAddress(), addr.isDefault()))
                .collect(Collectors.toList());
    }

    // 주소 추가
    @Transactional
    public AddressResponseDto addAddress(String username, AddressRequestDto requestDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (addressRepository.existsByUserAndAddress(user, requestDto.getAddress())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 등록된 주소입니다.");
        }

        if (requestDto.isDefault()) {
            resetDefaultAddress(user);
        }

        Address newAddress = new Address(UUID.randomUUID(), user, requestDto.getAddress(), requestDto.isDefault(), false);
        addressRepository.save(newAddress);

        return new AddressResponseDto(newAddress.getId(), newAddress.getAddress(), newAddress.isDefault());
    }

    // 주소 수정
    @Transactional
    public AddressResponseDto updateAddress(String username, UUID addressId, AddressRequestDto requestDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다."));

        if (!address.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 주소를 수정할 권한이 없습니다.");
        }

        // 기본 주소 변경 시 기존 기본 주소 초기화
        if (requestDto.isDefault()) {
            resetDefaultAddress(user);
        }

        address.updateAddress(requestDto.getAddress(), requestDto.isDefault());

        return new AddressResponseDto(address.getId(), address.getAddress(), address.isDefault());
    }

    // 기본 주소 변경
    @Transactional
    public AddressResponseDto updateDefaultAddress(String username, UUID addressId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다."));

        if (!address.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 주소의 기본 설정을 변경할 권한이 없습니다.");
        }

        resetDefaultAddress(user);
        address.setAsDefault();

        return new AddressResponseDto(address.getId(), address.getAddress(), address.isDefault());
    }

    // 기본 주소 초기화 (기본 주소가 하나만 존재하도록 유지)
    @Transactional
    public void resetDefaultAddress(User user) {
        List<Address> addresses = addressRepository.findByUser(user);

        for (Address address : addresses) {
            if (address.isDefault()) {
                address.unsetDefault();
            }
        }
    }

    // 주소 숨김 처리 (Soft Delete)
    @Transactional
    public AddressResponseDto softDeleteAddress(String username, UUID addressId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Address address = addressRepository.findByIdAndNotDeleted(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다."));

        if (!address.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 주소를 삭제할 권한이 없습니다.");
        }

        address.softDelete();

        return new AddressResponseDto(address.getId(), address.getAddress(), address.isDefault());
    }

    // 삭제된 주소 복구
    @Transactional
    public AddressResponseDto restoreAddress(String username, UUID addressId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다."));

        if (!address.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 주소를 복구할 권한이 없습니다.");
        }

        address.restore();
        return new AddressResponseDto(address.getId(), address.getAddress(), address.isDefault());
    }
}
