package krasa.release.tokenization;

import java.io.File;
import java.util.*;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.*;

import com.google.code.maven_replacer_plugin.ReplacerMojo;
import com.google.code.maven_replacer_plugin.file.FileUtils;

public class ReplacementCommand {

	protected static final Logger log = LoggerFactory.getLogger(ReplacementCommand.class);

	private final FileUtils fileUtils = new FileUtils();
	private Map<String, String> placeholderReplace;

	public static void main(String[] args) throws MojoExecutionException {
		TokenizationJobParameters tokenizationJobParameters = new TokenizationJobParameters();
		ArrayList<ReplacementDefinition> replacementDefinitions = new ArrayList<ReplacementDefinition>();
		ReplacementDefinition e = new ReplacementDefinition();
		e.setIncludes(Arrays.asList("**/logback.xml"));
		e.setReplacements(Arrays.asList(new Replacement("net.logstash.logback.appender.LogstashTcpSocketAppender\">",
				"tmsdp.util.logging.SdpLogstashTcpSocketAppender\">\n"
						+ "\t\t<enabled>@logstash.enabled@</enabled>")));
		replacementDefinitions.add(e);
		tokenizationJobParameters.setReplacementDefinitions(replacementDefinitions);
		new ReplacementCommand().replace(tokenizationJobParameters.getReplacementDefinitions(),
				tokenizationJobParameters.getPlaceholderReplace(), new File(
						"D:\\workspace\\_projekty\\_T-Mobile\\9999\\"));
	}

	protected void replace(List<ReplacementDefinition> replacementDefinitions, Map<String, String> placeholderReplace,
			File tempDir) throws MojoExecutionException {
		this.placeholderReplace = placeholderReplace;
		for (ReplacementDefinition replacementDefinition : replacementDefinitions) {
			if (replacementDefinition.getIncludes().isEmpty() || replacementDefinition.getReplacements().isEmpty()) {
				continue;
			}

			ReplacerMojo replacerMojo = new ReplacerMojo();
			replacerMojo.setLog(new ReplacerLog());
			replacerMojo.setBasedir(tempDir.getAbsolutePath());
			replacerMojo.getIncludes().addAll(replacementDefinition.getIncludes());
			List<com.google.code.maven_replacer_plugin.Replacement> replacements = getReplacements(replacementDefinition);
			replacerMojo.setReplacements(replacements);
			log(replacementDefinition, replacements);
			log.info("Executing replace");
			replacerMojo.execute();
		}
	}

	private void log(ReplacementDefinition replacementDefinition,
			List<com.google.code.maven_replacer_plugin.Replacement> replacements) {
		StringBuilder sb = new StringBuilder();
		sb.append("Includes:[\n");
		for (int i = 0; i < replacementDefinition.getIncludes().size(); i++) {
			String include = replacementDefinition.getIncludes().get(i);
			sb.append("\t").append(include);
			if (i != replacementDefinition.getIncludes().size() - 1) {
				sb.append(", \n");
			}
		}
		sb.append("\n]\n");
		sb.append("Replacements:[\n");
		for (int i = 0; i < replacements.size(); i++) {
			com.google.code.maven_replacer_plugin.Replacement replacement = replacements.get(i);
			sb.append("\t").append(replacement.getToken()).append(" -> ").append(replacement.getValue());
			if (i != replacements.size() - 1) {
				sb.append(", \n ");
			}
		}
		sb.append("\n]");
		log.info(sb.toString());

	}

	private ArrayList<com.google.code.maven_replacer_plugin.Replacement> getReplacements(
			ReplacementDefinition replacementDefinition) {
		ArrayList<com.google.code.maven_replacer_plugin.Replacement> replacements = new ArrayList<>();
		for (krasa.release.tokenization.Replacement replacement : replacementDefinition.getReplacements()) {
			replacements.add(new com.google.code.maven_replacer_plugin.Replacement(fileUtils,
					replacePlaceholders(replacement.getFrom()), replacePlaceholders(replacement.getTo()), false,
					null, Charsets.UTF_8.name()));
		}
		return replacements;
	}

	private String replacePlaceholders(String value) {
		return StrSubstitutor.replace(value, placeholderReplace);
	}
}
