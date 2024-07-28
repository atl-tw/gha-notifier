package com.thoughtworks.gha.notifier.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class Configuration {
    private String githubExecutable = "gh";
    private List<Repository> repositories = new ArrayList<>();
    private Map<String, State> lastStates = new HashMap<>();
    private Set<String> workflowsToNotify = new HashSet<>();

    public enum State {
        SUCCESS, FAILURE
    }
}
