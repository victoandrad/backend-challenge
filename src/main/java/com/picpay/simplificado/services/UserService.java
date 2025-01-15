package com.picpay.simplificado.services;

import com.picpay.simplificado.domain.user.User;
import com.picpay.simplificado.dtos.UserDTO;
import com.picpay.simplificado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User createUserFromDTO(UserDTO dto) throws Exception {
        User user = new User(dto);
        return this.insert(user);
    }

    public User findById(Long id) throws Exception {
        Optional<User> user = this.repository.findById(id);
        return user.orElseThrow(() -> new Exception("Resource Not Found"));
    }

    public User findByDocument(String document) throws Exception {
        Optional<User> user = this.repository.findByDocument(document);
        return user.orElseThrow(() -> new Exception("Resource Not Found"));
    }

    public User findByEmail(String email) throws Exception {
        Optional<User> user = this.repository.findByEmail(email);
        return user.orElseThrow(() -> new Exception("Resource Not Found"));
    }

    public List<User> findAll() throws Exception {
        return this.repository.findAll();
    }

    public User insert(User user) {
        return this.repository.save(user);
    }

    public void delete(Long id) throws Exception {
        try {
            this.repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new Exception(e.getMessage());
        } catch (EmptyResultDataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }

    public User update(Long id, UserDTO dto) throws Exception {
        User user = this.findById(id);
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setDocument(dto.document());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setUserType(dto.userType());
        return this.insert(user);
    }
}
