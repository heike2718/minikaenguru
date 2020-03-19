
export interface KatalogItem {
	level: number;
	kuerzel: string;
	name: string;
	kinder?: KatalogItem[];
}

export interface SchuleLage {
	landKuerzel: string;
	landName: string;
	ortKuerzel: string;
	ortName: string;
}

export interface Schule {
	kuerzel: string;
	name: string;
	lage: SchuleLage;
}
