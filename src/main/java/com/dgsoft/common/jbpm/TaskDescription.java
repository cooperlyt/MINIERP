package com.dgsoft.common.jbpm;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-4-25
 * Time: 下午2:29
 */
public interface TaskDescription {
    String getTaskOperationPage();

    List<String> getTags();

    String getBuninessKey();

    String getBusinessName();

    String getDescription();

    String getStringValue(String key);

    @Deprecated
    String getValue(String key);
}
