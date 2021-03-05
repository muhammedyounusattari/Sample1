package com.sample1.sample1.domain.core.authorization.userspreference;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserspreferenceManager {
    UserspreferenceEntity create(UserspreferenceEntity userspreference);

    void delete(UserspreferenceEntity userspreference);

    UserspreferenceEntity update(UserspreferenceEntity userspreference);

    UserspreferenceEntity findById(Long id);

    Page<UserspreferenceEntity> findAll(Predicate predicate, Pageable pageable);
}
