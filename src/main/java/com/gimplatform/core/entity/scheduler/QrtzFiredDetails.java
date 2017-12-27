package com.gimplatform.core.entity.scheduler;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务调度历史数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qrtz_fired_details")
public class QrtzFiredDetails implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="QrtzFiredDetailsGenerator")
    @TableGenerator(name = "QrtzFiredDetailsGenerator",table="sys_tb_generator",pkColumnName="GEN_NAME",valueColumnName="GEN_VALUE",pkColumnValue="QRTZ_FIRED_DETAILS_PK",allocationSize=1)
	@Column(name = "FIRED_ID", unique = true, nullable = false, precision = 10, scale = 0)
	private Long firedId;

	@Column(name = "JOB_NAME", length = 200)
	private String jobName;
	
	@Column(name = "JOB_GROUP", length = 200)
	private String jobGroup;

	@Column(name = "TRIGGER_NAME", length = 200)
	private String triggerName;

	@Column(name = "TRIGGER_GROUP", length = 200)
	private String triggerGroup;

	@Column(name = "JOB_TYPE", length = 256)
	private String jobType;

	@Column(name = "JOB_STATUS", length = 5)
	private String jobStatus;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FIRED_DATE")
	private Date firedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "NEXT_FIRED_DATE")
	private Date nextFiredDate;
	
	@Column(name = "TRIGGER_TYPE", length = 20)
	private String triggerType;

	@Column(name = "FIRED_RESULT", length = 500)
	private String firedResult;
	
}
