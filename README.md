# 🐞 Project BugBoard 26

**Benvenuti in BugBoard 26**, la soluzione definitiva per il tracciamento e la gestione delle problematiche software.

Sviluppato con un'architettura moderna **orientata ai servizi (SOA)** e un'interfaccia **JavaFX** reattiva, BugBoard trasforma il caos dei bug in un flusso di lavoro ordinato e produttivo. Che tu sia uno sviluppatore che deve risolvere un errore critico o un manager che deve assegnare priorità, BugBoard è lo strumento che fa per te.

---

## 🚀 Cosa fa BugBoard?

BugBoard non è solo un elenco di cose da fare; è un ecosistema completo per la gestione del ciclo di vita del software:

* 🔐 **Accesso Sicuro & Gestione Ruoli**
Sistema di autenticazione robusto basato su **JWT** (*Servizio Login*) per garantire che solo le persone autorizzate possano accedere ai dati sensibili.
* 📝 **Gestione Completa delle Issue**
* **Crea:** Segnala nuovi bug con dettagli precisi (*Servizio Inserimento*).
* **Modifica:** Aggiorna lo stato, la priorità o la descrizione delle issue man mano che evolvono (*Servizio Modifica*).
* **Recupera:** Visualizza lo storico e i dettagli di ogni segnalazione (*Servizio Recupero*).


* 💬 **Collaborazione in Tempo Reale**
Discuti delle soluzioni direttamente all'interno della issue grazie al sistema di commenti integrato (*Servizio Commenta*).
* 👥 **Gestione Utenti**
Aggiungi e gestisci i membri del team con facilità (*Servizio Inserimento Utente*).
* 📊 **Dashboard Intuitiva**
Una panoramica chiara di tutte le attività per avere sempre il controllo della situazione.

---

## 🛠️ Tecnologia sotto il cofano

Il progetto è costruito su solide basi tecnologiche per garantire scalabilità e performance:

* **Backend:** Architettura Orientata ai Servizi (SOA) sviluppata in Java (Spring Boot).
* **Frontend:** Interfaccia desktop nativa e performante realizzata in JavaFX.
* **Database:** Persistenza dei dati strutturata (SQL).
* **Cloud & Hosting:** Infrastruttura ospitata su **Microsoft Azure**.
* **Containerizzazione:** Pronto per Docker.

---

## ⚠️ Disclaimer Importante: Backend Azure

**Nota Operativa:** L'applicazione client è configurata per comunicare con i servizi ospitati in cloud.
Affinché l'app funzioni correttamente e possa effettuare il login o recuperare i dati, **la Virtual Machine (VM) su Azure deve essere ATTIVA**.

Se la VM è spenta o non raggiungibile, l'applicazione non sarà in grado di connettersi al backend remoto.

---

## ⚡ Guida Rapida all'Avvio (Quick Start)

Per avviare l'applicazione, segui questi passaggi:

1. Apri il terminale.
2. Spostati nella cartella del frontend:
```bash
cd frontend

```


3. Lancia l'applicazione:
```bash
mvn javafx:run

```



---

## 🐳 Avvio del Backend Locale (Opzionale)

Se preferisci non usare l'ambiente cloud di Azure e vuoi avviare l'intera infrastruttura in locale (Database e Servizi), puoi usare Docker Compose:

```bash
cd backend
docker-compose up --build

```

*Nota: Questo richiederà di riconfigurare i puntamenti dell'applicazione verso `localhost` invece che verso l'IP pubblico di Azure e configurare un file .env .*
