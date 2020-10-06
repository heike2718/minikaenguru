export interface TeilnahmeIdentifier {
	readonly jahr: number;
	readonly teilnahmenummer: string;
	readonly teilnahmeart: string;
}

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
};

export interface Teilnahme {
	readonly identifier: TeilnahmeIdentifier;
	readonly anzahlKinder: number;
};

export interface SchuleAdminOverview {
	readonly katalogData: SchulkatalogData;
	readonly minikaenguruData: SchuleMinikaenguruData;
	readonly aktuelleTeilnahme?: AktuelleSchulteilnahme;
	readonly schulteilnahmen: Teilnahme[];
};
