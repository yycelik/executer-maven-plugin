package com.celik.model;

public final class SshTimeoutException extends Exception {

    private static final long serialVersionUID = 1747556318497978741L;
  
    public SshTimeoutException(String cmd, String host, long timeout) {
      super("Command '" + cmd + "' on host '" + host + "' timed out after " + timeout + " seconds");
    }
  
  }
