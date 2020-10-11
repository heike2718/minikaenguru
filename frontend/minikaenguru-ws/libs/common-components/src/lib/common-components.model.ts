export interface TeilnahmeIdentifier {
	readonly jahr: number;
	readonly teilnahmenummer: string;
	readonly teilnahmeart: string;
};

export interface Teilnahme {
	readonly identifier: TeilnahmeIdentifier;
	readonly anzahlKinder: number;
};


