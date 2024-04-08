# Dealer app
Si scriva un programma in Java utilizzando Spring/Springboot e tutte le librerie accessorie per la gestione di un autosalone.

L’autosalone avrà degli utenti semplici (acquirenti) che possono effettuare l’ordine o l’acquisto di un modello in pronta consegna. O il noleggio di un veicolo.
Avrà degli utenti di amministrazione (admin) che potranno aggiungere veicoli in pronta consegna, usati, ordinabili, noleggiabili e potranno gestire le vendite i noleggi e gli ordini.
Avrà poi dei venditori che gestiranno le vendite e i noleggi.

Per ogni acquirente avremo una serie di attributi:
- Nome
- Cognome
- Telefono
- email
- password

Per ogni amministratore avremo una serie di attributi:
- Nome
- Cognome
- email
- password

Per ogni venditore avremo una serie di attributi:
- Nome
- Cognome
- Telefono
- email
- password

I veicoli possono essere di diversi tipi: auto, moto, scooter, furgoni.
Per ogni veicolo avremo una serie di attributi:
- Marca
- Modello
- Cilindrata
- Colore
- Potenza
- Tipo Cambio
- Anno immatricolazione
- Alimentazione
- Prezzo
- Eventuale sconto sul prezzo di listino
- Eventuali accessori a corredo
- Flag che identifichi se il veicolo è nuovo o usato
- Flag che identifichi se il veicolo è ordinabile, acquistabile o non più disponibile

Per ogni ordine/acquisto avremo:
- Anticipo
- Flag pagato
- Stato ordine
- Veicolo ordinato/acquistato

Per un noleggio avremo:
- Data inizio noleggio
- Data fine noleggio
- Costo giornaliero noleggio
- Costo totale noleggio
- Flag pagato
- Veicolo noleggiato

Un cliente potrà:
- Creare un ordine a partire da un veicolo contrassegnato come ordinabile
- Vedere i propri ordini
- Cancellare un ordine
- Creare un acquisto a partire da un veicolo contrassegnato come acquistabile
- Vedere i propri acquisti
- Creare un noleggio
- Vedere i propri noleggi
- Cancellare un noleggio
- Cancellare la propria utenza
- Modificare i dati dell’utente
- Ricercare un veicolo secondo diversi criteri (prezzo, colore, marca, modello, ecc)
- Ottenere i dettagli di un veicolo specifico

Un admin potrà:
- Aggiungere un veicolo
- Modificare un veicolo
- Cancellare un veicolo
- Cambiare lo stato di un veicolo
- Creare un ordine per un utente
- Cancellare un ordine per un utente
- Modificare un ordine per un utente
- Creare un noleggio per un utente
- Cancellare un noleggio per un utente
- Modificare un noleggio per un utente
- Creare un acquisto per un utente
- Cancellare un acquisto per un utente
- Modificare un acquisto per un utente
- Verificare un venditore quante vendite ha fatto in un determinato periodo di tempo
- Verificare un venditore quanti soldi ha generato in un determinato periodo di tempo
- Verificare il guadagno del salone in un determinato periodo
- Verificare i veicoli attualmente ordinabili/acquistabili/non disponibili/nuovi/usati
- Cancellare un utente
- Modificare un utente
- Cancellare un venditore
- Modificare un venditore
- Ottenere il veicolo più venduto in un dato periodo
- Ottenere il veicolo con valore più alto venduto fino a quel dato istante
- Ottenere il veicolo più rircercato/ordinato

Un venditore potrà:
- Ottenere i dettagli di un veicolo specifico
- Creare un ordine a partire da un veicolo ordinabile
- Cancellare un ordine creato
- Modificare un ordine creato
- Verificare lo stato di un ordine specifico
- Aggiornare lo stato di un ordine specifico
- Verificare tutti gli ordini filtrati per uno stato
- Creare un noleggio
- Cancellare un noleggio
- Modificare un noleggio

Il sistema dovrà inoltre permettere il login e la registrazione degli utenti attraverso due rotte specifiche.

