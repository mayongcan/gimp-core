package com.gimplatform.core.entity.scheduler;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RestfulInfo extends JobInfo {

    private String restType;

    private String restUrl;

    private List<?> restParams;
}
