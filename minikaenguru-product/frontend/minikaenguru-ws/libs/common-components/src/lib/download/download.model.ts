export interface DownloadData {
	blob: Blob;
	filename: string;
}

export interface DownloadCardModel {

	readonly id: string;
	readonly url: string;
	readonly dateiname: string;
	readonly mimetype: string;
	readonly cardTitle: string;
	readonly subtext: string;
};

export interface DownloadButtonModel {

	readonly id: string;
	readonly url: string;
	readonly dateiname: string;
	readonly mimetype: string;
	readonly buttonLabel: string;
	readonly tooltip: string;
	readonly class: string;
};

export const initialDownloadCardModel: DownloadCardModel = {
	cardTitle: '',
	dateiname: '',
	id: '',
	mimetype: '',
	subtext: '',
	url: ''
};

export const initialDownloadButtonModel: DownloadButtonModel = {
	buttonLabel: '',
	class: '',
	dateiname: '',
	id: '',
	mimetype: '',
	tooltip: '',
	url: ''
};

