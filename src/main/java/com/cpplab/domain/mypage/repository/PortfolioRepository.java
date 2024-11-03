package com.cpplab.domain.mypage.repository;

import com.cpplab.domain.auth.entity.UserEntity;
import com.cpplab.domain.mypage.entity.PortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {
    Optional<PortfolioEntity> findByUser(UserEntity userEntity);
    Optional<PortfolioEntity> findByUser_UserId(Long userId);
}
