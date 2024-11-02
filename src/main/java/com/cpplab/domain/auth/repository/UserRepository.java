package com.cpplab.domain.auth.repository;


import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.global.common.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByProviderAndEmail(Provider provider, String email);
}
