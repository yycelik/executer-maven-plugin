package com.celik.services;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.celik.helper.SshUtils;
import com.celik.model.SshConnection;
import com.celik.model.SshResponse;
import com.celik.model.SshTimeoutException;

@Mojo(name = "remote-bash-executer", defaultPhase = LifecyclePhase.COMPILE)
public class RemoteBashExecuterMojo extends AbstractMojo {
    @Parameter(defaultValue = "${hostname}", required = true)
    String hostname;

    @Parameter(defaultValue = "${port}", required = false)
    int port = 22;

    @Parameter(defaultValue = "${username}", required = true)
    String username;

    @Parameter(defaultValue = "${username}", required = true)
    String password;

    @Parameter(defaultValue = "${timeout}", required = false)
    long timeout = 100;

    @Parameter(defaultValue = "${source}", required = false)
    String source = "conf\\deploy.sh";

    @Parameter(defaultValue = "${destination}", required = false)
    String destination = "/tmp";
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        SshConnection conn = new SshConnection(username, password, hostname, port);
        try {
            SshResponse res = SshUtils.runBash(conn, source, destination, timeout);

            getLog().info("err: " + res.getErrOutput());
            getLog().info("return: " + res.getReturnCode());
            getLog().info("out: " + res.getStdOutput());
        } catch (SshTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
