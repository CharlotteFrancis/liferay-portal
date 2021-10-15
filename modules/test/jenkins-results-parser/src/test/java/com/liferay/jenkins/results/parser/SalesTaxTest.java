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

import java.io.IOException;
import java.io.InputStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

/**
 * @author Charlotte Wong
 */
public class SalesTaxTest {

	@Test
	public void testSalesTax() throws Exception {
		for (int i = 1; i < 4; i++) {
			shoppingCart = new TaxCalculator.ShoppingCart(
				JenkinsResultsParserUtil.combine(
					"input", String.valueOf(i), ".txt"));
			expectedOutput = _readDependencyFile(
				JenkinsResultsParserUtil.combine(
					"output", String.valueOf(i), ".txt"));

			if (!expectedOutput.equals(shoppingCart.getReceipt())) {
				errorCollector.addError(
					new Throwable(
						JenkinsResultsParserUtil.combine(
							"Test result does not match expected result.\n",
							"Expected:\n", expectedOutput, "\nActual:\n",
							shoppingCart.getReceipt())));
			}
		}
	}

	@Rule
	public ErrorCollector errorCollector = new ErrorCollector();

	protected String expectedOutput;
	protected TaxCalculator.ShoppingCart shoppingCart;

	private String _readDependencyFile(String dependencyFilename) {
		Class<?> clazz = SalesTaxTest.class;

		try (InputStream inputStream = clazz.getResourceAsStream(
				"/dependencies/SalesTaxTest/" + dependencyFilename)) {

			return JenkinsResultsParserUtil.readInputStream(inputStream);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read dependency file " + dependencyFilename,
				ioException);
		}
	}

}