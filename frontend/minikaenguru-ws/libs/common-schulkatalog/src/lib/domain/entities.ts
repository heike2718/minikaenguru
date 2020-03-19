export interface KatalogItem {
	readonly typ: Katalogtyp;
	readonly kuerzel: string;
	readonly name: string;
	readonly parent: KatalogItem;
	readonly anzahlKinder: number;
	readonly leaf: boolean;
}

export function compareKatalogItemsByName(ki1: KatalogItem, ki2: KatalogItem): number {
	return ki1.name.localeCompare(ki2.name);
}

export type Katalogtyp = 'LAND' | 'ORT' | 'SCHULE';
