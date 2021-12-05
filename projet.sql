DROP SCHEMA IF EXISTS projet2021 CASCADE;
CREATE SCHEMA projet2021;

CREATE TABLE projet2021.ues (
    id_ue SERIAL PRIMARY KEY,
   code_ue VARCHAR(100) UNIQUE NOT NULL CHECK ( code_ue SIMILAR TO 'BINV[1-3]%'),
   nom VARCHAR(100) NOT NULL CHECK (nom<>''),
   bloc INTEGER NOT NULL CHECK (bloc<=3 AND bloc>=1 AND bloc = CAST(substr(code_ue,5,1) AS INTEGER)),
   credits INTEGER NOT NULL CHECK (credits<=60 AND bloc>=1),
   nbr_inscrits INTEGER NOT NULL DEFAULT 0 CHECK (nbr_inscrits >= 0)
);

CREATE TABLE projet2021.etudiants (
    id_etudiant SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL CHECK (nom<>''),
    prenom VARCHAR(100) NOT NULL CHECK (prenom<>''),
    email VARCHAR(255) NOT NULL CHECK (email SIMILAR TO '%_@__%.__%') UNIQUE,
    mot_de_passe VARCHAR(60) NOT NULL CHECK (mot_de_passe<>''),
    bloc INTEGER NULL CHECK (bloc<=3 AND bloc>=1),
    pae_valide BOOLEAN NOT NULL DEFAULT FALSE,
    credits_pae INTEGER NOT NULL DEFAULT 0 CHECK (credits_pae >= 0),
    credits_valides INTEGER NOT NULL DEFAULT 0 CHECK (credits_valides >= 0)
);

CREATE TABLE projet2021.ues_valides (
    id_etudiant INTEGER REFERENCES projet2021.etudiants (id_etudiant) NOT NULL,
    id_ue INTEGER REFERENCES projet2021.ues (id_ue) NOT NULL,
    PRIMARY KEY (id_etudiant, id_ue)
);

CREATE TABLE projet2021.ues_pae_etudiants (
   id_etudiant INTEGER REFERENCES projet2021.etudiants (id_etudiant) NOT NULL,
   id_ue INTEGER REFERENCES projet2021.ues (id_ue) NOT NULL,
   PRIMARY KEY (id_etudiant, id_ue)
);

CREATE TABLE projet2021.prerequis (
   id_ue INTEGER REFERENCES projet2021.ues (id_ue) NOT NULL,
   id_prerequis INTEGER REFERENCES projet2021.ues (id_ue) NOT NULL,
   PRIMARY KEY (id_ue,id_prerequis)
);

-- Mock etudiants --
--------------------
INSERT INTO projet2021.ues (code_ue, nom, bloc, credits)
VALUES ('BINV11', 'BD1', 1, 31),
       ('BINV12', 'APOO', 1, 16),
       ('BINV13', 'ALGO', 1, 13),
       ('BINV21', 'BD2', 2, 42),
       ('BINV311', 'Anglais', 3, 16),
       ('BINV32', 'Stage', 3, 44);

--------------------------
-- APPLICATION CENTRALE --
--------------------------

-- 1) Ajouter une UE
CREATE OR REPLACE FUNCTION projet2021.ajouterUe(_code_ue VARCHAR(100), _nom VARCHAR(100), _bloc INTEGER, _credit INTEGER) RETURNS BOOLEAN AS $$
BEGIN
    INSERT INTO projet2021.ues(code_ue, nom, bloc, credits)
    VALUES (_code_ue, _nom, _bloc, _credit);
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- 2) Ajouter un prerequis a une UE
CREATE OR REPLACE FUNCTION projet2021.ajouterPrerequis(_code_ue VARCHAR(100), _code_prerequis VARCHAR(100)) RETURNS BOOLEAN AS $$
DECLARE
    _id_ue INTEGER;
    _id_prerequis INTEGER;
BEGIN
    SELECT id_ue FROM projet2021.ues WHERE code_ue = _code_ue INTO _id_ue;
    SELECT id_ue FROM projet2021.ues WHERE code_ue = _code_prerequis INTO _id_prerequis;

    INSERT INTO projet2021.prerequis(id_ue, id_prerequis)
    VALUES (_id_ue, _id_prerequis);
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;
-- Trigger check prerequis d'une année inférieur
CREATE OR REPLACE FUNCTION projet2021.checkBlockPrerequis() RETURNS TRIGGER AS $$
    DECLARE
        _bloc_ue INTEGER;
        _bloc_prerequis INTEGER;
    BEGIN
        SELECT u.bloc FROM projet2021.ues u WHERE u.id_ue = new.id_ue INTO _bloc_ue;
        SELECT u.bloc FROM projet2021.ues u WHERE u.id_ue = new.id_prerequis INTO _bloc_prerequis;

        IF (_bloc_prerequis >= _bloc_ue)
            THEN RAISE 'Le bloc du prerequis ne peut pas être supérieur ou égal au bloc de Ue' ;
        END IF;
    RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER checkPrerequis BEFORE INSERT ON projet2021.prerequis FOR EACH ROW
EXECUTE PROCEDURE projet2021.checkBlockPrerequis();



-- 3) Ajouter un etudiant
CREATE OR REPLACE FUNCTION projet2021.ajouterEtudiant(_nom VARCHAR(100), _prenom VARCHAR(100), _email VARCHAR(255), _mot_de_passe VARCHAR(255)) RETURNS BOOLEAN AS $$
BEGIN
    INSERT INTO projet2021.etudiants(nom, prenom, email, mot_de_passe)
    VALUES(_nom, _prenom, _email, _mot_de_passe);
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

-- 4) Encoder une UE validée
CREATE OR REPLACE FUNCTION projet2021.encoderUeValidee(_email VARCHAR(255), _code_ue VARCHAR(100)) RETURNS BOOLEAN AS $$
DECLARE
    _id_ue INTEGER;
    _id_etudiant INTEGER;
BEGIN
    SELECT id_ue FROM projet2021.ues WHERE code_ue = _code_ue INTO _id_ue;
    SELECT id_etudiant FROM projet2021.etudiants WHERE email = _email INTO _id_etudiant;

    INSERT INTO projet2021.ues_valides(id_etudiant, id_ue)
    VALUES (_id_etudiant, _id_ue);
    RETURN TRUE;
end;
$$ LANGUAGE plpgsql;
-- Trigger update 'credits_valide' dans etudiants quand ajoute une ue valide
CREATE OR REPLACE FUNCTION projet2021.updateCreditsValider() RETURNS TRIGGER AS $$
    DECLARE _credits_a_ajouter INTEGER;
    BEGIN
        SELECT u.credits FROM projet2021.ues u WHERE u.id_ue = new.id_ue INTO _credits_a_ajouter;

        UPDATE projet2021.etudiants SET credits_valides = etudiants.credits_valides + _credits_a_ajouter
        WHERE id_etudiant = NEW.id_etudiant;
    RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER encoderUeValidee AFTER INSERT ON projet2021.ues_valides FOR EACH ROW
EXECUTE PROCEDURE projet2021.updateCreditsValider();

-- 5) Visualiser tous les étudiants d'un bloc
CREATE OR REPLACE VIEW projet2021.visualiserEtudiantsDuBloc AS
SELECT nom, prenom, credits_pae, bloc FROM projet2021.etudiants
ORDER BY nom,prenom;

-- 6) Visualiser pour chaque etudiant le nombre de credits du PAE
CREATE OR REPLACE VIEW projet2021.visualiserCreditsPAEEtudiants AS
SELECT e.nom, e.prenom, e.bloc, e.credits_pae
FROM projet2021.etudiants e
ORDER BY e.credits_pae;

-- 7) Visualiser etudiants avec pae pas encore valide
CREATE OR REPLACE VIEW projet2021.visualiserEtudiantsPAEPasValide AS
    SELECT e.nom, e.prenom, e.credits_valides, e.bloc
    FROM projet2021.etudiants e
    WHERE e.pae_valide IS FALSE;

-- 8) Visualiser Ues d'un bloc
CREATE OR REPLACE VIEW projet2021.visualiserUesDuBloc AS
    SELECT bloc, code_ue, nom, nbr_inscrits FROM projet2021.ues
    ORDER BY nbr_inscrits;

--------------------------
-- Application etudiant --
--------------------------

-- 1) Ajouter Ue
CREATE OR REPLACE FUNCTION projet2021.ajouterUeEtudiant(_id_etudiant INTEGER,_code VARCHAR(100)) RETURNS BOOLEAN AS $$
    DECLARE
        _id_ue INTEGER;
    BEGIN
        SELECT id_ue FROM projet2021.ues WHERE code_ue = _code INTO _id_ue;

        IF (SELECT e.pae_valide FROM projet2021.etudiants e
            WHERE e.id_etudiant = _id_etudiant)
            THEN RAISE 'PAE déjà validé !';
        END IF;

        IF (EXISTS(SELECT * FROM projet2021.ues_valides WHERE id_ue = _id_ue AND id_etudiant = _id_etudiant)
            OR EXISTS(SELECT * FROM projet2021.ues_pae_etudiants WHERE id_ue = _id_ue AND id_etudiant = _id_etudiant))
            THEN RAISE 'Ue déjà validé ou dans le PAE !';
        END IF;

        IF 30 >= (SELECT credits_valides FROM projet2021.etudiants
            WHERE id_etudiant = _id_etudiant) AND 1 != (SELECT bloc FROM projet2021.ues
                WHERE id_ue = _id_ue)
            THEN RAISE 'Moins de 30 crédits validés et ue pas du bloc 1 !';
        END IF;


        IF EXISTS(SELECT p.id_ue FROM projet2021.prerequis p WHERE p.id_ue = _id_ue)
               AND ((SELECT count(p.id_prerequis) FROM projet2021.prerequis p WHERE p.id_ue = _id_ue)!= (SELECT count(uv.id_ue)
                    FROM projet2021.ues_valides uv, projet2021.prerequis p
                        WHERE uv.id_ue = p.id_prerequis AND uv.id_etudiant = _id_etudiant AND p.id_ue = _id_ue))
            THEN RAISE 'Prérequis pas encore validé !';
        END IF;

        INSERT INTO projet2021.ues_pae_etudiants VALUES (_id_etudiant, _id_ue);
        RETURN TRUE;

    END;
$$ LANGUAGE plpgsql;

-- Trigger update 'credits_pae' dans etudiants quand ajoute une ue du pae d'un etudiant
CREATE OR REPLACE FUNCTION projet2021.updateCreditsPAEEtudiantApresAjout() RETURNS TRIGGER AS $$
    DECLARE _credits_a_ajouter INTEGER;
    BEGIN
        SELECT u.credits FROM projet2021.ues u WHERE u.id_ue = new.id_ue INTO _credits_a_ajouter;

        UPDATE projet2021.etudiants SET credits_pae = etudiants.credits_pae + _credits_a_ajouter
        WHERE id_etudiant = NEW.id_etudiant;
    RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER ajouterCreditsPAEEtudiant AFTER INSERT ON projet2021.ues_pae_etudiants FOR EACH ROW
EXECUTE PROCEDURE projet2021.updateCreditsPAEEtudiantApresAjout();

-- 2) Enlever UE a PAE d'un etudiant
CREATE OR REPLACE FUNCTION projet2021.enleverUEAPAEEtudiant(_etudiant INTEGER, _code_ue VARCHAR(100)) RETURNS BOOLEAN AS $$
DECLARE _id_ue INTEGER;
BEGIN
    SELECT id_ue FROM projet2021.ues WHERE code_ue = _code_ue INTO _id_ue;

    IF(SELECT e.pae_valide FROM projet2021.etudiants e WHERE e.id_etudiant = _etudiant)
        THEN RAISE 'PAE déjà validé';
    END IF;
    IF ((SELECT id_ue FROM projet2021.ues_pae_etudiants WHERE id_etudiant = _etudiant AND id_ue = _id_ue)IS NULL)
        THEN RAISE 'Cette ue n est pas dans le PAE';
    END IF;

    DELETE FROM projet2021.ues_pae_etudiants
    WHERE id_etudiant = _etudiant
    AND id_ue = _id_ue;

    RETURN TRUE;
end;
$$ LANGUAGE plpgsql;

-- Trigger update 'credits_pae' dans etudiants quand delete une ue du pae d'un etudiant
CREATE OR REPLACE FUNCTION projet2021.updateCreditsPAEEtudiantApresDelete() RETURNS TRIGGER AS $$
    DECLARE _credits_a_retirer INTEGER;
    BEGIN
        SELECT u.credits FROM projet2021.ues u WHERE u.id_ue = OLD.id_ue INTO _credits_a_retirer;

        UPDATE projet2021.etudiants SET credits_pae = etudiants.credits_pae - _credits_a_retirer
        WHERE id_etudiant = OLD.id_etudiant;
    RETURN OLD;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER retirerCreditsPAEEtudiant AFTER DELETE ON projet2021.ues_pae_etudiants FOR EACH ROW
EXECUTE PROCEDURE projet2021.updateCreditsPAEEtudiantApresDelete();

-- 3) Valider PAE
CREATE OR REPLACE FUNCTION projet2021.validerPAEEtudiant(_etudiant INTEGER) RETURNS BOOLEAN AS $$
BEGIN

    IF (SELECT e.pae_valide FROM projet2021.etudiants e WHERE e.id_etudiant = _etudiant)
        THEN RAISE 'PAE déjà validé !';
    END IF;

    UPDATE projet2021.etudiants SET pae_valide = TRUE
    WHERE id_etudiant = _etudiant;

    RETURN TRUE;
end;
$$ LANGUAGE plpgsql;
-- Trigger before update pae valide to true
CREATE OR REPLACE FUNCTION projet2021.verifierPAEValide() RETURNS TRIGGER AS $$
DECLARE _credits_pae INTEGER;
DECLARE _credits_valides INTEGER;
BEGIN
    SELECT e.credits_pae FROM projet2021.etudiants e WHERE e.id_etudiant = NEW.id_etudiant INTO _credits_pae;
    SELECT e.credits_valides FROM projet2021.etudiants e WHERE e.id_etudiant = NEW.id_etudiant INTO _credits_valides;

    IF(OLD.pae_valide = NEW.pae_valide)
        THEN RETURN NEW;
    END IF;

    IF(_credits_pae > 74)
        THEN RAISE 'Trop de credits dans le pae (>74)';
    END IF;

   IF(_credits_valides < 45 AND _credits_pae > 60)
        THEN RAISE 'Trop de credits dans le pae (>60), alors que letudiant a valide moins de 45 credits';
    END IF;

    IF(_credits_valides > 45 AND _credits_pae < 55 AND _credits_valides + _credits_pae < 180)
        THEN RAISE 'Pas assez de credits (<55) dans le PAE';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- Trigger after update pae valide to true
CREATE OR REPLACE FUNCTION projet2021.updateNbrInscritsCoursPAEEtAssigneBloc() RETURNS TRIGGER AS $$
DECLARE _credits_pae INTEGER;
DECLARE _credits_valides INTEGER;
BEGIN
    SELECT e.credits_pae FROM projet2021.etudiants e WHERE e.id_etudiant = NEW.id_etudiant INTO _credits_pae;
    SELECT e.credits_valides FROM projet2021.etudiants e WHERE e.id_etudiant = NEW.id_etudiant INTO _credits_valides;

    IF(OLD.pae_valide = NEW.pae_valide)
        THEN RETURN NEW;
    END IF;

    --UPDATE NBR INSCRITS UES DU PAE DE L'ETUDIANT
    UPDATE projet2021.ues SET nbr_inscrits = nbr_inscrits + 1 FROM projet2021.ues_pae_etudiants
    WHERE projet2021.ues_pae_etudiants.id_etudiant = NEW.id_etudiant
    AND projet2021.ues_pae_etudiants.id_ue = projet2021.ues.id_ue;

    IF(_credits_valides < 45)
        THEN UPDATE projet2021.etudiants SET bloc = 1
        WHERE id_etudiant = NEW.id_etudiant;
    END IF;

    IF(_credits_valides + _credits_pae >= 180)
        THEN UPDATE projet2021.etudiants SET bloc = 3
        WHERE id_etudiant = NEW.id_etudiant;
    END IF;

    IF(_credits_valides >=45 AND _credits_valides + _credits_pae < 180)
        THEN UPDATE projet2021.etudiants SET bloc = 2
        WHERE id_etudiant = NEW.id_etudiant;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER validerPAETriggerBefore BEFORE UPDATE ON projet2021.etudiants FOR EACH ROW
EXECUTE PROCEDURE projet2021.verifierPAEValide();

CREATE TRIGGER validerPAETriggerAfter AFTER UPDATE ON projet2021.etudiants FOR EACH ROW
EXECUTE PROCEDURE projet2021.updateNbrInscritsCoursPAEEtAssigneBloc();

-- 4) Afficher Ue ajoutables
CREATE OR REPLACE FUNCTION projet2021.VisualiserUeAjoutableParEtudiant(_etudiant INTEGER)
    RETURNS TABLE(CODE VARCHAR(100), NOM VARCHAR(100), CREDITS INTEGER, BLOC INTEGER) AS $$
    DECLARE
        _credit_valide INTEGER;
    BEGIN

        SELECT e.credits_valides FROM projet2021.etudiants e WHERE e.id_etudiant = _etudiant INTO _credit_valide;

        IF (_credit_valide < 30) THEN
            RETURN QUERY (SELECT u.code_ue AS CODE, u.nom AS NOM, u.credits AS CREDITS, u.bloc AS BLOC FROM projet2021.ues u
                    WHERE u.id_ue NOT IN (SELECT uv.id_ue FROM projet2021.ues_valides uv WHERE uv.id_etudiant = _etudiant)
                    AND u.id_ue NOT IN (SELECT upa.id_ue FROM projet2021.ues_pae_etudiants upa WHERE upa.id_etudiant = _etudiant)
                    AND u.bloc = 1);
        END IF;
        IF (_credit_valide >= 30) THEN
            RETURN QUERY (SELECT u.code_ue AS CODE, u.nom AS NOM, u.credits AS CREDITS, u.bloc AS BLOC FROM projet2021.ues u
                    WHERE u.id_ue IN (SELECT p.id_ue FROM projet2021.prerequis p EXCEPT SELECT p.id_ue FROM projet2021.prerequis p WHERE id_prerequis IN
                        (SELECT p.id_prerequis FROM projet2021.prerequis p EXCEPT SELECT uv.id_ue FROM projet2021.ues_valides uv WHERE uv.id_etudiant = _etudiant))    -- Donne les ues accecibles par prerequis
                    AND u.id_ue NOT IN (SELECT uv.id_ue FROM projet2021.ues_valides uv WHERE uv.id_etudiant = _etudiant)            -- Vérifier ues déjà validées
                    AND u.id_ue NOT IN (SELECT upa.id_ue FROM projet2021.ues_pae_etudiants upa WHERE upa.id_etudiant = _etudiant)   -- Vérifier ues déjà dans le pae
                    OR (u.id_ue NOT IN (SELECT p.id_ue FROM projet2021.prerequis p)
                        AND u.id_ue NOT IN (SELECT uv.id_ue FROM projet2021.ues_valides uv WHERE uv.id_etudiant = _etudiant)
                        AND u.id_ue NOT IN (SELECT upa.id_ue FROM projet2021.ues_pae_etudiants upa WHERE upa.id_etudiant = _etudiant)));                                                 -- Ues n'ayant pas de prérequis
        END IF;
    END;
$$ LANGUAGE plpgsql;

-- 5) Visualiser son PAE (A FAIRE EN JAVA)
CREATE OR REPLACE VIEW projet2021.PAEEtudiant AS
    SELECT upe.id_etudiant AS id, u.code_ue AS code_ue, u.nom AS nom_ue, u.credits AS credit_ue, u.bloc AS bloc_ue FROM projet2021.ues_pae_etudiants upe, projet2021.ues u
    WHERE u.id_ue = upe.id_ue
    ORDER BY upe.id_etudiant,u.code_ue;

-- 6) Reinitialiser le pae
CREATE OR REPLACE FUNCTION projet2021.reinitialiserPAEEtudiant(_etudiant INTEGER) RETURNS BOOLEAN AS $$
DECLARE _id_ue INTEGER;
DECLARE _code_ue VARCHAR(100);
BEGIN
    IF(SELECT e.pae_valide FROM projet2021.etudiants e WHERE e.id_etudiant = _etudiant)
        THEN RAISE 'PAE déjà validé !';
    END IF;

    FOR _id_ue IN SELECT id_ue FROM projet2021.ues_pae_etudiants WHERE id_etudiant = _etudiant LOOP
        SELECT code_ue FROM projet2021.ues WHERE id_ue = _id_ue INTO _code_ue;
        PERFORM projet2021.enleverUEAPAEEtudiant(_etudiant, _code_ue);
    END LOOP;

    RETURN TRUE;
end;
$$ LANGUAGE plpgsql;

SELECT * FROM projet2021.etudiants;
SELECT * FROM projet2021.ues_valides WHERE id_etudiant = 7;
SELECT * FROM projet2021.ues_pae_etudiants WHERE id_etudiant = 7;
SELECT * FROM projet2021.prerequis;
SELECT * FROM projet2021.ues;