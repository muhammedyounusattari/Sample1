package com.sample1.sample1.application.core.appconfiguration;

import com.querydsl.core.BooleanBuilder;
import com.sample1.sample1.application.core.appconfiguration.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.appconfiguration.AppConfigurationEntity;
import com.sample1.sample1.domain.core.appconfiguration.IAppConfigurationRepository;
import com.sample1.sample1.domain.core.appconfiguration.QAppConfigurationEntity;
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

@Service("appConfigurationAppService")
@RequiredArgsConstructor
public class AppConfigurationAppService implements IAppConfigurationAppService {

    @Qualifier("appConfigurationRepository")
    @NonNull
    protected final IAppConfigurationRepository _appConfigurationRepository;

    @Qualifier("IAppConfigurationMapperImpl")
    @NonNull
    protected final IAppConfigurationMapper mapper;

    @NonNull
    protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateAppConfigurationOutput create(CreateAppConfigurationInput input) {
        AppConfigurationEntity appConfiguration = mapper.createAppConfigurationInputToAppConfigurationEntity(input);

        AppConfigurationEntity createdAppConfiguration = _appConfigurationRepository.save(appConfiguration);
        return mapper.appConfigurationEntityToCreateAppConfigurationOutput(createdAppConfiguration);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateAppConfigurationOutput update(Long appConfigurationId, UpdateAppConfigurationInput input) {
        AppConfigurationEntity existing = _appConfigurationRepository.findById(appConfigurationId).get();

        AppConfigurationEntity appConfiguration = mapper.updateAppConfigurationInputToAppConfigurationEntity(input);

        AppConfigurationEntity updatedAppConfiguration = _appConfigurationRepository.save(appConfiguration);
        return mapper.appConfigurationEntityToUpdateAppConfigurationOutput(updatedAppConfiguration);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long appConfigurationId) {
        AppConfigurationEntity existing = _appConfigurationRepository.findById(appConfigurationId).orElse(null);

        _appConfigurationRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindAppConfigurationByIdOutput findById(Long appConfigurationId) {
        AppConfigurationEntity foundAppConfiguration = _appConfigurationRepository
            .findById(appConfigurationId)
            .orElse(null);
        if (foundAppConfiguration == null) return null;

        return mapper.appConfigurationEntityToFindAppConfigurationByIdOutput(foundAppConfiguration);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindAppConfigurationByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<AppConfigurationEntity> foundAppConfiguration = _appConfigurationRepository.findAll(
            search(search),
            pageable
        );
        List<AppConfigurationEntity> appConfigurationList = foundAppConfiguration.getContent();
        Iterator<AppConfigurationEntity> appConfigurationIterator = appConfigurationList.iterator();
        List<FindAppConfigurationByIdOutput> output = new ArrayList<>();

        while (appConfigurationIterator.hasNext()) {
            AppConfigurationEntity appConfiguration = appConfigurationIterator.next();
            output.add(mapper.appConfigurationEntityToFindAppConfigurationByIdOutput(appConfiguration));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QAppConfigurationEntity appConfiguration = QAppConfigurationEntity.appConfigurationEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(appConfiguration, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("type") ||
                    list.get(i).replace("%20", "").trim().equals("value")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QAppConfigurationEntity appConfiguration,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("id")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(appConfiguration.id.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(appConfiguration.id.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(appConfiguration.id.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            appConfiguration.id.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(appConfiguration.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(appConfiguration.id.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("type")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(appConfiguration.type.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(appConfiguration.type.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(appConfiguration.type.ne(details.getValue().getSearchValue()));
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("value")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(appConfiguration.value.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(appConfiguration.value.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(appConfiguration.value.ne(details.getValue().getSearchValue()));
                }
            }
        }

        return builder;
    }
}
