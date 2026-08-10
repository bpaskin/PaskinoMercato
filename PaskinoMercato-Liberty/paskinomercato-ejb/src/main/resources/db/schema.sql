-- =============================================================
-- PaskinoMercato PostgreSQL Schema
-- Max 1503 products enforced via CHECK constraint
-- =============================================================

CREATE SCHEMA IF NOT EXISTS mercato;

-- ---------------------------------------------------------------
-- CATEGORIES
-- ---------------------------------------------------------------
CREATE TABLE mercato.categoria (
    id          SERIAL PRIMARY KEY,
    codice      VARCHAR(50)  NOT NULL UNIQUE,
    nome_it     VARCHAR(100) NOT NULL,
    nome_en     VARCHAR(100) NOT NULL,
    descrizione_it TEXT,
    descrizione_en TEXT,
    immagine    VARCHAR(255)
);

-- ---------------------------------------------------------------
-- PRODUCTS  (max 1503 rows enforced by trigger)
-- ---------------------------------------------------------------
CREATE TABLE mercato.prodotto (
    id              SERIAL PRIMARY KEY,
    codice          VARCHAR(50)     NOT NULL UNIQUE,
    nome_it         VARCHAR(200)    NOT NULL,
    nome_en         VARCHAR(200)    NOT NULL,
    descrizione_it  TEXT,
    descrizione_en  TEXT,
    prezzo          NUMERIC(10,2)   NOT NULL CHECK (prezzo >= 0),
    unita_misura    VARCHAR(20)     NOT NULL DEFAULT 'pz',
    quantita_stock  INTEGER         NOT NULL DEFAULT 0,
    categoria_id    INTEGER         NOT NULL REFERENCES mercato.categoria(id),
    immagine        VARCHAR(255),
    attivo          BOOLEAN         NOT NULL DEFAULT TRUE,
    peso_kg         NUMERIC(6,3),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- Enforce max 1503 products
CREATE OR REPLACE FUNCTION mercato.check_max_prodotti()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT COUNT(*) FROM mercato.prodotto) >= 1503 THEN
        RAISE EXCEPTION 'Maximum product limit of 1503 reached';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_max_prodotti
BEFORE INSERT ON mercato.prodotto
FOR EACH ROW EXECUTE FUNCTION mercato.check_max_prodotti();

-- ---------------------------------------------------------------
-- CUSTOMERS
-- ---------------------------------------------------------------
CREATE TABLE mercato.cliente (
    id              SERIAL PRIMARY KEY,
    email           VARCHAR(200)    NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    nome            VARCHAR(100)    NOT NULL,
    cognome         VARCHAR(100)    NOT NULL,
    telefono        VARCHAR(20),
    lingua          CHAR(2)         NOT NULL DEFAULT 'it' CHECK (lingua IN ('it','en')),
    attivo          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ---------------------------------------------------------------
-- ADDRESSES  (Italian only - validated at application level)
-- ---------------------------------------------------------------
CREATE TABLE mercato.indirizzo (
    id              SERIAL PRIMARY KEY,
    cliente_id      INTEGER         NOT NULL REFERENCES mercato.cliente(id),
    via             VARCHAR(255)    NOT NULL,
    civico          VARCHAR(20)     NOT NULL,
    citta           VARCHAR(100)    NOT NULL,
    cap             CHAR(5)         NOT NULL,
    provincia       CHAR(2)         NOT NULL,
    paese           CHAR(2)         NOT NULL DEFAULT 'IT' CHECK (paese = 'IT'),
    predefinito     BOOLEAN         NOT NULL DEFAULT FALSE
);

-- ---------------------------------------------------------------
-- ORDERS
-- ---------------------------------------------------------------
CREATE TABLE mercato.ordine (
    id              SERIAL PRIMARY KEY,
    numero_ordine   VARCHAR(30)     NOT NULL UNIQUE,
    cliente_id      INTEGER         NOT NULL REFERENCES mercato.cliente(id),
    indirizzo_id    INTEGER         NOT NULL REFERENCES mercato.indirizzo(id),
    stato           VARCHAR(30)     NOT NULL DEFAULT 'IN_ATTESA'
                        CHECK (stato IN ('IN_ATTESA','CONFERMATO','IN_PREPARAZIONE',
                                         'SPEDITO','CONSEGNATO','ANNULLATO')),
    totale          NUMERIC(10,2)   NOT NULL CHECK (totale >= 0),
    note            TEXT,
    email_inviata   BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ---------------------------------------------------------------
-- ORDER LINES
-- ---------------------------------------------------------------
CREATE TABLE mercato.riga_ordine (
    id              SERIAL PRIMARY KEY,
    ordine_id       INTEGER         NOT NULL REFERENCES mercato.ordine(id),
    prodotto_id     INTEGER         NOT NULL REFERENCES mercato.prodotto(id),
    quantita        INTEGER         NOT NULL CHECK (quantita > 0),
    prezzo_unitario NUMERIC(10,2)   NOT NULL CHECK (prezzo_unitario >= 0),
    subtotale       NUMERIC(10,2)   GENERATED ALWAYS AS (quantita * prezzo_unitario) STORED
);

-- ---------------------------------------------------------------
-- SHOPPING CART (persisted, linked to customer)
-- ---------------------------------------------------------------
CREATE TABLE mercato.carrello (
    id              SERIAL PRIMARY KEY,
    cliente_id      INTEGER         NOT NULL REFERENCES mercato.cliente(id),
    prodotto_id     INTEGER         NOT NULL REFERENCES mercato.prodotto(id),
    quantita        INTEGER         NOT NULL CHECK (quantita > 0),
    aggiunto_il     TIMESTAMP       NOT NULL DEFAULT NOW(),
    UNIQUE (cliente_id, prodotto_id)
);

-- ---------------------------------------------------------------
-- INDEXES
-- ---------------------------------------------------------------
CREATE INDEX idx_prodotto_categoria  ON mercato.prodotto(categoria_id);
CREATE INDEX idx_prodotto_attivo     ON mercato.prodotto(attivo);
CREATE INDEX idx_prodotto_nome_it    ON mercato.prodotto(nome_it);
CREATE INDEX idx_ordine_cliente      ON mercato.ordine(cliente_id);
CREATE INDEX idx_ordine_stato        ON mercato.ordine(stato);
CREATE INDEX idx_carrello_cliente    ON mercato.carrello(cliente_id);

-- ---------------------------------------------------------------
-- SAMPLE CATEGORIES
-- ---------------------------------------------------------------
INSERT INTO mercato.categoria (codice, nome_it, nome_en, descrizione_it, descrizione_en) VALUES
('FRUTTA_VERDURA', 'Frutta e Verdura',    'Fruit & Vegetables',  'Prodotti freschi di stagione',    'Fresh seasonal produce'),
('CARNE_PESCE',    'Carne e Pesce',       'Meat & Fish',         'Macelleria e pescheria',          'Butcher and fishmonger'),
('FORMAGGI_SALUMI','Formaggi e Salumi',   'Cheese & Charcuterie','Latticini e affettati italiani',  'Italian dairy and cold cuts'),
('PANE_PASTA',     'Pane e Pasta',        'Bread & Pasta',       'Panetteria e pasta fresca',       'Bakery and fresh pasta'),
('BEVANDE',        'Bevande',             'Beverages',           'Acqua, vini, birre e succhi',     'Water, wines, beers and juices'),
('SURGELATI',      'Surgelati',           'Frozen Foods',        'Prodotti surgelati',              'Frozen products'),
('PULIZIA_CASA',   'Pulizia Casa',        'Home Cleaning',       'Detergenti e prodotti per casa',  'Detergents and home products'),
('IGIENE_PERSONA', 'Igiene Persona',      'Personal Care',       'Cura del corpo e cosmetici',      'Body care and cosmetics'),
('DISPENSA',       'Dispensa',            'Pantry',              'Olio, conserve, legumi, riso',    'Oil, preserves, pulses, rice'),
('DOLCI_SNACK',    'Dolci e Snack',       'Sweets & Snacks',     'Biscotti, cioccolato, merendine', 'Biscuits, chocolate, snacks');
