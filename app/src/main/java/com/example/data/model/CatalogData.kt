package com.example.data.model

import com.example.R

object CatalogData {
    // Prices reduced by 60% as requested by the user (-60% off original)
    val sampleProducts = listOf(
        BraceletProduct(
            id = "br_chakra_01",
            name = "Pulsera Armonía 7 Chakras",
            category = "Chakras & Energía",
            description = "Elaborada minuciosamente con 7 gemas naturales auténticas (amatista, lapislázuli, turquesa, aventurina verde, ojo de tigre, ágata roja y jaspe) combinadas con piedra volcánica difusora de aceites esenciales.",
            materials = "Piedra volcánica porosa, gemas naturales semipreciosas, separadores de aleación tibetana",
            technique = "Enfilado elástico reforzado con nudo invisible de seguridad",
            basePriceUsd = 11.99,
            originalPriceUsd = 29.99,
            drawableResId = R.drawable.img_bracelet_chakra,
            rating = 4.9,
            reviewsCount = 142,
            availableColors = listOf("Volcánica Negra", "Obsidiana Brillante"),
            availableSizesCm = listOf(16.0, 18.0, 20.0, 22.0),
            tags = listOf("-60% OFF", "Bestseller", "Chakras", "Difusor")
        ),
        BraceletProduct(
            id = "br_macrame_02",
            name = "Pulsera Bohemio Macramé & Turquesa",
            category = "Macramé & Hilos",
            description = "Tejida a mano nudo a nudo por maestras artesanas utilizando técnica de micromacramé con hilo encerado impermeable de alta resistencia. Coronada con un cabujón de turquesa natural sin tratar.",
            materials = "Hilo encerado Linhasita de Brasil, gema turquesa natural, cuentas de latón dorado",
            technique = "Micromacramé trenzado a mano con nudo corredizo ajustable",
            basePriceUsd = 9.80,
            originalPriceUsd = 24.50,
            drawableResId = R.drawable.img_bracelet_macrame,
            rating = 4.8,
            reviewsCount = 98,
            availableColors = listOf("Arena Natural", "Marrón Tierra", "Turquesa Profundo", "Terracota"),
            availableSizesCm = listOf(15.0, 17.0, 19.0, 21.0),
            tags = listOf("-60% OFF", "Hecho a Mano", "Boho Chic", "Turquesa")
        ),
        BraceletProduct(
            id = "br_leather_03",
            name = "Brazalete Cuero Trenzado & Plata Tribal",
            category = "Cuero & Plata",
            description = "Elegante y robusto brazalete confeccionado en cuero curtido vegetal trenzado a cuatro cabos. Incluye una pieza central grabada con motivos florales tribales y cierre magnético de acero quirúrgico hipoalergénico.",
            materials = "Cuero genuino curtido vegetal, cilindro central grabado, acero inoxidable 316L",
            technique = "Trenzado tradicional a mano y ensamble de precisión",
            basePriceUsd = 14.40,
            originalPriceUsd = 36.00,
            drawableResId = R.drawable.img_bracelet_leather,
            rating = 4.95,
            reviewsCount = 215,
            availableColors = listOf("Marrón Café", "Negro Azabache", "Coñac Envejecido"),
            availableSizesCm = listOf(18.0, 19.5, 21.0, 22.5),
            tags = listOf("-60% OFF", "Cuero Genuino", "Plata Tribal", "Magnético")
        ),
        BraceletProduct(
            id = "br_tigereye_04",
            name = "Pulsera Ojo de Tigre & Medallón Solar",
            category = "Piedras Naturales",
            description = "Poderosa combinación de cuentas pulidas de ojo de tigre grado A con su característico efecto tornasolado y cuentas de lava mate. Destaca un medallón protector de león solar en plata envejecida.",
            materials = "Ojo de tigre natural 8mm, piedra de lava, medallón grabado acabado plata antigua",
            technique = "Enfilado doble con cordón de silicona alemana de alta tenacidad",
            basePriceUsd = 12.80,
            originalPriceUsd = 32.00,
            drawableResId = R.drawable.img_bracelet_tigereye,
            rating = 4.9,
            reviewsCount = 180,
            availableColors = listOf("Ojo de Tigre Dorado", "Ojo de Tigre Azul"),
            availableSizesCm = listOf(17.0, 19.0, 21.0),
            tags = listOf("-60% OFF", "Protección", "Ojo de Tigre", "Artesanal")
        ),
        BraceletProduct(
            id = "br_tree_05",
            name = "Pulsera Árbol de la Vida & Amatista",
            category = "Chakras & Energía",
            description = "Diseño místico que une la serenidad de la amatista violeta con el amuleto del árbol de la vida, símbolo de crecimiento, raíces profundas y conexión universal.",
            materials = "Amatista natural facetada 6mm, dije de plata esterlina 925, cordón de seda",
            technique = "Micro engarce artesanal y nudos franciscanos",
            basePriceUsd = 11.00,
            originalPriceUsd = 27.50,
            drawableResId = R.drawable.img_bracelet_chakra,
            rating = 4.75,
            reviewsCount = 76,
            availableColors = listOf("Amatista Violeta", "Cuarzo Rosa"),
            availableSizesCm = listOf(16.0, 18.0, 20.0),
            tags = listOf("-60% OFF", "Plata 925", "Amatista", "Espiritual")
        ),
        BraceletProduct(
            id = "br_boho_06",
            name = "Pulsera Multi-Hilos Macramé Sunset",
            category = "Macramé & Hilos",
            description = "Inspirada en las puestas de sol andinas, entrelaza hilos terracota, mostaza y verde olivo con pequeñas cuentas doradas de latón que brillan sutilmente con la luz natural.",
            materials = "Hilo encerado resistente al agua, mostacillas doradas",
            technique = "Macramé en telar circular con terminación en borlas",
            basePriceUsd = 8.40,
            originalPriceUsd = 21.00,
            drawableResId = R.drawable.img_bracelet_macrame,
            rating = 4.85,
            reviewsCount = 63,
            availableColors = listOf("Atardecer Andino", "Aurora Boreal"),
            availableSizesCm = listOf(15.0, 17.5, 20.0),
            tags = listOf("-60% OFF", "Waterproof", "Colores Vivos", "Verano")
        )
    )

    val categories = listOf("Todas", "Chakras & Energía", "Macramé & Hilos", "Cuero & Plata", "Piedras Naturales")

    // Customizer options with 60% price reduction
    val customBaseMaterials = listOf(
        CustomOption("macrame", "Hilo Macramé Andino", "Hilo encerado impermeable trenzado a mano", 7.20),
        CustomOption("volcanic", "Piedra Volcánica & Minerales", "Cuentas esféricas de lava y cuarzos", 9.60),
        CustomOption("leather", "Cuero Genuino Trenzado", "Cuero vacuno curtido con tintes vegetales", 10.40),
        CustomOption("miyuki", "Telar Cuentas Miyuki", "Micro cuentas de cristal japonés de alta precisión", 8.80),
        CustomOption("minimalist", "Seda Minimalista & Plata", "Cordón ultrafino de seda natural con remates de plata", 8.00)
    )

    val customStones = listOf(
        CustomOption("ojo_de_tigre", "Ojo de Tigre", "Fuerza, coraje y protección energética", 2.40),
        CustomOption("amatista", "Amatista Natural", "Calma mental, serenidad e intuición", 2.80),
        CustomOption("turquesa", "Turquesa Imperial", "Sanación, comunicación y buena fortuna", 3.20),
        CustomOption("cuarzo_rosa", "Cuarzo Rosa", "Amor incondicional, compasión y ternura", 2.00),
        CustomOption("arbol_vida", "Dije Árbol de la Vida", "Plata esterlina 925 tallada a mano", 3.60),
        CustomOption("hamsa", "Dije Mano de Fátima", "Protección contra malas energías", 3.20),
        CustomOption("loto", "Flor de Loto Sagrada", "Pureza espiritual y renacimiento", 3.00)
    )

    val customColors = listOf(
        ColorOption("negro", "Negro Ónix", 0xFF1C1917),
        ColorOption("terracota", "Terracota Rústico", 0xFF8B4513),
        ColorOption("rojo", "Rojo Carmesí", 0xFFB71C1C),
        ColorOption("turquesa", "Turquesa Caribe", 0xFF00838F),
        ColorOption("dorado", "Dorado Solar", 0xFFE6A117),
        ColorOption("verde", "Verde Esmeralda", 0xFF1B5E20),
        ColorOption("arena", "Arena Natural", 0xFFD7CCC8)
    )

    val customClasps = listOf(
        CustomOption("nudo_corredizo", "Nudo Corredizo Ajustable", "Adaptable a cualquier grosor de muñeca", 0.0),
        CustomOption("magnetico_acero", "Broche Magnético Acero", "Cierre rápido en acero inoxidable 316L", 1.60),
        CustomOption("plata_925", "Mosquetón Plata 925", "Cierre fino tradicional en plata de ley", 2.40)
    )

    // Complete, comprehensive list of all countries worldwide for international checkout
    val allCountries = listOf(
        "Afganistán", "Albania", "Alemania", "Andorra", "Angola", "Antigua y Barbuda",
        "Arabia Saudita", "Argelia", "Argentina", "Armenia", "Australia", "Austria",
        "Azerbaiyán", "Bahamas", "Bangladés", "Barbados", "Baréin", "Bélgica",
        "Belice", "Benín", "Bielorrusia", "Birmania / Myanmar", "Bolivia",
        "Bosnia y Herzegovina", "Botsuana", "Brasil", "Brunéi", "Bulgaria",
        "Burkina Faso", "Burundi", "Bután", "Cabo Verde", "Camboya", "Camerún",
        "Canadá", "Catar", "Chad", "Chile", "China", "Chipre", "Colombia",
        "Comoras", "Corea del Norte", "Corea del Sur", "Costa de Marfil",
        "Costa Rica", "Croacia", "Cuba", "Dinamarca", "Dominica", "Ecuador",
        "Egipto", "El Salvador", "Emiratos Árabes Unidos", "Eritrea", "Eslovaquia",
        "Eslovenia", "España", "Estados Unidos", "Estonia", "Eswatini", "Etiopía",
        "Filipinas", "Finlandia", "Fiyi", "Francia", "Gabón", "Gambia", "Georgia",
        "Ghana", "Granada", "Grecia", "Guatemala", "Guinea", "Guinea-Bisáu",
        "Guinea Ecuatorial", "Guyana", "Haití", "Honduras", "Hungría", "India",
        "Indonesia", "Irak", "Irán", "Irlanda", "Islandia", "Islas Marshall",
        "Islas Salomón", "Israel", "Italia", "Jamaica", "Japón", "Jordania",
        "Kazajistán", "Kenia", "Kirguistán", "Kiribati", "Kuwait", "Laos",
        "Lesoto", "Letonia", "Líbano", "Liberia", "Libia", "Liechtenstein",
        "Lituania", "Luxemburgo", "Macedonia del Norte", "Madagascar", "Malasia",
        "Malaui", "Maldivas", "Malí", "Malta", "Marruecos", "Mauricio",
        "Mauritania", "México", "Micronesia", "Moldavia", "Mónaco", "Mongolia",
        "Montenegro", "Mozambique", "Namibia", "Nauru", "Nepal", "Nicaragua",
        "Níger", "Nigeria", "Noruega", "Nueva Zelanda", "Omán", "Países Bajos",
        "Pakistán", "Palaos", "Panamá", "Papúa Nueva Guinea", "Paraguay", "Perú",
        "Polonia", "Portugal", "Puerto Rico", "Reino Unido", "República Centroafricana",
        "República Checa", "República del Congo", "República Democrática del Congo",
        "República Dominicana", "Ruanda", "Rumanía", "Rusia", "Samoa", "San Cristóbal y Nieves",
        "San Marino", "San Vicente y las Granadinas", "Santa Lucía", "Santo Tomé y Príncipe",
        "Senegal", "Serbia", "Seychelles", "Sierra Leona", "Singapur", "Siria",
        "Somalia", "Sri Lanka", "Sudáfrica", "Sudán", "Sudán del Sur", "Suecia",
        "Suiza", "Surinam", "Tailandia", "Taiwán", "Tanzania", "Tayikistán",
        "Timor Oriental", "Togo", "Tonga", "Trinidad y Tobago", "Túnez",
        "Turkmenistán", "Turquía", "Tuvalu", "Ucrania", "Uganda", "Uruguay",
        "Uzbekistán", "Vanuatu", "Vaticano", "Venezuela", "Vietnam", "Yemen",
        "Yibuti", "Zambia", "Zimbabue"
    )
}

data class CustomOption(
    val id: String,
    val name: String,
    val description: String,
    val extraPriceUsd: Double
)

data class ColorOption(
    val id: String,
    val name: String,
    val hexLong: Long
)
