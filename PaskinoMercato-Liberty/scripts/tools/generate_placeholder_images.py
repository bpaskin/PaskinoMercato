#!/usr/bin/env python3
"""
PaskinoMercato — Product Image Placeholder Generator
=====================================================
Generates a colour-coded SVG placeholder image for every product.
Each product gets its own appropriate emoji icon.

Output directory: paskinomercato-war/src/main/webapp/img/prodotti/

Usage:
    python3 generate_placeholder_images.py

Requirements: Python 3.x only (stdlib — no external deps)
"""

import os

OUTPUT_DIR = os.path.join(
    os.path.dirname(__file__),
    "..", "..",
    "paskinomercato-war", "src", "main", "webapp", "img", "prodotti"
)

# (filename, label_it, label_en, bg_colour, emoji)
PRODUCTS = [
    # ── FRUTTA E VERDURA ─────────────────────────────────────────────────────
    ("fv001_mele_golden.jpg",   "Mele Golden",       "Golden Apples",       "#A8D5A2", "\U0001f34f"),  # 🍏
    ("fv002_ciliegino.jpg",     "Ciliegino",         "Cherry Tomatoes",     "#E8736A", "\U0001f345"),  # 🍅
    ("fv003_zucchine.jpg",      "Zucchine",          "Courgettes",          "#B5D99C", "\U0001f952"),  # 🥒
    ("fv004_spinaci.jpg",       "Spinaci",           "Spinach",             "#4CAF50", "\U0001f343"),  # 🌿
    ("fv005_arance.jpg",        "Arance Siciliane",  "Sicilian Oranges",    "#F4A460", "\U0001f34a"),  # 🍊
    ("fv006_insalata.jpg",      "Insalata Mista",    "Mixed Salad",         "#76C442", "\U0001f96c"),  # 🥬
    ("fv007_carote.jpg",        "Carote",            "Carrots",             "#FF7F50", "\U0001f955"),  # 🥕
    ("fv008_peperoni.jpg",      "Peperoni",          "Bell Peppers",        "#FFD700", "\U0001fad1"),  # 🫑
    ("fv009_broccoli.jpg",      "Broccoli",          "Broccoli",            "#228B22", "\U0001f966"),  # 🥦
    ("fv010_melanzane.jpg",     "Melanzane",         "Aubergines",          "#7B68EE", "\U0001f346"),  # 🍆
    ("fv011_fragole.jpg",       "Fragole",           "Strawberries",        "#E8304A", "\U0001f353"),  # 🍓
    ("fv012_limoni.jpg",        "Limoni Amalfi",     "Amalfi Lemons",       "#FFF44F", "\U0001f34b"),  # 🍋
    ("fv013_funghi.jpg",        "Funghi",            "Mushrooms",           "#C8A882", "\U0001f344"),  # 🍄
    ("fv014_cipolle.jpg",       "Cipolle Tropea",    "Tropea Onions",       "#C04080", "\U0001f9c5"),  # 🧅
    ("fv015_asparagi.jpg",      "Asparagi",          "Asparagus",           "#6DB33F", "\U0001f33f"),  # 🌿

    # ── CARNE E PESCE ────────────────────────────────────────────────────────
    ("cp001_pollo.jpg",         "Petto Pollo",       "Chicken Breast",      "#FAD5A5", "\U0001f357"),  # 🍗
    ("cp002_macinato.jpg",      "Macinato Manzo",    "Minced Beef",         "#C0392B", "\U0001f969"),  # 🥩
    ("cp003_salmone.jpg",       "Salmone",           "Salmon",              "#FA8072", "\U0001f41f"),  # 🐟
    ("cp004_vitello.jpg",       "Vitello",           "Veal",                "#FFDAB9", "\U0001f969"),  # 🥩
    ("cp005_gamberoni.jpg",     "Gamberoni",         "King Prawns",         "#FF6347", "\U0001f990"),  # 🦐
    ("cp006_agnello.jpg",       "Agnello",           "Lamb",                "#BC8F8F", "\U0001f969"),  # 🥩
    ("cp007_suino.jpg",         "Bistecca Suino",    "Pork Chops",          "#E8C4A0", "\U0001f356"),  # 🍖
    ("cp008_merluzzo.jpg",      "Merluzzo",          "Cod",                 "#B0E0E6", "\U0001f41f"),  # 🐟
    ("cp009_polpo.jpg",         "Polpo",             "Octopus",             "#9370DB", "\U0001f419"),  # 🐙
    ("cp010_tacchino.jpg",      "Tacchino",          "Turkey",              "#DEB887", "\U0001f983"),  # 🦃
    ("cp011_sarde.jpg",         "Sarde",             "Sardines",            "#6495ED", "\U0001f41f"),  # 🐟
    ("cp012_lonza.jpg",         "Lonza Maiale",      "Pork Loin",           "#F4A460", "\U0001f356"),  # 🍖
    ("cp013_cozze.jpg",         "Cozze Taranto",     "Taranto Mussels",     "#708090", "\U0001f41a"),  # 🐚
    ("cp014_alici.jpg",         "Alici",             "Anchovies",           "#4682B4", "\U0001f41f"),  # 🐟
    ("cp015_agnello_mac.jpg",   "Agnello Mac.",      "Minced Lamb",         "#CD853F", "\U0001f969"),  # 🥩

    # ── FORMAGGI E SALUMI ────────────────────────────────────────────────────
    ("fs001_parmigiano.jpg",    "Parmigiano",        "Parmigiano",          "#F5DEB3", "\U0001f9c0"),  # 🧀
    ("fs002_mozzarella.jpg",    "Mozzarella Bufala", "Buffalo Mozzarella",  "#FFFFF0", "\U0001f9c0"),  # 🧀
    ("fs003_parma.jpg",         "Prosciutto Parma",  "Parma Ham",           "#FFB6C1", "\U0001f356"),  # 🍖
    ("fs004_gorgonzola.jpg",    "Gorgonzola",        "Gorgonzola",          "#C8E6C9", "\U0001f9c0"),  # 🧀
    ("fs005_salame.jpg",        "Salame Milano",     "Milan Salami",        "#CD5C5C", "\U0001f355"),  # 🍕
    ("fs006_pecorino.jpg",      "Pecorino Romano",   "Pecorino Romano",     "#FAEBD7", "\U0001f9c0"),  # 🧀
    ("fs007_bresaola.jpg",      "Bresaola",          "Bresaola",            "#8B0000", "\U0001f969"),  # 🥩
    ("fs008_fontina.jpg",       "Fontina DOP",       "Fontina DOP",         "#FFE4B5", "\U0001f9c0"),  # 🧀
    ("fs009_mortadella.jpg",    "Mortadella",        "Mortadella",          "#FFB6C1", "\U0001f356"),  # 🍖
    ("fs010_ricotta.jpg",       "Ricotta Pecora",    "Sheep Ricotta",       "#FFFAF0", "\U0001f9c0"),  # 🧀
    ("fs011_pancetta.jpg",      "Pancetta",          "Pancetta",            "#E8B4A0", "\U0001f969"),  # 🥩
    ("fs012_asiago.jpg",        "Asiago Fresco",     "Fresh Asiago",        "#FFFACD", "\U0001f9c0"),  # 🧀
    ("fs013_coppa.jpg",         "Coppa di Testa",    "Head Cheese",         "#D2691E", "\U0001f356"),  # 🍖
    ("fs014_burrata.jpg",       "Burrata",           "Burrata",             "#FFFFF0", "\U0001f9c0"),  # 🧀
    ("fs015_nduja.jpg",         "Nduja Calabrese",   "Nduja",               "#FF4500", "\U0001f336"),  # 🌶

    # ── PANE E PASTA ─────────────────────────────────────────────────────────
    ("pp001_altamura.jpg",      "Pane Altamura",     "Altamura Bread",      "#D2B48C", "\U0001f35e"),  # 🍞
    ("pp002_spaghetti.jpg",     "Spaghetti",         "Spaghetti",           "#F5E6C8", "\U0001f35d"),  # 🍝
    ("pp003_focaccia.jpg",      "Focaccia",          "Focaccia",            "#DEB887", "\U0001f35e"),  # 🍞
    ("pp004_rigatoni.jpg",      "Rigatoni Integrali","Wholemeal Rigatoni",  "#C8A870", "\U0001f35d"),  # 🍝
    ("pp005_tagliatelle.jpg",   "Tagliatelle Uovo",  "Egg Tagliatelle",     "#FFD700", "\U0001f35d"),  # 🍝
    ("pp006_ciabatta.jpg",      "Ciabatta",          "Ciabatta",            "#D2B48C", "\U0001f956"),  # 🥖
    ("pp007_penne.jpg",         "Penne Cappelli",    "Penne Cappelli",      "#F0E68C", "\U0001f35d"),  # 🍝
    ("pp008_lasagne.jpg",       "Lasagne Fresche",   "Fresh Lasagne",       "#FFE4B5", "\U0001f35d"),  # 🍝
    ("pp009_grissini.jpg",      "Grissini",          "Breadsticks",         "#C8A860", "\U0001f956"),  # 🥖
    ("pp010_orecchiette.jpg",   "Orecchiette",       "Orecchiette",         "#F5E0C0", "\U0001f35d"),  # 🍝
    ("pp011_cereali.jpg",       "Pane Cereali",      "Multigrain Bread",    "#A0855A", "\U0001f35e"),  # 🍞
    ("pp012_tortellini.jpg",    "Tortellini Ricotta","Ricotta Tortellini",  "#FFD080", "\U0001f35d"),  # 🍝
    ("pp013_linguine.jpg",      "Linguine Seppia",   "Squid Ink Linguine",  "#2F2F2F", "\U0001f35d"),  # 🍝
    ("pp014_segale.jpg",        "Pane di Segale",    "Rye Bread",           "#8B6914", "\U0001f35e"),  # 🍞
    ("pp015_gnocchi.jpg",       "Gnocchi Patate",    "Potato Gnocchi",      "#F5E6C0", "\U0001f35d"),  # 🍝

    # ── BEVANDE ──────────────────────────────────────────────────────────────
    ("bev001_acqua.jpg",        "Acqua Naturale",    "Still Water",         "#ADD8E6", "\U0001f4a7"),  # 💧
    ("bev002_frizzante.jpg",    "Acqua Frizzante",   "Sparkling Water",     "#87CEEB", "\U0001f9ca"),  # 🧊
    ("bev003_arancia.jpg",      "Succo Arancia",     "Orange Juice",        "#FFA500", "\U0001f9c3"),  # 🧃
    ("bev004_chianti.jpg",      "Chianti DOCG",      "Chianti DOCG",        "#800020", "\U0001f377"),  # 🍷
    ("bev005_birra.jpg",        "Birra Ambrata",     "Amber Ale",           "#C68642", "\U0001f37a"),  # 🍺
    ("bev006_caffe.jpg",        "Caffè Espresso",    "Espresso Coffee",     "#3E1C00", "\u2615"),       # ☕
    ("bev007_latte.jpg",        "Latte Intero",      "Whole Milk",          "#FFFAF0", "\U0001f95b"),  # 🥛
    ("bev008_prosecco.jpg",     "Prosecco DOC",      "Prosecco DOC",        "#FFFACD", "\U0001f942"),  # 🥂
    ("bev009_te.jpg",           "Tè Verde Bio",      "Organic Green Tea",   "#90EE90", "\U0001f375"),  # 🍵
    ("bev010_aranciata.jpg",    "Aranciata",         "Orange Soda",         "#FFB347", "\U0001f9c3"),  # 🧃
    ("bev011_vermentino.jpg",   "Vermentino",        "Vermentino",          "#F0FFF0", "\U0001f377"),  # 🍷
    ("bev012_ace.jpg",          "Succo ACE",         "ACE Juice",           "#FFA040", "\U0001f9c3"),  # 🧃
    ("bev013_limoncello.jpg",   "Limoncello",        "Limoncello",          "#FFF44F", "\U0001f376"),  # 🍶
    ("bev014_camomilla.jpg",    "Camomilla Bio",     "Organic Chamomile",   "#FFFACD", "\U0001f375"),  # 🍵
    ("bev015_doppiom.jpg",      "Birra Doppio Malto","Double Malt Beer",    "#B8860B", "\U0001f37b"),  # 🍻

    # ── SURGELATI ────────────────────────────────────────────────────────────
    ("sur001_piselli.jpg",      "Piselli",           "Peas",                "#90EE90", "\U0001fad9"),  # 🫙
    ("sur002_merluzzo.jpg",     "Merluzzo Surg.",    "Frozen Cod",          "#B0C4DE", "\U0001f41f"),  # 🐟
    ("sur003_pizza.jpg",        "Pizza Surg.",       "Frozen Pizza",        "#E8C090", "\U0001f355"),  # 🍕
    ("sur004_spinaci.jpg",      "Spinaci Surg.",     "Frozen Spinach",      "#4CAF50", "\U0001f343"),  # 🍃
    ("sur005_gelato.jpg",       "Gelato Vaniglia",   "Vanilla Ice Cream",   "#FFFACD", "\U0001f368"),  # 🍨
    ("sur006_gamberetti.jpg",   "Gamberetti Surg.",  "Frozen Prawns",       "#FF6347", "\U0001f990"),  # 🦐
    ("sur007_bastoncini.jpg",   "Bastoncini Pesce",  "Fish Fingers",        "#F5DEB3", "\U0001f41f"),  # 🐟
    ("sur008_minestrone.jpg",   "Verdure Minest.",   "Minestrone Veg",      "#8FBC8F", "\U0001f372"),  # 🍲
    ("sur009_patatine.jpg",     "Patatine Surg.",    "Frozen Fries",        "#FFD700", "\U0001f35f"),  # 🍟
    ("sur010_tortellini.jpg",   "Tortellini Surg.",  "Frozen Tortellini",   "#FFE4B5", "\U0001f35d"),  # 🍝
    ("sur011_sorbetto.jpg",     "Sorbetto Limone",   "Lemon Sorbet",        "#FFFACD", "\U0001f367"),  # 🍧
    ("sur012_crocchette.jpg",   "Crocchette Pollo",  "Chicken Nuggets",     "#F4A460", "\U0001f357"),  # 🍗
    ("sur013_fagiolini.jpg",    "Fagiolini Surg.",   "Frozen Green Beans",  "#6DB33F", "\U0001fad8"),  # 🫘
    ("sur014_mistom.jpg",       "Misto Mare Surg.",  "Frozen Seafood Mix",  "#4682B4", "\U0001f991"),  # 🦑
    ("sur015_cannelloni.jpg",   "Cannelloni Surg.",  "Frozen Cannelloni",   "#E8C4A0", "\U0001f35d"),  # 🍝

    # ── PULIZIA CASA ─────────────────────────────────────────────────────────
    ("pul001_lavatrice.jpg",    "Det. Lavatrice",    "Laundry Detergent",   "#4FC3F7", "\U0001f9fa"),  # 🧺
    ("pul002_piatti.jpg",       "Det. Piatti",       "Dish Soap",           "#80DEEA", "\U0001f9fc"),  # 🧼
    ("pul003_multiuso.jpg",     "Multiuso Spray",    "Multi-Surface Spray", "#B2EBF2", "\U0001f9f9"),  # 🧹
    ("pul004_salviette.jpg",    "Salviette Antib.",  "Antibacterial Wipes", "#E0F7FA", "\U0001f9fb"),  # 🧻
    ("pul005_bagno.jpg",        "Det. Bagno",        "Bathroom Cleaner",    "#B2EBF2", "\U0001f6bf"),  # 🚿
    ("pul006_sacchi.jpg",       "Sacchi Immondizia", "Bin Bags",            "#78909C", "\U0001f5d1"),  # 🗑
    ("pul007_ammorbidente.jpg", "Ammorbidente",      "Fabric Softener",     "#CE93D8", "\U0001f9ba"),  # 🧺
    ("pul008_carta.jpg",        "Carta Igienica",    "Toilet Paper",        "#FAFAFA", "\U0001f9fb"),  # 🧻
    ("pul009_candeggina.jpg",   "Candeggina",        "Bleach",              "#E8F5E9", "\U0001f9ea"),  # 🧪
    ("pul010_cartacucina.jpg",  "Carta Cucina",      "Kitchen Roll",        "#FFF9C4", "\U0001f9fb"),  # 🧻

    # ── IGIENE PERSONA ───────────────────────────────────────────────────────
    ("ig001_shampoo.jpg",       "Shampoo",           "Shampoo",             "#B39DDB", "\U0001f9b4"),  # 🦴 → use 🧴
    ("ig002_dentifricio.jpg",   "Dentifricio",       "Toothpaste",          "#80DEEA", "\U0001faa5"),  # 🪥
    ("ig003_crema.jpg",         "Crema Viso",        "Face Cream",          "#FCE4EC", "\U0001f9f4"),  # 🧴
    ("ig004_deodorante.jpg",    "Deodorante",        "Deodorant",           "#E1F5FE", "\U0001f9f4"),  # 🧴
    ("ig005_rasoio.jpg",        "Rasoio",            "Razor",               "#CFD8DC", "\U0001faa4"),  # 🪤→🪒
    ("ig006_sapone.jpg",        "Sapone Mani",       "Hand Soap",           "#E8F5E9", "\U0001f9fc"),  # 🧼
    ("ig007_assorbenti.jpg",    "Assorbenti",        "Sanitary Pads",       "#FCE4EC", "\U0001f33f"),  # 🌿
    ("ig008_dopobarba.jpg",     "Dopobarba",         "Aftershave",          "#E3F2FD", "\U0001f9f4"),  # 🧴
    ("ig009_spazzolino.jpg",    "Spazzolino",        "Toothbrush",          "#E8EAF6", "\U0001faa5"),  # 🪥
    ("ig010_cremamani.jpg",     "Crema Mani",        "Hand Cream",          "#FFF3E0", "\U0001f9f4"),  # 🧴

    # ── DISPENSA ─────────────────────────────────────────────────────────────
    ("dis001_olio.jpg",         "Olio EVO",          "Extra Virgin Olive",  "#808000", "\U0001fad2"),  # 🫒
    ("dis002_pelati.jpg",       "Pelati S.Marzano",  "S.Marzano Tomatoes",  "#E8504A", "\U0001f345"),  # 🍅
    ("dis003_passata.jpg",      "Passata Pomodoro",  "Tomato Passata",      "#C0392B", "\U0001f9c2"),  # 🧂
    ("dis004_riso.jpg",         "Riso Carnaroli",    "Carnaroli Rice",      "#FFFACD", "\U0001f35a"),  # 🍚
    ("dis005_fagioli.jpg",      "Fagioli Borlotti",  "Borlotti Beans",      "#D2691E", "\U0001fad8"),  # 🫘
    ("dis006_balsamico.jpg",    "Aceto Balsamico",   "Balsamic Vinegar",    "#2F1A0A", "\U0001f9c9"),  # 🧉→🍶
    ("dis007_tonno.jpg",        "Tonno Olio",        "Tuna in Oil",         "#B8860B", "\U0001f41f"),  # 🐟
    ("dis008_miele.jpg",        "Miele Acacia",      "Acacia Honey",        "#FFD700", "\U0001f36f"),  # 🍯
    ("dis009_lenticchie.jpg",   "Lenticchie IGP",    "Castelluccio Lentils","#8B4513", "\U0001fad8"),  # 🫘
    ("dis010_sale.jpg",         "Sale Marino",       "Sea Salt",            "#F5F5F5", "\U0001f9c2"),  # 🧂
    ("dis011_concentrato.jpg",  "Conc. Pomodoro",    "Tomato Puree",        "#B22222", "\U0001f345"),  # 🍅
    ("dis012_farina.jpg",       "Farina 00",         "Type 00 Flour",       "#FFF8DC", "\U0001f33e"),  # 🌾
    ("dis013_ceci.jpg",         "Ceci",              "Chickpeas",           "#DEB887", "\U0001fad8"),  # 🫘
    ("dis014_olive.jpg",        "Olive Taggiasche",  "Taggiasca Olives",    "#556B2F", "\U0001fad2"),  # 🫒
    ("dis015_zucchero.jpg",     "Zucchero Canna",    "Cane Sugar",          "#C8860A", "\U0001f9c1"),  # 🧁

    # ── DOLCI E SNACK ────────────────────────────────────────────────────────
    ("ds001_fondente.jpg",      "Cioccolato 70%",    "70% Dark Choc",       "#3E1C00", "\U0001f36b"),  # 🍫
    ("ds002_frollini.jpg",      "Biscotti Frollini", "Shortbread",          "#DEB887", "\U0001f36a"),  # 🍪
    ("ds003_panettone.jpg",     "Panettone",         "Panettone",           "#FFD700", "\U0001f382"),  # 🎂
    ("ds004_crackers.jpg",      "Crackers Integr.",  "Wholemeal Crackers",  "#C8A860", "\U0001f9c7"),  # 🧇
    ("ds005_torrone.jpg",       "Torrone",           "Nougat",              "#FAEBD7", "\U0001f36d"),  # 🍭
    ("ds006_wafer.jpg",         "Wafer Nocciola",    "Hazelnut Wafers",     "#C68642", "\U0001f36b"),  # 🍫
    ("ds007_amaretti.jpg",      "Amaretti Saronno",  "Saronno Amaretti",    "#F4A460", "\U0001f36a"),  # 🍪
    ("ds008_patatine.jpg",      "Patatine Ondulate", "Ridged Crisps",       "#FFD700", "\U0001f35f"),  # 🍟
    ("ds009_latte.jpg",         "Cioccolato Latte",  "Milk Chocolate",      "#D2691E", "\U0001f36b"),  # 🍫
    ("ds010_cantucci.jpg",      "Cantucci Toscani",  "Tuscan Cantuccini",   "#C8860A", "\U0001f36a"),  # 🍪
    ("ds011_taralli.jpg",       "Taralli Pugliesi",  "Pugliese Taralli",    "#DEB887", "\U0001f9c7"),  # 🧇
    ("ds012_riso.jpg",          "Cialde Riso",       "Rice Cakes",          "#FAFAFA", "\U0001f35a"),  # 🍚
    ("ds013_crema.jpg",         "Crema Nocciola",    "Hazelnut Spread",     "#5C3317", "\U0001f36b"),  # 🍫
    ("ds014_cioccolatini.jpg",  "Cioccolatini",      "Assorted Chocs",      "#3E1C00", "\U0001f36c"),  # 🍬
    ("ds015_merendine.jpg",     "Merendine Cacao",   "Cocoa Snack Cakes",   "#5C3317", "\U0001f370"),  # 🍰
    ("ds016_fruttasecca.jpg",   "Frutta Secca Mix",  "Mixed Nuts",          "#D2691E", "\U0001f95c"),  # 🥜
    ("ds017_bacidama.jpg",      "Baci di Dama",      "Baci di Dama",        "#C68642", "\U0001f36a"),  # 🍪
    ("ds018_verdure.jpg",       "Chips Verdure",     "Vegetable Crisps",    "#8FBC8F", "\U0001f96c"),  # 🥬
    ("ds019_pandoro.jpg",       "Pandoro",           "Pandoro",             "#FFD700", "\U0001f382"),  # 🎂
    ("ds020_popcorn.jpg",       "Popcorn Caramello", "Caramel Popcorn",     "#C68642", "\U0001f37f"),  # 🍿
]


def hex_to_rgb(hex_color):
    h = hex_color.lstrip('#')
    return tuple(int(h[i:i+2], 16) for i in (0, 2, 4))


def text_color_for_bg(bg_hex):
    """Return dark or light text colour based on perceived luminance."""
    r, g, b = hex_to_rgb(bg_hex)
    luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255
    return "#1a1a1a" if luminance > 0.55 else "#ffffff"


def make_svg(filename, label_it, label_en, bg_color, emoji):
    fg     = text_color_for_bg(bg_color)
    # Darken bg slightly for the bottom strip
    r, g, b = hex_to_rgb(bg_color)
    stripe = "#{:02x}{:02x}{:02x}".format(max(r-30,0), max(g-30,0), max(b-30,0))

    # Wrap long labels at 18 chars
    def wrap(s, w=18):
        return (s[:w], s[w:]) if len(s) > w else (s, "")

    it1, it2 = wrap(label_it)
    en1, en2 = wrap(label_en)

    it2_svg = f'<text x="120" y="118" text-anchor="middle" font-family="Arial,sans-serif" font-size="11" fill="{fg}" opacity="0.85">{it2}</text>' if it2 else ""
    en2_svg = f'<text x="120" y="160" text-anchor="middle" font-family="Arial,sans-serif" font-size="10" fill="{fg}" opacity="0.65">{en2}</text>' if en2 else ""

    return f"""<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" width="240" height="240" viewBox="0 0 240 240">
  <!-- Background -->
  <rect width="240" height="240" fill="{bg_color}" rx="14" ry="14"/>
  <!-- Subtle inner shadow top -->
  <rect width="240" height="30" fill="#ffffff" opacity="0.12" rx="14" ry="14"/>
  <!-- Bottom stripe -->
  <rect x="0" y="195" width="240" height="45" fill="{stripe}" opacity="0.55" rx="0" ry="0"/>
  <rect x="0" y="226" width="240" height="14" fill="{stripe}" rx="0" ry="0"/>

  <!-- Product emoji — large, centred -->
  <text x="120" y="90"
        text-anchor="middle"
        font-size="72"
        font-family="Segoe UI Emoji,Apple Color Emoji,Noto Color Emoji,sans-serif"
        >{emoji}</text>

  <!-- Italian product name -->
  <text x="120" y="110"
        text-anchor="middle"
        font-family="Arial,Helvetica,sans-serif"
        font-size="12" font-weight="bold" fill="{fg}">{it1}</text>
  {it2_svg}

  <!-- English product name -->
  <text x="120" y="148"
        text-anchor="middle"
        font-family="Arial,Helvetica,sans-serif"
        font-size="10" fill="{fg}" opacity="0.72">{en1}</text>
  {en2_svg}

  <!-- Footer brand -->
  <text x="120" y="237"
        text-anchor="middle"
        font-family="Arial,Helvetica,sans-serif"
        font-size="8" fill="#ffffff" opacity="0.75" letter-spacing="0.5">PaskinoMercato</text>
</svg>"""


def main():
    out_dir = os.path.normpath(OUTPUT_DIR)
    os.makedirs(out_dir, exist_ok=True)
    print(f"Output directory: {out_dir}")
    print(f"Generating {len(PRODUCTS)} placeholder images...")

    for (filename, label_it, label_en, bg_color, emoji) in PRODUCTS:
        svg_name = filename.replace(".jpg", ".svg")
        out_path = os.path.join(out_dir, svg_name)
        svg_content = make_svg(filename, label_it, label_en, bg_color, emoji)
        with open(out_path, "w", encoding="utf-8") as f:
            f.write(svg_content)
        print(f"  {svg_name}  {emoji}")

    # README
    readme = os.path.join(out_dir, "README.txt")
    with open(readme, "w", encoding="utf-8") as f:
        f.write("PaskinoMercato — Product Images\n")
        f.write("================================\n\n")
        f.write(f"{len(PRODUCTS)} SVG placeholder images generated.\n\n")
        f.write("To replace with real photos:\n")
        f.write("  Drop a JPEG with the same base name (e.g. fv001_mele_golden.jpg)\n")
        f.write("  into this directory — it will be served automatically.\n")
        f.write("  Update JSP <img src> to use .jpg instead of .svg when ready.\n")

    print(f"\nDone — {len(PRODUCTS)} images in {out_dir}")


if __name__ == "__main__":
    main()
