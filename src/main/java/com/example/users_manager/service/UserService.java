package com.example.users_manager.service;

import com.example.users_manager.dto.CreateUpdateUserRequestDto;
import com.example.users_manager.dto.PagedResponse;
import com.example.users_manager.dto.UserDetailsResponseDto;
import com.example.users_manager.dto.UserListItemDto;
import com.example.users_manager.entity.Address;
import com.example.users_manager.entity.User;
import com.example.users_manager.entity.enums.AddressType;
import com.example.users_manager.entity.enums.Gender;
import com.example.users_manager.exception.ApiException;
import com.example.users_manager.repository.UserListProjection;
import com.example.users_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long createUser(CreateUpdateUserRequestDto dto) {

        try {

            User user = User.builder()
                    .name(dto.getName().trim())
                    .surname(dto.getSurname().trim())
                    .gender(dto.getGender())
                    .birthdate(dto.getBirthdate())
                    .addresses(new ArrayList<>())
                    .build();

            addAddressIfPresent(user, AddressType.HOME, dto.getHomeAddress());
            addAddressIfPresent(user, AddressType.WORK, dto.getWorkAddress());

            return userRepository.save(user).getId();

        } catch (Exception e) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create user"
            );
        }
    }

    @Transactional(readOnly = true)
    public UserDetailsResponseDto getUserById(Long id) {

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

            String home = null;
            String work = null;

            if (user.getAddresses() != null) {
                for (Address a : user.getAddresses()) {
                    if (a.getType() == AddressType.HOME) home = a.getValue();
                    if (a.getType() == AddressType.WORK) work = a.getValue();
                }
            }

            return UserDetailsResponseDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .gender(user.getGender())
                    .birthdate(user.getBirthdate())
                    .homeAddress(home)
                    .workAddress(work)
                    .build();

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    @Transactional
    public void deleteUser(Long id) {

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

            userRepository.delete(user);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserListItemDto> listUsers(Gender gender, String search, int page, int size) {

        try {
            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by("name").ascending()
            );

            Page<UserListProjection> result =
                    userRepository.searchUsers(gender, search, pageable);

            List<UserListItemDto> users = result.getContent()
                    .stream()
                    .map(p -> UserListItemDto.builder()
                            .id(p.getId())
                            .name(p.getName())
                            .surname(p.getSurname())
                            .build())
                    .toList();

            return PagedResponse.<UserListItemDto>builder()
                    .content(users)
                    .page(result.getNumber() + 1)
                    .size(result.getSize())
                    .totalElements(result.getTotalElements())
                    .totalPages(result.getTotalPages())
                    .hasNext(result.hasNext())
                    .hasPrevious(result.hasPrevious())
                    .build();

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    @Transactional
    public void updateUser(Long id, CreateUpdateUserRequestDto dto) {

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

            user.setName(dto.getName().trim());
            user.setSurname(dto.getSurname().trim());
            user.setGender(dto.getGender());
            user.setBirthdate(dto.getBirthdate());

            if (user.getAddresses() == null) {
                user.setAddresses(new ArrayList<>());
            }

            updateAddress(user, AddressType.HOME, dto.getHomeAddress());
            updateAddress(user, AddressType.WORK, dto.getWorkAddress());

            userRepository.save(user);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }
    }

    private void addAddressIfPresent(User user, AddressType type, String value) {
        if (value == null) return;

        String trimmed = value.trim();
        if (trimmed.isEmpty()) return;

        user.getAddresses().add(
                Address.builder()
                        .type(type)
                        .value(trimmed)
                        .user(user)
                        .build()
        );
    }

    // null -> don't change, "" -> clear, text -> upsert
    private void updateAddress(User user, AddressType type, String value) {
        if (value == null) return;

        String trimmed = value.trim();

        Address existing = user.getAddresses().stream()
                .filter(a -> a.getType() == type)
                .findFirst()
                .orElse(null);

        if (trimmed.isEmpty()) {
            if (existing != null) {
                user.getAddresses().remove(existing);
            }
            return;
        }

        if (existing == null) {
            user.getAddresses().add(
                    Address.builder()
                            .type(type)
                            .value(trimmed)
                            .user(user)
                            .build()
            );
        } else {
            existing.setValue(trimmed);
        }
    }
}