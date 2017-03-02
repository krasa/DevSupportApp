package krasa.build.backend.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.util.comparator.BooleanComparator;

import krasa.build.backend.DateUtils;
import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.Status;

public class BuildJobDto implements Serializable {

	private String environment;
	private String status;
	private String component;
	private String caller;
	private Date start;
	private Date end;
	private Date expected;
	private Integer buildJobId;
	private Integer componentId;

	private BuildJobDto() {
	}

	public static List<BuildJobDto> translate(Collection<BuildJob> all) {
		List<BuildJobDto> buildJobs = new ArrayList<>();

		for (BuildJob buildJob : all) {
			BuildJobDto e = new BuildJobDto();
			e.setBuildJobId(buildJob.getId());
			e.setComponent(buildJob.getBuildableComponent().getName());
			e.setComponentId(buildJob.getBuildableComponent().getId());
			e.setStatus(buildJob.getStatus().name());
			e.setEnvironment(buildJob.getBuildableComponent().getEnvironment().getName());
			e.setStart(buildJob.getStartTime());
			e.setEnd(buildJob.getEndTime());
			e.setCaller(buildJob.getCaller());
			e.setExpected(buildJob.getBuildableComponent().getLastSuccessBuildDuration());
			buildJobs.add(e);
		}
		Collections.sort(buildJobs, new Comparator<BuildJobDto>() {

			@Override
			public int compare(BuildJobDto o1, BuildJobDto o2) {
				Date start = o1.getEnd();
				Date start1 = o2.getEnd();

				boolean processRunning = Status.RUNNING.name().equals(o1.getStatus());
				boolean processRunning2 = Status.RUNNING.name().equals(o2.getStatus());
				int compare = BooleanComparator.TRUE_LOW.compare(processRunning, processRunning2);
				if (compare == 0) {
					return DateUtils.compareDates(start, start1);
				}
				return compare;
			}
		});

		return buildJobs;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getExpected() {
		return expected;
	}

	public void setExpected(Date expected) {
		this.expected = expected;
	}

	public String getRemainsAsString() {
		if (start == null || expected == null) {
			return null;
		}
		long startTime = start.getTime();
		long expectedTime = expected.getTime();
		long l = startTime + expectedTime - System.currentTimeMillis();
		boolean negative = false;
		if (l < 0) {
			negative = true;
			l = -l;
		}
		Date date = new Date(l);
		String format = new SimpleDateFormat("mm:ss").format(date);
		if (negative) {
			return "-" + format;
		}
		return format;
	}

	public Integer getBuildJobId() {
		return buildJobId;
	}

	public void setBuildJobId(Integer buildJobId) {
		this.buildJobId = buildJobId;
	}

	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
	}

	public Integer getComponentId() {
		return componentId;
	}
}
