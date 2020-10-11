import { PrivatteilnahmeAdminOverview } from './teilnahmen.model';

export type VeranstalterSuchkriterium = 'EMAIL' | 'NAME' | 'TEILNAHMENUMMER' | 'UUID';
export type VeranstalterRolle = 'LEHRER' | 'PRIVAT';
export type ZugangUnterlagen = 'DEFAULT' | 'ERTEILT' | 'ENTZOGEN';

export interface VeranstalterSuchanfrage {
	suchkriterium: VeranstalterSuchkriterium;
	suchstring: string;
};

export interface Veranstalter {
	readonly uuid: string;
	readonly fullName: string;
	readonly email: string;
	readonly newsletterAbonniert: boolean;
	readonly rolle: VeranstalterRolle;
	readonly zugangsstatusUnterlagen: ZugangUnterlagen;
	readonly teilnahmenummern: string[];
	readonly privatOverview?: PrivatteilnahmeAdminOverview;
};

export interface VeranstalterWithID {
	readonly uuid: string;
	readonly veranstalter: Veranstalter;

};

export class VeranstalterMap {


	private veranstalter: Map<string, Veranstalter> = new Map();

	constructor(readonly items: VeranstalterWithID[]) {

		if (items !== undefined) {
			for (const v of items) {
				this.veranstalter.set(v.uuid, v.veranstalter);
			}
		}
	}

	public has(uuid: string): boolean {

		if (uuid === undefined) {
			return false;
		}
		return this.veranstalter.has(uuid);

	}

	public get(uuid: string): Veranstalter {

		if (uuid === undefined) {
			return null;
		}
		return this.veranstalter.get(uuid);

	}

	public toArray(): Veranstalter[] {

		return [...this.veranstalter.values()];

	}

	public add(veranstalters: Veranstalter[]): VeranstalterWithID[] {

		const result: VeranstalterWithID[] = [...this.items];

		for (const veranstalter of veranstalters) {
			result.push({uuid: veranstalter.uuid, veranstalter: veranstalter});
		}

		return result;
	}

	public merge(veranstalter: Veranstalter[]): VeranstalterWithID[] {

		const result: VeranstalterWithID[] = [];

		for (const theVeranstalter of veranstalter) {
			for (const item of this.items) {
				if (item.uuid !== theVeranstalter.uuid) {
					result.push(item);
				} else {
					result.push({ uuid: theVeranstalter.uuid, veranstalter: theVeranstalter });
				}
			}
		}
		return result;
	}

}


