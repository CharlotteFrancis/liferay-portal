/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.SourceFormatterExcludes;
import com.liferay.source.formatter.util.FileUtil;
import com.liferay.source.formatter.util.GradleBuildFile;
import com.liferay.source.formatter.util.GradleDependency;
import com.liferay.source.formatter.util.SourceFormatterUtil;

import java.io.File;
import java.io.IOException;

import java.util.List;

/**
 * @author Alan Huang
 */
public class GradleMissingDependenciesForUpgradeJava11Check
	extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws IOException {

		if (!absolutePath.endsWith("/build.gradle")) {
			return content;
		}

		int x = absolutePath.lastIndexOf(CharPool.SLASH);

		List<String> javaFileNames = SourceFormatterUtil.scanForFileNames(
			absolutePath.substring(0, x + 1), new String[0],
			new String[] {"**/*.java"}, new SourceFormatterExcludes(), false);

		for (String javaFileName : javaFileNames) {
			String javaFileContent = FileUtil.read(new File(javaFileName));

			if (javaFileContent.contains("import javax.annotation.")) {
				content = _fixMissingDependencies(
					content, "compileOnly", "javax.annotation",
					"javax.annotation-api", "1.3.2");
			}

			if (javaFileContent.contains("import javax.xml.bind.annotation.")) {
				content = _fixMissingDependencies(
					content, "compileOnly", "javax.xml.bind", "jaxb-api",
					"2.3.0");
			}
		}

		return content;
	}

	private String _fixMissingDependencies(
		String content, String configuration, String group, String name,
		String version) {

		GradleBuildFile gradleBuildFile = new GradleBuildFile(content);

		for (GradleDependency gradleDependency :
				gradleBuildFile.getGradleDependencies()) {

			String gradleDependencyConfiguration =
				gradleDependency.getConfiguration();
			String gradleDependencyGroup = gradleDependency.getGroup();
			String gradleDependencyName = gradleDependency.getName();

			if (Validator.isNull(gradleDependencyConfiguration) ||
				Validator.isNull(gradleDependencyGroup) ||
				Validator.isNull(gradleDependencyName)) {

				continue;
			}

			if (gradleDependencyConfiguration.equals(configuration) &&
				gradleDependencyGroup.equals(group) &&
				gradleDependencyName.equals(name)) {

				return content;
			}
		}

		gradleBuildFile.insertGradleDependency(
			configuration, group, name, version);

		return gradleBuildFile.getSource();
	}

}