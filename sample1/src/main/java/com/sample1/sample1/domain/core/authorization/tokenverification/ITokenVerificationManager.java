package com.sample1.sample1.domain.core.authorization.tokenverification;

import java.time.*;

public interface ITokenVerificationManager {
    TokenverificationEntity save(TokenverificationEntity entity);

    void delete(TokenverificationEntity entity);

    TokenverificationEntity findByTokenAndType(String token, String tokenType);

    TokenverificationEntity findByUsersIdAndType(Long id, String tokenType);
}
