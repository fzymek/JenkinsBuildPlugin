package pl.fzymek 

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskInstantiationException

class JenkinsBuildInfoPlugin implements Plugin<Project> {

	void apply(Project project) {
		verifyRequiredPlugins project

		project.extensions.create("jenkinsBuildInfo", JenkinsBuildInfoExtension)

		// TODO known limitation if there is no Flavours the plugin will not apply to default src/main
		project.afterEvaluate {
			project.android.productFlavors.each { flavour ->

				if (flavour.hasProperty("includeJenkinsBuildInfo") && !flavour.ext.includeJenkinsBuildInfo) return

				//create dynamic task
				def taskAddBuildInfo = "addJenkinsBuidInfoFor${flavour.name}"

				//add dynamic task to tasks graph
				project.tasks.find { task ->
					def pattern = ~/(?i)merge${flavour.name}.*Assets/
					pattern.matcher(task.name).matches()
				}.dependsOn project.tasks.create([name: "$taskAddBuildInfo", type: JenkinsBuildInfoTask], {
					flavor = flavour.name
				})
			}
		}
	}

	// check if 'android' plugin is applied to the project
	private static void verifyRequiredPlugins(Project project) {
		if (!project.plugins.hasPlugin(AppPlugin) && !project.plugins.hasPlugin(LibraryPlugin)) {
			throw new TaskInstantiationException("'android' or 'android-library' plugin has to be applied before")
		}
	}

}
