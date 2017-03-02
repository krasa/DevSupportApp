package krasa.release.tokenization;

import static org.apache.commons.lang3.StringUtils.substring;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

public class Default {

	public static final String SVN_REPO_URL = "http://svn/sdp";
	// public static final String TEMP = "target/branchPrepare-" + System.currentTimeMillis() + "-";
	public static final String TEMP = "target/branchPrepare";

	protected static TokenizationJobParameters loadFromJsonFake() {
		List<ReplacementDefinition> replacementDefinitions = new ArrayList<>();
		addPom(replacementDefinitions);
		addProperties(replacementDefinitions);
		addEclipse(replacementDefinitions);

		Map<String, String> map = new TreeMap<>();
		map.put("old.version", "9999");
		map.put("new.version", "14100");
		map.put("old.pom.version", "99.9.9-SNAPSHOT");

		map.put("new.build.version", "14100");
		map.put("new.portal.db.version", "14100");
		map.put("new.sac.db.version", "14100");
		map.put("new.pit.db.version", "14100");
		map.put("new.pom.version", "14.1.0");
		TokenizationJobParameters tokenizationJobParameters = new TokenizationJobParameters(replacementDefinitions, map);
		String s1 = tokenizationJobParameters.toJson(tokenizationJobParameters);

		return loadFromJson(s1);
	}

	public static String generateJson(String oldVersion, String newVersion, String newPortalDb, String newSacDb,
			String newPitDb) {
		List<ReplacementDefinition> replacementDefinitions = new ArrayList<>();
		addPom(replacementDefinitions);
		addProperties(replacementDefinitions);
		addEclipse(replacementDefinitions);

		Map<String, String> stringStringHashMap = new TreeMap<>();
		//
		stringStringHashMap.put("old.version", oldVersion);
		stringStringHashMap.put("new.version", newVersion);
		stringStringHashMap.put("old.pom.version", toPomVersion(oldVersion));

		stringStringHashMap.put("new.build.version", newVersion);
		stringStringHashMap.put("new.portal.db.version", newPortalDb);
		stringStringHashMap.put("new.sac.db.version", newSacDb);
		stringStringHashMap.put("new.pit.db.version", newPitDb);
		stringStringHashMap.put("new.pom.version", toPomVersion(newVersion));
		TokenizationJobParameters tokenizationJobParameters = new TokenizationJobParameters(replacementDefinitions,
				stringStringHashMap);
		String s1 = tokenizationJobParameters.toJson(tokenizationJobParameters);

		return s1;
	}

	protected static String toPomVersion(String fromVersion) {
		StringBuilder sb = new StringBuilder();
		sb.append(substring(fromVersion, 0, 2));
		appendVersion(fromVersion, sb, 2, 3);
		appendVersion(fromVersion, sb, 3, 4);
		appendVersion(fromVersion, sb, 4, 5);

		if ("9999".equals(fromVersion)) {
			sb.append("-SNAPSHOT");
		}
		return sb.toString();
	}

	private static void appendVersion(String fromVersion, StringBuilder sb, int start, int end) {
		String substring = substring(fromVersion, start, end);
		if (StringUtils.isNotEmpty(substring)) {
			sb.append(".");
			sb.append(substring);
		}
	}

	protected static TokenizationJobParameters loadFromJson(String s1) {
		return new Gson().fromJson(s1, TokenizationJobParameters.class);
	}

	private static void addEclipse(List<ReplacementDefinition> replacementDefinitions) {
		ReplacementDefinition definition = new ReplacementDefinition();
		replacementDefinitions.add(definition);

		List<String> includes = definition.getIncludes();
		includes.add("**/.project");

		List<Replacement> replacements = definition.getReplacements();
		replacements.add(new Replacement("<name>portal-{old.version}</name>", "<name>portal-{new.version}</name>"));
	}

	private static void addProperties(List<ReplacementDefinition> replacementDefinitions) {
		ReplacementDefinition definition = new ReplacementDefinition();
		replacementDefinitions.add(definition);

		List<String> includes = definition.getIncludes();
		includes.add("**/*.properties");
		// eclipse
		includes.add("**/.project");
		/* spi-pai */
		includes.add("**/*.sql");
		includes.add("**/PartnerContractDataProviderTest.java");

		List<Replacement> replacements = definition.getReplacements();
		replacements.add(new Replacement("build.number=${old.version}", "build.number=" + "${new.build.version}"));
		replacements.add(new Replacement("<name>portal-${old.version}</name>", "<name>portal-${new.version}</name>"));
		replacements.add(new Replacement("pit${old.version}", "pit" + "${new.pit.db.version}"));
		replacements.add(new Replacement("pai${old.version}", "pai" + "${new.portal.db.version}"));
		replacements.add(new Replacement("sac${old.version}", "sac" + "${new.sac.db.version}"));
		replacements.add(new Replacement("sdf${old.version}", "sdf" + "${new.portal.db.version}"));
		replacements.add(new Replacement(".version=${old.version}", ".version=" + "${new.version}"));
		replacements.add(new Replacement("default.component_id=${old.version}", "default.component_id="
				+ "${new.portal.db.version}"));
		replacements.add(new Replacement("default.docrootapp_id=${old.version}", "default.docrootapp_id="
				+ "${new.portal.db.version}"));
	}

	private static void addPom(List<ReplacementDefinition> replacementDefinitions) {
		ReplacementDefinition src = new ReplacementDefinition();
		replacementDefinitions.add(src);
		src.getIncludes().add("**/pom.xml");
		src.getReplacements().add(new Replacement("${old.pom.version}", "${new.pom.version}"));
	}

}
