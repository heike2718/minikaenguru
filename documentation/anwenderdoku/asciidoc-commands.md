# Kommandozeilenbefehle zum Generieren von html oder pdf

## html
asciidoctor -a imagesdir=images@ anleitung-fuer-lehrer.adoc 
asciidoctor -a imagesdir=images@ passwort-vergessen.adoc 

## pdf
asciidoctor-pdf anleitung-fuer-schulen.adoc
asciidoctor-pdf passwort-vergessen.adoc 