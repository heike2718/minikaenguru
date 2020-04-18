# authprovider Release Notes

__Release 7.0.0__:

update quarkus to 1.3.0-Final

__Release 6.2.2__:

CVE-2020-8840: upgrade to [hewi-java-commons version 1.4.4](https://github.com/heike2718/hewi-java-commons/releases/tag/1.4.4)

__Release 6.2.1__:

[ER-Diagramm von CLIENTS aktualisieren](https://github.com/heike2718/authprovider/issues/22)

__Release 6.2.0__:

[REST-API zum Löschen des Benutzerkontos](https://github.com/heike2718/authprovider/issues/13)

[URIs ohne UUIDs](https://github.com/heike2718/authprovider/issues/20)

[Info per Mail über Löschung eines Accounts](https://github.com/heike2718/authprovider/issues/21)

__Release 6.1.2__:

[DB Clients und Accesstokens zurückbauen](https://github.com/heike2718/authprovider/issues/17)

__Release 6.1.1__:

[Add XSRF protection](https://github.com/heike2718/authprovider/issues/7)

[README aktualisieren](https://github.com/heike2718/authprovider/issues/15)

__Release 6.1.0__:

[fix logger categories](https://github.com/heike2718/authprovider/issues/10)

upgrade to [hewi-java-commons version 1.4.1](https://github.com/heike2718/hewi-java-commons/releases/tag/1.4.1)

__Release 6.0.2__:

log endpoint for apps

__Release 6.0.1__:

security leak in change profile data fixed

__Release 6.0.0__:

client access tokens only serverside

extracted the api for changing profile to an own microservice profil-server

__Release 5.2.1__:

CORS: restricted the allowed origins to only one URL

__Release 5.2.0__:

newer quarkus version in order to fix several CVEs

__Release 5.1.1__:

resiliente Implementierung von authorizeClient. Back-Button führte bisher dazu, dass man in der auth-app mit 401 hängen blieb. Jetzt wird redirected, damit ein neues gültiges client access token generiert wird.

__Release 5.1.0__:

upgrade to quarkus 0.26.1

__Release 5.0.3__:

JWTRefreshService: NPE weil ContainerRequestContext null war

__Release 5.0.2__:

404 on client access token replace fixed

__Release 5.0.1__:

SessionExpiredException moved

Allowed Headers fixed

new API /authprovider/version

__Release 5.0.0__:

now runs with __quarkus__ :D

__Release 4.1.1__:

bugfixes for import users (mapped directories changed)

__Release 4.1.0__:

default role renamed to STANDARD

import api for existing users in mkv

__Release 4.0.0__:

sign up with roles

serverside logs of client errors

refresh tokens in sleep modus removed

delete clientAccessTokens on refresh

__Release 3.0.2__:

allow nultiple chlientAccessTokens on refreshing jwt because of single sign on

__Release 3.0.1__:

issue transaction required wehn refreshing jwt

__Release 3.0.0__:

Backend zum refreshen des Client-Accesstokens sowie des JWT. Beides nicht abwärtskompatibel.

__Release 2.1.0__:

Backend zum Ändern der Daten des Benutzerkontos (Name, Mail, Passwort,...) für profil-app. Exceptionhandling stabilisiert
