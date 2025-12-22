CREATE EXTENSION IF NOT EXISTS pgcrypto;
DROP TABLE IF EXISTS Issue_TAG CASCADE;
DROP TABLE IF EXISTS Commento CASCADE;
DROP TABLE IF EXISTS Cronologia CASCADE;
DROP TABLE IF EXISTS Issue CASCADE;
DROP TABLE IF EXISTS Progetto CASCADE;
DROP TABLE IF EXISTS Appartenenza CASCADE;
DROP TABLE IF EXISTS TAG CASCADE;

DROP TABLE IF EXISTS Utente CASCADE;
DROP TABLE IF EXISTS Team CASCADE;


DROP TYPE IF EXISTS enum_stato CASCADE;
DROP TYPE IF EXISTS enum_priorita CASCADE;
DROP TYPE IF EXISTS enum_tipologia CASCADE;

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
                        DataDiNascita DATE NOT NULL,
                        Sesso CHAR(1) NOT NULL CHECK (Sesso IN ('M', 'F')),
                        Mail VARCHAR(50) NOT NULL UNIQUE,
                        Username VARCHAR(255) NOT NULL UNIQUE,
                        Password VARCHAR(255) NOT NULL,
                        IsAdmin BOOLEAN NOT NULL DEFAULT FALSE,
                        DataInizioRuolo DATE,

                        CONSTRAINT chk_admin_data CHECK ( (IsAdmin = TRUE AND DataInizioRuolo IS NOT NULL) OR (IsAdmin = FALSE AND DataInizioRuolo IS NULL)),
                        CONSTRAINT check_lunghezza_CF CHECK (LENGTH(CF) = 16)

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



--trigger necessari

CREATE OR REPLACE FUNCTION check_assegnatario_nel_team()
    RETURNS TRIGGER AS $$
DECLARE
    team_progetto INTEGER;
    is_membro BOOLEAN;
BEGIN
    IF NEW.IdAssegnatario IS NULL THEN
        RETURN NEW;
    END IF;


    SELECT IdTeam INTO team_progetto
    FROM Progetto
    WHERE IdProgetto = NEW.IdProgetto;


    SELECT EXISTS (
        SELECT 1
        FROM Appartenenza
        WHERE IdUtente = NEW.IdAssegnatario
          AND IdTeam = team_progetto
    ) INTO is_membro;


    IF NOT is_membro THEN
        RAISE EXCEPTION 'L''utente assegnatario (ID %) non fa parte del team che gestisce questo progetto.', NEW.IdAssegnatario;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_check_assegnatario ON Issue;

CREATE TRIGGER trg_check_assegnatario
    BEFORE INSERT OR UPDATE ON Issue
    FOR EACH ROW
EXECUTE FUNCTION check_assegnatario_nel_team();




CREATE OR REPLACE FUNCTION prevent_change_closed_issue()
    RETURNS TRIGGER AS $$
BEGIN
    IF OLD.Stato = 'CLOSED' AND NEW.Stato = 'CLOSED' THEN
        IF NEW.Titolo != OLD.Titolo OR
           NEW.Descrizione != OLD.Descrizione OR
           NEW.Priorita != OLD.Priorita THEN

            RAISE EXCEPTION 'Non è possibile modificare i dettagli di una Issue nello stato CLOSED.';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_freeze_closed_issue ON Issue;

CREATE TRIGGER trg_freeze_closed_issue
    BEFORE UPDATE ON Issue
    FOR EACH ROW
EXECUTE FUNCTION prevent_change_closed_issue();




CREATE OR REPLACE FUNCTION check_commento_su_issue_aperta()
    RETURNS TRIGGER AS $$
DECLARE
    stato_issue enum_stato;
BEGIN

    SELECT Stato INTO stato_issue
    FROM Issue
    WHERE IdIssue = NEW.IdIssue;


    IF stato_issue = 'CLOSED' THEN
        RAISE EXCEPTION 'Non puoi commentare una Issue chiusa (ID %).', NEW.IdIssue;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_block_comments_closed ON Commento;

CREATE TRIGGER trg_block_comments_closed
    BEFORE INSERT ON Commento
    FOR EACH ROW
EXECUTE FUNCTION check_commento_su_issue_aperta();


CREATE OR REPLACE FUNCTION check_permessi_commento()
    RETURNS TRIGGER AS $$
DECLARE
    team_progetto INTEGER;
    is_admin BOOLEAN;
    is_membro_team BOOLEAN;
BEGIN
    -- 1. Controllo se l'utente è ADMIN (Jolly)
    SELECT IsAdmin INTO is_admin
    FROM Utente
    WHERE IdUtente = NEW.IdUtente;

    IF is_admin IS TRUE THEN
        RETURN NEW;
    END IF;

    SELECT P.IdTeam INTO team_progetto
    FROM Issue I
             JOIN Progetto P ON I.IdProgetto = P.IdProgetto
    WHERE I.IdIssue = NEW.IdIssue;


    SELECT EXISTS (
        SELECT 1
        FROM Appartenenza
        WHERE IdUtente = NEW.IdUtente
          AND IdTeam = team_progetto
    ) INTO is_membro_team;


    IF NOT is_membro_team THEN
        RAISE EXCEPTION 'Accesso negato: Solo i membri del team (o Admin) possono commentare questo progetto.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_check_permessi_commento ON Commento;

CREATE TRIGGER trg_check_permessi_commento
    BEFORE INSERT ON Commento
    FOR EACH ROW
EXECUTE FUNCTION check_permessi_commento();




-- 1. Funzione di Calcolo del Carico
CREATE OR REPLACE FUNCTION assegna_carico_bilanciato()
    RETURNS TRIGGER AS $$
DECLARE
    target_team INTEGER;
    scelto_utente INTEGER;
BEGIN

    IF NEW.IdAssegnatario IS NOT NULL THEN
        RETURN NEW;
    END IF;


    SELECT IdTeam INTO target_team
    FROM Progetto
    WHERE IdProgetto = NEW.IdProgetto;


    SELECT A.IdUtente INTO scelto_utente
    FROM Appartenenza A LEFT JOIN Issue I ON A.IdUtente = I.IdAssegnatario AND I.Stato IN ('TO-DO', 'IN PROGRESS')
    WHERE A.IdTeam = target_team
    GROUP BY A.IdUtente
    ORDER BY
        COALESCE(SUM(
                         CASE
                             WHEN I.Priorita = 'MASSIMA' THEN 10
                             WHEN I.Priorita = 'ALTA' THEN 5
                             WHEN I.Priorita = 'MEDIA' THEN 3
                             WHEN I.Priorita = 'BASSA' THEN 1
                             ELSE 0
                             END
                 ), 0) ASC,
        RANDOM()
    LIMIT 1;


    IF scelto_utente IS NULL THEN
        RAISE EXCEPTION 'Impossibile assegnare automaticamente: Il Team del progetto è vuoto.';
    END IF;


    NEW.IdAssegnatario := scelto_utente;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;



DROP TRIGGER IF EXISTS trg_load_balancer ON Issue;

CREATE TRIGGER trg_load_balancer
    BEFORE INSERT ON Issue
    FOR EACH ROW
EXECUTE FUNCTION assegna_carico_bilanciato();



CREATE OR REPLACE FUNCTION check_maggiorenne()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.DataDiNascita >= (CURRENT_DATE - INTERVAL '18 years') THEN
        RAISE EXCEPTION 'L''utente deve essere maggiorenne. Data di nascita inserita: %', NEW.DataDiNascita;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS trg_check_maggiorenne ON Utente;

CREATE TRIGGER trg_check_maggiorenne
    BEFORE INSERT OR UPDATE ON Utente
    FOR EACH ROW
EXECUTE FUNCTION check_maggiorenne();


CREATE OR REPLACE FUNCTION log_solo_assegnazione()
RETURNS TRIGGER AS $$
DECLARE
nome_utente VARCHAR;
    cognome_utente VARCHAR;
BEGIN
    IF NEW.IdAssegnatario IS NOT NULL AND
       (TG_OP = 'INSERT' OR NEW.IdAssegnatario IS DISTINCT FROM OLD.IdAssegnatario) THEN

SELECT Nome, Cognome INTO nome_utente, cognome_utente
FROM Utente
WHERE IdUtente = NEW.IdAssegnatario;

INSERT INTO Cronologia (Data, Descrizione, IdUtente, IdIssue)
VALUES (
                   CURRENT_TIMESTAMP,
                   'Issue assegnata a ' || nome_utente || ' ' || cognome_utente,
                   NEW.IdAssegnatario,
                   NEW.IdIssue
       );

END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_log_assegnazione ON Issue;

CREATE TRIGGER trg_log_assegnazione
    AFTER INSERT OR UPDATE ON Issue
                        FOR EACH ROW
                        EXECUTE FUNCTION log_solo_assegnazione();