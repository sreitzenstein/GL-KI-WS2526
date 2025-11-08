# Übungsblatt: Entscheidungsbäume (Decision Tree Learner DTL)

## DTL.01: Entscheidungsbäume mit CAL3 und ID3 (6P)

| Nr. | Alter      | Einkommen | Bildung  | Kandidat |
|:----|:-----------|:----------|:---------|:---------|
| 1   | $`\ge 35`$ | hoch      | Abitur   | O        |
| 2   | $`< 35`$   | niedrig   | Master   | O        |
| 3   | $`\ge 35`$ | hoch      | Bachelor | M        |
| 4   | $`\ge 35`$ | niedrig   | Abitur   | M        |
| 5   | $`\ge 35`$ | hoch      | Master   | O        |
| 6   | $`< 35`$   | hoch      | Bachelor | O        |
| 7   | $`< 35`$   | niedrig   | Abitur   | M        |

[CAL3](cal3.pdf), Schwellen $`S_1=4`$ und $`S_2=0.7`$.

[ID3](id3.pdf)



## DTL.02: Pruning (1P)


``` math
x_3(x_2(x_1(C,A), x_1(B,A)), x_1(x_2(C,B), A))
```

allg. Transformationsregel (für unser problem mit $x_1$ und $x_2$ vertauscht, für verständnis)
 $x_2(x_1(a,b), x_1(c,d))  \Leftrightarrow x_1(x_2(a,c), x_2(b, d))$

 angewendet auf 

```math
 x_2(x_1(C, A), x_1(B, A)) 
 ```
```math

  \Leftrightarrow x_1(x_2(C, B), x_2(A, A)) 
```

$x_2(A, A)$ ist bedingt irrelevant, wird zu nur A

```math
\Leftrightarrow x_1(x_2(C, B), A)
```
---
``` math
x_3(x_1(x_2(C, B), A)), x_1(x_2(C,B), A))
```
Jetzt geht $x_3$ in allen Fällen auf $x_1$, bzw sogar beide male auf $x_1(x_2(C, B), A))$, also grob gesagt haben wir grade $x_3(x_1(...), x_1(...))$ , was wieder bedingt irrelevant ist

```math
\Leftrightarrow 
(x_1(x_2(C, B), A))
```


## DTL.03: Machine Learning mit Weka (3P)

Weka mit Package simpleEducationalLearningSchemes 1.0.1

links: Explorer um zu Preprocess zu kommen

in zoo und restaurant eine kopfzeile ergänzt



### Training mit J48 (1P)

zoo.csv
```
=== Classifier model (full training set) ===

J48 pruned tree
------------------

feathers <= 0
|   milk <= 0
|   |   backbone <= 0
|   |   |   airbone <= 0
|   |   |   |   predator <= 0
|   |   |   |   |   legs <= 2: shellfish (2.0)
|   |   |   |   |   legs > 2: insect (2.0)
|   |   |   |   predator > 0: shellfish (8.0)
|   |   |   airbone > 0: insect (6.0)
|   |   backbone > 0
|   |   |   fins <= 0
|   |   |   |   tail <= 0: amphibian (3.0)
|   |   |   |   tail > 0: reptile (6.0/1.0)
|   |   |   fins > 0: fish (13.0)
|   milk > 0: mammal (41.0)
feathers > 0: bird (20.0)

Number of Leaves  : 	9

Size of the tree : 	17


=== Confusion Matrix ===

  a  b  c  d  e  f  g   <-- classified as
 41  0  0  0  0  0  0 |  a = mammal
  0 13  0  0  0  0  0 |  b = fish
  0  0 20  0  0  0  0 |  c = bird
  0  0  0 10  0  0  0 |  d = shellfish
  0  0  0  0  8  0  0 |  e = insect
  0  0  0  0  0  3  1 |  f = amphibian
  0  0  0  0  0  0  5 |  g = reptile

  
=== Summary ===


Correctly Classified Instances         100               99.0099 %
Incorrectly Classified Instances         1                0.9901 %

```

In der confusion Matrix erkennt man,  dass eine amphibie falsch erkannt wurde, ansonsten jede klasse korrekt zugewiesen wurde. Der Baum funktioniert also nahezu perfekt

---
restaurant.csv
```
=== Classifier model (full training set) ===

J48 pruned tree
------------------

restaurantFull =  Some:    Yes (3.0)
restaurantFull =  Full:   Yes (4.0/2.0)
restaurantFull =   Some:    Yes (1.0)
restaurantFull =   Full:     No  (2.0)
restaurantFull =   None:    No (2.0/1.0)

Number of Leaves  : 	5

Size of the tree : 	6


Time taken to build model: 0 seconds


=== Confusion Matrix ===

 a b c d e f g   <-- classified as
 4 0 0 0 0 0 0 | a =    Yes
 0 0 1 0 0 0 0 | b =   No
 0 0 2 0 0 0 0 | c =   Yes
 0 0 0 2 0 0 0 | d =     No 
 0 0 0 0 1 0 0 | e =    No
 0 0 1 0 0 0 0 | f =   No 
 0 0 0 0 1 0 0 | g =    No 

 
=== Summary ===

Correctly Classified Instances           9               75      %
Incorrectly Classified Instances         3               25      %

```

es hat keinen Baum generiert -> es scheint nur "restaurantFull" relevant zu sein
Wenn ein paar gäste da sind, scheint sich das warten zu lohnen. wenn es voll ist, kann es sich lohnen, kann sich aber auch nicht lohnen. ein leeres restaurant scheint sich nicht zu lohnen

die confusion matrix confused mich allerdings

### ARFF-Format (1P)

norminal 
- Attribut hat diskret/abzählbare Werte ohne Reihenfolge
- bsp {yes, no}, {rot, gruen, blau}

numeric / ordinal
- Attribut hat geordete Werte oder Zahlen, mit/auf denen gerechnet werden kann
- bsp {0,1,2} oder 0-100 

string
- freitext, keine feste wertemenge

Konvertierung per hand, da die Links für die Konverter alle ungültig sind.. aber ist ja im endeffekt nur eine typ und werte zuweisung

### Training mit ID3 und J48 (1P)

Zoo lässt es mich nicht mit arff trainieren, also weder id3 noch j48

restaurant, j48: 
```
J48 pruned tree
------------------

restaurantFull = Some: Yes (4.0)
restaurantFull = None: No (2.0)
restaurantFull = Full: No (6.0/2.0)

Number of Leaves  : 	3

Size of the tree : 	4

=== Summary ===

Correctly Classified Instances           6               50      %
Incorrectly Classified Instances         6               50      %
Kappa statistic                          0     
Mean absolute error                      0.4583
Root mean squared error                  0.5957
Relative absolute error                 87.1951 %
Root relative squared error            113.2687 %
Total Number of Instances               12     

=== Detailed Accuracy By Class ===

                 TP Rate  FP Rate  Precision  Recall   F-Measure  MCC      ROC Area  PRC Area  Class
                 0,500    0,500    0,500      0,500    0,500      0,000    0,597     0,702     Yes
                 0,500    0,500    0,500      0,500    0,500      0,000    0,597     0,593     No
Weighted Avg.    0,500    0,500    0,500      0,500    0,500      0,000    0,597     0,648     

=== Confusion Matrix ===

 a b   <-- classified as
 3 3 | a = Yes
 3 3 | b = No


```

restaurant, id3:
```
=== Classifier model (full training set) ===

Id3


restaurantFull = Some: Yes
restaurantFull = None: No
restaurantFull = Full
|  type = French: No
|  type = Thai
|  |  fridayOrSaturday = Yes: Yes
|  |  fridayOrSaturday = No: No
|  type = Italian: No
|  type = Burger
|  |  alternateRestaurantAvailable = Yes: Yes
|  |  alternateRestaurantAvailable = No: No

=== Summary ===

Correctly Classified Instances           6               50      %
Incorrectly Classified Instances         5               41.6667 %
Kappa statistic                          0.0678
Mean absolute error                      0.4545
Root mean squared error                  0.6742
Relative absolute error                 94.5455 %
Root relative squared error            134.18   %
UnClassified Instances                   1                8.3333 %
Total Number of Instances               12     

=== Detailed Accuracy By Class ===

                 TP Rate  FP Rate  Precision  Recall   F-Measure  MCC      ROC Area  PRC Area  Class
                 0,667    0,600    0,571      0,667    0,615      0,069    0,583     0,548     Yes
                 0,400    0,333    0,500      0,400    0,444      0,069    0,500     0,500     No
Weighted Avg.    0,545    0,479    0,539      0,545    0,538      0,069    0,545     0,526     

=== Confusion Matrix ===

 a b   <-- classified as
 4 2 | a = Yes
 3 2 | b = No
```

j48 hat viel kleineren baum (direkt pruning angewendet), betrachtet nur restaurantFull

id3 nutzt mehr attribute, hat unklassifizierten datensatz

beide nur 50% korrekt
