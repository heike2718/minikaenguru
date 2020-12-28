export type Urkundenart = 'KAENGURUSPRUNG' | 'TEILNAHME';

export type Farbschema = 'BLUE' | 'GREEN' | 'ORANGE';

export interface UIUrkundenart {
	readonly art: Urkundenart;
	readonly label: string;
};

export interface UIFarbschema {
	readonly farbe: Farbschema;
	readonly label: string;
};

export const Farbschemata: UIFarbschema[] = [
	{
		farbe: 'BLUE',
		label: 'blau'
	},
	{
		farbe: 'GREEN',
		label: 'grün'
	},
	{
		farbe: 'ORANGE',
		label: 'orange'
	}
];

export const Urkundenarten: UIUrkundenart[] = [
	{
		art: 'KAENGURUSPRUNG',
		label: 'Kängurusprung'
	},
	{
		art: 'TEILNAHME',
		label: 'Teilnahme'
	}
];

export interface UrkundenauftragEinzelkind {
	readonly urkundenart?: Urkundenart;
	readonly kindUuid: string;
	readonly dateString?: string;
	readonly farbschema?: Farbschema;
};




