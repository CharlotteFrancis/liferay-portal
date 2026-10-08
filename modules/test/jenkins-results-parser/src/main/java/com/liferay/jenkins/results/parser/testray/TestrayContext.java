/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.PortalFixpackRelease;
import com.liferay.jenkins.results.parser.PortalHotfixRelease;
import com.liferay.jenkins.results.parser.PortalRelease;
import com.liferay.jenkins.results.parser.PullRequest;

import java.util.Date;
import java.util.List;

/**
 * @author Michael Hashimoto
 */
public interface TestrayContext {

	public BuildDatabase getBuildDatabase();

	public List<Job> getJobs();

	public PortalFixpackRelease getPortalFixpackRelease();

	public PortalHotfixRelease getPortalHotfixRelease();

	public PortalRelease getPortalRelease();

	public PullRequest getPullRequest();

	public Date getTestrayBuildDate();

	public String getTestrayBuildDescription();

	public String getTestrayBuildSHA();

	public String replace(String string);

	public String replaceSlack(String string, TestrayBuild testrayBuild);

}