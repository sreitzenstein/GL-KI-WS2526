# Übungsblatt: Problemlösen, Suche

## Bonus: Möglichkeiten und Grenzen sowie Auswirkungen der KI (2P)

> **_Antwort:_**  
>
>Was kann bereits gelöst werden? 
> * z.B. Lagerlogistik, verpacken von Bestellungen, packen von Bestellungen
> * Unterstützung durch KI in der Diagnose von z.B. Krebszellen
>
> Was geht noch nicht? 
> * Auf die Ergebnisse von KI vertrauen (Recherchen gerne erfunden)
> * -> Einsatz in kritischen Infrastrukturen (Verkehr, Raumfahrt, Medizin...)
> * Umwelt schonen und KI Einsatz vereinbart
>
> Auswirkungen auf die Gesellschaft durch LLMs
> * Pro:
> * * schnelle Informationsbeschaffung
> * * Unterstützung beim Verfassen von Text
> * * Lernhilfe durch z.B. Übungsaufgabenerzeungung
> * Con: 
> * * Falschinformationen 
> * * Verlust der eigenen Kreativität, Denkkraft -> Abhängigkeit
> * * Probleme zu Unterscheiden, was "echt" ist und was von KI stammt
> * * hoher Energieverbrauch

## Search.01: Problemformalisierung, Zustandsraum (3P)


> **_Antwort:_**  
>
> 1: _Formalisieren_
> 
> Zustände:
> * EL: Elben Links
> * ER: Elben Rechts
> * OL: Orks Links
> * OR: Orks Rechts
> * P: Perd (= {L,R})
> * -> Zustand ist beschrieben durch (EL, ER, OL, OR, P)
>
> Aktionen: Bewegen(E, O) mit 1<= E + O <= 2
>
> Startzustand: (3,0,3,0,L)
>
> Endzustand: (0,3,0,3,R)
>
> 2: _Problemgraph_
> 1. (3,0,3,0,L) -> Bewegen(0,2)
> 2. (3,0,1,2,R) -> Bewegen(0,1)
> 3. (3,0,2,1,L) -> Bewegen(0,2)
> 4. (3,0,0,3,R) -> Bewegen(0,1)
> 5. (3,0,1,2,L) -> Bewegen(2,0)
> 6. (1,2,1,2,R) -> Bewegen(1,1)
> 7. (2,1,2,1,L) -> Bewegen(2,0)
> 8. (0,3,2,1,R) -> Bewegen(0,1)
> 9. (0,3,3,0,L) -> Bewegen(0,2)
> 10. (0,3,1,2,R) -> Bewegen(0,1)
> 11. (0,3,2,1,L) -> Bewegen(0,2)
> 12. (0,3,0,3,R)

## Search.02: Suchverfahren (5P)

<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/MapGermanyGraph.svg/476px-MapGermanyGraph.svg.png" width="40%">

Quelle: [MapGermanyGraph.svg](https://commons.wikimedia.org/wiki/File:MapGermanyGraph.svg)
by [Regnaron](https://de.wikipedia.org/wiki/Benutzer:Regnaron) and
[Jahobr](https://commons.wikimedia.org/wiki/User:Jahobr) on Wikimedia
Commons ([Public
Domain](https://en.wikipedia.org/wiki/en:public_domain))

<picture><source media="(prefers-color-scheme: light)" srcset="images/MapGermanyGraph-Kosten_light.png"><source media="(prefers-color-scheme: dark)" srcset="images/MapGermanyGraph-Kosten_dark.png"><img src="images/MapGermanyGraph-Kosten.png" width="40%"></picture>



> **_Antwort:_**
>
>  _Weg von Würzburg nach München_
> 
> * 1) Tiefensuche - in alphabetischer Reihenfolge besuchen
>
> | Schritt | Besuchte Knoten | Stack | Was passiert|
> |-|-|-|-|
> |1|-|[Würzburg]|Beginn bei Würzburg|
>|2|{Würzburg}|[Erfurt, Frankfurt, Nürnberg]| Würzburg expandieren, nachfolger in Stack|
>|3|{Würzburg, Erfurt}|[Frankfurt, Nürnberg]| Erfurt ist Sackgasse, Frankfurt und Nürnberg noch übrig|
>|4|{Würzburg, Erfurt, Frankfurt}|[Nürnberg]|In Frankfurt, Frankfurt expandieren|
>|5|{Würzburg, Erfurt, Frankfurt}|[Kassel, Mannheim, Nürnberg]|gehe nach Kassel|
>|6|{Würzburg, Erfurt, Frankfurt, Kassel}|[Mannheim, Nürnberg]|Kassel hat München|
>|7|{Würzburg, Erfurt, Frankfurt, Kassel, München}|[Mannheim, Nürnberg, München]|von Kassel nach München|
>
> gefundener Weg: Würzburg -> Frankfurt -> Kassel -> München 
>
> * 2) Breitensuche
>
> |Schritt|Queue|Besucht|Wo sind wir grade?|
>|-|-|-|-|
>|1|[Würzburg]| - |Würzburg|
>|2|[Erfurt, Frankfurt, Nürnberg]| {Würzburg} | Würzburg|
>|3|[Frankfurt, Nürnberg]|{Würzburg, Erfurt}|Erfurt|
>|4|[Nürnberg, Kasseln, Mannheim]| {Würzburg, Erfurt, Frankfurt} |Frankfurt|
>|5|[Kassel, Mannheim]|{Würzburg, Erfurt, Frankfurt, Nürnberg} |Nürnberg|
>|6|[Mannheim, München]|{Würzburg, Erfurt, Frankfurt, Nürnberg, Kassel} |Kassel|
>|7|[München]| {Würzburg, Erfurt, Frankfurt, Nürnberg, Kassel, Mannheim}| Mannheim|
>|8|[]|{Würzburg, Erfurt, Frankfurt, Nürnberg, Kassel, Mannheim, München} | München gefunden!|
>
> gefundener Weg: Würzburg -> Nürnberg -> München
>
> * 3) A*
> Heuristiken (Ich nehme hier die addierten Entfernungen von Stadt x bis München vom Graph abgelesen)
>
>|Stadt|Entfernung bis München|
>|-|-:|
>|München|0|
>|Augsburg|84|
>|Karlsruhe|334|
>|Mannheim|414|
>|Frankfurt|487|
>|Würzburg|270|
>|Erfurt|456|
>|Stuttgart|350|
>|Nürnberg|167|
>|Kassel|502|
>
>|Knoten|$`g(n)`$|$`h(n)`$|$`f(n) = g(n) + h(n)`$|
>|-|-:|-:|-:|
>|Würzburg|0|270|270|
>|-> Nürnberg|103|167|270|
>|-> Frankurt|217|487|704|
>|-> Erfurt|186|456|642|
>
> -> Wähle Nürnberg, da kleinstes $`f(n) = g(n) + h(n)`$ (270)
>|Knoten|$`g(n)`$|$`h(n)`$|$`f(n) = g(n) + h(n)`$|
>|-|-:|-:|-:|
>|Nürnberg|0|167|167|
>|-> Würzburg|103|270|373|
>|-> Stuttgart|183|350|533|
>|-> München|167|0|167|
>
> München erreicht!
>
>gefundener Weg: Würzburg -> Nürnberg -> München
>
> 4) Vergleich
>
> |Algorithmus|max Einträge in DS|Durchläufe Hauptschleife|
> |-|-:|-:|
>|Tiefensuche|3|5|
>|Breitensuche|4|7|
>|A*|4|3|
>
>



> **_Antwort:_**
>
> _Restkostenabschätzungen zugelassen?_
>
> Dazu müsste ich die realen entfernungen googlen, um sagen zu können, ob die angegebenen Werte eine "Überschätzung" sind. Ich gehe mal davon aus, dass es Unterschätzungen sind. Somit sind sie zulässig

## Search.03: Dominanz (1P)

> **_Antwort:_**
>
> $`h_1(n)`$ dominiert $`h_2(n)`$, wenn $`h_1(n) >= h_2(n)`$ für alle _n_ gilt und die Heuristiken zulässig sind
>
> $`h_1`$ liefert immer mindestens so hohe, aber nicht überschätzende Schätzwerte wie $`h_2`$

> **_Antwort:_**
>
> _Auswirkungen dominante Heuristik auf A*
>
> $`h_1(n)`$ kommt mit der Schätzung näher an den reellen Wert dran, da sowas wie $`reeller Restwert >= h_1(n) >= h_2(n)`$ gelten muss, da die Heuristiken nicht überschätzen
>
> Dadurch wird der Suchbaum stärker eingeschränkt, die Suche wird effizienter


> **_Antwort:_**
> 
> BSP: Suche vom Weg nach München
>
> $`h_2(n)`$ = 100 für jeden Knoten (Definitiv nicht überschätzt, so gibt es allerdings keinen priorisierten Nachfolger)
> $`h_1(n)`$ exakte Luftlinienwerte -> es werden nur Würzburg und Nürnberg expandiert


## Search.04: Beweis der Optimalität von A\* (1P)

> **_Antwort:_**
>
> _Beweis A* optimal_
> 
> A* wählt immer den Knoten mit kleinstem $`f(n) = g(n) + h(n)`$. Wenn $`h`$ zulässig ist, kann ein "teurer" Pfad niemals vor einem optimalen Pfad expandiert werden, da dieser "billiger" ist. 

