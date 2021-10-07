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

package com.liferay.jenkins.results.parser.TaxCalculator;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Charlotte Wong
 */
public class Receipt {

	public static void main(String[] args) {
		Receipt testReceipt = new Receipt("input1.txt");

		System.out.println(testReceipt);

		testReceipt = new Receipt("input2.txt");

		System.out.println(testReceipt);

		testReceipt = new Receipt("input3.txt");

		System.out.println(testReceipt);
	}

	public Receipt(String fileName) {
		loadInputResourceFile(fileName);
	}

	public double calculateTax(Item item) {
		double tax = 0.0;

		if (!item.isExempt()) {
			tax += item.getPrice() * .10;
		}

		if (item.isImported()) {
			tax += item.getPrice() * .05;
		}

		tax *= 20;

		tax = Double.valueOf(Math.round(tax));

		tax /= 20;

		return tax;
	}

	public void parseInput(String input) {
		Matcher matcher = _inputPattern.matcher(input);

		if (!matcher.matches()) {
			return;
		}

		_items.add(
			new Item(
				Integer.parseInt(matcher.group("count")), matcher.group("name"),
				Double.parseDouble(matcher.group("rate"))));
	}

	@Override
	public String toString() {
		double total = 0.0;
		double totalTax = 0.0;

		StringBuilder sb = new StringBuilder();

		for (Item item : _items) {
			double itemTax = calculateTax(item);

			double itemCostWithTax = item.getPrice() + itemTax;

			sb.append(String.valueOf(item.getAmount()));
			sb.append(" ");
			sb.append(item.getName());
			sb.append(": ");
			sb.append(String.format("%.2f", itemCostWithTax));
			sb.append("\n");

			totalTax += itemTax;

			total += itemCostWithTax;
		}

		sb.append("Sales Taxes: ");
		sb.append(String.format("%.2f", totalTax));
		sb.append("\n");
		sb.append("Total: ");
		sb.append(String.format("%.2f", total));
		sb.append("\n");

		return sb.toString();
	}

	protected void loadInputResourceFile(String fileName) {
		Class<?> clazz = Receipt.class;

		String resourceFileContents;

		try (InputStream resourceInputStream = clazz.getResourceAsStream(
				JenkinsResultsParserUtil.combine(
					_URL_RESOURCE_FOLDER, "/TaxCalculatorInputs/", fileName))) {

			resourceFileContents = JenkinsResultsParserUtil.readInputStream(
				resourceInputStream);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read resource file", ioException);
		}

		for (String resourceFileLine :
				resourceFileContents.split("\\s*\n\\s*")) {

			parseInput(resourceFileLine);
		}
	}

	private static final String _URL_RESOURCE_FOLDER =
		"/com/liferay/jenkins/results/parser/dependencies";

	private static final Pattern _inputPattern = Pattern.compile(
		"(?<count>\\d+)\\s+(?<name>.*)\\s+at\\s+(?<rate>[\\d\\.]+)");

	private final List<Item> _items = new ArrayList<>();

}