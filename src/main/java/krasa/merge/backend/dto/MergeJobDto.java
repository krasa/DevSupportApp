package krasa.merge.backend.dto;

import krasa.merge.backend.service.automerge.AutoMergeJob;
import krasa.merge.backend.service.automerge.AutoMergeProcess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class MergeJobDto implements Serializable, Comparable<MergeJobDto> {

	public Date startTime;
	public Date endTime;
	private String from;
	private String to;
	private long revision;
	private String status;
	private Integer mergeJobId;

	public static List<MergeJobDto> translate(Collection<AutoMergeProcess> all) {
		List<MergeJobDto> mergebuildJobs = new ArrayList<>();

		for (AutoMergeProcess autoMergeProcess : all) {
			MergeJobDto e = AutoMergeJob.getMergeJobDto(autoMergeProcess);
			mergebuildJobs.add(e);
		}
		Collections.sort(mergebuildJobs);
		return mergebuildJobs;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getMergeJobId() {
		return mergeJobId;
	}

	public void setMergeJobId(Integer mergeJobId) {
		this.mergeJobId = mergeJobId;
	}

	@Override
	public int compareTo(MergeJobDto o) {
		Date start = this.getStartTime();
		Date start1 = o.getStartTime();

		if (start == null && start1 == null) {
			return 0;
		}
		if (start == null) {
			return 1;
		}
		if (start1 == null) {
			return -1;
		}
		return start.compareTo(start1);
	}
}
