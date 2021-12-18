import { SchulkatalogData } from "../shared/shared-entities.model";

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
	readonly fileName: string;
	readonly schule?: SchulkatalogData;
};

export interface UploadMonitoringInfoWithID {
	readonly uuid: string;
	readonly uploadMonitoringInfo: UploadMonitoringInfo;
};

export interface UploadsMonitoringPage {
	readonly pageNumber: number;
	readonly content: UploadMonitoringInfo[]; 
};

export class UploadsMonitoringPageMap {


	private pages: Map<number, UploadMonitoringInfo[]> = new Map();

	constructor(readonly items: UploadsMonitoringPage[]) {

		for (let item of items) {
			this.pages.set(item.pageNumber, item.content);
		}

	}

	public has(pageNumber: number): boolean {

        return this.pages.has(pageNumber);
    }

    public getContent(pageNumber: number): UploadMonitoringInfo[] {

        if (!this.has(pageNumber)) {
            return [];
        }

        return this.pages.get(pageNumber)!;
    }

    public merge(page: UploadsMonitoringPage): UploadsMonitoringPage[] {

        const result : UploadsMonitoringPage[] = [];

        if (!this.has(page.pageNumber)) {
            result.push(page);            
        }

        for (let p of this.items) {

            if (p.pageNumber !== page.pageNumber) {
                result.push(p);
            } else {
                result.push(page);
            }
        }

        return result;
    }
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

