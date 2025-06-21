import { ZugangUnterlagen } from '../veranstalter.model';

export interface ZugangsstatusButtonModel {
	readonly label: string;
	readonly tooltip: string;
	readonly neuerStatus: ZugangUnterlagen;
};

export interface ZugangsstatusButtons {
	readonly buttons: ZugangsstatusButtonModel[];
};


export function createZugangUnterlagenButtons(aktuellerZugangsstatus: ZugangUnterlagen): ZugangsstatusButtons {

	const buttons: ZugangsstatusButtonModel[] = [];

	switch (aktuellerZugangsstatus) {
		case 'DEFAULT':
			{
				buttons.push({
					label: 'erteilen',
					tooltip: 'Zugang zu Unterlagen erteilen',
					neuerStatus: 'ERTEILT'
				});
				buttons.push({
					label: 'entziehen',
					tooltip: 'Zugang zu Unterlagen entziehen',
					neuerStatus: 'ENTZOGEN'
				});
			}
			break;
		case 'ENTZOGEN':
			{
				buttons.push({
					label: 'zurücksetzen',
					tooltip: 'Zugang zu Unterlagen zurücksetzen',
					neuerStatus: 'DEFAULT'
				});
				buttons.push({
					label: 'erteilt',
					tooltip: 'Zugang zu Unterlagen erteilen',
					neuerStatus: 'ERTEILT'
				});
			}
			break;
		case 'ERTEILT':
			{
				buttons.push({
					label: 'zurücksetzen',
					tooltip: 'Zugang zu Unterlagen zurücksetzen',
					neuerStatus: 'DEFAULT'
				});
				buttons.push({
					label: 'entziehen',
					tooltip: 'Zugang zu Unterlagen entziehen',
					neuerStatus: 'ENTZOGEN'
				});
			}
			break;

	}

	return {
		buttons: buttons
	};

};

