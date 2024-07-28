package com.thoughtworks.gha.notifier.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Repository {

  private String path;
  private List<Workflow> workflows = new ArrayList<>();

  public String toString(){
    return path;
  }
}
