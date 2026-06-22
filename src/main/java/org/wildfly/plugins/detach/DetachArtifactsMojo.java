/*
 * Copyright The WildFly Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.wildfly.plugins.detach;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Detaches artifacts from the project based on a regex pattern matching the artifact ID.
 * This is useful when you want to build artifacts (e.g., for signing) but don't want to
 * install or deploy them to repositories.
 *
 * @author WildFly Community
 */
@Mojo(name = "detach-artifacts", threadSafe = true)
public class DetachArtifactsMojo extends AbstractMojo {

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Regular expression pattern to match against artifact IDs.
     * Artifacts whose IDs match this pattern will be detached from the project.
     * <p>
     * Example: {@code \S{1,}tar\.gz\S*} will match all tar.gz files and their signatures.
     * </p>
     */
    @Parameter(required = true)
    private String regex;

    @Override
    public void execute() throws MojoExecutionException {
        if (regex == null || regex.trim().isEmpty()) {
            throw new MojoExecutionException("The 'regex' parameter is required and cannot be empty");
        }

        Pattern pattern;
        try {
            pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            throw new MojoExecutionException("Invalid regex pattern: " + regex, e);
        }

        List<Artifact> attachedArtifacts = project.getAttachedArtifacts();
        if (attachedArtifacts == null || attachedArtifacts.isEmpty()) {
            getLog().info("No attached artifacts found to detach");
            return;
        }

        List<Artifact> toDetach = new ArrayList<>();
        for (Artifact artifact : attachedArtifacts) {
            String artifactId = artifact.getId();
            if (artifactId != null && pattern.matcher(artifactId).matches()) {
                toDetach.add(artifact);
                getLog().info("Detaching artifact: " + artifactId);
            }
        }

        if (toDetach.isEmpty()) {
            for (Artifact artifact : attachedArtifacts) {
                File file = artifact.getFile();
                if (file != null && pattern.matcher(file.getName()).matches()) {
                    toDetach.add(artifact);
                    getLog().info("Detaching artifact: " + artifact.getId());
                }
            }

        }

        if (toDetach.isEmpty()) {
            getLog().info("No artifacts matched the pattern: " + regex);
        } else {
            attachedArtifacts.removeAll(toDetach);
            getLog().info("Detached " + toDetach.size() + " artifact(s) matching pattern: " + regex);
        }
    }
}

