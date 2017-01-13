package pl.fzymek

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

class JenkinsBuildInfoTask extends DefaultTask {

	@Input
	String flavor

	@TaskAction
	void exec() {
	   getOutputFile().withWriter {
			out ->
				out.writeLine("#AUTO GENERATED, DO NOT EDIT!")
				out.writeLine("buildNumber=${getBuildNumber()}")
				out.writeLine("gitCommit=${getGitSha()}")
		}
	}

	@Input
	def getBuildNumber() {
		def build = System.getenv("BUILD_NUMBER")
		return build != null && !build.isEmpty() ? build : "Local build"
	}

	@Input
	def getGitSha() {
		return 'git rev-parse --short HEAD'.execute().text.trim()
	}

	@OutputFile
	File getOutputFile () {
		def parentDir = "${project.projectDir}/src/${flavor}/assets"
		new File(parentDir).mkdirs()
		new File(parentDir, "${project.jenkinsBuildInfo.fileName}.${project.jenkinsBuildInfo.fileExtension}")
	}

}

