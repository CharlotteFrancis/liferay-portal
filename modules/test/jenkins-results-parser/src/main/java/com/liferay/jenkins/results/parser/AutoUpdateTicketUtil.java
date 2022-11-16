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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Charlotte Wong
 */

public class AutoUpdateTicketUtil {
    public static String generateComment(){
        StringBuilder sb = new StringBuilder();

        sb.append("The following tickets were automatically submitted for review: \n");

        for (String ticket:_ticketList) {
            sb.append(ticket);

            sb.append(": https://issues.liferay.com/browse/");

            sb.append(ticket);

            sb.append("\n");
        }

        sb.append("\n");

        sb.append("Pull request: ");

        _ownerUsername = _pullRequest.getOwnerUsername();

        _gitHubRemoteGitRepositoryName = _pullRequest.getGitHubRemoteGitRepositoryName();

        _number = _pullRequest.getNumber();

        sb.append(_pullRequest.getURL(_ownerUsername, _gitHubRemoteGitRepositoryName, _number));

        return sb.toString();
    }

    public static void parseCommits(){
        for (int i = 0; i < _commitList.size(); i++) {

            _message = _commitList.get(i).getMessage();

            Matcher matcher = _pattern.matcher(_message);

            if (matcher.find()) {
                String group = matcher.group(0);

                if (!_ticketList.contains(group)) {
                    _ticketList.add(group);
                }
            }
        }
    }

    public static void updateTicket(String pullRequestURL) {
        _pullRequest = new PullRequest(pullRequestURL);

        _commitList = _pullRequest.getGitHubRemoteCommits();

        _ticketList = new ArrayList<>();

        parseCommits();

        if (_ticketList.isEmpty()) {
            throw new IllegalStateException("Unable to find any valid tickets within pull request.");
        }

        _comment = generateComment();

        _pullRequest.addComment(_comment);

        for (String ticket: _ticketList) {
            JiraTicket jiraTicket = new JiraTicket(ticket);
            jiraTicket.submitForReview(_comment);
        }
    }

    private static String _comment;

    private static List<GitHubRemoteGitCommit> _commitList;

    private static String _gitHubRemoteGitRepositoryName;

    private static String _message;

    private static String _number;

    private static String _ownerUsername;

    private static final Pattern _pattern = Pattern.compile(
            "^([A-Z]+[-][\\d]+)");

    private static PullRequest _pullRequest;

    private static List<String> _ticketList;



}
