import * as moment from 'moment';


export function clientAccessTokenExpired(expiresAt: number): boolean {

	if (!expiresAt) {
		return true;
	}
	const expiration = moment(expiresAt);
	// lassen 3,5 Minuten Vorsprung zum refreshen des accessTokens.
	const expired = moment().add(210, 'seconds').isAfter(expiration);
	return expired;
}

export function jwtExprired(expiresAt: number): boolean {

	if (!expiresAt) {
		return true;
	}
	// das expiresAt sind Sekunden seit 01.01.1970
	const expiration = moment(expiresAt * 1000);
	return moment().isAfter(expiration);
}

