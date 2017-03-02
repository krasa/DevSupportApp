package krasa.build.frontend.pages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.io.IOUtils;
import org.apache.wicket.model.IModel;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.dto.LogFileDto;
import krasa.build.frontend.components.LogModel;
import krasa.core.frontend.pages.FileSystemLogUtils;

class BuildLogModel extends LogModel {

	private IModel<BuildJob> model;

	public BuildLogModel(IModel<BuildJob> jobIModel) {
		model = jobIModel;
	}

	@Override
	public boolean isAlive() {
		BuildJob buildJob = model.getObject();
		return buildJob.isProcessAlive();
	}

	@Override
	public LogFileDto getLog() {
		BuildJob buildJob = model.getObject();
		File logFileByName = FileSystemLogUtils.getLogFileByName(buildJob.getLogFileName());
		if (!logFileByName.exists()) {
			return new LogFileDto(-1, "File does not exists: " + logFileByName.getAbsolutePath());
		}
		return FileSystemLogUtils.readLogFileWithSizeLimit(logFileByName, FileSystemLogUtils.BUFFER_SIZE);
	}

	@Override
	public LogFileDto getNextLog(int offset) {
		BuildJob buildJob = model.getObject();

		File logFileByName = FileSystemLogUtils.getLogFileByName(buildJob.getLogFileName());
		try (BufferedReader reader = new BufferedReader(new FileReader(logFileByName))) {
			reader.skip(offset);
			String s = IOUtils.toString(reader);
			return new LogFileDto(s.length() + offset, s);
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public boolean exists() {
		BuildJob buildJob = model.getObject();
		return buildJob != null;
	}
}
