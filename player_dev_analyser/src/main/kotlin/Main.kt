import java.io.File
import java.time.YearMonth
import org.knowm.xchart.SwingWrapper
import org.knowm.xchart.XYChartBuilder

fun main(args: Array<String>) {
    val files = File("player_dev_analyser/samples/boavista-fm-2020")

    val samples = files.listFiles()
            .sortedBy { it.name }
//            .take(1)
            .map { f ->
        parse(f)
    }

    val queiros = samples.map { it.first to it.second.first { it.uid==1915518341 } }.sortedBy { it.first }
    println("queiros:")
    queiros.forEach { println(it) }
    chart(queiros)

}

fun parse(f: File): Pair<YearMonth, List<Player>> {
    println(f.absolutePath)
    val (year, month) = "\\d+ \\d+".toRegex().find(f.absolutePath.toString())!!.groupValues[0]
            .split(" ")
            .let { it[0].toInt() to it[1].toInt() }
//    println("date: year:$year month:$month")

    val lines = f.readLines().filter { it.isNotEmpty() && !it.contains("----------------") }
//    println("num lines: ${lines.size}")

    val headerLine = lines.first()
    val headerLineDelimiters = headerLine.mapIndexedNotNull { index, char ->
        if(char=='|')
            index
        else
            null
    }

    val columnParsers = headerLineDelimiters.zipWithNext().map {
        val columnRange = IntRange(it.first+1, it.second-1)
        val extractColumnFromLine = { s: String -> s.substring(columnRange).trim() }
        extractColumnFromLine(headerLine) to extractColumnFromLine
    }
//            .apply { println("header: ${this.map { it.first }}") }
//    println(columnParsers)
//    println("header -> $header")
//    println("header -> ${header.size}")

    val players = lines.drop(1)
//            .take(1)
            .mapNotNull { l ->
                val attrs = columnParsers.map { (header, extractor) ->
                    header to extractor(l)
                }
                if(attrs.all { it.second.isBlank() }) {
                    System.err.println("Nothing on this line to parse: $l\n---> $attrs")
                    null
//                println("attrs -> ${attrs.size}")
                } else if(attrs.first { it.first=="Type" }.second  =="Trial") {
                    System.err.println("Skipping player on trial since some attributes will be ranges/unknown: ${attrs.first { it.first=="Name"}}")
                    null
                } else {
//                    println("attrs -> $attrs")
                    attrs
//                            .apply { println("zipped -> $this") }
                            .toMap().toPlayer()
                }
            }
    players.sortedByDescending { it.CA }.forEach {
//        println("player -> $it")
    }
    return YearMonth.of(year, month) to players
}

private fun Map<String,String>.toPlayer(): Player = Player(
        name = get("Name")!!,
        homeGrown = get("Home-Grown Status")!!,
        uid = get("UID")!!.toInt(),
        // hidden mental
        adaptability = get("Ada")!!.toInt(),
        versatility = get("Vers")!!.toInt(),
        temperament = get("Temp")!!.toInt(),
        professionalism = get("Prof")!!.toInt(),
        pressure = get("Pres")!!.toInt(),
        loyalty = get("Loy")!!.toInt(),
        importantMatches = get("Imp M")!!.toInt(),
        controversy = get("Cont")!!.toIntOrNull(),
        sportmanship = get("Spor")!!.toInt(),
//        consistency = get("Cons")!!.toIntOrNull(), // FIXME: Same header name
//        cons = get("Cons")!!.toString(), // FIXME: Same header name
        dirtyness = get("Dirt")!!.toInt(),
        ambition = get("Amb")!!.toInt(),
        injuryProneness = get("Inj Pr")!!.toInt(),

        // mentals
        determination = get("Det")!!.toInt(),


//        assmanAbility = get("Ability")!!.toIntOrNull(),
//        assmanPotential = get("Potential")!!.toIntOrNull(),

        CA = get("CA")!!.toIntOrNull(),
        PA = get("PA")!!.toIntOrNull(),

        personality = get("Personality")!!

//        worldReputation = get("WR")?.replace(",","")?.toIntOrNull() // FIXME: Same header name as "stars WR"
)

data class Player(
        val name: String,
        val uid: Int,
        val adaptability: Int,
        val versatility: Int,
        val temperament: Int,
        val injuryProneness: Int,
        val professionalism: Int,
        val pressure: Int,
        val loyalty: Int,
        val sportmanship: Int,
        val importantMatches: Int,
//        val consistency: Int, // FIXME: Same header name
//        val cons: String, // FIXME: Same header name
        val controversy: Int?,
        val dirtyness: Int,
        val ambition: Int,

        val determination: Int,

//        val assmanAbility: Int?,
//        val assmanPotential: Int?,
        val CA: Int?,
        val PA: Int?,
//        val worldReputation: Int?,
        val homeGrown: String,
        val personality: String
)


fun chart(playerHistory: List<Pair<YearMonth, Player>>) {

    val chart = XYChartBuilder()
            .width(1600)
            .height(800)
            .title(playerHistory.first().second.name)
            .xAxisTitle("Year/month")
            .yAxisTitle("Value")
            .build()

    Player::class.members
            .forEach { attr ->
                println(attr)
//        if(attr.type.name=="int") {
//            val attrHistory = playerHistory.map { (yyyymm, player) ->
//                yyyymm to attr.get(player) as Int
//            }
//            val xx =  attrHistory.map { it.first.year }.toTypedArray()
//            val yy = attrHistory.map { it.second }.toTypedArray() //doubleArrayOf(-3.0, 5.0, 9.0, 6.0, 5.0)
//            chart.addSeries(attr.name, xx.toMutableList(), yy.toMutableList())
//        }
    }

// Show it
    SwingWrapper(chart).displayChart()
}