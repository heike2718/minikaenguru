export interface KatalogItem {
	readonly typ: Katalogtyp;
	readonly kuerzel: string;
	readonly name: string;
	readonly parent: KatalogItem;
	readonly anzahlKinder: number;
	readonly leaf: boolean;
}


export type Katalogtyp = 'LAND' | 'ORT' | 'SCHULE';
