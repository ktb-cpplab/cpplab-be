package com.cpplab.domain.auth.repository;


import com.cpplab.domain.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String username); // username을 전달하여 해당하는 엔티티 가져오기(JPA)
}
