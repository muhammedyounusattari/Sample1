package com.sample1.sample1.domain.core.authorization.tokenverification;

import java.time.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ITokenverificationRepository
    extends
        JpaRepository<TokenverificationEntity, TokenverificationId>,
        QuerydslPredicateExecutor<TokenverificationEntity> {
    TokenverificationEntity findByTokenAndTokenType(String token, String tokenType);

    TokenverificationEntity findByUsersIdAndTokenType(Long id, String tokenType);
}
