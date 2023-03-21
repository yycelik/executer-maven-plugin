package com.celik.model;

public final class SshTimeoutException extends Exception {

    private static final long serialVersionUID = 1747556318497978741L;
  
    public SshTimeoutException(String host, long timeout) {
      super("host '" + host + "' timed out after " + timeout + " seconds");
    }
  
  }
