import java.util.logging.Logger
import kotlin.math.abs
import kotlin.math.sin

fun trapezoidalRule(f: (Double) -> Double, a: Double, b: Double, n: Int): Double {
    val h = (b - a) / n
    var sum = 0.5 * (f(a) + f(b))
    for (i in 1 until n) {
        val x = a + i * h
        sum += f(x)
    }
    return sum * h
}

fun simpsonsRule(f: (Double) -> Double, a: Double, b: Double, n: Int): Double {
    require(n % 2 == 0) { "n must be even" }
    val h = (b - a) / n
    var sum = f(a) + f(b)
    for (i in 1..<n) {
        val x = a + i * h
        sum += if (i % 2 == 0) 2 * f(x) else 4 * f(x)
    }
    return sum * h / 3
}

fun integrateWithRunge(
    f: (Double) -> Double,
    a: Double,
    method: Method,
    epsilon: Double,
    initialB: Double,
    initialN: Int,
    isInfinity: Boolean
): Double? {

    var B = initialB
    var n = initialN

    var I1: Double
    var I2: Double
    var error: Double
    var count = 0
    val log = Logger.getLogger("Looog")

    do {
        if (count > 20) return null
        I1 = when (method) {
            Method.TRAPEZOIDAL -> trapezoidalRule(f, a, B, n)
            Method.SIMPSONS -> simpsonsRule(f, a, B, n)
        }

        n *= 2
        if (isInfinity) B *= 2

        I2 = when (method) {
            Method.TRAPEZOIDAL -> trapezoidalRule(f, a, B, n)
            Method.SIMPSONS -> simpsonsRule(f, a, B, n)
        }

        error =
            when (method) {
                Method.TRAPEZOIDAL -> abs(I2 - I1) / 3
                Method.SIMPSONS -> abs(I2 - I1) / 15
            }
        count++
        log.info("$count")

    } while (error > epsilon)

    return I2
}

fun main() {
    val scanner = java.util.Scanner(System.`in`)

    println("Введите нижний предел интегрирования a:")
    val a = scanner.nextDouble()

    println("Введите верхний предел интегрирования b (введите 'inf' для бесконечности):")
    val bInput = scanner.next()
    val b: Double? = if (bInput == "inf") null else bInput.toDouble()

    println("Введите точность вычислений epsilon:")
    val epsilon = scanner.nextDouble()

    val initialN = 2
    val initialB: Double =
        if (b == null) {
            println("Введите начальное значение B для верхнего предела:")
            scanner.nextDouble()
        } else {
            b
        }

    println("Выберите метод интегрирования: 1 - Метод трапеций, 2 - Метод Симпсона")
    val method =
        when (scanner.nextInt()) {
            1 -> Method.TRAPEZOIDAL
            2 -> Method.SIMPSONS
            else -> Method.TRAPEZOIDAL
        }

    val f: (Double) -> Double = { x -> sin(x) }

    val result = integrateWithRunge(f, a, method, epsilon, initialB, initialN, b == null)
    if (result != null) {
        println("Приближенное значение интеграла: $result")
    } else {
        println("Интеграл не сходится")
    }
}

enum class Method {
    TRAPEZOIDAL,
    SIMPSONS
}