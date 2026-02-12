/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

/**
 * @author Charlotte Wong
 */
public class SanitizeLanguageTopLevelBuild
	extends DefaultTopLevelBuild implements PortalWorkspaceBuild {

	public SanitizeLanguageTopLevelBuild(
		String buildURL, TopLevelBuild topLevelBuild) {

		super(buildURL, topLevelBuild);

		StringBuilder sb = new StringBuilder();

		sb.append("https://github.com/");
		sb.append(getParameterValue("GITHUB_RECIEVER_USERNAME"));
		sb.append("/liferay-portal");

		sb.append("/pull/");
		sb.append(getParameterValue("GITHUB_PULL_REQUEST_NUMBER"));

		_pullRequest = PullRequestFactory.newPullRequest(sb.toString());
	}

	@Override
	public String getBaseGitRepositoryName() {
		return "liferay-portal";
	}

	@Override
	public String getBranchName() {
		return getParameterValue("GITHUB_UPSTREAM_BRANCH_NAME");
	}

	@Override
	public Job.BuildProfile getBuildProfile() {
		return Job.BuildProfile.DXP;
	}

	@Override
	public PortalWorkspace getPortalWorkspace() {
		Workspace workspace = getWorkspace();

		return (PortalWorkspace)workspace;
	}

	public PullRequest getPullRequest() {
		return _pullRequest;
	}

	@Override
	public String getTestSuiteName() {
		String ciTestSuite = getParameterValue("CI_TEST_SUITE");

		if (JenkinsResultsParserUtil.isNullOrEmpty(ciTestSuite)) {
			ciTestSuite = "default";
		}

		return ciTestSuite;
	}

	@Override
	public Workspace getWorkspace() {
		System.out.println(
			"@@ cw in getWorkspace() in SanitizeLanguageTopLevelBuild");

		PullRequest pullRequest = getPullRequest();

		System.out.println("@@ cw pullRequest gotten");

		System.out.println(
			"@@ cw UPSTREAM_BRANCH_NAME: " +
				getParameterValue("UPSTREAM_BRANCH_NAME"));

		Workspace workspace = WorkspaceFactory.newWorkspace(
			"liferay-portal", getParameterValue("UPSTREAM_BRANCH_NAME"),
			"sanitize-language");

		System.out.println("@@ cw workspace created");

		if (workspace instanceof PortalWorkspace) {
			System.out.println("@@ cw workspace instanceof PortalWorkspace");

			PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

			System.out.println(
				"@@ cw in SanitizeLanguageTopLevelBuild aftr portalWorkspace");

			portalWorkspace.setBuildProfile(getBuildProfile());
		}

		System.out.println("@@ cw workspace build profile set");

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		System.out.println("@@ cw workspaceGitRepository gotten");

		workspaceGitRepository.setGitHubURL(pullRequest.getHtmlURL());

		System.out.println("@@ cw workspaceGitRepository GitHubURL set");

		String senderBranchSHA = _getSenderBranchSHA();

		System.out.println("@@ cw senderBranchSHA: " + senderBranchSHA);

		if (JenkinsResultsParserUtil.isSHA(senderBranchSHA)) {
			System.out.println("@@ cw senderBranchSHA is SHA");
			workspaceGitRepository.setSenderBranchSHA(senderBranchSHA);
		}

		String upstreamBranchSHA = _getUpstreamBranchSHA();

		System.out.println("@@ cw upstreamBranchSHA: " + upstreamBranchSHA);

		if (JenkinsResultsParserUtil.isSHA(upstreamBranchSHA)) {
			System.out.println("@@ cw upstreamBranchSHA is SHA");
			workspaceGitRepository.setBaseBranchSHA(upstreamBranchSHA);
		}

		return workspace;
	}

	private String _getSenderBranchSHA() {
		String senderBranchSHA = getParameterValue("GITHUB_SENDER_BRANCH_SHA");

		if (JenkinsResultsParserUtil.isSHA(senderBranchSHA)) {
			return senderBranchSHA;
		}

		return null;
	}

	private String _getUpstreamBranchSHA() {
		String upstreamBranchSHA = getParameterValue("UPSTREAM_BRANCH_SHA");

		if (JenkinsResultsParserUtil.isSHA(upstreamBranchSHA)) {
			return upstreamBranchSHA;
		}

		return null;
	}

	private final PullRequest _pullRequest;

}