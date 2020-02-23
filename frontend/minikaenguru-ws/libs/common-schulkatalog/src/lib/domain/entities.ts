export interface InverseKatalogItem {
	readonly typ: Katalogtyp;
	readonly kuerzel: string;
	readonly name: string;
	readonly parent: InverseKatalogItem;
	readonly anzahlKinder: number;
	readonly leaf: boolean;
}


export type Katalogtyp = 'LAND' | 'ORT' | 'SCHULE';
