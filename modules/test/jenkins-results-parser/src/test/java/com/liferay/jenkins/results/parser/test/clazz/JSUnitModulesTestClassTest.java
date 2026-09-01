/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Charlotte Wong
 */
public class JSUnitModulesTestClassTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetTestTaskName() throws Exception {
		File workspaceModuleDir = temporaryFolder.newFolder(
			"workspaces", "liferay-osbfaro-workspace", "modules",
			"osb-faro-web");

		Assert.assertEquals(
			":modules:osb-faro-web:packageRunTest",
			_getTestTaskName(workspaceModuleDir));

		File portalModuleDir = temporaryFolder.newFolder(
			"modules", "apps", "foo", "foo-web");

		Assert.assertEquals(
			":apps:foo:foo-web:packageRunTest",
			_getTestTaskName(portalModuleDir));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private String _getTestTaskName(File moduleDir) {
		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doReturn(
			temporaryFolder.getRoot()
		).when(
			portalGitWorkingDirectory
		).getWorkingDirectory();

		Mockito.doCallRealMethod(
		).when(
			portalGitWorkingDirectory
		).getGradleBuildRootDir(
			Mockito.any(File.class)
		);

		JSUnitModulesTestClass jsUnitModulesTestClass = Mockito.mock(
			JSUnitModulesTestClass.class);

		Mockito.doReturn(
			moduleDir
		).when(
			jsUnitModulesTestClass
		).getTestClassFile();

		Mockito.doReturn(
			portalGitWorkingDirectory
		).when(
			jsUnitModulesTestClass
		).getPortalGitWorkingDirectory();

		Mockito.doReturn(
			"packageRunTest"
		).when(
			jsUnitModulesTestClass
		).getTaskName();

		Mockito.doCallRealMethod(
		).when(
			jsUnitModulesTestClass
		).getModuleBaseDir();

		Mockito.doCallRealMethod(
		).when(
			jsUnitModulesTestClass
		).getPortalModulesBaseDir();

		Mockito.doCallRealMethod(
		).when(
			jsUnitModulesTestClass
		).getTestTaskName();

		return jsUnitModulesTestClass.getTestTaskName();
	}

}