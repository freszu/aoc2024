fun main() {
    data class ClawMachineSetup(
        val aDX: Long, val aDY: Long,
        val bDX: Long, val bDY: Long,
        val prizeX: Long, val prizeY: Long
    ) {
        /**
         * return A and B buttons press count required to reach the prize
         */
        fun solve(): Pair<Long, Long>? {
            val a = (prizeX * bDY - prizeY * bDX).toDouble() / (aDX * bDY - aDY * bDX)
            val b = (prizeX * aDY - prizeY * aDX).toDouble() / (bDX * aDY - bDY * aDX)
            return if (a.rem(1.0) == 0.0 && b.rem(1.0) == 0.0) {
                Pair(a.toLong(), b.toLong())
            } else {
                null
            }
        }
    }

    fun clawMachines(input: String): List<ClawMachineSetup> {
        val regex = Regex(
            """Button A: X\+(\d*), Y\+(\d*)\nButton B: X\+(\d*), Y\+(\d*)\nPrize: X=(\d*), Y=(\d*)"""
        )
        return input.split("\n\n")
            .map {
                val (aDX, aDY, bDX, bDY, prizeX, prizeY) = regex.find(it)!!.destructured
                ClawMachineSetup(
                    aDX.toLong(), aDY.toLong(), bDX.toLong(), bDY.toLong(), prizeX.toLong(), prizeY.toLong()
                )
            }
    }

    fun part1(input: String): Long {
        return clawMachines(input)
            .map { it.solve() ?: Pair(0L, 0L) }
            .sumOf { (aPress, bPress) -> 3 * aPress + 1 * bPress }
    }

    fun part2(input: String): Long {
        return clawMachines(input)
            .map { it.copy(prizeX = 10000000000000 + it.prizeX, prizeY = 10000000000000 + it.prizeY) }
            .map { it.solve() ?: Pair(0L, 0L) }
            .sumOf { (aPress, bPress) -> 3 * aPress + 1 * bPress }
    }

    val testInput = readInputAsString("Day13_test")
    check(part1(testInput) == 480L)

    val input = readInputAsString("Day13")
    part1(input).println()
    part2(input).println()
}
