# Logica di Sensor Fusion: Navette V-Shuttle

## 1. Spiegazione della Logica dell'Algoritmo
L'algoritmo utilizza un modello di media ponderata dinamica per fondere le confidenze restituite dai tre sensori di bordo. A ogni sensore è assegnato un "peso" fisso che ne modella l'affidabilità hardware intrinseca: **0.8** per la telecamera frontale, **0.5** per quella laterale e **1.0** per il ricevitore V2I. 

La chiave per mantenere l'algoritmo deterministico e resistente ai crash risiede nel processo di **normalizzazione dinamica**. Il sistema valuta istantaneamente quali sensori hanno fornito una lettura valida (non `null`). Il denominatore della formula non è una costante fissa, ma è calcolato dinamicamente sommando esclusivamente i pesi dei sensori effettivamente attivi in quello specifico scenario. 

Se il ricevitore V2I risulta offline, il suo peso (1.0) viene escluso sia dal calcolo della somma pesata (numeratore) sia dalla somma dei pesi (denominatore). Questo riproporziona matematicamente il peso delle due telecamere rimanenti, assicurando che la confidenza finale rimanga coerente e compresa tra 0 e 1.

## 2. Implementazione in Pseudocodice (Python)

```python
# Definizione dei pesi intrinseci dei sensori
PESI_SENSORI = {
    "camera_frontale": 0.8,
    "camera_laterale": 0.5,
    "V2I_receiver": 1.0
}

def calcola_confidenza_totale(sensori_scenario):
    somma_pesata = 0.0
    somma_pesi = 0.0
    
    # Itera attraverso tutti i sensori previsti
    for nome_sensore, peso in PESI_SENSORI.items():
        # Estrae i dati del sensore corrente dal JSON
        dati_sensore = sensori_scenario.get(nome_sensore)
        
        # Verifica che il sensore esista e che la confidenza non sia null
        if dati_sensore is not None and dati_sensore.get("confidenza") is not None:
            confidenza = dati_sensore.get("confidenza")
            
            somma_pesata += confidenza * peso
            somma_pesi += peso
            
    # Gestione del caso limite: tutti i sensori sono null/offline
    if somma_pesi == 0.0:
        return 0.0 
        
    # Calcolo della confidenza fusa normalizzata
    confidenza_totale = somma_pesata / somma_pesi
    
    return round(confidenza_totale, 4)
```

## 3. Formula Matematica

Definiamo $S$ come l'insieme di tutti i sensori disponibili a bordo e $A \subseteq S$ come il sottoinsieme dei sensori che restituiscono un valore di confidenza valido (non nullo) per un dato scenario $t$.

Sia $w_i$ il peso di affidabilità intrinseca del sensore $i$ e $c_i$ la confidenza restituita dal sensore $i$. La confidenza totale $C_{tot}$ è espressa come:

$$C_{tot} = \frac{\sum_{i \in A} w_i \cdot c_i}{\sum_{i \in A} w_i}$$

Dove i pesi di base sono definiti come:
* $w_{frontale} = 0.8$
* $w_{laterale} = 0.5$
* $w_{V2I} = 1.0$