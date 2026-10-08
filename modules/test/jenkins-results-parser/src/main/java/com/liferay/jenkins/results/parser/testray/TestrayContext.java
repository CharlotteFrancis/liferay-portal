/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildDatabase;

import java.util.Date;

/**
 * @author Michael Hashimoto
 */
public interface TestrayContext {

	public BuildDatabase getBuildDatabase();

	public Date getTestrayBuildDate();

	public String getTestrayBuildDescription();

	public String getTestrayBuildSHA();

	public String replace(String string);

	public String replaceSlack(String string, TestrayBuild testrayBuild);

}