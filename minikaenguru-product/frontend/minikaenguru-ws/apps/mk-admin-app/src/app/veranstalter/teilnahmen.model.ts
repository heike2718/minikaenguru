import { TeilnahmeIdentifier, Teilnahme } from '@minikaenguru-ws/common-components';


export interface AktuellePrivatteilnahme {
	readonly identifier: TeilnahmeIdentifier;
	readonly anzahlKinder: number;
	readonly kinderGeladen?: boolean;
	readonly kinder?: []; // TODO das wird noch ein API-Model und kommt nach commons

}

export interface PrivatteilnahmeAdminOverview {
	readonly aktuellAngemeldet: boolean;
	readonly anzahlTeilnahmen: number;
	readonly aktuelleTeilnahme? : AktuellePrivatteilnahme;
	readonly privatteilnahmen: Teilnahme[];
};





