package krasa.release.tokenization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.maven.plugin.MojoExecutionException;
import org.tmatesoft.svn.core.SVNException;

import com.google.gson.Gson;

public class Example {

	public static final String SVN_REPO_URL = "http://svn/sdp";
	// public static final String TEMP = "target/branchPrepare-" + System.currentTimeMillis() + "-";
	public static final String TEMP = "target/branchPrepare";

	protected static TokenizationJobParameters loadFromJsonFake() {
		List<ReplacementDefinition> replacementDefinitions = new ArrayList<>();
		addPom(replacementDefinitions);
		addProperties(replacementDefinitions);

		Map<String, String> stringStringHashMap = new TreeMap<>();

		stringStringHashMap.put("old.build.version", "9999");
		stringStringHashMap.put("old.db.version", "9999");
		stringStringHashMap.put("old.pom.version", "99.9.9-SNAPSHOT");

		stringStringHashMap.put("new.build.version", "14100");
		stringStringHashMap.put("new.db.version", "14100");
		stringStringHashMap.put("new.pom.version", "14.1.0");
		TokenizationJobParameters tokenizationJobParameters = new TokenizationJobParameters(replacementDefinitions,
				stringStringHashMap);

		String s1 = tokenizationJobParameters.toJson(tokenizationJobParameters);

		return loadFromJson(s1);
	}

	protected static TokenizationJobParameters loadFromJson(String s1) {
		System.err.println(s1);
		return new Gson().fromJson(s1, TokenizationJobParameters.class);
	}

	private static void addProperties(List<ReplacementDefinition> replacementDefinitions) {
		ReplacementDefinition definition = new ReplacementDefinition();
		replacementDefinitions.add(definition);

		final List<String> includes = definition.getIncludes();
		includes.add("**/*deployment.properties");
		includes.add("**/default.properties");
		includes.add("**/servers.properties");

		final List<Replacement> replacements = definition.getReplacements();
		replacements.add(new Replacement("build.number=${old.build.version}", "build.number=" + "${new.build.version}"));
		replacements.add(new Replacement("pit${old.db.version}", "pit" + "${new.db.version}"));
		replacements.add(new Replacement("pai${old.db.version}", "pai" + "${new.db.version}"));
		replacements.add(new Replacement("sac${old.db.version}", "sac" + "${new.db.version}"));
		replacements.add(new Replacement("sdf${old.db.version}", "sdf" + "${new.db.version}"));
		replacements.add(new Replacement(".version=${old.db.version}", ".version=" + "${new.db.version}"));
		replacements.add(new Replacement("default.component_id=${old.db.version}", "default.component_id="
				+ "${new.db.version}"));
		replacements.add(new Replacement("default.docrootapp_id=${old.db.version}", "default.docrootapp_id="
				+ "${new.db.version}"));
	}

	private static void addPom(List<ReplacementDefinition> replacementDefinitions) {
		ReplacementDefinition src = new ReplacementDefinition();
		replacementDefinitions.add(src);
		src.getIncludes().add("**/pom.xml");
		src.getReplacements().add(new Replacement("${old.pom.version}", "${new.pom.version}"));
	}

	public static void main(String[] args) throws SVNException, IOException, MojoExecutionException {
		TokenizationJobParameters jobParameters = loadFromJsonFake();

		TokenizationJobCommand tokenizationJobCommand = new TokenizationJobCommand(jobParameters, SVN_REPO_URL,
				new File(TEMP), ".*_9999");

		// tokenizationJobCommand.replace();
	}
}
