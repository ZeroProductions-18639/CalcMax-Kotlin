package com.example.domain

import kotlin.math.*

sealed interface Node {
    fun evaluate(vars: Map<String, Double>): Double
}

class NumberNode(val value: Double) : Node {
    override fun evaluate(vars: Map<String, Double>): Double = value
}

class VariableNode(val name: String) : Node {
    override fun evaluate(vars: Map<String, Double>): Double {
        return vars[name.lowercase()] ?: when (name.lowercase()) {
            "pi", "π" -> Math.PI
            "e" -> Math.E
            "phi", "φ" -> 1.618033988749895
            "tau", "τ" -> Math.PI * 2.0
            "inf", "∞" -> Double.POSITIVE_INFINITY
            else -> 0.0
        }
    }
}

class AddNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double = left.evaluate(vars) + right.evaluate(vars)
}

class SubNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double = left.evaluate(vars) - right.evaluate(vars)
}

class MulNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double = left.evaluate(vars) * right.evaluate(vars)
}

class DivNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double {
        val r = right.evaluate(vars)
        if (r == 0.0) return Double.NaN
        return left.evaluate(vars) / r
    }
}

class ModNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double = left.evaluate(vars) % right.evaluate(vars)
}

class PowerNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double = left.evaluate(vars).pow(right.evaluate(vars))
}

class UnaryMinusNode(val child: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double = -child.evaluate(vars)
}

class FactorialNode(val child: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double {
        val v = child.evaluate(vars)
        if (v < 0.0) return Double.NaN
        return gamma(v + 1.0)
    }
}

class AndNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double =
        (left.evaluate(vars).toLong() and right.evaluate(vars).toLong()).toDouble()
}

class OrNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double =
        (left.evaluate(vars).toLong() or right.evaluate(vars).toLong()).toDouble()
}

class XorNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double =
        (left.evaluate(vars).toLong() xor right.evaluate(vars).toLong()).toDouble()
}

class NotNode(val child: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double =
        (child.evaluate(vars).toLong().inv()).toDouble()
}

class LshNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double =
        (left.evaluate(vars).toLong() shl right.evaluate(vars).toLong().toInt()).toDouble()
}

class RshNode(val left: Node, val right: Node) : Node {
    override fun evaluate(vars: Map<String, Double>): Double =
        (left.evaluate(vars).toLong() shr right.evaluate(vars).toLong().toInt()).toDouble()
}

class FunctionNode(val name: String, val args: List<Node>) : Node {
    override fun evaluate(vars: Map<String, Double>): Double {
        val evaluatedArgs = args.map { it.evaluate(vars) }
        if (evaluatedArgs.isEmpty()) return 0.0
        val useDegrees = vars["__use_degrees__"] == 1.0
        val xRaw = evaluatedArgs[0]
        val isTrig = name.lowercase() in listOf("sin", "cos", "tan")
        val x = if (isTrig && useDegrees) xRaw * Math.PI / 180.0 else xRaw
        return when (name.lowercase()) {
            "sin" -> sin(x)
            "cos" -> cos(x)
            "tan" -> {
                val t = tan(x)
                if (abs(t) > 1e15) Double.NaN else t
            }
            "asin" -> {
                val r = asin(xRaw)
                if (useDegrees) r * 180.0 / Math.PI else r
            }
            "acos" -> {
                val r = acos(xRaw)
                if (useDegrees) r * 180.0 / Math.PI else r
            }
            "atan" -> {
                val r = atan(xRaw)
                if (useDegrees) r * 180.0 / Math.PI else r
            }
            "sinh" -> sinh(x)
            "cosh" -> cosh(x)
            "tanh" -> tanh(x)
            "log" -> log10(x)
            "ln" -> ln(x)
            "sqrt" -> {
                if (x < 0.0) Double.NaN else sqrt(x)
            }
            "cbrt" -> Math.cbrt(x)
            "abs" -> abs(x)
            "floor" -> floor(x)
            "ceil" -> ceil(x)
            "round" -> round(x).toDouble()
            "exp" -> exp(x)
            "sign" -> sign(x)
            "fact", "factorial" -> {
                if (x < 0.0) Double.NaN else gamma(x + 1.0)
            }
            "gamma" -> gamma(x)
            "beta" -> {
                if (evaluatedArgs.size < 2) Double.NaN else beta(evaluatedArgs[0], evaluatedArgs[1])
            }
            "erf" -> erf(x)
            "min" -> {
                if (evaluatedArgs.size < 2) x else min(evaluatedArgs[0], evaluatedArgs[1])
            }
            "max" -> {
                if (evaluatedArgs.size < 2) x else max(evaluatedArgs[0], evaluatedArgs[1])
            }
            else -> 0.0
        }
    }
}

// Factorial/Gamma approximations
fun gamma(x: Double): Double {
    if (x <= 0.0) {
        if (x == floor(x)) return Double.NaN
        return Math.PI / (sin(Math.PI * x) * gamma(1.0 - x))
    }
    if (x < 0.5) return Math.PI / (sin(Math.PI * x) * gamma(1.0 - x))
    val y = x - 1.0
    // Lanczos approximation
    val p = doubleArrayOf(
        676.5203681218851,
        -1259.1392167224028,
        771.32342877765313,
        -176.61502916214059,
        12.507343278686905,
        -0.13857109526572012,
        9.9843695780195716e-6,
        1.5056327351493116e-7
    )
    val g = 7
    var s = 0.99999999999980993
    for (i in p.indices) {
        s += p[i] / (y + i + 1)
    }
    val t = y + g + 0.5
    return sqrt(2.0 * Math.PI) * t.pow(y + 0.5) * exp(-t) * s
}

fun beta(m: Double, n: Double): Double {
    return gamma(m) * gamma(n) / gamma(m + n)
}

fun erf(x: Double): Double {
    // A quick high-precision approximation for the error function
    val a1 = 0.254829592
    val a2 = -0.284496736
    val a3 = 1.421413741
    val a4 = -1.453152027
    val a5 = 1.061405429
    val p = 0.3275911

    val sign = if (x < 0) -1 else 1
    val absX = abs(x)

    val t = 1.0 / (1.0 + p * absX)
    val y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * exp(-absX * absX)

    return sign * y
}

class ExpressionParser(private val expr: String) {
    private var pos = 0
    private val len = expr.length

    private fun peek(): Char = if (pos < len) expr[pos] else '\u0000'
    private fun next(): Char = if (pos < len) expr[pos++] else '\u0000'

    fun parse(): Node {
        val result = parseExpression()
        if (pos < len) {
            throw IllegalArgumentException("Unexpected character at position $pos: '${expr[pos]}'")
        }
        return result
    }

    private fun parseExpression(): Node {
        return parseBitwiseOr()
    }

    private fun parseBitwiseOr(): Node {
        var node = parseBitwiseXor()
        while (true) {
            skipWhitespace()
            if (matchKeyword("OR")) {
                node = OrNode(node, parseBitwiseXor())
            } else {
                break
            }
        }
        return node
    }

    private fun parseBitwiseXor(): Node {
        var node = parseBitwiseAnd()
        while (true) {
            skipWhitespace()
            if (matchKeyword("XOR")) {
                node = XorNode(node, parseBitwiseAnd())
            } else {
                break
            }
        }
        return node
    }

    private fun parseBitwiseAnd(): Node {
        var node = parseShift()
        while (true) {
            skipWhitespace()
            if (matchKeyword("AND")) {
                node = AndNode(node, parseShift())
            } else {
                break
            }
        }
        return node
    }

    private fun parseShift(): Node {
        var node = parseArithmeticExpression()
        while (true) {
            skipWhitespace()
            if (matchKeyword("LSH")) {
                node = LshNode(node, parseArithmeticExpression())
            } else if (matchKeyword("RSH")) {
                node = RshNode(node, parseArithmeticExpression())
            } else {
                break
            }
        }
        return node
    }

    private fun parseArithmeticExpression(): Node {
        var node = parseTerm()
        while (true) {
            skipWhitespace()
            val c = peek()
            if (c == '+') {
                next()
                node = AddNode(node, parseTerm())
            } else if (c == '-') {
                next()
                node = SubNode(node, parseTerm())
            } else {
                break
            }
        }
        return node
    }

    private fun parseTerm(): Node {
        var node = parseFactor()
        while (true) {
            skipWhitespace()
            val c = peek()
            if (c == '*' || c == '×') {
                next()
                node = MulNode(node, parseFactor())
            } else if (c == '/' || c == '÷') {
                next()
                node = DivNode(node, parseFactor())
            } else if (c == '%') {
                next()
                node = ModNode(node, parseFactor())
            } else if (c == '(' || c == '[' || c == '{') {
                // Implicit multiplication, e.g., 2(3+4)
                node = MulNode(node, parseFactor())
            } else {
                // Check if we have an identifier (implied multiplication, e.g., 2x, 2pi, 3cos(x))
                val savedPos = pos
                skipWhitespace()
                val nextC = peek()
                if (nextC.isLetter() || nextC == 'π' || nextC == 'φ' || nextC == 'θ') {
                    // Let's check if it's a letter, which implies an implicit multiplication
                    node = MulNode(node, parseFactor())
                } else {
                    pos = savedPos
                    break
                }
            }
        }
        return node
    }

    private fun parseFactor(): Node {
        var node = parsePrimary()
        while (true) {
            skipWhitespace()
            val c = peek()
            if (c == '^') {
                next()
                node = PowerNode(node, parseFactor()) // Right-associative exponentiation
            } else if (c == '!') {
                next()
                node = FactorialNode(node)
            } else {
                break
            }
        }
        return node
    }

    private fun parsePrimary(): Node {
        skipWhitespace()

        if (matchKeyword("NOT")) {
            return NotNode(parseFactor())
        }

        val c = peek()

        if (c == '-') {
            next()
            return UnaryMinusNode(parseFactor())
        }
        if (c == '+') {
            next()
            return parseFactor()
        }

        if (c == '(' || c == '[' || c == '{') {
            next() // Consume bracket
            val closingChar = when (c) {
                '(' -> ')'
                '[' -> ']'
                else -> '}'
            }
            val node = parseExpression()
            skipWhitespace()
            if (peek() == closingChar) {
                next() // Consume matching bracket
            }
            return node
        }

        if (c.isDigit() || c == '.') {
            val start = pos
            var hasDot = false
            while (pos < len) {
                val current = peek()
                if (current.isDigit()) {
                    pos++
                } else if (current == '.' && !hasDot) {
                    hasDot = true
                    pos++
                } else {
                    break
                }
            }
            // Check scientific notation exponent, e.g., 1.23e-4 or 2E10
            if (peek() == 'e' || peek() == 'E') {
                val savedPos = pos
                pos++ // consume 'e'
                val expSign = peek()
                if (expSign == '+' || expSign == '-') {
                    pos++
                }
                if (peek().isDigit()) {
                    while (pos < len && peek().isDigit()) {
                        pos++
                    }
                } else {
                    pos = savedPos // roll back if not a valid scientific notation
                }
            }
            val numStr = expr.substring(start, pos)
            val value = numStr.toDoubleOrNull() ?: 0.0
            return NumberNode(value)
        }

        if (c.isLetter() || c == 'π' || c == 'φ' || c == 'θ' || c == '∞') {
            val start = pos
            while (pos < len) {
                val current = peek()
                if (current.isLetter() || current.isDigit() || current == 'π' || current == 'φ' || current == 'θ' || current == '∞') {
                    pos++
                } else {
                    break
                }
            }
            val name = expr.substring(start, pos)
            skipWhitespace()
            if (peek() == '(') {
                next() // consume '('
                val args = mutableListOf<Node>()
                if (peek() != ')') {
                    args.add(parseExpression())
                    while (peek() == ',') {
                        next() // consume ','
                        args.add(parseExpression())
                    }
                }
                if (peek() == ')') {
                    next() // consume ')'
                }
                return FunctionNode(name, args)
            }
            return VariableNode(name)
        }

        throw IllegalArgumentException("Expected expression but found character: '${if (c == '\u0000') "EOF" else c}'")
    }

    private fun skipWhitespace() {
        while (pos < len && expr[pos].isWhitespace()) {
            pos++
        }
    }

    private fun matchKeyword(keyword: String): Boolean {
        val savedPos = pos
        skipWhitespace()
        var match = true
        for (i in keyword.indices) {
            val c = peek()
            if (c.uppercaseChar() != keyword[i]) {
                match = false
                break
            }
            next()
        }
        if (match) {
            val nextC = peek()
            if (nextC.isLetter() || nextC.isDigit()) {
                match = false
            }
        }
        if (!match) {
            pos = savedPos
        }
        return match
    }

    companion object {
        fun evaluate(expression: String, vars: Map<String, Double> = emptyMap(), useDegrees: Boolean = false): Double {
            val sanitized = expression
                .replace("×", "*")
                .replace("÷", "/")
                .replace("π", "pi")
                .replace("φ", "phi")
                .replace("θ", "theta")
                .replace("√", "sqrt")
            if (sanitized.isBlank()) return 0.0
            val mutableVars = vars.toMutableMap()
            mutableVars["__use_degrees__"] = if (useDegrees) 1.0 else 0.0
            return try {
                val parser = ExpressionParser(sanitized)
                val node = parser.parse()
                node.evaluate(mutableVars)
            } catch (e: Exception) {
                Double.NaN
            }
        }

        fun preprocessProgrammerExpression(expr: String, base: String): String {
            val regex = Regex("([a-zA-Z0-9]+|[^a-zA-Z0-9\\s])")
            val matches = regex.findAll(expr)
            val builder = StringBuilder()
            
            for (match in matches) {
                val token = match.value
                val tokenUpper = token.uppercase()
                
                if (tokenUpper in listOf("AND", "OR", "XOR", "NOT", "LSH", "RSH")) {
                    builder.append(" ").append(tokenUpper).append(" ")
                } else if (isValidNumberInBase(tokenUpper, base)) {
                    try {
                        val radix = when (base) {
                            "HEX" -> 16
                            "OCT" -> 8
                            "BIN" -> 2
                            else -> 10
                        }
                        val decValue = tokenUpper.toLong(radix)
                        builder.append(" ").append(decValue.toString()).append(" ")
                    } catch (e: Exception) {
                        builder.append(token)
                    }
                } else {
                    builder.append(token)
                }
            }
            return builder.toString()
        }

        private fun isValidNumberInBase(token: String, base: String): Boolean {
            if (token.isEmpty()) return false
            return when (base) {
                "BIN" -> token.all { it == '0' || it == '1' }
                "OCT" -> token.all { it in '0'..'7' }
                "DEC" -> token.all { it.isDigit() }
                "HEX" -> token.all { it.isDigit() || it in 'A'..'F' }
                else -> token.all { it.isDigit() }
            }
        }
    }
}
