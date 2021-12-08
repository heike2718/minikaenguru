export type DateiTyp = 'EXCEL_ALT' | 'EXCEL_NEU' | 'ODS' | 'TEXT';
export type UploadStatus = 'ABGEWIESEN' | 'HOCHGELADEN' | 'IMPORTIERT' | 'DATENFEHLER' | 'LEER' | 'EXCEPTION';
export type UploadType = 'KLASSENLISTE' | 'AUSWERTUNG';



export interface UploadMonitoringInfo {
	readonly uuid: string;
	readonly teilnahmenummer: string;
	readonly nameSchule: string;
	readonly nameLehrer: string;
	readonly emailLehrer: string;
	readonly dateiTyp: DateiTyp;
	readonly uploadStatus: UploadStatus;
	readonly uploadType: UploadType;
	readonly uploadDatum: string;
	readonly sortnumber: number;
};

export interface UploadMonitoringInfoWithID {
	readonly uuid: string;
	readonly uploadMonitoringInfo: UploadMonitoringInfo;
};


export class UploadMonitoringInfoMap {

	private uploadinfos: Map<string, UploadMonitoringInfo> = new Map();

	constructor(readonly items: UploadMonitoringInfoWithID[]) {

		if (items !== undefined) {
			for(const ui of items) {
				this.uploadinfos.set(ui.uuid, ui.uploadMonitoringInfo);
			}
		}
	}

	public findUploadInfoByTeilnahmeNummer(uploadType: UploadType, teilnahmenummer: string): UploadMonitoringInfo[] {

		const treffer: UploadMonitoringInfo[] = this.toArray().filter(info => info.uploadType == uploadType && info.teilnahmenummer === teilnahmenummer);

		if (treffer.length === 0) {
			return [];
		}

		return treffer;
	}

	public has(uuid: string): boolean {

		if (!uuid) {
			return false;
		}

		return this.uploadinfos.has(uuid);
	}

	public get(uuid: string): UploadMonitoringInfo | undefined {

		if (!this.has(uuid)) {
			return undefined;
		}

		return this.uploadinfos.get(uuid);
	}

	public toArray(): UploadMonitoringInfo[] {
		return [...this.uploadinfos.values()];
	}

	public merge(uploadInfos: UploadMonitoringInfo[]): UploadMonitoringInfoWithID[] {

		const result: UploadMonitoringInfoWithID[] = [];

		if (uploadInfos.length === 0) {			
			return this.add(uploadInfos);
		}

		const itemsSorted: UploadMonitoringInfoWithID[] = this.items.sort((info1, info2) => compareUploadMonitoringInfos(info1, info2));		

		for (let uploadInfo of uploadInfos) {

			for (let item of itemsSorted) {
				if (item.uploadMonitoringInfo.uuid !== uploadInfo.uuid) {
					result.push(item);
				} else {
					result.push({uuid: uploadInfo.uuid, uploadMonitoringInfo: uploadInfo});
				}
			}
		}
		return result;
	}

	public add(uploadInfos: UploadMonitoringInfo[]): UploadMonitoringInfoWithID[] {

		const result: UploadMonitoringInfoWithID[] = [...this.items];

		if (uploadInfos.length === 0) {
			return result;
		}

		for (const info of uploadInfos) {
			result.push({uuid: info.uuid, uploadMonitoringInfo: info});
		}

		return result;
	}
}

export function mapUploadInfosToUploadInfosWithID(items: UploadMonitoringInfo[]): UploadMonitoringInfoWithID[] {

	const result: UploadMonitoringInfoWithID[] = [];
	items.forEach(uploadInfo => result.push({uuid: uploadInfo.uuid, uploadMonitoringInfo: uploadInfo}));
     return result;
};

export function compareUploadMonitoringInfos(info1: UploadMonitoringInfoWithID, info2: UploadMonitoringInfoWithID): number {

	return info1.uploadMonitoringInfo.sortnumber - info2.uploadMonitoringInfo.sortnumber;
};

