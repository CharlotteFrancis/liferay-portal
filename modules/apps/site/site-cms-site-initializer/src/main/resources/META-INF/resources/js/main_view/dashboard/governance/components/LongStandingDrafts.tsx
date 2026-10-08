/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import {sub} from 'frontend-js-web';
import React, {useContext} from 'react';

import InteractiveCard from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';

function PercentageOfTotal({
	label,
	percentage,
}: {
	label: string;
	percentage: number;
}) {
	return (
		<div className="mt-2">
			<Text color="secondary" size={3}>
				{label}
			</Text>

			<div aria-hidden="true" className="mt-2 progress">
				<div
					className="progress-bar"
					style={{width: `${percentage}%`}}
				/>
			</div>
		</div>
	);
}

export function LongStandingDraftsCard() {
	const {loadingStatistics, statistics} = useContext(GovernanceContext);

	const count = statistics?.longStandingDraftsCount ?? 0;
	const total = statistics?.totalCount ?? 0;

	const percentage = total ? (count / total) * 100 : 0;

	const languageId = Liferay.ThemeDisplay.getBCP47LanguageId();

	const percentageLabel = sub(
		Liferay.Language.get('x-of-x-assets'),
		new Intl.NumberFormat(languageId, {
			maximumFractionDigits: 1,
			style: 'percent',
		}).format(percentage / 100),
		new Intl.NumberFormat(languageId).format(total)
	);

	return (
		<InteractiveCard
			color="purple"
			description={Liferay.Language.get(
				'content-that-has-remained-in-draft-status-for-longer-than-expected'
			)}
			icon="pencil"
			loading={loadingStatistics}
			title={Liferay.Language.get('long-standing-drafts')}
			value={count}
		>
			<PercentageOfTotal
				label={percentageLabel}
				percentage={percentage}
			/>
		</InteractiveCard>
	);
}
