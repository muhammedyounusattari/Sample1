package com.sample1.sample1.application.core.authorization.users;

import com.querydsl.core.BooleanBuilder;
import com.sample1.sample1.application.core.authorization.users.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.authorization.users.IUsersRepository;
import com.sample1.sample1.domain.core.authorization.users.QUsersEntity;
import com.sample1.sample1.domain.core.authorization.users.UsersEntity;
import com.sample1.sample1.domain.core.authorization.userspreference.IUserspreferenceRepository;
import com.sample1.sample1.domain.core.authorization.userspreference.UserspreferenceEntity;
import com.sample1.sample1.security.SecurityUtils;
import java.time.*;
import java.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("usersAppService")
@RequiredArgsConstructor
public class UsersAppService implements IUsersAppService {

    public static final long PASSWORD_TOKEN_EXPIRATION_TIME = 3_600_000; // 1 hour

    @Qualifier("usersRepository")
    @NonNull
    protected final IUsersRepository _usersRepository;

    @Qualifier("userspreferenceRepository")
    @NonNull
    protected final IUserspreferenceRepository _userspreferenceRepository;

    @Qualifier("IUsersMapperImpl")
    @NonNull
    protected final IUsersMapper mapper;

    @NonNull
    protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateUsersOutput create(CreateUsersInput input) {
        UsersEntity users = mapper.createUsersInputToUsersEntity(input);

        UsersEntity createdUsers = _usersRepository.save(users);
        UserspreferenceEntity usersPreference = createDefaultUsersPreference(createdUsers);
        return mapper.usersEntityToCreateUsersOutput(createdUsers, usersPreference);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateUsersOutput update(Long usersId, UpdateUsersInput input) {
        UsersEntity existing = _usersRepository.findById(usersId).get();

        UsersEntity users = mapper.updateUsersInputToUsersEntity(input);
        users.setTimesheetsSet(existing.getTimesheetsSet());
        users.setTokenverificationsSet(existing.getTokenverificationsSet());
        users.setUserspermissionsSet(existing.getUserspermissionsSet());
        users.setUsersrolesSet(existing.getUsersrolesSet());
        users.setUsertasksSet(existing.getUsertasksSet());

        UsersEntity updatedUsers = _usersRepository.save(users);
        return mapper.usersEntityToUpdateUsersOutput(updatedUsers);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long usersId) {
        UsersEntity existing = _usersRepository.findById(usersId).orElse(null);

        UserspreferenceEntity userspreference = _userspreferenceRepository.findById(usersId).orElse(null);
        _userspreferenceRepository.delete(userspreference);

        _usersRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindUsersByIdOutput findById(Long usersId) {
        UsersEntity foundUsers = _usersRepository.findById(usersId).orElse(null);
        if (foundUsers == null) return null;

        UserspreferenceEntity usersPreference = _userspreferenceRepository.findById(usersId).orElse(null);

        return mapper.usersEntityToFindUsersByIdOutput(foundUsers, usersPreference);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserspreferenceEntity createDefaultUsersPreference(UsersEntity users) {
        UserspreferenceEntity userspreference = new UserspreferenceEntity();
        userspreference.setTheme("default-theme");
        userspreference.setLanguage("en");
        userspreference.setId(users.getId());
        userspreference.setUsers(users);

        return _userspreferenceRepository.save(userspreference);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateTheme(UsersEntity users, String theme) {
        UserspreferenceEntity userspreference = _userspreferenceRepository.findById(users.getId()).orElse(null);
        userspreference.setTheme(theme);

        _userspreferenceRepository.save(userspreference);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateLanguage(UsersEntity users, String language) {
        UserspreferenceEntity userspreference = _userspreferenceRepository.findById(users.getId()).orElse(null);
        userspreference.setLanguage(language);

        _userspreferenceRepository.save(userspreference);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindUsersWithAllFieldsByIdOutput findWithAllFieldsById(Long usersId) {
        UsersEntity foundUsers = _usersRepository.findById(usersId).orElse(null);
        if (foundUsers == null) return null;

        return mapper.usersEntityToFindUsersWithAllFieldsByIdOutput(foundUsers);
    }

    public UsersProfile getProfile(FindUsersByIdOutput user) {
        return mapper.findUsersByIdOutputToUsersProfile(user);
    }

    public UsersProfile updateUsersProfile(FindUsersWithAllFieldsByIdOutput users, UsersProfile usersProfile) {
        UpdateUsersInput usersInput = mapper.findUsersWithAllFieldsByIdOutputAndUsersProfileToUpdateUsersInput(
            users,
            usersProfile
        );
        UpdateUsersOutput output = update(users.getId(), usersInput);

        return mapper.updateUsersOutputToUsersProfile(output);
    }

    @Transactional(readOnly = true)
    public UsersEntity getUsers() {
        return _usersRepository.findByUsername(SecurityUtils.getCurrentUserLogin().orElse(null));
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindUsersByUsernameOutput findByUsername(String username) {
        UsersEntity foundUsers = _usersRepository.findByUsername(username);
        if (foundUsers == null) {
            return null;
        }

        return mapper.usersEntityToFindUsersByUsernameOutput(foundUsers);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindUsersByUsernameOutput findByEmailaddress(String emailAddress) {
        UsersEntity foundUsers = _usersRepository.findByEmailaddress(emailAddress);
        if (foundUsers == null) {
            return null;
        }

        return mapper.usersEntityToFindUsersByUsernameOutput(foundUsers);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUsersData(FindUsersWithAllFieldsByIdOutput users) {
        UsersEntity foundUsers = mapper.findUsersWithAllFieldsByIdOutputToUsersEntity(users);
        _usersRepository.save(foundUsers);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindUsersByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<UsersEntity> foundUsers = _usersRepository.findAll(search(search), pageable);
        List<UsersEntity> usersList = foundUsers.getContent();
        Iterator<UsersEntity> usersIterator = usersList.iterator();
        List<FindUsersByIdOutput> output = new ArrayList<>();

        while (usersIterator.hasNext()) {
            UsersEntity users = usersIterator.next();
            UserspreferenceEntity usersPreference = _userspreferenceRepository.findById(users.getId()).orElse(null);
            output.add(mapper.usersEntityToFindUsersByIdOutput(users, usersPreference));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QUsersEntity users = QUsersEntity.usersEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(users, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("emailaddress") ||
                    list.get(i).replace("%20", "").trim().equals("firstname") ||
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("isactive") ||
                    list.get(i).replace("%20", "").trim().equals("joinDate") ||
                    list.get(i).replace("%20", "").trim().equals("lastname") ||
                    list.get(i).replace("%20", "").trim().equals("password") ||
                    list.get(i).replace("%20", "").trim().equals("triggerGroup") ||
                    list.get(i).replace("%20", "").trim().equals("triggerName") ||
                    list.get(i).replace("%20", "").trim().equals("username")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QUsersEntity users,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("emailaddress")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.emailaddress.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.emailaddress.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.emailaddress.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("firstname")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.firstname.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.firstname.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.firstname.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("id")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.id.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(users.id.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(users.id.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            users.id.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(users.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(users.id.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("isactive")) {
                if (
                    details.getValue().getOperator().equals("equals") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(users.isactive.eq(Boolean.parseBoolean(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    (
                        details.getValue().getSearchValue().equalsIgnoreCase("true") ||
                        details.getValue().getSearchValue().equalsIgnoreCase("false")
                    )
                ) {
                    builder.and(users.isactive.ne(Boolean.parseBoolean(details.getValue().getSearchValue())));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("joinDate")) {
                if (
                    details.getValue().getOperator().equals("equals") &&
                    SearchUtils.stringToLocalDate(details.getValue().getSearchValue()) != null
                ) {
                    builder.and(users.joinDate.eq(SearchUtils.stringToLocalDate(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    SearchUtils.stringToLocalDate(details.getValue().getSearchValue()) != null
                ) {
                    builder.and(users.joinDate.ne(SearchUtils.stringToLocalDate(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    LocalDate startLocalDate = SearchUtils.stringToLocalDate(details.getValue().getStartingValue());
                    LocalDate endLocalDate = SearchUtils.stringToLocalDate(details.getValue().getEndingValue());
                    if (startLocalDate != null && endLocalDate != null) {
                        builder.and(users.joinDate.between(startLocalDate, endLocalDate));
                    } else if (endLocalDate != null) {
                        builder.and(users.joinDate.loe(endLocalDate));
                    } else if (startLocalDate != null) {
                        builder.and(users.joinDate.goe(startLocalDate));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("lastname")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.lastname.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.lastname.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.lastname.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("password")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.password.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.password.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.password.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("triggerGroup")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.triggerGroup.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.triggerGroup.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.triggerGroup.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("triggerName")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.triggerName.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.triggerName.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.triggerName.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("username")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(users.username.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(users.username.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(users.username.ne(details.getValue().getSearchValue()));
                }
            }
        }

        return builder;
    }

    public Map<String, String> parseTimesheetsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("userid", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseUserspermissionsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("usersId", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseUsersrolesJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("usersId", keysString);

        return joinColumnMap;
    }

    public Map<String, String> parseUsertasksJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("userid", keysString);

        return joinColumnMap;
    }
}
