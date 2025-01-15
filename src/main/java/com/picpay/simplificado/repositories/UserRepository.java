package com.picpay.simplificado.repositories;

import com.picpay.simplificado.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByDocument(String document);

    User findByEmail(String email);
}
