package com.example.lingonary_
data class WordTimestamp(
    val id: Int,
    val word: String,
    val startTime: Long,
    val endTime: Long,
    val definition: String
)
// Full transcript with definitions
val navidadTranscript = listOf(
    WordTimestamp(1, "Las", 0, 300, "The (Feminine Plural)"),
    WordTimestamp(2, "vacaciones", 300, 1000, "Vacations / Holidays"),
    WordTimestamp(3, "de", 1000, 1200, "Of / From"),
    WordTimestamp(4, "Navidad", 1200, 1800, "Christmas"),
    WordTimestamp(5, "duran", 1800, 2200, "Last (verb duration)"),
    WordTimestamp(6, "dos", 2200, 2500, "Two"),
    WordTimestamp(7, "semanas", 2500, 3000, "Weeks"),
    WordTimestamp(8, "y", 3000, 3100, "And"),
    WordTimestamp(9, "las", 3100, 3300, "The (Feminine Plural)"),
    WordTimestamp(10, "fiestas", 3300, 3800, "Parties / Festivities"),
    WordTimestamp(11, "más", 3800, 4000, "More / Most"),
    WordTimestamp(12, "importantes", 4000, 4800, "Important"),
    WordTimestamp(13, "son", 4800, 5000, "They are"),
    WordTimestamp(14, "Nochebuena,", 5000, 5800, "Christmas Eve"),
    WordTimestamp(15, "Navidad,", 5800, 6500, "Christmas Day"),
    WordTimestamp(16, "Nochevieja", 6500, 7200, "New Year's Eve"),
    WordTimestamp(17, "y", 7200, 7300, "And"),
    WordTimestamp(18, "Reyes.", 7300, 7800, "Three Wise Men / Epiphany"),
    WordTimestamp(19, "En", 7800, 8000, "In / On"),
    WordTimestamp(20, "las", 8000, 8200, "The"),
    WordTimestamp(21, "casas", 8200, 8700, "Houses / Homes"),
    WordTimestamp(22, "se", 8700, 8900, "One (Impersonal pronoun)"),
    WordTimestamp(23, "pone", 8900, 9200, "Puts / Sets up"),
    WordTimestamp(24, "el", 9200, 9400, "The (Masculine Singular)"),
    WordTimestamp(25, "tradicional", 9400, 10200, "Traditional"),
    WordTimestamp(26, "belén...", 10200, 11000, "Nativity Scene")
)