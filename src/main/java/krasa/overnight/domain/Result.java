package krasa.overnight.domain;

import java.util.Date;

import javax.persistence.*;

import krasa.core.backend.domain.AbstractEntity;

@Entity(name = "results")
public class Result extends AbstractEntity {

	@Column(name = "time_stamp")
	Date timeStamp;

	@Column(name = "duration")
	Integer duration;

	@ManyToOne
	@JoinColumn(name = "id_test")
	TestName testName;
	@ManyToOne
	@JoinColumn(name = "id_target_env")
	Environment targetEnvironment;

	@ManyToOne
	@JoinColumn(name = "id_injector_env")
	Environment injectorEnvironment;

	@ManyToOne
	@JoinColumn(name = "id_country")
	Country country;
	@ManyToOne
	@JoinColumn(name = "id_testingTool")
	TestingTool testingTool;

	@ManyToOne
	@JoinColumn(name = "id_resultcode")
	ResultCode result;

	public TestingTool getTestingTool() {
		return testingTool;
	}

	public void setTestingTool(TestingTool testingTool) {
		this.testingTool = testingTool;
	}

	public String getName() {
		return testName.getName();
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public TestName getTestName() {
		return testName;
	}

	public void setTestName(TestName testName) {
		this.testName = testName;
	}

	public Environment getTargetEnvironment() {
		return targetEnvironment;
	}

	public void setTargetEnvironment(Environment targetEnvironment) {
		this.targetEnvironment = targetEnvironment;
	}

	public Environment getInjectorEnvironment() {
		return injectorEnvironment;
	}

	public void setInjectorEnvironment(Environment injectorEnvironment) {
		this.injectorEnvironment = injectorEnvironment;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public ResultCode getResult() {
		return result;
	}

	public void setResult(ResultCode result) {
		this.result = result;
	}
}
