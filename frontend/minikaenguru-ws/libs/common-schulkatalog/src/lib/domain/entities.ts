export interface InverseKatalogItem {
	readonly typ: Katalogtyp;
	readonly kuerzel: string;
	readonly name: string;
	readonly parent: InverseKatalogItem;
}


export type Katalogtyp = 'LAND' | 'ORT' | 'SCHULE';
