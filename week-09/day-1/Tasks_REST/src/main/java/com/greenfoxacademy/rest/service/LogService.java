package com.greenfoxacademy.rest.service;

import com.greenfoxacademy.rest.model.Log;
import com.greenfoxacademy.rest.model.LogEntry;

public interface LogService {
  Log getLog();
  void addNewLogEntry(LogEntry newLog);
}
