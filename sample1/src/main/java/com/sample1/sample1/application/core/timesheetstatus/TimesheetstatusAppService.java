package com.sample1.sample1.application.core.timesheetstatus;

import com.querydsl.core.BooleanBuilder;
import com.sample1.sample1.application.core.timesheetstatus.dto.*;
import com.sample1.sample1.commons.logging.LoggingHelper;
import com.sample1.sample1.commons.search.*;
import com.sample1.sample1.domain.core.timesheetstatus.ITimesheetstatusRepository;
import com.sample1.sample1.domain.core.timesheetstatus.QTimesheetstatusEntity;
import com.sample1.sample1.domain.core.timesheetstatus.TimesheetstatusEntity;
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

@Service("timesheetstatusAppService")
@RequiredArgsConstructor
public class TimesheetstatusAppService implements ITimesheetstatusAppService {

    @Qualifier("timesheetstatusRepository")
    @NonNull
    protected final ITimesheetstatusRepository _timesheetstatusRepository;

    @Qualifier("ITimesheetstatusMapperImpl")
    @NonNull
    protected final ITimesheetstatusMapper mapper;

    @NonNull
    protected final LoggingHelper logHelper;

    @Transactional(propagation = Propagation.REQUIRED)
    public CreateTimesheetstatusOutput create(CreateTimesheetstatusInput input) {
        TimesheetstatusEntity timesheetstatus = mapper.createTimesheetstatusInputToTimesheetstatusEntity(input);

        TimesheetstatusEntity createdTimesheetstatus = _timesheetstatusRepository.save(timesheetstatus);
        return mapper.timesheetstatusEntityToCreateTimesheetstatusOutput(createdTimesheetstatus);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UpdateTimesheetstatusOutput update(Long timesheetstatusId, UpdateTimesheetstatusInput input) {
        TimesheetstatusEntity existing = _timesheetstatusRepository.findById(timesheetstatusId).get();

        TimesheetstatusEntity timesheetstatus = mapper.updateTimesheetstatusInputToTimesheetstatusEntity(input);
        timesheetstatus.setTimesheetsSet(existing.getTimesheetsSet());

        TimesheetstatusEntity updatedTimesheetstatus = _timesheetstatusRepository.save(timesheetstatus);
        return mapper.timesheetstatusEntityToUpdateTimesheetstatusOutput(updatedTimesheetstatus);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Long timesheetstatusId) {
        TimesheetstatusEntity existing = _timesheetstatusRepository.findById(timesheetstatusId).orElse(null);

        _timesheetstatusRepository.delete(existing);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public FindTimesheetstatusByIdOutput findById(Long timesheetstatusId) {
        TimesheetstatusEntity foundTimesheetstatus = _timesheetstatusRepository
            .findById(timesheetstatusId)
            .orElse(null);
        if (foundTimesheetstatus == null) return null;

        return mapper.timesheetstatusEntityToFindTimesheetstatusByIdOutput(foundTimesheetstatus);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<FindTimesheetstatusByIdOutput> find(SearchCriteria search, Pageable pageable) throws Exception {
        Page<TimesheetstatusEntity> foundTimesheetstatus = _timesheetstatusRepository.findAll(search(search), pageable);
        List<TimesheetstatusEntity> timesheetstatusList = foundTimesheetstatus.getContent();
        Iterator<TimesheetstatusEntity> timesheetstatusIterator = timesheetstatusList.iterator();
        List<FindTimesheetstatusByIdOutput> output = new ArrayList<>();

        while (timesheetstatusIterator.hasNext()) {
            TimesheetstatusEntity timesheetstatus = timesheetstatusIterator.next();
            output.add(mapper.timesheetstatusEntityToFindTimesheetstatusByIdOutput(timesheetstatus));
        }
        return output;
    }

    protected BooleanBuilder search(SearchCriteria search) throws Exception {
        QTimesheetstatusEntity timesheetstatus = QTimesheetstatusEntity.timesheetstatusEntity;
        if (search != null) {
            Map<String, SearchFields> map = new HashMap<>();
            for (SearchFields fieldDetails : search.getFields()) {
                map.put(fieldDetails.getFieldName(), fieldDetails);
            }
            List<String> keysList = new ArrayList<String>(map.keySet());
            checkProperties(keysList);
            return searchKeyValuePair(timesheetstatus, map, search.getJoinColumns());
        }
        return null;
    }

    protected void checkProperties(List<String> list) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            if (
                !(
                    list.get(i).replace("%20", "").trim().equals("id") ||
                    list.get(i).replace("%20", "").trim().equals("statusname")
                )
            ) {
                throw new Exception("Wrong URL Format: Property " + list.get(i) + " not found!");
            }
        }
    }

    protected BooleanBuilder searchKeyValuePair(
        QTimesheetstatusEntity timesheetstatus,
        Map<String, SearchFields> map,
        Map<String, String> joinColumns
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        for (Map.Entry<String, SearchFields> details : map.entrySet()) {
            if (details.getKey().replace("%20", "").trim().equals("id")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(timesheetstatus.id.like(details.getValue().getSearchValue() + "%"));
                } else if (
                    details.getValue().getOperator().equals("equals") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(timesheetstatus.id.eq(Long.valueOf(details.getValue().getSearchValue())));
                } else if (
                    details.getValue().getOperator().equals("notEqual") &&
                    StringUtils.isNumeric(details.getValue().getSearchValue())
                ) {
                    builder.and(timesheetstatus.id.ne(Long.valueOf(details.getValue().getSearchValue())));
                } else if (details.getValue().getOperator().equals("range")) {
                    if (
                        StringUtils.isNumeric(details.getValue().getStartingValue()) &&
                        StringUtils.isNumeric(details.getValue().getEndingValue())
                    ) {
                        builder.and(
                            timesheetstatus.id.between(
                                Long.valueOf(details.getValue().getStartingValue()),
                                Long.valueOf(details.getValue().getEndingValue())
                            )
                        );
                    } else if (StringUtils.isNumeric(details.getValue().getStartingValue())) {
                        builder.and(timesheetstatus.id.goe(Long.valueOf(details.getValue().getStartingValue())));
                    } else if (StringUtils.isNumeric(details.getValue().getEndingValue())) {
                        builder.and(timesheetstatus.id.loe(Long.valueOf(details.getValue().getEndingValue())));
                    }
                }
            }
            if (details.getKey().replace("%20", "").trim().equals("statusname")) {
                if (details.getValue().getOperator().equals("contains")) {
                    builder.and(
                        timesheetstatus.statusname.likeIgnoreCase("%" + details.getValue().getSearchValue() + "%")
                    );
                } else if (details.getValue().getOperator().equals("equals")) {
                    builder.and(timesheetstatus.statusname.eq(details.getValue().getSearchValue()));
                } else if (details.getValue().getOperator().equals("notEqual")) {
                    builder.and(timesheetstatus.statusname.ne(details.getValue().getSearchValue()));
                }
            }
        }

        return builder;
    }

    public Map<String, String> parseTimesheetsJoinColumn(String keysString) {
        Map<String, String> joinColumnMap = new HashMap<String, String>();
        joinColumnMap.put("timesheetstatusid", keysString);

        return joinColumnMap;
    }
}
