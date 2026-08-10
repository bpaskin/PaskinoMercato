-- =============================================================
-- PaskinoMercato — 145 Original Seed Products
-- All product names, descriptions and prices are original.
-- Currency: Euro (€). Run AFTER schema.sql.
-- =============================================================

-- Retrieve category IDs into variables via a DO block
-- so inserts are robust regardless of ID assignment order.

DO $$
DECLARE
    id_fv   INTEGER; -- Frutta e Verdura
    id_cp   INTEGER; -- Carne e Pesce
    id_fs   INTEGER; -- Formaggi e Salumi
    id_pp   INTEGER; -- Pane e Pasta
    id_bev  INTEGER; -- Bevande
    id_sur  INTEGER; -- Surgelati
    id_pul  INTEGER; -- Pulizia Casa
    id_ig   INTEGER; -- Igiene Persona
    id_dis  INTEGER; -- Dispensa
    id_ds   INTEGER; -- Dolci e Snack
BEGIN
    SELECT id INTO id_fv  FROM mercato.categoria WHERE codice = 'FRUTTA_VERDURA';
    SELECT id INTO id_cp  FROM mercato.categoria WHERE codice = 'CARNE_PESCE';
    SELECT id INTO id_fs  FROM mercato.categoria WHERE codice = 'FORMAGGI_SALUMI';
    SELECT id INTO id_pp  FROM mercato.categoria WHERE codice = 'PANE_PASTA';
    SELECT id INTO id_bev FROM mercato.categoria WHERE codice = 'BEVANDE';
    SELECT id INTO id_sur FROM mercato.categoria WHERE codice = 'SURGELATI';
    SELECT id INTO id_pul FROM mercato.categoria WHERE codice = 'PULIZIA_CASA';
    SELECT id INTO id_ig  FROM mercato.categoria WHERE codice = 'IGIENE_PERSONA';
    SELECT id INTO id_dis FROM mercato.categoria WHERE codice = 'DISPENSA';
    SELECT id INTO id_ds  FROM mercato.categoria WHERE codice = 'DOLCI_SNACK';

    -- =========================================================
    -- FRUTTA E VERDURA (15 products)
    -- =========================================================
    INSERT INTO mercato.prodotto (codice,nome_it,nome_en,descrizione_it,descrizione_en,prezzo,unita_misura,quantita_stock,categoria_id,immagine,peso_kg) VALUES
    ('FV001','Mele Golden Bio','Organic Golden Apples','Mele golden biologiche italiane, dolci e croccanti','Italian organic golden apples, sweet and crispy',2.49,'kg',120,id_fv,'fv001_mele_golden.jpg',1.0),
    ('FV002','Pomodori Ciliegino','Cherry Tomatoes','Pomodorini ciliegino freschi di stagione','Fresh seasonal cherry tomatoes',1.99,'vaschetta',95,id_fv,'fv002_ciliegino.jpg',0.5),
    ('FV003','Zucchine Chiare','Light Courgettes','Zucchine chiare fresche, ideali per grigliate','Fresh light courgettes, ideal for grilling',1.79,'kg',80,id_fv,'fv003_zucchine.jpg',1.0),
    ('FV004','Spinaci Freschi','Fresh Spinach','Spinaci freschi in busta, pronti al lavaggio','Fresh bagged spinach, ready to wash',1.59,'busta',110,id_fv,'fv004_spinaci.jpg',0.3),
    ('FV005','Arance Navel Siciliane','Sicilian Navel Oranges','Arance navel dalla Sicilia, succose e profumate','Juicy and fragrant navel oranges from Sicily',3.29,'kg',70,id_fv,'fv005_arance.jpg',1.0),
    ('FV006','Insalata Mista','Mixed Salad Bag','Misto di foglie per insalata, già lavate','Mix of salad leaves, pre-washed',1.49,'busta',130,id_fv,'fv006_insalata.jpg',0.2),
    ('FV007','Carote Novelle','Baby Carrots','Carote novelle dolci, ideali crude o cotte','Sweet baby carrots, great raw or cooked',1.29,'kg',150,id_fv,'fv007_carote.jpg',1.0),
    ('FV008','Peperoni Misti','Mixed Bell Peppers','Peperoni rossi, gialli e verdi freschi','Fresh red, yellow and green bell peppers',2.19,'confezione',65,id_fv,'fv008_peperoni.jpg',0.6),
    ('FV009','Broccoli Verdi','Green Broccoli','Broccoli verdi freschi, ricchi di vitamine','Fresh green broccoli, rich in vitamins',1.89,'cad.',88,id_fv,'fv009_broccoli.jpg',0.5),
    ('FV010','Melanzane Viola','Purple Aubergines','Melanzane viola fresche, ideali alla parmigiana','Fresh purple aubergines, ideal for parmigiana',1.99,'kg',72,id_fv,'fv010_melanzane.jpg',1.0),
    ('FV011','Fragole di Stagione','Seasonal Strawberries','Fragole fresche italiane, dolci e profumate','Fresh Italian strawberries, sweet and fragrant',3.49,'vaschetta',55,id_fv,'fv011_fragole.jpg',0.5),
    ('FV012','Limoni di Amalfi','Amalfi Lemons','Limoni sfusato amalfitano IGP, buccia spessa','Amalfi coast IGP lemons, thick-skinned',2.99,'rete',60,id_fv,'fv012_limoni.jpg',1.0),
    ('FV013','Funghi Champignon','Champignon Mushrooms','Funghi champignon freschi, in vaschetta','Fresh champignon mushrooms, in punnet',2.29,'vaschetta',85,id_fv,'fv013_funghi.jpg',0.5),
    ('FV014','Cipolle di Tropea','Tropea Red Onions','Cipolle rosse di Tropea IGP, dolci e aromatiche','Tropea IGP red onions, sweet and aromatic',2.49,'rete',90,id_fv,'fv014_cipolle.jpg',1.0),
    ('FV015','Asparagi Verdi','Green Asparagus','Asparagi verdi freschi, mazzo da 500g','Fresh green asparagus, 500g bunch',3.99,'mazzo',40,id_fv,'fv015_asparagi.jpg',0.5);

    -- =========================================================
    -- CARNE E PESCE (15 products)
    -- =========================================================
    INSERT INTO mercato.prodotto (codice,nome_it,nome_en,descrizione_it,descrizione_en,prezzo,unita_misura,quantita_stock,categoria_id,immagine,peso_kg) VALUES
    ('CP001','Petto di Pollo Fresco','Fresh Chicken Breast','Petto di pollo fresco italiano, senza ossa','Fresh Italian chicken breast, boneless',5.99,'kg',60,id_cp,'cp001_pollo.jpg',0.5),
    ('CP002','Macinato di Manzo','Minced Beef','Macinato di manzo fresco, 15% di grassi','Fresh minced beef, 15% fat',7.49,'kg',45,id_cp,'cp002_macinato.jpg',0.5),
    ('CP003','Salmone Fresco Atlantico','Fresh Atlantic Salmon','Filetto di salmone atlantico fresco, skin-on','Fresh Atlantic salmon fillet, skin-on',12.99,'kg',30,id_cp,'cp003_salmone.jpg',0.4),
    ('CP004','Fettine di Vitello','Veal Escalopes','Fettine di vitello tenere, pronte per scaloppine','Tender veal escalopes, ready for scaloppine',14.99,'kg',25,id_cp,'cp004_vitello.jpg',0.3),
    ('CP005','Gamberoni Argentini','Argentinian King Prawns','Gamberoni argentini freschi, calibro 16/20','Fresh Argentinian king prawns, size 16/20',18.99,'kg',20,id_cp,'cp005_gamberoni.jpg',0.5),
    ('CP006','Coscia di Agnello','Leg of Lamb','Coscia di agnello italiano, ottima al forno','Italian leg of lamb, excellent roasted',13.99,'kg',18,id_cp,'cp006_agnello.jpg',1.2),
    ('CP007','Bistecca di Suino','Pork Chops','Bistecche di suino italiane, già marinate','Italian pork chops, pre-marinated',6.99,'kg',50,id_cp,'cp007_suino.jpg',0.4),
    ('CP008','Merluzzo Fresco','Fresh Cod','Filetto di merluzzo fresco, delicato e saporito','Fresh cod fillet, delicate and flavourful',9.99,'kg',28,id_cp,'cp008_merluzzo.jpg',0.4),
    ('CP009','Polpo Pulito','Cleaned Octopus','Polpo già pulito, pronto per la cottura','Pre-cleaned octopus, ready to cook',11.49,'kg',22,id_cp,'cp009_polpo.jpg',0.8),
    ('CP010','Tacchino a Fette','Turkey Slices','Fettine di petto di tacchino, magre e versatili','Lean and versatile turkey breast slices',5.49,'kg',55,id_cp,'cp010_tacchino.jpg',0.4),
    ('CP011','Sarde Fresche','Fresh Sardines','Sarde fresche italiane, ottime grigliate o fritte','Fresh Italian sardines, great grilled or fried',4.99,'kg',35,id_cp,'cp011_sarde.jpg',0.5),
    ('CP012','Lonza di Maiale','Pork Loin','Lonza di maiale fresca, ottima arrosto','Fresh pork loin, excellent roasted',7.99,'kg',40,id_cp,'cp012_lonza.jpg',0.6),
    ('CP013','Cozze di Taranto','Taranto Mussels','Cozze fresche di Taranto, in rete da 1kg','Fresh Taranto mussels, 1kg net bag',5.29,'rete',30,id_cp,'cp013_cozze.jpg',1.0),
    ('CP014','Alici Fresche','Fresh Anchovies','Alici fresche italiane, ideali marinate o fritte','Fresh Italian anchovies, great marinated or fried',6.49,'kg',25,id_cp,'cp014_alici.jpg',0.5),
    ('CP015','Agnello Macinato','Minced Lamb','Macinato di agnello fresco, ideale per kofte','Fresh minced lamb, ideal for kofte',9.49,'kg',20,id_cp,'cp015_agnello_mac.jpg',0.4);

    -- =========================================================
    -- FORMAGGI E SALUMI (15 products)
    -- =========================================================
    INSERT INTO mercato.prodotto (codice,nome_it,nome_en,descrizione_it,descrizione_en,prezzo,unita_misura,quantita_stock,categoria_id,immagine,peso_kg) VALUES
    ('FS001','Parmigiano Reggiano 24 Mesi','Parmigiano Reggiano 24 months','Parmigiano Reggiano DOP stagionato 24 mesi','DOP Parmigiano Reggiano aged 24 months',24.99,'kg',40,id_fs,'fs001_parmigiano.jpg',0.3),
    ('FS002','Mozzarella di Bufala DOP','Buffalo Mozzarella DOP','Mozzarella di bufala campana DOP, 250g','Buffalo mozzarella from Campania DOP, 250g',3.99,'cad.',75,id_fs,'fs002_mozzarella.jpg',0.25),
    ('FS003','Prosciutto Crudo di Parma','Parma Ham','Prosciutto crudo di Parma DOP, affettato sottile','Thinly sliced Parma ham DOP',4.49,'busta',60,id_fs,'fs003_parma.jpg',0.1),
    ('FS004','Gorgonzola Piccante DOP','Spicy Gorgonzola DOP','Gorgonzola piccante DOP, erborinato intenso','Intense veined spicy gorgonzola DOP',3.79,'etto',50,id_fs,'fs004_gorgonzola.jpg',0.2),
    ('FS005','Salame Milano Artigianale','Milan Salami','Salame Milano artigianale, leggermente speziato','Artisan Milan salami, lightly spiced',3.29,'busta',70,id_fs,'fs005_salame.jpg',0.15),
    ('FS006','Pecorino Romano DOP','Pecorino Romano DOP','Pecorino romano DOP stagionato, sapore deciso','Aged Pecorino Romano DOP, bold flavour',9.99,'kg',35,id_fs,'fs006_pecorino.jpg',0.25),
    ('FS007','Bresaola della Valtellina','Bresaola from Valtellina','Bresaola della Valtellina IGP, magra e saporita','Bresaola from Valtellina IGP, lean and tasty',5.49,'busta',45,id_fs,'fs007_bresaola.jpg',0.1),
    ('FS008','Fontina Valle d Aosta DOP','Fontina DOP','Fontina DOP, formaggio fondente dal gusto dolce','Fontina DOP, melting cheese with sweet flavour',4.99,'etto',40,id_fs,'fs008_fontina.jpg',0.2),
    ('FS009','Mortadella Bologna IGP','Mortadella Bologna IGP','Mortadella Bologna IGP con pistacchi, affettata','Sliced Bologna IGP mortadella with pistachios',2.99,'busta',80,id_fs,'fs009_mortadella.jpg',0.2),
    ('FS010','Ricotta Fresca di Pecora','Fresh Sheep Ricotta','Ricotta fresca di pecora, cremosa e delicata','Fresh sheep ricotta, creamy and delicate',3.49,'cad.',55,id_fs,'fs010_ricotta.jpg',0.25),
    ('FS011','Pancetta Tesa Affumicata','Smoked Flat Pancetta','Pancetta tesa affumicata artigianale a fette','Artisan sliced smoked flat pancetta',3.19,'busta',65,id_fs,'fs011_pancetta.jpg',0.15),
    ('FS012','Asiago Fresco DOP','Fresh Asiago DOP','Asiago fresco DOP, morbido e latteo','Fresh Asiago DOP, soft and milky',4.29,'etto',42,id_fs,'fs012_asiago.jpg',0.2),
    ('FS013','Coppa di Testa Artigianale','Artisan Head Cheese','Coppa di testa artigianale toscana a fette','Tuscan artisan head cheese, sliced',2.79,'busta',38,id_fs,'fs013_coppa.jpg',0.15),
    ('FS014','Burrata Pugliese','Puglian Burrata','Burrata artigianale pugliese, cuore cremoso','Artisan Puglian burrata, creamy centre',2.49,'cad.',50,id_fs,'fs014_burrata.jpg',0.125),
    ('FS015','Nduja Calabrese','Calabrian Nduja','Nduja piccante calabrese in vasetto, spalmabile','Spicy spreadable Calabrian nduja in jar',4.99,'vasetto',44,id_fs,'fs015_nduja.jpg',0.2);

    -- =========================================================
    -- PANE E PASTA (15 products)
    -- =========================================================
    INSERT INTO mercato.prodotto (codice,nome_it,nome_en,descrizione_it,descrizione_en,prezzo,unita_misura,quantita_stock,categoria_id,immagine,peso_kg) VALUES
    ('PP001','Pane di Altamura DOP','Altamura Bread DOP','Pane di Altamura DOP con semola di grano duro','Altamura DOP bread with durum wheat semolina',4.49,'cad.',35,id_pp,'pp001_altamura.jpg',0.5),
    ('PP002','Spaghetti di Gragnano IGP','Spaghetti di Gragnano IGP','Spaghetti di Gragnano IGP trafilati al bronzo','Bronze-drawn Gragnano IGP spaghetti',2.29,'confezione',100,id_pp,'pp002_spaghetti.jpg',0.5),
    ('PP003','Focaccia Genovese','Genoese Focaccia','Focaccia genovese alta con olio EVO e sale grosso','Thick Genoese focaccia with EVO oil and sea salt',3.49,'cad.',30,id_pp,'pp003_focaccia.jpg',0.4),
    ('PP004','Rigatoni Integrali','Wholemeal Rigatoni','Rigatoni integrali di grano duro, trafilati al bronzo','Bronze-drawn wholemeal durum wheat rigatoni',1.99,'confezione',90,id_pp,'pp004_rigatoni.jpg',0.5),
    ('PP005','Tagliatelle all''Uovo Fresche','Fresh Egg Tagliatelle','Tagliatelle fresche all''uovo artigianali','Artisan fresh egg tagliatelle',3.29,'confezione',55,id_pp,'pp005_tagliatelle.jpg',0.25),
    ('PP006','Ciabatta Artigianale','Artisan Ciabatta','Ciabatta artigianale con pasta madre e olio EVO','Sourdough artisan ciabatta with EVO oil',2.19,'cad.',40,id_pp,'pp006_ciabatta.jpg',0.35),
    ('PP007','Penne Rigate Senatore Cappelli','Senatore Cappelli Penne','Penne rigate con grano Senatore Cappelli antico','Penne rigate with ancient Senatore Cappelli wheat',2.79,'confezione',75,id_pp,'pp007_penne.jpg',0.5),
    ('PP008','Lasagne Fresche all''Uovo','Fresh Egg Lasagne Sheets','Sfoglie per lasagne fresche, 250g','Fresh lasagne sheets, 250g',2.99,'confezione',48,id_pp,'pp008_lasagne.jpg',0.25),
    ('PP009','Grissini Torinesi Artigianali','Artisan Turin Breadsticks','Grissini torinesi artigianali all''olio d''oliva','Artisan Turin breadsticks with olive oil',2.49,'confezione',85,id_pp,'pp009_grissini.jpg',0.25),
    ('PP010','Orecchiette Pugliesi','Puglian Orecchiette','Orecchiette pugliesi artigianali di semola','Artisan Puglian orecchiette with semolina',2.59,'confezione',68,id_pp,'pp010_orecchiette.jpg',0.5),
    ('PP011','Pane ai Cereali Biologico','Organic Multigrain Bread','Pane biologico con 7 cereali e semi di girasole','Organic bread with 7 cereals and sunflower seeds',3.19,'cad.',38,id_pp,'pp011_cereali.jpg',0.5),
    ('PP012','Tortellini Ricotta e Spinaci Freschi','Fresh Ricotta Spinach Tortellini','Tortellini freschi con ricotta e spinaci','Fresh tortellini filled with ricotta and spinach',4.49,'confezione',42,id_pp,'pp012_tortellini.jpg',0.25),
    ('PP013','Linguine al Nero di Seppia','Squid Ink Linguine','Linguine al nero di seppia, colore e sapore unici','Squid ink linguine, unique colour and flavour',3.49,'confezione',50,id_pp,'pp013_linguine.jpg',0.5),
    ('PP014','Pane di Segale','Rye Bread','Pane di segale nordico con semi di carvi','Nordic rye bread with caraway seeds',2.99,'cad.',32,id_pp,'pp014_segale.jpg',0.5),
    ('PP015','Gnocchi di Patate Freschi','Fresh Potato Gnocchi','Gnocchi di patate freschi artigianali, 500g','Artisan fresh potato gnocchi, 500g',2.89,'confezione',60,id_pp,'pp015_gnocchi.jpg',0.5);

    -- =========================================================
    -- BEVANDE (15 products)
    -- =========================================================
    INSERT INTO mercato.prodotto (codice,nome_it,nome_en,descrizione_it,descrizione_en,prezzo,unita_misura,quantita_stock,categoria_id,immagine,peso_kg) VALUES
    ('BEV001','Acqua Minerale Naturale','Still Mineral Water','Acqua minerale naturale italiana, bottiglia 1.5L','Italian still mineral water, 1.5L bottle',0.49,'bottiglia',200,id_bev,'bev001_acqua.jpg',1.5),
    ('BEV002','Acqua Frizzante','Sparkling Water','Acqua minerale frizzante, bottiglia 1.5L','Sparkling mineral water, 1.5L bottle',0.59,'bottiglia',180,id_bev,'bev002_frizzante.jpg',1.5),
    ('BEV003','Succo di Arancia 100%','100% Orange Juice','Succo di arancia 100% senza zuccheri aggiunti','100% orange juice with no added sugars',2.29,'bottiglia',90,id_bev,'bev003_arancia.jpg',1.0),
    ('BEV004','Vino Chianti Classico DOCG','Chianti Classico DOCG Wine','Chianti Classico DOCG, annata 2021, 750ml','Chianti Classico DOCG, vintage 2021, 750ml',9.99,'bottiglia',55,id_bev,'bev004_chianti.jpg',1.2),
    ('BEV005','Birra Artigianale Ambrata','Craft Amber Ale','Birra artigianale italiana ambrata, 33cl, 5.2%','Italian craft amber ale, 33cl, 5.2% ABV',2.49,'bottiglia',120,id_bev,'bev005_birra.jpg',0.4),
    ('BEV006','Caffè Espresso Moka','Moka Espresso Coffee','Miscela espresso per moka, arabica e robusta, 250g','Espresso blend for moka, arabica and robusta, 250g',4.29,'confezione',95,id_bev,'bev006_caffe.jpg',0.25),
    ('BEV007','Latte Intero Fresco','Fresh Whole Milk','Latte intero fresco pastorizzato, 1L','Pasteurised fresh whole milk, 1L',1.49,'bottiglia',110,id_bev,'bev007_latte.jpg',1.0),
    ('BEV008','Prosecco DOC Extra Dry','Prosecco DOC Extra Dry','Prosecco DOC Treviso Extra Dry, 750ml, 11%','Prosecco DOC Treviso Extra Dry, 750ml, 11% ABV',7.49,'bottiglia',65,id_bev,'bev008_prosecco.jpg',1.2),
    ('BEV009','Tè Verde Bio in Foglie','Organic Green Leaf Tea','Tè verde biologico in foglie sciolte, 100g','Organic loose-leaf green tea, 100g',5.99,'confezione',48,id_bev,'bev009_te.jpg',0.1),
    ('BEV010','Aranciata Artigianale','Craft Orange Soda','Aranciata artigianale italiana con succo reale','Italian craft orange soda with real juice, 275ml',1.89,'bottiglia',100,id_bev,'bev010_aranciata.jpg',0.3),
    ('BEV011','Vino Bianco Vermentino','Vermentino White Wine','Vermentino di Sardegna DOC, fresco e floreale, 750ml','Vermentino di Sardegna DOC, fresh and floral, 750ml',8.49,'bottiglia',42,id_bev,'bev011_vermentino.jpg',1.2),
    ('BEV012','Succo ACE Carota Arancia','ACE Carrot Orange Juice','Succo misto ACE con carota, arancia e limone, 1L','ACE mixed juice with carrot, orange and lemon, 1L',2.79,'bottiglia',75,id_bev,'bev012_ace.jpg',1.0),
    ('BEV013','Limoncello Artigianale','Artisan Limoncello','Limoncello artigianale di Sorrento, 500ml, 30%','Artisan Sorrento limoncello, 500ml, 30% ABV',12.99,'bottiglia',30,id_bev,'bev013_limoncello.jpg',0.6),
    ('BEV014','Camomilla Biologica','Organic Chamomile Tea','Camomilla biologica in filtri, confezione da 20','Organic chamomile tea bags, pack of 20',2.99,'confezione',85,id_bev,'bev014_camomilla.jpg',0.04),
    ('BEV015','Birra Doppio Malto','Double Malt Beer','Birra italiana doppio malto, 66cl, 7%','Italian double malt beer, 66cl, 7% ABV',3.49,'bottiglia',70,id_bev,'bev015_doppiom.jpg',0.8);

    -- =========================================================
    -- SURGELATI (15 products)
    -- =========================================================
    INSERT INTO mercato.prodotto (codice,nome_it,nome_en,descrizione_it,descrizione_en,prezzo,unita_misura,quantita_stock,categoria_id,immagine,peso_kg) VALUES
    ('SUR001','Piselli Fini Surgelati','Frozen Fine Peas','Piselli fini surgelati, calibro extra fine, 450g','Frozen extra fine peas, 450g',1.79,'confezione',150,id_sur,'sur001_piselli.jpg',0.45),
    ('SUR002','Filetti di Merluzzo Surgelati','Frozen Cod Fillets','Filetti di merluzzo MSC surgelati, 400g','MSC frozen cod fillets, 400g',5.99,'confezione',80,id_sur,'sur002_merluzzo.jpg',0.4),
    ('SUR003','Pizza Margherita Surgelata','Frozen Margherita Pizza','Pizza margherita surgelata con pomodoro e fior di latte','Frozen margherita pizza with tomato and fior di latte',4.49,'cad.',95,id_sur,'sur003_pizza.jpg',0.35),
    ('SUR004','Spinaci in Foglia Surgelati','Frozen Leaf Spinach','Spinaci in foglia surgelati, porzioni da 450g','Frozen leaf spinach, 450g portions',1.89,'confezione',120,id_sur,'sur004_spinaci.jpg',0.45),
    ('SUR005','Gelato alla Vaniglia','Vanilla Ice Cream','Gelato artigianale alla vaniglia del Madagascar, 500ml','Artisan Madagascar vanilla ice cream, 500ml',4.99,'vaschetta',60,id_sur,'sur005_gelato.jpg',0.5),
    ('SUR006','Gamberetti Sgusciati Surgelati','Frozen Peeled Prawns','Gamberetti sgusciati surgelati MSC, 400g','MSC frozen peeled prawns, 400g',6.49,'confezione',70,id_sur,'sur006_gamberetti.jpg',0.4),
    ('SUR007','Bastoncini di Pesce','Fish Fingers','Bastoncini di merluzzo impanati, 10 pezzi, 280g','Breaded cod fish fingers, 10 pieces, 280g',3.99,'confezione',85,id_sur,'sur007_bastoncini.jpg',0.28),
    ('SUR008','Verdure Miste per Minestrone','Mixed Veg for Minestrone','Verdure miste surgelate per minestrone, 1kg','Frozen mixed vegetables for minestrone, 1kg',2.49,'confezione',100,id_sur,'sur008_minestrone.jpg',1.0),
    ('SUR009','Patatine a Bastoncino','Frozen French Fries','Patatine surgelate a bastoncino, 750g','Frozen French fries, 750g',2.79,'confezione',130,id_sur,'sur009_patatine.jpg',0.75),
    ('SUR010','Tortellini al Prosciutto Surgelati','Frozen Ham Tortellini','Tortellini al prosciutto crudo surgelati, 500g','Frozen prosciutto crudo tortellini, 500g',4.29,'confezione',75,id_sur,'sur010_tortellini.jpg',0.5),
    ('SUR011','Sorbetto al Limone','Lemon Sorbet','Sorbetto artigianale al limone di Sicilia, 500ml','Artisan Sicilian lemon sorbet, 500ml',3.99,'vaschetta',50,id_sur,'sur011_sorbetto.jpg',0.5),
    ('SUR012','Crocchette di Pollo','Chicken Nuggets','Crocchette di pollo impanate, 500g, 20 pezzi','Breaded chicken nuggets, 500g, 20 pieces',4.49,'confezione',90,id_sur,'sur012_crocchette.jpg',0.5),
    ('SUR013','Fagiolini Verdi Surgelati','Frozen Green Beans','Fagiolini verdi surgelati interi, 450g','Whole frozen green beans, 450g',1.99,'confezione',110,id_sur,'sur013_fagiolini.jpg',0.45),
    ('SUR014','Misto Mare Surgelato','Frozen Seafood Mix','Misto mare surgelato con calamari, gamberi e cozze, 500g','Frozen seafood mix with squid, prawns and mussels, 500g',7.99,'confezione',55,id_sur,'sur014_mistom.jpg',0.5),
    ('SUR015','Cannelloni Ripieni Surgelati','Frozen Stuffed Cannelloni','Cannelloni ripieni con ricotta e spinaci, 4 pezzi, 400g','Frozen stuffed cannelloni with ricotta and spinach, 4 pieces, 400g',5.49,'confezione',45,id_sur,'sur015_cannelloni.jpg',0.4);

    -- =========================================================
    -- PULIZIA CASA (10 products)
    -- =========================================================
    INSERT INTO mercato.prodotto (codice,nome_it,nome_en,descrizione_it,descrizione_en,prezzo,unita_misura,quantita_stock,categoria_id,immagine,peso_kg) VALUES
    ('PUL001','Detersivo Lavatrice Liquido','Liquid Laundry Detergent','Detersivo liquido per lavatrice, 30 lavaggi, 1.5L','Liquid laundry detergent, 30 washes, 1.5L',6.49,'bottiglia',80,id_pul,'pul001_lavatrice.jpg',1.5),
    ('PUL002','Detersivo Piatti Concentrato','Concentrated Dish Soap','Detersivo concentrato per piatti al limone, 500ml','Concentrated lemon dish soap, 500ml',1.99,'bottiglia',110,id_pul,'pul002_piatti.jpg',0.5),
    ('PUL003','Multiuso Spray Cucina','Kitchen Multi-Surface Spray','Detergente multiuso spray per cucina, sgrassante, 750ml','Kitchen degreasing multi-surface spray, 750ml',2.49,'bottiglia',95,id_pul,'pul003_multiuso.jpg',0.75),
    ('PUL004','Salviette Antibatteriche','Antibacterial Wipes','Salviette umidificate antibatteriche, 80 pezzi','Antibacterial wet wipes, 80 pieces',3.29,'confezione',100,id_pul,'pul004_salviette.jpg',0.3),
    ('PUL005','Detergente Bagno Anticalcare','Anti-Limescale Bathroom Cleaner','Detergente bagno anticalcare gel, 750ml','Anti-limescale bathroom gel cleaner, 750ml',2.79,'bottiglia',85,id_pul,'pul005_bagno.jpg',0.75),
    ('PUL006','Sacchi Immondizia Grandi','Large Bin Bags','Sacchi per immondizia grandi resistenti, 70L, 20 pezzi','Strong large bin bags, 70L, 20 pieces',3.49,'rotolo',90,id_pul,'pul006_sacchi.jpg',0.5),
    ('PUL007','Ammorbidente Concentrato','Concentrated Fabric Softener','Ammorbidente concentrato al muschio bianco, 750ml','Concentrated white musk fabric softener, 750ml',3.99,'bottiglia',75,id_pul,'pul007_ammorbidente.jpg',0.75),
    ('PUL008','Carta Igienica 3 Veli','3-Ply Toilet Paper','Carta igienica 3 veli extra morbida, 12 rotoli','Extra soft 3-ply toilet paper, 12 rolls',5.99,'confezione',120,id_pul,'pul008_carta.jpg',1.0),
    ('PUL009','Candeggina Gel','Gel Bleach','Candeggina gel con profumo, 900ml, 5% di cloro attivo','Scented gel bleach, 900ml, 5% active chlorine',1.89,'bottiglia',95,id_pul,'pul009_candeggina.jpg',0.9),
    ('PUL010','Carta da Cucina Maxi','Maxi Kitchen Roll','Carta da cucina maxi assorbente, 4 rotoli','Maxi absorbent kitchen roll, 4 rolls',4.49,'confezione',100,id_pul,'pul010_cartacucina.jpg',0.6);

    -- =========================================================
    -- IGIENE PERSONA (10 products)
    -- =========================================================
    INSERT INTO mercato.prodotto (codice,nome_it,nome_en,descrizione_it,descrizione_en,prezzo,unita_misura,quantita_stock,categoria_id,immagine,peso_kg) VALUES
    ('IG001','Shampoo Rivitalizzante','Revitalising Shampoo','Shampoo rivitalizzante con olio di argan, 400ml','Revitalising shampoo with argan oil, 400ml',4.99,'bottiglia',80,id_ig,'ig001_shampoo.jpg',0.4),
    ('IG002','Dentifricio Sbiancante','Whitening Toothpaste','Dentifricio sbiancante con fluoro, 75ml, protezione 12h','Whitening toothpaste with fluoride, 75ml, 12h protection',2.99,'tubo',110,id_ig,'ig002_dentifricio.jpg',0.075),
    ('IG003','Crema Idratante Viso','Face Moisturiser','Crema idratante viso quotidiana con SPF 15, 50ml','Daily face moisturiser with SPF 15, 50ml',7.99,'tubetto',55,id_ig,'ig003_crema.jpg',0.05),
    ('IG004','Deodorante Roll-On 48h','48h Roll-On Deodorant','Deodorante roll-on 48h senza alcool e parabeni, 50ml','48h roll-on deodorant, alcohol and paraben free, 50ml',3.49,'cad.',90,id_ig,'ig004_deodorante.jpg',0.05),
    ('IG005','Rasoio Monouso 3 Lame','3-Blade Disposable Razors','Rasoio monouso a 3 lame con gel idratante, 5 pezzi','3-blade disposable razor with moisturising gel, 5 pieces',4.29,'confezione',75,id_ig,'ig005_rasoio.jpg',0.1),
    ('IG006','Sapone Liquido Mani','Liquid Hand Soap','Sapone liquido mani con aloe vera, ricarica 500ml','Liquid hand soap with aloe vera, refill 500ml',2.49,'bottiglia',100,id_ig,'ig006_sapone.jpg',0.5),
    ('IG007','Assorbenti Ultra Sottili','Ultra Thin Sanitary Pads','Assorbenti ultra sottili con ali, 14 pezzi','Ultra thin sanitary pads with wings, 14 pieces',3.99,'confezione',85,id_ig,'ig007_assorbenti.jpg',0.1),
    ('IG008','Balsamo Dopobarba','Aftershave Balm','Balsamo dopobarba idratante e lenitivo, 100ml','Moisturising and soothing aftershave balm, 100ml',5.49,'tubetto',45,id_ig,'ig008_dopobarba.jpg',0.1),
    ('IG009','Spazzolino Soft','Soft Toothbrush','Spazzolino a setole morbide con manico ergonomico','Soft-bristle toothbrush with ergonomic handle',1.99,'cad.',130,id_ig,'ig009_spazzolino.jpg',0.02),
    ('IG010','Crema Mani Nutriente','Nourishing Hand Cream','Crema mani nutriente con burro di karitè, 75ml','Nourishing hand cream with shea butter, 75ml',3.29,'tubo',70,id_ig,'ig010_cremamani.jpg',0.075);

    -- =========================================================
    -- DISPENSA (15 products)
    -- =========================================================
    INSERT INTO mercato.prodotto (codice,nome_it,nome_en,descrizione_it,descrizione_en,prezzo,unita_misura,quantita_stock,categoria_id,immagine,peso_kg) VALUES
    ('DIS001','Olio EVO Toscano DOP','Tuscan DOP Extra Virgin Olive Oil','Olio extravergine di oliva toscano DOP, 750ml','Tuscan DOP extra virgin olive oil, 750ml',12.99,'bottiglia',70,id_dis,'dis001_olio.jpg',0.75),
    ('DIS002','Pomodori Pelati San Marzano DOP','San Marzano DOP Peeled Tomatoes','Pomodori pelati San Marzano DOP, lattina 400g','San Marzano DOP peeled tomatoes, 400g tin',2.49,'lattina',120,id_dis,'dis002_pelati.jpg',0.4),
    ('DIS003','Passata di Pomodoro Artigianale','Artisan Tomato Passata','Passata di pomodoro artigianale, bottiglia 700ml','Artisan tomato passata, 700ml bottle',2.19,'bottiglia',110,id_dis,'dis003_passata.jpg',0.7),
    ('DIS004','Riso Carnaroli','Carnaroli Rice','Riso Carnaroli italiano, ideale per risotto, 1kg','Italian Carnaroli rice, ideal for risotto, 1kg',3.99,'confezione',85,id_dis,'dis004_riso.jpg',1.0),
    ('DIS005','Fagioli Borlotti in Barattolo','Jarred Borlotti Beans','Fagioli borlotti cotti al naturale, 400g','Naturally cooked borlotti beans, 400g jar',1.49,'barattolo',130,id_dis,'dis005_fagioli.jpg',0.4),
    ('DIS006','Aceto Balsamico di Modena IGP','Balsamic Vinegar of Modena IGP','Aceto balsamico di Modena IGP invecchiato, 250ml','Aged balsamic vinegar of Modena IGP, 250ml',4.99,'bottiglia',65,id_dis,'dis006_balsamico.jpg',0.25),
    ('DIS007','Tonno in Olio di Oliva','Tuna in Olive Oil','Tonno pinna gialla in olio di oliva, 3x80g','Yellowfin tuna in olive oil, 3x80g',4.29,'multipack',90,id_dis,'dis007_tonno.jpg',0.24),
    ('DIS008','Miele di Acacia Bio','Organic Acacia Honey','Miele di acacia biologico italiano, 500g','Italian organic acacia honey, 500g',6.99,'vasetto',55,id_dis,'dis008_miele.jpg',0.5),
    ('DIS009','Lenticchie di Castelluccio IGP','Castelluccio Lentils IGP','Lenticchie di Castelluccio di Norcia IGP, 400g','Castelluccio di Norcia IGP lentils, 400g',3.49,'confezione',70,id_dis,'dis009_lenticchie.jpg',0.4),
    ('DIS010','Sale Marino Integrale di Cervia','Cervia Whole Sea Salt','Sale marino integrale di Cervia, macinatura fine, 1kg','Whole sea salt from Cervia, finely ground, 1kg',2.79,'confezione',95,id_dis,'dis010_sale.jpg',1.0),
    ('DIS011','Concentrato di Pomodoro','Tomato Puree','Concentrato di pomodoro doppio, tubetto 200g','Double tomato puree concentrate, 200g tube',1.29,'tubetto',150,id_dis,'dis011_concentrato.jpg',0.2),
    ('DIS012','Farina 00 Tipo Artigianale','Artisan Type 00 Flour','Farina tipo 00 per pizza e pasta, 1kg','Type 00 flour for pizza and pasta, 1kg',1.79,'confezione',120,id_dis,'dis012_farina.jpg',1.0),
    ('DIS013','Ceci in Barattolo','Jarred Chickpeas','Ceci cotti al naturale, 400g','Naturally cooked chickpeas, 400g jar',1.39,'barattolo',125,id_dis,'dis013_ceci.jpg',0.4),
    ('DIS014','Olive Taggiasche in Olio EVO','Taggiasca Olives in EVO Oil','Olive taggiasche denocciolate in olio EVO, 190g','Pitted Taggiasca olives in EVO oil, 190g',4.49,'vasetto',60,id_dis,'dis014_olive.jpg',0.19),
    ('DIS015','Zucchero di Canna Integrale','Whole Cane Sugar','Zucchero di canna integrale biologico, 500g','Organic whole cane sugar, 500g',2.99,'confezione',88,id_dis,'dis015_zucchero.jpg',0.5);

    -- =========================================================
    -- DOLCI E SNACK (20 products)
    -- =========================================================
    INSERT INTO mercato.prodotto (codice,nome_it,nome_en,descrizione_it,descrizione_en,prezzo,unita_misura,quantita_stock,categoria_id,immagine,peso_kg) VALUES
    ('DS001','Cioccolato Fondente 70%','70% Dark Chocolate','Tavoletta cioccolato fondente 70% cacao, 100g','70% cocoa dark chocolate bar, 100g',2.99,'cad.',100,id_ds,'ds001_fondente.jpg',0.1),
    ('DS002','Biscotti Frollini Burro','Butter Shortbread Biscuits','Biscotti frollini al burro artigianali, 300g','Artisan butter shortbread biscuits, 300g',3.29,'confezione',90,id_ds,'ds002_frollini.jpg',0.3),
    ('DS003','Panettone Artigianale','Artisan Panettone','Panettone artigianale con uvetta e canditi, 750g','Artisan panettone with raisins and candied peel, 750g',14.99,'cad.',25,id_ds,'ds003_panettone.jpg',0.75),
    ('DS004','Crackers Integrali','Wholemeal Crackers','Crackers integrali croccanti con semi di lino, 250g','Crispy wholemeal crackers with flaxseeds, 250g',2.19,'confezione',110,id_ds,'ds004_crackers.jpg',0.25),
    ('DS005','Torrone al Miele e Noci','Honey Walnut Nougat','Torrone artigianale duro al miele e noci, 200g','Hard artisan nougat with honey and walnuts, 200g',5.49,'cad.',45,id_ds,'ds005_torrone.jpg',0.2),
    ('DS006','Wafer alla Nocciola','Hazelnut Wafers','Wafer alla crema di nocciola, 250g, 10 pezzi','Hazelnut cream wafers, 250g, 10 pieces',2.49,'confezione',95,id_ds,'ds006_wafer.jpg',0.25),
    ('DS007','Amaretti Morbidi di Saronno','Soft Amaretti from Saronno','Amaretti morbidi di Saronno artigianali, 200g','Artisan soft amaretti from Saronno, 200g',4.99,'confezione',55,id_ds,'ds007_amaretti.jpg',0.2),
    ('DS008','Patatine Ondulate al Sale','Ridged Salted Crisps','Patatine ondulate al sale, 150g, non-GMO','Ridged salted crisps, 150g, non-GMO',1.99,'confezione',130,id_ds,'ds008_patatine.jpg',0.15),
    ('DS009','Cioccolato al Latte con Nocciole','Milk Chocolate with Hazelnuts','Tavoletta cioccolato al latte con nocciole intere, 100g','Milk chocolate bar with whole hazelnuts, 100g',2.49,'cad.',115,id_ds,'ds009_latte.jpg',0.1),
    ('DS010','Cantucci Toscani','Tuscan Cantuccini','Cantucci alle mandorle toscani artigianali, 250g','Artisan Tuscan almond cantuccini, 250g',3.79,'confezione',70,id_ds,'ds010_cantucci.jpg',0.25),
    ('DS011','Taralli Pugliesi al Finocchio','Fennel Pugliese Taralli','Taralli pugliesi artigianali al finocchio, 300g','Artisan Puglian fennel taralli, 300g',2.99,'confezione',80,id_ds,'ds011_taralli.jpg',0.3),
    ('DS012','Cialde di Riso Soffiato','Puffed Rice Cakes','Cialde di riso soffiato senza glutine, 130g, 10 pezzi','Gluten-free puffed rice cakes, 130g, 10 pieces',2.29,'confezione',95,id_ds,'ds012_riso.jpg',0.13),
    ('DS013','Nutella Artigianale alla Nocciola','Artisan Hazelnut Spread','Crema spalmabile artigianale alle nocciole, 400g','Artisan hazelnut spread, 400g',5.99,'vasetto',85,id_ds,'ds013_crema.jpg',0.4),
    ('DS014','Cioccolatini Assortiti','Assorted Chocolates','Cioccolatini assortiti artigianali in scatola, 250g','Assorted artisan chocolates in box, 250g',8.99,'scatola',38,id_ds,'ds014_cioccolatini.jpg',0.25),
    ('DS015','Merendine al Cacao','Cocoa Snack Cakes','Merendine soffici al cacao con ripieno cremoso, 6 pezzi','Soft cocoa snack cakes with creamy filling, 6 pieces',2.99,'confezione',100,id_ds,'ds015_merendine.jpg',0.27),
    ('DS016','Frutta Secca Mista Premium','Premium Mixed Dried Fruit and Nuts','Mix premium di noci, mandorle, anacardi e uvetta, 200g','Premium mix of walnuts, almonds, cashews and raisins, 200g',4.79,'confezione',75,id_ds,'ds016_fruttasecca.jpg',0.2),
    ('DS017','Baci di Dama Artigianali','Artisan Baci di Dama','Baci di dama piemontesi con nocciola e cioccolato, 180g','Piedmontese artisan baci di dama with hazelnut and chocolate, 180g',5.29,'confezione',48,id_ds,'ds017_bacidama.jpg',0.18),
    ('DS018','Chips di Verdure Miste','Mixed Vegetable Crisps','Chips di verdure miste disidratate, 80g','Mixed dehydrated vegetable crisps, 80g',3.49,'confezione',60,id_ds,'ds018_verdure.jpg',0.08),
    ('DS019','Pandoro Artigianale','Artisan Pandoro','Pandoro artigianale veronese con zucchero a velo, 750g','Artisan Veronese pandoro with icing sugar, 750g',13.99,'cad.',22,id_ds,'ds019_pandoro.jpg',0.75),
    ('DS020','Popcorn al Caramello','Caramel Popcorn','Popcorn al caramello croccante, 90g','Crunchy caramel popcorn, 90g',2.19,'confezione',90,id_ds,'ds020_popcorn.jpg',0.09);

END $$;
