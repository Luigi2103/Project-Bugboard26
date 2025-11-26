CREATE TYPE enum_stato AS ENUM (
    'TO-DO',
    'IN PROGRESS',
    'RESOLVED',
    'CLOSED'
);

CREATE TYPE enum_priorita AS ENUM (
    'BASSA',
    'MEDIA',
    'ALTA',
    'MASSIMA'
);

CREATE TYPE enum_tipologia AS ENUM (
    'BUG',
    'QUESTION',
    'DOCUMENTATION',
    'FEATURE'
);


CREATE TABLE Utente (
                        IdUtente SERIAL PRIMARY KEY,
                        Nome VARCHAR(100) NOT NULL,
                        Cognome VARCHAR(100) NOT NULL,
                        CF VARCHAR(16) NOT NULL UNIQUE,
                        Sesso CHAR(1) NOT NULL CHECK (Sesso IN ('M', 'F')),
                        Eta INTEGER NOT NULL CHECK (Eta >= 18 AND Eta <= 70),
                        Username VARCHAR(255) NOT NULL UNIQUE,
                        Password VARCHAR(255) NOT NULL,
                        IsAdmin BOOLEAN NOT NULL DEFAULT FALSE,
                        DataInizioRuolo DATE,

                        CONSTRAINT chk_admin_data CHECK (
                            (IsAdmin = TRUE AND DataInizioRuolo IS NOT NULL) OR
                            (IsAdmin = FALSE)
                            )
);


CREATE TABLE Team (
                      IdTeam SERIAL PRIMARY KEY,
                      Nome VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE Appartenenza (
                              Id SERIAL PRIMARY KEY,
                              IdUtente INTEGER NOT NULL,
                              IdTeam INTEGER NOT NULL,

                              CONSTRAINT fk_appartenenza_utente FOREIGN KEY (IdUtente)
                                  REFERENCES Utente(IdUtente) ON DELETE CASCADE,
                              CONSTRAINT fk_appartenenza_team FOREIGN KEY (IdTeam)
                                  REFERENCES Team(IdTeam) ON DELETE CASCADE,
                              CONSTRAINT unica_appartenenza_utente_team UNIQUE (IdUtente, IdTeam)
);


CREATE TABLE Progetto (
                          IdProgetto SERIAL PRIMARY KEY,
                          Nome VARCHAR(200) NOT NULL,
                          Descrizione TEXT NOT NULL,
                          DataInizio DATE NOT NULL,
                          DataFine DATE,
                          IdTeam INTEGER NOT NULL,

                          CONSTRAINT fk_progetto_team FOREIGN KEY (IdTeam)
                              REFERENCES Team(IdTeam) ON DELETE RESTRICT,
                          CONSTRAINT chk_progetto_date CHECK (
                              DataFine IS NULL OR DataFine >= DataInizio
                              )
);


CREATE TABLE Issue (
                       IdIssue SERIAL PRIMARY KEY,
                       Titolo VARCHAR(200) NOT NULL,
                       Descrizione TEXT NOT NULL,
                       Stato enum_stato NOT NULL DEFAULT 'TO-DO',
                       Priorita enum_priorita,
                       Foto BYTEA,
                       Tipologia enum_tipologia NOT NULL,
                       DataCreazione TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       IdProgetto INTEGER NOT NULL,
                       IdSegnalatore INTEGER NOT NULL,
                       IdAssegnatario INTEGER,
                       PassiPerRiprodurre TEXT,
                       Richiesta TEXT,
                       TitoloDocumento TEXT,
                       DescrizioneProblema TEXT,
                       RichiestaFunzionalita TEXT,

                       CONSTRAINT fk_issue_progetto FOREIGN KEY (IdProgetto)
                           REFERENCES Progetto(IdProgetto) ON DELETE CASCADE,
                       CONSTRAINT fk_issue_segnalatore FOREIGN KEY (IdSegnalatore)
                           REFERENCES Utente(IdUtente) ON DELETE RESTRICT,
                       CONSTRAINT fk_issue_assegnatario FOREIGN KEY (IdAssegnatario)
                           REFERENCES Utente(IdUtente) ON DELETE RESTRICT,

                       CONSTRAINT chk_bug_attributes CHECK (
                           Tipologia != 'BUG' OR PassiPerRiprodurre IS NOT NULL
),
    CONSTRAINT chk_question_attributes CHECK (
        Tipologia != 'QUESTION' OR Richiesta IS NOT NULL
    ),
    CONSTRAINT chk_documentation_attributes CHECK (
        Tipologia != 'DOCUMENTATION' OR
        (TitoloDocumento IS NOT NULL AND DescrizioneProblema IS NOT NULL)
    ),
    CONSTRAINT chk_feature_attributes CHECK (
        Tipologia != 'FEATURE' OR RichiestaFunzionalita IS NOT NULL
    )
);


CREATE TABLE Cronologia (
                            IdCronologia SERIAL PRIMARY KEY,
                            Data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            Descrizione TEXT,
                            IdUtente INTEGER NOT NULL,
                            IdIssue INTEGER NOT NULL,

                            CONSTRAINT fk_cronologia_utente FOREIGN KEY (IdUtente)
                                REFERENCES Utente(IdUtente) ON DELETE RESTRICT,
                            CONSTRAINT fk_cronologia_issue FOREIGN KEY (IdIssue)
                                REFERENCES Issue(IdIssue) ON DELETE CASCADE
);


CREATE TABLE Commento (
                          IdCommento SERIAL PRIMARY KEY,
                          Testo TEXT NOT NULL,
                          Data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          IdIssue INTEGER NOT NULL,
                          IdUtente INTEGER NOT NULL,

                          CONSTRAINT fk_commento_issue FOREIGN KEY (IdIssue)
                              REFERENCES Issue(IdIssue) ON DELETE CASCADE,
                          CONSTRAINT fk_commento_utente FOREIGN KEY (IdUtente)
                              REFERENCES Utente(IdUtente) ON DELETE RESTRICT
);


CREATE TABLE TAG (
                     IdTag SERIAL PRIMARY KEY,
                     Nome VARCHAR(50) NOT NULL UNIQUE
);


CREATE TABLE Issue_TAG (
                           Id SERIAL PRIMARY KEY,
                           IdIssue INTEGER NOT NULL,
                           IdTag INTEGER NOT NULL,

                           CONSTRAINT fk_issuetag_issue FOREIGN KEY (IdIssue)
                               REFERENCES Issue(IdIssue) ON DELETE CASCADE,
                           CONSTRAINT fk_issuetag_tag FOREIGN KEY (IdTag)
                               REFERENCES TAG(IdTag) ON DELETE CASCADE,
                           CONSTRAINT unica_issue_tag UNIQUE (IdIssue, IdTag)
);