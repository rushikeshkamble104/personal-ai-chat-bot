package com.management.role.controller;

import com.management.role.dto.MessageResponse;
import com.management.role.dto.request.ActivityRequest;
import com.management.role.model.ActivityModel;
import com.management.role.repository.ActivityRepository;
import com.management.role.service.ActivityService;
import com.management.role.util.RoleEnum;
import com.management.role.util.RoleLogger;
import com.management.role.util.RoleUtility;
import io.micronaut.core.annotation.ReflectiveAccess;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static com.management.role.util.RoleUtility.FAILURE;
import static com.management.role.util.RoleUtility.SUCCESS;

/**
 * This class is a controller for activity
 */
@Controller
public class ActivityController {

    @Inject
    RoleLogger roleLogger;
    @Inject
    private ActivityService activityService;
    @Inject
    private ActivityRepository activityRepository;

    final String className = getClass().getSimpleName();

    /**
     * This method is used for saving activity
     */
    @ReflectiveAccess
    @Post("/postactivity")
    public MessageResponse saveActivity(@Body ActivityRequest activityRequest) {

        final List<RoleEnum> roleEnumList = new ArrayList<>();
        final String methodName = "saveActivity";

        if (RoleUtility.blankOrNullActivityId(activityRequest.getActivityId())) {
            roleLogger.logs(className, methodName, "EAZValidator.validateActivityIdRequest :: activityModel.getActivityId object is blank or null");
            roleEnumList.add(RoleEnum.ERROR_REQUEST_ACTIVITY_ID_IS_BLANK_OR_NULL);
        }
        else if (RoleUtility.invalidActivityId(activityRequest.getActivityId())) {
            roleLogger.logs(className, methodName, "EAZValidator.validateActivityIdRequest :: activityModel.getActivityId object is invalid");
            roleEnumList.add(RoleEnum.ERROR_REQUEST_ACTIVITY_ID_IS_INVALID);
        }
        else if (activityRepository.existsById(activityRequest.getActivityId())) {
            roleLogger.logs(className, methodName, "EAZValidator.validateActivityIdRequest :: activityModel.getActivityId object is already present in database");
            roleEnumList.add(RoleEnum.ERROR_REQUEST_ACTIVITY_ID_ALREADY_EXISTS);
        }
        if (RoleUtility.blankOrNullActivityName(activityRequest.getActivityName())) {
            roleLogger.logs(className, methodName, "EAZValidator.validateActivityNameRequest :: activityModel.getActivityName object is blank or null");
            roleEnumList.add(RoleEnum.ERROR_REQUEST_ACTIVITY_NAME_BLANK_OR_NULL);
        }
        if (!roleEnumList.isEmpty()) return RoleUtility.addMessage(FAILURE, roleEnumList);
        else {
            final ActivityModel activityModel = new ActivityModel();
            activityModel.setActivityId(activityRequest.getActivityId());
            activityModel.setActivityName(activityRequest.getActivityName());
            activityService.saveActivity(activityModel);
            roleLogger.logs(className, methodName, "Activity Validated and Added Successfully");
            roleEnumList.add(RoleEnum.ACTIVITY_VALIDATED_AND_ADDED_SUCCESSFULLY);
            return RoleUtility.addMessage(SUCCESS, roleEnumList);
        }
    }

    /**
     * This method is used for fetching all activities
     */
    @Get("/getactivity")
    public List<ActivityModel> getActivity() {
        return activityService.getActivity();
    }

    /**
     * This method is used for updating activity
     */
    @Put("/updateactivity/{id}")
    public MessageResponse updateActivity(@PathVariable String id, @Body ActivityModel activityModel) {
        final List<RoleEnum> roleEnumList = new ArrayList<>();
        final String methodName = "updateActivity";
        activityService.updateActivity(id, activityModel);
        roleLogger.logs(className, methodName, "Activity updated successfully");
        roleEnumList.add(RoleEnum.ACTIVITY_UPDATED_SUCCESSFULLY);
        return RoleUtility.addMessage(SUCCESS, roleEnumList);
    }

    /**
     * This method is used for deleting activity
     */
    @Delete("/deleteactivity/{id}")
    public MessageResponse deleteActivity(@PathVariable("id") String id) {
        final List<RoleEnum> roleEnumList = new ArrayList<>();
        final String methodName = "deleteActivity";
        activityService.deleteActivity(id);
        roleLogger.logs(className, methodName, "Activity deleted successfully");
        roleEnumList.add(RoleEnum.ACTIVITY_DELETED_SUCCESSFULLY);
        return RoleUtility.addMessage(SUCCESS, roleEnumList);
    }
}
