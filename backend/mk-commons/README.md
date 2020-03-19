# mk-commons

shared functionality with minikaenguru

## required application properties

__block.on.missing.origin.referer:__ boolean, default false

__target.origin:__ string, mathe-jung-alt.de  wird im OriginRefererFilter verwendet

__allowedOrigin:__ string, https://mathe-jung-alt.de wird im SecureHeadersFilter ausgewertet.

Parallel: es darf keine quarkus-CORS-Konfiguration in application.properties geben, da dieser Teil sehr volatil zu sein scheint und sich mit jedem neuen Quarkus-Release ein bisschen anders verhält, so dass es zu plötzlichen CORS-issues kommt.


## Constants

are in the constants package

## Exceptions

are in the exceptions package