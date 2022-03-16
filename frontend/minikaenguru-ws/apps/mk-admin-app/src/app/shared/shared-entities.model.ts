export type MUSTRETEXT_KATEGORIE = 'MAIL' | 'NEWSLETTER' | 'UNDEFINED';

export interface InvalidProperty {
	readonly sortnumber: number;
	readonly name: string;
	readonly message: string;
};

export interface SchulkatalogData {
	readonly kuerzel: string;
	readonly name: string;
	readonly ort: string;
	readonly land: string;
	readonly kuerzelLand: string;
};

export interface Mustertext {
	readonly uuid: string;
	readonly kategorie: MUSTRETEXT_KATEGORIE;
	readonly name: string;
	readonly text?: string;
};

export const NEUER_MUSTERTEXT: Mustertext = {
	uuid: 'neu',
	kategorie: 'UNDEFINED',
	name: '',
	text: ''
};

