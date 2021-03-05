package com.sample1.sample1.application.core.timeofftype;

import com.querydsl.core.BooleanBuilder;
import com.sample1.sample1.application.core.timeofftype.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.timeofftype.ITimeofftypeRepository;
import com.sample1.sample1.domain.core.timeofftype.QTimeofftypeEntity;
import com.sample1.sample1.domain.core.timeofftype.TimeofftypeEntity;
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

@Service("timeofftypeAppService")
@RequiredArgsConstructor
public class TimeofftypeAppService implements ITimeofftypeAppService {

    @Qualifier("timeofftypeRepository")
    @NonNull
    protected final ITimeofftypeRepository _timeofftypeRepository;

    @Qualifier("ITimeofftypeMapperImpl")
    @NonNull
    protected final ITimeofftypeMapper mapper;

    @NonNull
    protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateTimeofftypeOutput create(CreateTimeofftypeInput input) {
        TimeofftypeEntity timeofftype = mapper.createTimeofftypeInputToTimeofftypeEntity(input);

        TimeofftypeEntity createdTimeofftype = _timeofftypeRepository.save(timeofftype);
        return mapper.timeofftypeEntityToCreateTimeofftypeOutput(createdTimeofftype);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateTimeofftypeOutput update(Long timeofftypeId, UpdateTimeofftypeInput input) {
        TimeofftypeEntity existing = _timeofftypeRepository.findById(timeofftypeId).get();

        TimeofftypeEntity timeofftype = mapper.updateTimeofftypeInputToTimeofftypeEntity(input);
        timeofftype.setTimesheetdetailsSet(existing.getTimesheetdetailsSet());

        TimeofftypeEntity updatedTimeofftype = _timeofftypeRepository.save(timeofftype);
        return mapper.timeofftypeEntityToUpdateTimeofftypeOutput(updatedTimeofftype);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long timeofftypeId) {
        TimeofftypeEntity existing = _timeofftypeRepository.findById(timeofftypeId).orElse(null);

        _timeofftypeRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindTimeofftypeByIdOutput findById(Long timeofftypeId) {
        TimeofftypeEntity foundTimeofftype = _timeofftypeRepository.findById(timeofftypeId).orElse(null);
        if (foundTimeofftype == null) return null;

        return mapper.timeofftypeEntityToFindTimeofftypeByIdOutput(foundTimeofftype);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindTimeofftypeByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<TimeofftypeEntity> foundTimeofftype = _timeofftypeRepository.findAll(search(search), pageable);
        List<TimeofftypeEntity> timeofftypeList = foundTimeofftype.getContent();
        Iterator<TimeofftypeEntity> timeofftypeIterator = timeofftypeList.iterator();
        List<FindTimeofftypeByIdOutput> output = new ArrayList<>();

        while (timeofftypeIterator.hasNext()) {
            TimeofftypeEntity timeofftype = timeofftypeIterator.next();
            output.add(mapper.timeofftypeEntityToFindTimeofftypeByIdOutput(timeofftype));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QTimeofftypeEntity timeofftype = QTimeofftypeEntity.timeofftypeEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(timeofftype, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("typename")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QTimeofftypeEntity timeofftype,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("id")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(timeofftype.id.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(timeofftype.id.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(timeofftype.id.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            timeofftype.id.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(timeofftype.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(timeofftype.id.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("typename")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(timeofftype.typename.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%"));
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(timeofftype.typename.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(timeofftype.typename.ne(details.getValue().getSearchValue()));
                }
            }
        }

        return builder;
    }

    public Map<String, String> parseTimesheetdetailsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("timeofftypeid", keysString);

        return joinColumnMap;
    }
}
