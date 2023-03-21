package com.celik.model;

public class SshConnection {

    private String username;
    private String password;
    private String hostname;
    private int port;
  
    public SshConnection(String username, String password, String hostname, int port) {
      super();
      this.username = username;
      this.password = password;
      this.hostname = hostname;
      this.port = port;
    }
  
    public String getUsername() {
      return username;
    }
  
    public String getPassword() {
      return password;
    }
  
    public String getHostname() {
      return hostname;
    }

    public int getPort() {
      return port;
    }
  
  }
