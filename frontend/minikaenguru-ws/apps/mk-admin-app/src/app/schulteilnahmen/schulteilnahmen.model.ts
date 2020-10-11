import { TeilnahmeIdentifier, Teilnahme } from '@minikaenguru-ws/common-components';

export interface SchulkatalogData {
	readonly kuerzel: string;
	readonly name: string;
	readonly ort: string;
	readonly land: string;
	readonly kuerzelLand: string;
};

export interface SchuleMinikaenguruData {
	readonly aktuellAngemeldet: boolean;
	readonly kollegen: string;
	readonly anzahlTeilnahmen: number;
	readonly hatAdv: boolean;
};


export interface AktuelleSchulteilnahme {
	readonly identifier: TeilnahmeIdentifier;
	readonly nameUrkunde: string;
	readonly anzahlKlassen: number;
	readonly anzahlKinder: number;
	readonly angemeldetDurch: string;
};

export interface SchuleAdminOverview {
	readonly kuerzel: string,
	readonly katalogData: SchulkatalogData;
	readonly minikaenguruData: SchuleMinikaenguruData;
	readonly aktuelleTeilnahme?: AktuelleSchulteilnahme;
	readonly schulteilnahmen: Teilnahme[];
};

export interface SchuleAdminOverviewWithID {
	readonly kuerzel: string,
	readonly schuleOverview: SchuleAdminOverview;
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

	public get(kuerzel: string): SchuleAdminOverview {

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


