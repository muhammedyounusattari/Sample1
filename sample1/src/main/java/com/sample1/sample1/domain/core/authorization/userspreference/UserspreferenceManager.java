package com.sample1.sample1.domain.core.authorization.userspreference;

import com.querydsl.core.types.Predicate;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserspreferenceManager implements IUserspreferenceManager {

    @NonNull
    private final IUserspreferenceRepository _userspreferenceRepository;

    public UserspreferenceEntity create(UserspreferenceEntity userspreference) {
        return _userspreferenceRepository.save(userspreference);
    }

    public void delete(UserspreferenceEntity userspreference) {
        _userspreferenceRepository.delete(userspreference);
    }

    public UserspreferenceEntity update(UserspreferenceEntity userspreference) {
        return _userspreferenceRepository.save(userspreference);
    }

    public UserspreferenceEntity findById(Long usersId) {
        Optional<UserspreferenceEntity> dbUsers = _userspreferenceRepository.findById(usersId);
        if (dbUsers.isPresent()) {
            UserspreferenceEntity existingUsers = dbUsers.get();
            return existingUsers;
        } else {
            return null;
        }
    }

    public Page<UserspreferenceEntity> findAll(Predicate predicate, Pageable pageable) {
        return _userspreferenceRepository.findAll(predicate, pageable);
    }
}
