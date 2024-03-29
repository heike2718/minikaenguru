import { Teilnahme } from '@minikaenguru-ws/common-components';
import { SchulkatalogData } from '../shared/shared-entities.model';



export interface SchuleMinikaenguruData {
	readonly aktuellAngemeldet: boolean;
	readonly kollegen: string;
	readonly anzahlTeilnahmen: number;
	readonly hatAdv: boolean;
};

export interface SchuleAdminOverview {
	readonly kuerzel: string,
	readonly katalogData: SchulkatalogData;
	readonly minikaenguruData: SchuleMinikaenguruData;
	readonly angemeldetDurch?: string;
	readonly nameUrkunde?: string;
	readonly anzahlUploadKlassenlisten: number;
	readonly schulteilnahmen: Teilnahme[];
};

export interface SchuleAdminOverviewWithID {
	readonly kuerzel: string,
	readonly schuleOverview: SchuleAdminOverview;
};

export interface SchuleUploadModel {
	readonly kuerzel: string;
	readonly katalogData: SchulkatalogData;
	readonly jahr: number;
	readonly anzahlKinder: number;
};

export interface AuswertungImportReport {
	readonly teilnahme?: Teilnahme;
	readonly fehlerhafteZeilen: string[];
};

export class SchulenOverviewMap {

	private schulen: Map<string, SchuleAdminOverview> = new Map();

	constructor(readonly items: SchuleAdminOverviewWithID[]) {

		if (items !== undefined) {
			for (const s of items) {
				this.schulen.set(s.kuerzel, s.schuleOverview);
			}
		}
	}

	public has(kuerzel: string): boolean {

		if (kuerzel === undefined) {
			return false;
		}
		return this.schulen.has(kuerzel);
	}

	public get(kuerzel: string): SchuleAdminOverview | undefined {

		if (kuerzel === undefined) {
			return undefined;
		}
		return this.schulen.get(kuerzel);
	}

	public toArray(): SchuleAdminOverview[] {
		return [...this.schulen.values()];
	}

	public add(schule: SchuleAdminOverview): SchuleAdminOverviewWithID[] {

		const result: SchuleAdminOverviewWithID[] =  [...this.items];

		result.push({kuerzel: schule.kuerzel, schuleOverview: schule});

		return result;
	}

};

export function replaceTeilnahme(teilnahme?: Teilnahme, teilnahmen?: Teilnahme[]): Teilnahme[] {

	if (!teilnahme || !teilnahmen) {
		return [];
	}

	const result: Teilnahme[] = [];

	teilnahmen.forEach(t => {
		if (t.identifier.jahr !== teilnahme.identifier.jahr) {
			result.push(t);
		} else {
			result.push(teilnahme);
		}
	});

	return result;
}

