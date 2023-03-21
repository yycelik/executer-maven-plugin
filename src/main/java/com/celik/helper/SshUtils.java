package com.celik.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;

import com.celik.model.SshConnection;
import com.celik.model.SshResponse;
import com.celik.model.SshTimeoutException;

/**
 * Example utility to use SSH via Apache Mina SSHD
 * 
 * @author Drew Thorstensen
 *
 */
public final class SshUtils {

  private SshUtils() {}

  /**
   * Runs a SSH command against a remote system.
   * 
   * @param conn Defines the connection to the system.
   * @param cmd Commands can be add with " \n "
   * @param timeout The amount of time to wait for the command to run before timing out. This is in
   *        seconds. This is used as two separate timeouts, one for login another for command
   *        execution.
   * @return The {@link SshResponse} contains the output of a successful command.
   * @throws SshTimeoutException Raised if the command times out.
   * @throws IOException Raised in the event of a general failure (wrong authentication or something
   *         of that nature).
   */
  public static SshResponse runCommand(SshConnection conn, String cmd, long timeout)
      throws SshTimeoutException, IOException {
    return runCommand(conn, cmd, "", "", timeout);
  }

  /**
   * Runs a SSH command against a remote system.
   * 
   * @param conn Defines the connection to the system.
   * @param source source file wich will be use sftp
   * @param destination destination file wich will be use sftp
   * @param timeout The amount of time to wait for the command to run before timing out. This is in
   *        seconds. This is used as two separate timeouts, one for login another for command
   *        execution.
   * @return The {@link SshResponse} contains the output of a successful command.
   * @throws SshTimeoutException Raised if the command times out.
   * @throws IOException Raised in the event of a general failure (wrong authentication or something
   *         of that nature).
   */
  public static SshResponse runBash(SshConnection conn, String source, String destination, long timeout)
      throws SshTimeoutException, IOException {
    // prepare path
    Path sourcePath = Paths.get(source);

    // run shell file
    String cmd = "chmod +x " + destination + "/" + sourcePath.getFileName() + " \n " + destination + "/" + sourcePath.getFileName();

    return runCommand(conn, cmd, source, destination, timeout);
  }
  
  /**
   * Runs a SSH command against a remote system.
   * 
   * @param conn Defines the connection to the system.
   * @param cmd Commands can be add with " \n "
   * @param source source file wich will be use sftp
   * @param destination destination file wich will be use sftp
   * @param timeout The amount of time to wait for the command to run before timing out. This is in
   *        seconds. This is used as two separate timeouts, one for login another for command
   *        execution.
   * @return The {@link SshResponse} contains the output of a successful command.
   * @throws SshTimeoutException Raised if the command times out.
   * @throws IOException Raised in the event of a general failure (wrong authentication or something
   *         of that nature).
   */
  private static SshResponse runCommand(SshConnection conn, String cmd, String source, String destination, long timeout)
      throws SshTimeoutException, IOException {
    try (SshClient client = SshClient.setUpDefaultClient()){
      // Open the client
      client.start();

      // Connect to the server
      ConnectFuture cf = client.connect(conn.getUsername(), conn.getHostname(), 22);
      ClientSession session = cf.verify().getSession();
      session.addPasswordIdentity(conn.getPassword());
      session.auth().verify(TimeUnit.SECONDS.toMillis(timeout));

      //
      if(cmd.isEmpty()){
        // prepare pathnames
        Path sourcePath = Paths.get(source);

        // upload file
        sendFile(session, source, destination + "/" + sourcePath.getFileName());
      }

      // Create the exec and channel its output/error streams
      ChannelExec ce = session.createExecChannel(cmd);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();
      ce.setOut(out);
      ce.setErr(err);

      // Execute and wait
      ce.open();
      Set<ClientChannelEvent> events = ce.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(timeout));
      session.close(false);

      // Check if timed out
      if (events.contains(ClientChannelEvent.TIMEOUT)) {
        throw new SshTimeoutException(conn.getHostname(), timeout);
      }

      // Respond
      return new SshResponse(out.toString(), err.toString(), ce.getExitStatus());
    }
  }

  private static void sendFile(ClientSession session, String source, String destination)
      throws SshTimeoutException, IOException {
    // Create SftpClient
    SftpClientFactory factory = SftpClientFactory.instance();
    try (SftpClient sftpClient = factory.createSftpClient(session)){
      // destinaiton path control can be add
      //sftpClient.mkdir(destination);

      // write file
      try (OutputStream outputStream = sftpClient.write(destination, EnumSet.of(SftpClient.OpenMode.Write, SftpClient.OpenMode.Create))) { 
        File initialFile = new File(source);
        byte[] fileContent = Files.readAllBytes(initialFile.toPath());
        outputStream.write(fileContent);
      }
    }
  }
}
