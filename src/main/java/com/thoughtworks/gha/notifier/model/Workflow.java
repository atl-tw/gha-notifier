package com.thoughtworks.gha.notifier.model;

import lombok.Data;

@Data
public class Workflow {
  private String id;
  private String name;
  private String path;
  private String state;
  private String mainBranch = "main";

  public String toString() {
    return this.name + " (" + this.path + ")";
  }
}
