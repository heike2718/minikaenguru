export interface KatalogItem {
	readonly typ: Katalogtyp;
	readonly kuerzel: string;
	readonly name: string;
	readonly kinder?: KatalogItem[];
}


export type Katalogtyp = 'LAND' | 'ORT' | 'SCHULE';
