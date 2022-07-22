# Progetto_TIW
Web App Galleria D'Immagini

---------------------------------------------------------------------------------------------------------

Versione PUREHTML

Un’applicazione web consente la gestione di una galleria d’immagini. L’applicazione supporta
registrazione e login mediante una pagina pubblica con opportune form. La registrazione
controlla la validità sintattica dell’indirizzo di email e l’uguaglianza tra i campi “password” e
“ripeti password”. La registrazione controlla l’unicità dello username. Ogni immagine è
memorizzata come file nel file system del server su cui l’applicazione è rilasciata. Inoltre nella
base di dati sono memorizzati i seguenti attributi: un titolo, una data, un testo descrittivo e il
percorso del file dell’immagine nel file system del server. Le immagini sono associate all’utente
che le carica. L’utente può creare album e associare a questi le proprie immagini. Un album
ha un titolo, il creatore e la data di creazione. Le immagini sono associate a uno o più
commenti inseriti dagli utenti (dal proprietario o da altri utenti). Un commento ha un testo e il
nome dell’utente che lo ha creato. Quando l’utente accede all’HOME PAGE, questa presenta
l’elenco degli album che ha creato e l’elenco degli album creati da altri utenti. Entrambi gli
elenchi sono ordinati per data di creazione decrescente. Quando l’utente clicca su un album
che appare negli elenchi della HOME PAGE, appare la pagina ALBUM PAGE che contiene
inizialmente una tabella di una riga e cinque colonne. Ogni cella contiene una miniatura
(thumbnail) e il titolo dell’immagine. Le miniature sono ordinate da sinistra a destra per data
decrescente. Se l’album contiene più di cinque immagini, sono disponibili comandi per vedere
il precedente e successivo insieme di cinque immagini. Se la pagina ALBUM PAGE mostra il
primo blocco d’immagini e ne esistono altre successive nell’ordinamento, compare a destra
della riga il bottone SUCCESSIVE, che permette di vedere le successive cinque immagini. Se
la pagina ALBUM PAGE mostra l’ultimo blocco d’immagini e ne esistono altre precedenti
nell’ordinamento, compare a sinistra della riga il bottone PRECEDENTI, che permette di
vedere le cinque immagini precedenti. Se la pagina ALBUM PAGE mostra un blocco
d’immagini e ne esistono altre precedenti e successive nell’ordinamento, compare a destra
della riga il bottone SUCCESSIVE, che permette di vedere le successive cinque immagini, e
a sinistra il bottone PRECEDENTI, che permette di vedere le cinque immagini precedenti.
Quando l’utente seleziona una miniatura, la pagina ALBUM PAGE mostra tutti i dati
dell’immagine scelta, tra cui la stessa immagine a grandezza naturale e i commenti
eventualmente presenti. La pagina mostra anche una form per aggiungere un commento.
L’invio del commento con un bottone INVIA ripresenta la pagina ALBUM PAGE, con tutti i dati
aggiornati della stessa immagine. La pagina ALBUM PAGE contiene anche un collegamento
per tornare all’HOME PAGE. L’applicazione consente il logout dell’utente.

-------------------------------------------------------------------------------------------------

Versione con JAVASCRIPT

Si realizzi un’applicazione client server web che modifica le specifiche precedenti come segue:

● La registrazione controlla la validità sintattica dell’indirizzo di email e l’uguaglianza tra
  i campi “password” e “ripeti password” anche a lato client.
	
● Dopo il login dell’utente, l’intera applicazione è realizzata con un’unica pagina.

● Ogni interazione dell’utente è gestita senza ricaricare completamente la pagina, ma
  produce l’invocazione asincrona del server e l’eventuale modifica del contenuto da
  aggiornare a seguito dell’evento.
	
● L’evento di visualizzazione del blocco precedente/successivo d’immagini di un album
  è gestito a lato client senza generare una richiesta al server.
	
● Quando l’utente passa con il mouse su una miniatura, l’applicazione mostra una
  finestra modale con tutte le informazioni dell’immagine, tra cui la stessa a grandezza
  naturale, i commenti eventualmente presenti e la form per inserire un commento.
	
● L’applicazione controlla anche a lato client che non si invii un commento vuoto.

● Errori a lato server devono essere segnalati mediante un messaggio di allerta
  all’interno della pagina.
	
● Si deve consentire all’utente di riordinare l’elenco dei propri album con un criterio
  diverso da quello di default (data decrescente). L’utente trascina il titolo di un album
  nell’elenco e lo colloca in una posizione diversa per realizzare l’ordinamento che
  desidera, senza invocare il server. Quando l’utente ha raggiunto l’ordinamento
  desiderato, usa un bottone “salva ordinamento”, per memorizzare la sequenza sul
  server. Ai successivi accessi, l’ordinamento personalizzato è usato al posto di quello
  di default.
