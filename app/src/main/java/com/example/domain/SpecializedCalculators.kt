package com.example.domain

import kotlin.math.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object SpecializedCalculators {

    // Helper to format doubles nicely
    fun formatVal(v: Double, precision: Int = 6): String {
        if (v.isNaN()) return "NaN"
        if (v.isInfinite()) return "Infinity"
        val df = DecimalFormat("#.######")
        df.maximumFractionDigits = precision
        return df.format(v)
    }

    // ========================================== PERCENTAGE
    fun calculatePercentageOf(percent: Double, total: Double): Double = (percent / 100.0) * total
    fun calculatePercentOneIsOfAnother(x: Double, y: Double): Double = if (y == 0.0) 0.0 else (x / y) * 100.0
    fun calculatePercentageChange(old: Double, new: Double): Double = if (old == 0.0) 0.0 else ((new - old) / old) * 100.0
    fun calculatePercentageDifference(x: Double, y: Double): Double {
        val denom = (x + y) / 2.0
        return if (denom == 0.0) 0.0 else (abs(x - y) / denom) * 100.0
    }

    // ========================================== RATIO AND PROPORTION
    fun simplifyRatio(a: Int, b: Int): Pair<Int, Int> {
        val g = gcd(a.toLong(), b.toLong()).toInt()
        return if (g == 0) Pair(a, b) else Pair(a / g, b / g)
    }
    fun solveProportion(a: Double?, b: Double, c: Double, d: Double): Double = (b * c) / d // a/b = c/d -> a = bc/d

    // ========================================== TIME AND DATE
    fun checkLeapYear(year: Long): Boolean {
        return (year % 4 == 0L && year % 100 != 0L) || (year % 400 == 0L)
    }

    // ========================================== FINANCE
    data class InterestResult(val totalAmount: Double, val interestEarned: Double)
    fun calculateSimpleInterest(principal: Double, ratePercent: Double, timeYears: Double): InterestResult {
        val interest = (principal * ratePercent * timeYears) / 100.0
        return InterestResult(principal + interest, interest)
    }
    fun calculateCompoundInterest(principal: Double, ratePercent: Double, timeYears: Double, timesCompoundedPerYear: Int): InterestResult {
        val r = ratePercent / 100.0
        val amount = principal * (1.0 + r / timesCompoundedPerYear).pow(timesCompoundedPerYear * timeYears)
        return InterestResult(amount, amount - principal)
    }
    data class EmiResult(val emi: Double, val totalInterest: Double, val totalPayment: Double)
    fun calculateEmi(loanAmount: Double, annualRatePercent: Double, tenureMonths: Double): EmiResult {
        val r = (annualRatePercent / 12.0) / 100.0
        if (r == 0.0) {
            val emi = if (tenureMonths > 0) loanAmount / tenureMonths else 0.0
            return EmiResult(emi, 0.0, loanAmount)
        }
        val emi = (loanAmount * r * (1.0 + r).pow(tenureMonths)) / ((1.0 + r).pow(tenureMonths) - 1.0)
        val totalPayment = emi * tenureMonths
        return EmiResult(emi, totalPayment - loanAmount, totalPayment)
    }
    data class ProfitLossResult(val profitLoss: Double, val profitLossPercent: Double, val isProfit: Boolean)
    fun calculateProfitLoss(costPrice: Double, sellingPrice: Double): ProfitLossResult {
        val diff = sellingPrice - costPrice
        val isProfit = diff >= 0
        val percent = if (costPrice == 0.0) 0.0 else (abs(diff) / costPrice) * 100.0
        return ProfitLossResult(abs(diff), percent, isProfit)
    }
    data class DiscountResult(val discountAmount: Double, val finalPrice: Double)
    fun calculateDiscount(originalPrice: Double, discountPercent: Double): DiscountResult {
        val discount = originalPrice * (discountPercent / 100.0)
        return DiscountResult(discount, originalPrice - discount)
    }
    data class TaxResult(val taxAmount: Double, val totalPrice: Double)
    fun calculateTax(amount: Double, taxPercent: Double, isExclusive: Boolean): TaxResult {
        return if (isExclusive) {
            val tax = amount * (taxPercent / 100.0)
            TaxResult(tax, amount + tax)
        } else {
            val originalPrice = amount / (1.0 + taxPercent / 100.0)
            TaxResult(amount - originalPrice, amount)
        }
    }

    // ========================================== HEALTH
    data class BmiResult(val score: Double, val category: String)
    fun calculateBmi(weightKg: Double, heightCm: Double): BmiResult {
        val hm = heightCm / 100.0
        if (hm == 0.0) return BmiResult(0.0, "Invalid height")
        val score = weightKg / (hm * hm)
        val category = when {
            score < 18.5 -> "Underweight"
            score < 25.0 -> "Normal"
            score < 30.0 -> "Overweight"
            else -> "Obese"
        }
        return BmiResult(score, category)
    }
    fun calculateBmr(weightKg: Double, heightCm: Double, ageYears: Int, isMale: Boolean): Double {
        // Mifflin-St Jeor Equation
        return if (isMale) {
            10.0 * weightKg + 6.25 * heightCm - 5.0 * ageYears + 5.0
        } else {
            10.0 * weightKg + 6.25 * heightCm - 5.0 * ageYears - 161.0
        }
    }

    // ========================================== NUMBER THEORY
    fun isPrime(n: Long): Boolean {
        if (n <= 1L) return false
        if (n <= 3L) return true
        if (n % 2L == 0L || n % 3L == 0L) return false
        var i = 5L
        while (i * i <= n) {
            if (n % i == 0L || n % (i + 2L) == 0L) return false
            i += 6L
        }
        return true
    }

    fun primeFactorization(n: Long): List<Long> {
        var temp = n
        val factors = mutableListOf<Long>()
        if (temp <= 1) return factors
        while (temp % 2L == 0L) {
            factors.add(2L)
            temp /= 2L
        }
        var i = 3L
        while (i * i <= temp) {
            while (temp % i == 0L) {
                factors.add(i)
                temp /= i
            }
            i += 2L
        }
        if (temp > 1L) factors.add(temp)
        return factors
    }

    fun gcd(a: Long, b: Long): Long {
        var x = abs(a)
        var y = abs(b)
        while (y != 0L) {
            val t = y
            y = x % y
            x = t
        }
        return x
    }

    fun lcm(a: Long, b: Long): Long {
        if (a == 0L || b == 0L) return 0L
        return abs(a * b) / gcd(a, b)
    }

    // ========================================== ALGEBRA SOLVERS
    data class QuadraticRoots(val root1: String, val root2: String, val discriminant: Double)
    fun solveQuadratic(a: Double, b: Double, c: Double): QuadraticRoots {
        val d = b * b - 4 * a * c
        return if (d >= 0) {
            val r1 = (-b + sqrt(d)) / (2 * a)
            val r2 = (-b - sqrt(d)) / (2 * a)
            QuadraticRoots(formatVal(r1), formatVal(r2), d)
        } else {
            val real = -b / (2 * a)
            val imag = sqrt(-d) / (2 * a)
            QuadraticRoots("${formatVal(real)} + ${formatVal(imag)}i", "${formatVal(real)} - ${formatVal(imag)}i", d)
        }
    }

    // 2x2 Simultaneous equation solver
    // a1 x + b1 y = c1
    // a2 x + b2 y = c2
    data class Linear2DResult(val x: Double, val y: Double, val determinant: Double)
    fun solveSimultaneous2D(a1: Double, b1: Double, c1: Double, a2: Double, b2: Double, c2: Double): Linear2DResult? {
        val det = a1 * b2 - b1 * a2
        if (det == 0.0) return null
        val x = (c1 * b2 - b1 * c2) / det
        val y = (a1 * c2 - c1 * a2) / det
        return Linear2DResult(x, y, det)
    }

    // ========================================== GEOMETRY (2D & 3D)
    fun circleArea(r: Double) = PI * r * r
    fun circlePerimeter(r: Double) = 2.0 * PI * r
    fun sphereVolume(r: Double) = (4.0 / 3.0) * PI * r.pow(3)
    fun cylinderVolume(r: Double, h: Double) = PI * r * r * h
    fun coneVolume(r: Double, h: Double) = (1.0 / 3.0) * PI * r * r * h

    // ========================================== STATISTICS
    data class StatsResult(
        val mean: Double,
        val median: Double,
        val modes: List<Double>,
        val range: Double,
        val variance: Double,
        val stdDev: Double
    )
    fun calculateStats(data: List<Double>): StatsResult {
        if (data.isEmpty()) return StatsResult(0.0, 0.0, emptyList(), 0.0, 0.0, 0.0)
        val sorted = data.sorted()
        val mean = data.average()
        val median = if (sorted.size % 2 == 0) {
            (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0
        } else {
            sorted[sorted.size / 2]
        }
        val range = sorted.last() - sorted.first()
        // Mode
        val freqs = data.groupingBy { it }.eachCount()
        val maxFreq = freqs.values.maxOrNull() ?: 0
        val modes = if (maxFreq <= 1) emptyList() else freqs.filter { it.value == maxFreq }.keys.toList()

        val variance = if (data.size <= 1) 0.0 else data.fold(0.0) { acc, num -> acc + (num - mean).pow(2) } / (data.size - 1)
        val stdDev = sqrt(variance)

        return StatsResult(mean, median, modes, range, variance, stdDev)
    }

    // ========================================== PROBABILITY
    fun factorial(n: Int): Double {
        if (n < 0) return Double.NaN
        var ans = 1.0
        for (i in 2..n) ans *= i
        return ans
    }
    fun permutation(n: Int, r: Int): Double {
        if (n < r || n < 0 || r < 0) return 0.0
        var ans = 1.0
        for (i in (n - r + 1)..n) ans *= i
        return ans
    }
    fun combination(n: Int, r: Int): Double {
        if (n < r || n < 0 || r < 0) return 0.0
        val actualR = min(r, n - r)
        var ans = 1.0
        for (i in 1..actualR) {
            ans = ans * (n - i + 1) / i
        }
        return ans
    }

    // ========================================== MATRIX OPERATIONS
    fun matrixAddition(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray>? {
        if (a.size != b.size || a[0].size != b[0].size) return null
        val r = Array(a.size) { DoubleArray(a[0].size) }
        for (i in a.indices) {
            for (j in a[0].indices) {
                r[i][j] = a[i][j] + b[i][j]
            }
        }
        return r
    }

    fun matrixMultiplication(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray>? {
        if (a[0].size != b.size) return null
        val r = Array(a.size) { DoubleArray(b[0].size) }
        for (i in a.indices) {
            for (j in b[0].indices) {
                var sum = 0.0
                for (k in a[0].indices) {
                    sum += a[i][k] * b[k][j]
                }
                r[i][j] = sum
            }
        }
        return r
    }

    fun matrixTranspose(a: Array<DoubleArray>): Array<DoubleArray> {
        val r = Array(a[0].size) { DoubleArray(a.size) }
        for (i in a.indices) {
            for (j in a[0].indices) {
                r[j][i] = a[i][j]
            }
        }
        return r
    }

    fun matrixDeterminant2x2(a: Array<DoubleArray>): Double {
        return a[0][0] * a[1][1] - a[0][1] * a[1][0]
    }

    fun matrixDeterminant3x3(a: Array<DoubleArray>): Double {
        return a[0][0]*(a[1][1]*a[2][2] - a[1][2]*a[2][1]) -
               a[0][1]*(a[1][0]*a[2][2] - a[1][2]*a[2][0]) +
               a[0][2]*(a[1][0]*a[2][1] - a[1][1]*a[2][0])
    }

    fun matrixInverse2x2(a: Array<DoubleArray>): Array<DoubleArray>? {
        val det = matrixDeterminant2x2(a)
        if (det == 0.0) return null
        val r = Array(2) { DoubleArray(2) }
        r[0][0] = a[1][1] / det
        r[0][1] = -a[0][1] / det
        r[1][0] = -a[1][0] / det
        r[1][1] = a[0][0] / det
        return r
    }

    // ========================================== VECTOR OPERATIONS
    fun vectorMagnitude(v: DoubleArray): Double = sqrt(v.fold(0.0) { acc, num -> acc + num * num })
    fun vectorDotProduct(a: DoubleArray, b: DoubleArray): Double {
        if (a.size != b.size) return 0.0
        var dot = 0.0
        for (i in a.indices) dot += a[i] * b[i]
        return dot
    }
    fun vectorCrossProduct3D(a: DoubleArray, b: DoubleArray): DoubleArray {
        val r = DoubleArray(3)
        r[0] = a[1]*b[2] - a[2]*b[1]
        r[1] = a[2]*b[0] - a[0]*b[2]
        r[2] = a[0]*b[1] - a[1]*b[0]
        return r
    }
}
