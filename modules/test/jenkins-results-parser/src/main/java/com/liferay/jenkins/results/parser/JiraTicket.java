/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.jenkins.results.parser;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import io.atlassian.util.concurrent.Promise;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

/**
 * @author Charlotte Wong
 */

public class JiraTicket {
    public String getTicketURL() {
        return "https://issues.liferay.com/browse/" + _ticket;
    }

    protected JiraTicket(String ticket)
            throws IOException {
        _getProperties();

        _jiraRestClientFactory = new AsynchronousJiraRestClientFactory();

        _jiraRestClient =
                _jiraRestClientFactory.createWithBasicHttpAuthentication(
                        _URI, _jiraAdminUsername,
                        _jiraAdminPassword);

        _issueRestClient = _jiraRestClient.getIssueClient();

        _ticket = ticket;

        _issue = _getIssue();
    }

    public void submitForReview(String comment) {

        TransitionInput transitionInput = new TransitionInput(71, Comment.valueOf(comment));

        try {
            Promise<Void> transition = _issueRestClient.transition(_issue, transitionInput);

            transition.get();

        } catch (Exception exception) {
            System.out.println("jira rest client process workflow action error. cause: " + exception.getMessage());
        }
    }

    private Issue _getIssue() {

        Promise<Issue> promise = _issueRestClient.getIssue(_ticket);

        return promise.claim();
    }

    private static void _getProperties() {

        try {
            _jenkinsBuildProperties =
                    JenkinsResultsParserUtil.getBuildProperties();
        }
        catch (IOException ioException) {
            throw new RuntimeException(
                    "Unable to get build properties", ioException);
        }

        _jiraAdminPassword = _jenkinsBuildProperties.getProperty("ci.jira.admin.password");
        _jiraAdminUsername = _jenkinsBuildProperties.getProperty("ci.jira.admin.username");
    }

    private Issue _issue;

    private IssueRestClient _issueRestClient;

    private static Properties _jenkinsBuildProperties;

    private static String _jiraAdminPassword;

    private static String _jiraAdminUsername;

    private JiraRestClient _jiraRestClient;

    private JiraRestClientFactory _jiraRestClientFactory;

    private String _ticket;

    private static final URI _URI = URI.create("https://issues.liferay.com");
}