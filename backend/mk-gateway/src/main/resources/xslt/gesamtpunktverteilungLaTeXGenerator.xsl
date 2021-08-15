<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" indent="no" omit-xml-declaration="yes" />

	<xsl:template match="/">
		<xsl:call-template name="latexStart"></xsl:call-template>
		<xsl:apply-templates />
		<xsl:call-template name="tableEnd"></xsl:call-template>
		<xsl:call-template name="latexEnd"></xsl:call-template>
	</xsl:template>

	<xsl:template match="titel">
\begin{minipage}{2cm}
\input{../branding/aglogo_level_4_20mm}
\end{minipage}
\begin{minipage}[c]{10cm}
\begin{center}
\LARGE <xsl:value-of select="." />
\end{center}
\end{minipage}
\begin{minipage}{3.5cm}
\bild{3.0cm}{../resources/001/00140.eps}
\end{minipage}
\\
\rule{16cm}{0.5pt}\\
\rule[5ex]{16cm}{0.5pt}
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="." />
	</xsl:template>

		<xsl:template match="basis">
        <xsl:value-of select="." />
		\par
	</xsl:template>

	<xsl:template match="bewertung">
        <xsl:value-of select="." />
		<xsl:call-template name="prozentrangTableStart"/>
	</xsl:template>

	<xsl:template match="intervallItem">
<xsl:apply-templates />
\\ \hline
	</xsl:template>

	<xsl:template match="intervall">
		<xsl:value-of select="." />
		<xsl:call-template name="and"/>
	</xsl:template>

	<xsl:template match="anzahlImIntervall">
		<xsl:value-of select="." />
		<xsl:call-template name="and"/>
	</xsl:template>

	<xsl:template match="intervallPR">
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="teilnehmerzahl">
	<xsl:call-template name="and"/> {\bf <xsl:value-of select="." />}<xsl:call-template name="and"/>
	\\ \hline
	<xsl:call-template name="longtableEnd"/>
	</xsl:template>

	<xsl:template match="median">
Der Median (Prozentrang 50) liegt bei {\bf <xsl:value-of select="." /> Punkten} .\newpage {\large \bf Prozentränge}\par
    <xsl:call-template name="prozentrangTableStart"/>
	</xsl:template>

	<xsl:template match="rohpunktitem">
<xsl:apply-templates />
\\ \hline
	</xsl:template>

	<xsl:template match="punkte">
		<xsl:value-of select="." />
		<xsl:call-template name="and"/>
	</xsl:template>

	<xsl:template match="anzahl">
		<xsl:value-of select="." />
		<xsl:call-template name="and"/>
	</xsl:template>

	<xsl:template match="prozentrang">
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="sectionEinzelergebnisse">
	    <xsl:call-template name="longtableEnd"/> \newpage {\large \bf
        <xsl:value-of select="." /> }
		<xsl:call-template name="aufgabenergebnisseTableStart"/>
	</xsl:template>

	<xsl:template match="aufgabeErgebnis">
<xsl:apply-templates />
\\ \hline
	</xsl:template>

	<xsl:template match="nummer">
		<xsl:value-of select="." />
		<xsl:call-template name="and"/>
	</xsl:template>

	<xsl:template match="anzahlRichtigGeloest">
		<xsl:value-of select="." />
		<xsl:call-template name="and"/>
	</xsl:template>

	<xsl:template match="anteilRichtigGeloest">
		<xsl:value-of select="." />
		<xsl:call-template name="and"/>
	</xsl:template>

	<xsl:template match="anzahlFalschGeloest">
		<xsl:value-of select="." />
		<xsl:call-template name="and"/>
	</xsl:template>

	<xsl:template match="anteilFalschGeloest">
		<xsl:value-of select="." />
		<xsl:call-template name="and"/>
	</xsl:template>

	<xsl:template match="anzahlNichtGeloest">
		<xsl:value-of select="." />
		<xsl:call-template name="and"/>
	</xsl:template>

	<xsl:template match="anteilNichtGeloest">
		<xsl:value-of select="." />
	</xsl:template>


	<xsl:template name="latexStart">
\documentclass[12pt,a4paper,twoside,german]{article}
\input{../include/vorspann}
\input{../include/commands}
\pagestyle{empty}
\begin{document}
\renewcommand\arraystretch{1.3}
Heike Winkelvoß, www.mathe-jung-alt.de/minikaenguru\par\vspace{1cm}

	</xsl:template>

 	<xsl:template name="intervallTableStart">
\begin{center}
\begin{tabular}{*{2}{|r}|}
\hline
{\bf Punkte} <xsl:call-template name="and"/> {\bf Anzahl}\\\hline\hline
	</xsl:template>


 	<xsl:template name="prozentrangTableStart">
\begin{center}
\begin{longtable}{*{3}{|r}|}
\hline
{\bf Punkte} <xsl:call-template name="and"/> {\bf Anzahl} <xsl:call-template name="and"/> {\bf Prozentrang}\\\hline\hline
	</xsl:template>

 	<xsl:template name="aufgabenergebnisseTableStart">
\begin{center}
\begin{tabular}{|c||r|r||r|r||r|r|}
\hline
{\bf Aufgabe} <xsl:call-template name="and"/> {\bf richtig} <xsl:call-template name="and"/> {\bf \% richtig} <xsl:call-template name="and"/> {\bf falsch} <xsl:call-template name="and"/> {\bf \% falsch} <xsl:call-template name="and"/>  {\bf nicht gelöst} <xsl:call-template name="and"/>  {\bf \% nicht gelöst} \\\hline\hline
	</xsl:template>



	<xsl:template name="tableEnd">
\end{tabular}
\end{center}
	</xsl:template>

	<xsl:template name="longtableEnd">
\end{longtable}
\end{center}
	</xsl:template>


	<xsl:template name="latexEnd">

\end{document}
	</xsl:template>

	<xsl:template name="and"><xsl:text> <![CDATA[&]]> </xsl:text></xsl:template>


</xsl:stylesheet>