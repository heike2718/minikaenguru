export interface KatalogItem {
	readonly typ: Katalogtyp;
	readonly kuerzel: string;
	readonly name: string;
	readonly pfad: string;
	readonly parent: KatalogItem;
	readonly anzahlKinder: number;
	readonly leaf: boolean;
}

export interface SchulkatalogAntrag {
	readonly email: string;
	readonly land: string;
	readonly ort: string;
	readonly plz: string;
	readonly schulname: string;
	readonly strasse: string;
	readonly hausnummer: string;
	readonly honeypot: string;
}

export function compareKatalogItemsByName(ki1: KatalogItem, ki2: KatalogItem): number {
	return ki1.name.localeCompare(ki2.name);
}

export type Katalogtyp = 'LAND' | 'ORT' | 'SCHULE';
