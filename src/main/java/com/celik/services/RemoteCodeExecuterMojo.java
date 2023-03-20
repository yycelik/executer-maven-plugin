package com.celik.services;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "remote-code-executer", defaultPhase = LifecyclePhase.COMPILE)
public class RemoteCodeExecuterMojo extends AbstractMojo {
    @Parameter(defaultValue = "${serverNames}", required = true)
    String serverNames;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Testing: " + serverNames);
    }
}
