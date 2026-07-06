package com.example.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

data class ConverterUnit(
    val name: String,
    val symbol: String,
    val factorToBase: Double, // value * factorToBase = baseValue
    val offset: Double = 0.0  // (value + offset) * factorToBase = baseValue
)

data class ConverterCategory(
    val name: String,
    val units: List<ConverterUnit>,
    val baseUnitName: String,
    val isSpecial: Boolean = false // e.g. Temperature, Fuel, Number System
)

object UnitConverter {
    val categories = listOf(
        ConverterCategory("Length", listOf(
            ConverterUnit("Nanometer", "nm", 1e-9),
            ConverterUnit("Micrometer", "µm", 1e-6),
            ConverterUnit("Millimeter", "mm", 0.001),
            ConverterUnit("Centimeter", "cm", 0.01),
            ConverterUnit("Decimeter", "dm", 0.1),
            ConverterUnit("Meter", "m", 1.0),
            ConverterUnit("Dekameter", "dam", 10.0),
            ConverterUnit("Hectometer", "hm", 100.0),
            ConverterUnit("Kilometer", "km", 1000.0),
            ConverterUnit("Inch", "in", 0.0254),
            ConverterUnit("Foot", "ft", 0.3048),
            ConverterUnit("Yard", "yd", 0.9144),
            ConverterUnit("Chain", "ch", 20.1168),
            ConverterUnit("Furlong", "fur", 201.168),
            ConverterUnit("Mile", "mi", 1609.344),
            ConverterUnit("Nautical Mile", "nmi", 1852.0),
            ConverterUnit("Astronomical Unit", "au", 1.495978707e11),
            ConverterUnit("Light Year", "ly", 9.4607304725808e15),
            ConverterUnit("Parsec", "pc", 3.085677581491367e16)
        ), "Meter"),

        ConverterCategory("Area", listOf(
            ConverterUnit("Square Millimeter", "mm²", 1e-6),
            ConverterUnit("Square Centimeter", "cm²", 1e-4),
            ConverterUnit("Square Meter", "m²", 1.0),
            ConverterUnit("Square Kilometer", "km²", 1e6),
            ConverterUnit("Square Inch", "in²", 0.00064516),
            ConverterUnit("Square Foot", "ft²", 0.09290304),
            ConverterUnit("Square Yard", "yd²", 0.83612736),
            ConverterUnit("Acre", "ac", 4046.8564224),
            ConverterUnit("Hectare", "ha", 10000.0),
            ConverterUnit("Square Mile", "mi²", 2589988.110336)
        ), "Square Meter"),

        ConverterCategory("Volume", listOf(
            ConverterUnit("Cubic Millimeter", "mm³", 1e-9),
            ConverterUnit("Cubic Centimeter", "cm³", 1e-6),
            ConverterUnit("Milliliter", "mL", 1e-6),
            ConverterUnit("Liter", "L", 0.001),
            ConverterUnit("Cubic Meter", "m³", 1.0),
            ConverterUnit("Cubic Inch", "in³", 1.6387064e-5),
            ConverterUnit("Cubic Foot", "ft³", 0.028316846592),
            ConverterUnit("US Gallon", "gal", 0.003785411784),
            ConverterUnit("Imperial Gallon", "imp gal", 0.00454609),
            ConverterUnit("Quart", "qt", 0.000946352946),
            ConverterUnit("Pint", "pt", 0.000473176473),
            ConverterUnit("Fluid Ounce", "fl oz", 2.95735295625e-5)
        ), "Cubic Meter"),

        ConverterCategory("Mass", listOf(
            ConverterUnit("Microgram", "µg", 1e-9),
            ConverterUnit("Milligram", "mg", 1e-6),
            ConverterUnit("Gram", "g", 1e-3),
            ConverterUnit("Kilogram", "kg", 1.0),
            ConverterUnit("Metric Ton", "t", 1000.0),
            ConverterUnit("Ounce", "oz", 0.028349523125),
            ConverterUnit("Pound", "lb", 0.45359237),
            ConverterUnit("Stone", "st", 6.35029318),
            ConverterUnit("Carat", "ct", 0.0002),
            ConverterUnit("Atomic Mass Unit", "u", 1.660539066605e-27)
        ), "Kilogram"),

        ConverterCategory("Time", listOf(
            ConverterUnit("Nanosecond", "ns", 1e-9),
            ConverterUnit("Microsecond", "µs", 1e-6),
            ConverterUnit("Millisecond", "ms", 0.001),
            ConverterUnit("Second", "s", 1.0),
            ConverterUnit("Minute", "min", 60.0),
            ConverterUnit("Hour", "h", 3600.0),
            ConverterUnit("Day", "d", 86400.0),
            ConverterUnit("Week", "wk", 604800.0),
            ConverterUnit("Month", "mo", 2629746.0), // 30.4368 days
            ConverterUnit("Year", "yr", 31556952.0), // 365.2425 days
            ConverterUnit("Decade", "dec", 315569520.0),
            ConverterUnit("Century", "cen", 3155695200.0)
        ), "Second"),

        ConverterCategory("Speed", listOf(
            ConverterUnit("Meter per Second", "m/s", 1.0),
            ConverterUnit("Kilometer per Hour", "km/h", 1.0 / 3.6),
            ConverterUnit("Mile per Hour", "mph", 0.44704),
            ConverterUnit("Foot per Second", "ft/s", 0.3048),
            ConverterUnit("Knot", "kn", 0.514444),
            ConverterUnit("Mach", "M", 343.0),
            ConverterUnit("Speed of Light", "c", 299792458.0)
        ), "Meter per Second"),

        ConverterCategory("Acceleration", listOf(
            ConverterUnit("Meter per Second²", "m/s²", 1.0),
            ConverterUnit("Foot per Second²", "ft/s²", 0.3048),
            ConverterUnit("Gal", "Gal", 0.01)
        ), "Meter per Second²"),

        ConverterCategory("Temperature", listOf(
            ConverterUnit("Celsius", "°C", 1.0),
            ConverterUnit("Fahrenheit", "°F", 1.0),
            ConverterUnit("Kelvin", "K", 1.0),
            ConverterUnit("Rankine", "°R", 1.0)
        ), "Kelvin", isSpecial = true),

        ConverterCategory("Pressure", listOf(
            ConverterUnit("Pascal", "Pa", 1.0),
            ConverterUnit("Kilopascal", "kPa", 1000.0),
            ConverterUnit("Megapascal", "MPa", 1e6),
            ConverterUnit("Gigapascal", "GPa", 1e9),
            ConverterUnit("Bar", "bar", 1e5),
            ConverterUnit("Millibar", "mbar", 100.0),
            ConverterUnit("Atmosphere", "atm", 101325.0),
            ConverterUnit("PSI", "psi", 6894.757293168),
            ConverterUnit("Torr", "Torr", 133.322368421),
            ConverterUnit("Millimeter of Mercury", "mmHg", 133.322387415)
        ), "Pascal"),

        ConverterCategory("Force", listOf(
            ConverterUnit("Newton", "N", 1.0),
            ConverterUnit("Kilonewton", "kN", 1000.0),
            ConverterUnit("Dyne", "dyn", 1e-5),
            ConverterUnit("Kilogram Force", "kgf", 9.80665),
            ConverterUnit("Pound Force", "lbf", 4.44822161526)
        ), "Newton"),

        ConverterCategory("Energy", listOf(
            ConverterUnit("Joule", "J", 1.0),
            ConverterUnit("Kilojoule", "kJ", 1000.0),
            ConverterUnit("Megajoule", "MJ", 1e6),
            ConverterUnit("Calorie", "cal", 4.184),
            ConverterUnit("Kilocalorie", "kcal", 4184.0),
            ConverterUnit("Electron Volt", "eV", 1.602176634e-19),
            ConverterUnit("Watt Hour", "Wh", 3600.0),
            ConverterUnit("Kilowatt Hour", "kWh", 3.6e6)
        ), "Joule"),

        ConverterCategory("Power", listOf(
            ConverterUnit("Watt", "W", 1.0),
            ConverterUnit("Kilowatt", "kW", 1000.0),
            ConverterUnit("Megawatt", "MW", 1e6),
            ConverterUnit("Gigawatt", "GW", 1e9),
            ConverterUnit("Horsepower", "hp", 745.699871582)
        ), "Watt"),

        ConverterCategory("Angle", listOf(
            ConverterUnit("Degree", "°", 1.0),
            ConverterUnit("Radian", "rad", 180.0 / Math.PI),
            ConverterUnit("Gradian", "gon", 0.9),
            ConverterUnit("Revolution", "rev", 360.0)
        ), "Degree"),

        ConverterCategory("Frequency", listOf(
            ConverterUnit("Hertz", "Hz", 1.0),
            ConverterUnit("Kilohertz", "kHz", 1000.0),
            ConverterUnit("Megahertz", "MHz", 1e6),
            ConverterUnit("Gigahertz", "GHz", 1e9),
            ConverterUnit("Terahertz", "THz", 1e12)
        ), "Hertz"),

        ConverterCategory("Density", listOf(
            ConverterUnit("Kilogram per Cubic Meter", "kg/m³", 1.0),
            ConverterUnit("Gram per Cubic Centimeter", "g/cm³", 1000.0),
            ConverterUnit("Pound per Cubic Foot", "lb/ft³", 16.018463)
        ), "Kilogram per Cubic Meter"),

        ConverterCategory("Torque", listOf(
            ConverterUnit("Newton Meter", "N·m", 1.0),
            ConverterUnit("Pound Foot", "lb·ft", 1.3558179483),
            ConverterUnit("Pound Inch", "lb·in", 0.112984829)
        ), "Newton Meter"),

        ConverterCategory("Flow Rate", listOf(
            ConverterUnit("Liter per Second", "L/s", 0.001),
            ConverterUnit("Liter per Minute", "L/min", 1.6666667e-5),
            ConverterUnit("Liter per Hour", "L/h", 2.7777778e-7),
            ConverterUnit("Cubic Meter per Hour", "m³/h", 1.0 / 3600.0),
            ConverterUnit("US Gallon per Minute", "gpm (US)", 6.30901964e-5),
            ConverterUnit("Imperial Gallon per Minute", "gpm (UK)", 7.57682e-5)
        ), "Cubic Meter per Hour"),

        ConverterCategory("Fuel Consumption", listOf(
            ConverterUnit("Kilometer per Liter", "km/L", 1.0),
            ConverterUnit("Liter per 100 Kilometer", "L/100km", 1.0),
            ConverterUnit("Miles per Gallon (US)", "mpg (US)", 1.0),
            ConverterUnit("Miles per Gallon (UK)", "mpg (UK)", 1.0)
        ), "Kilometer per Liter", isSpecial = true),

        ConverterCategory("Electric Current", listOf(
            ConverterUnit("Ampere", "A", 1.0),
            ConverterUnit("Milliampere", "mA", 0.001),
            ConverterUnit("Microampere", "µA", 1e-6),
            ConverterUnit("Nanoampere", "nA", 1e-9),
            ConverterUnit("Kiloampere", "kA", 1000.0)
        ), "Ampere"),

        ConverterCategory("Voltage", listOf(
            ConverterUnit("Volt", "V", 1.0),
            ConverterUnit("Millivolt", "mV", 0.001),
            ConverterUnit("Microvolt", "µV", 1e-6),
            ConverterUnit("Kilovolt", "kV", 1000.0),
            ConverterUnit("Megavolt", "MV", 1e6)
        ), "Volt"),

        ConverterCategory("Resistance", listOf(
            ConverterUnit("Ohm", "Ω", 1.0),
            ConverterUnit("Kiloohm", "kΩ", 1000.0),
            ConverterUnit("Megaohm", "MΩ", 1e6),
            ConverterUnit("Gigaohm", "GΩ", 1e9)
        ), "Ohm"),

        ConverterCategory("Capacitance", listOf(
            ConverterUnit("Farad", "F", 1.0),
            ConverterUnit("Millifarad", "mF", 0.001),
            ConverterUnit("Microfarad", "µF", 1e-6),
            ConverterUnit("Nanofarad", "nF", 1e-9),
            ConverterUnit("Picofarad", "pF", 1e-12)
        ), "Farad"),

        ConverterCategory("Inductance", listOf(
            ConverterUnit("Henry", "H", 1.0),
            ConverterUnit("Millihenry", "mH", 0.001),
            ConverterUnit("Microhenry", "µH", 1e-6)
        ), "Henry"),

        ConverterCategory("Electric Charge", listOf(
            ConverterUnit("Coulomb", "C", 1.0),
            ConverterUnit("Milliampere Hour", "mAh", 3.6),
            ConverterUnit("Ampere Hour", "Ah", 3600.0)
        ), "Coulomb"),

        ConverterCategory("Electric Field", listOf(
            ConverterUnit("Volt per Meter", "V/m", 1.0),
            ConverterUnit("Newton per Coulomb", "N/C", 1.0)
        ), "Volt per Meter"),

        ConverterCategory("Magnetic Field (Flux Density)", listOf(
            ConverterUnit("Tesla", "T", 1.0),
            ConverterUnit("Gauss", "G", 1e-4)
        ), "Tesla"),

        ConverterCategory("Magnetic Flux", listOf(
            ConverterUnit("Weber", "Wb", 1.0),
            ConverterUnit("Maxwell", "Mx", 1e-8)
        ), "Weber"),

        ConverterCategory("Illuminance", listOf(
            ConverterUnit("Lux", "lx", 1.0),
            ConverterUnit("Foot Candle", "fc", 10.763910417)
        ), "Lux"),

        ConverterCategory("Luminous Flux", listOf(
            ConverterUnit("Lumen", "lm", 1.0)
        ), "Lumen"),

        ConverterCategory("Luminous Intensity", listOf(
            ConverterUnit("Candela", "cd", 1.0)
        ), "Candela"),

        ConverterCategory("Luminance", listOf(
            ConverterUnit("Nit", "nt", 1.0),
            ConverterUnit("Candela per Square Meter", "cd/m²", 1.0)
        ), "Nit"),

        ConverterCategory("Radioactivity", listOf(
            ConverterUnit("Becquerel", "Bq", 1.0),
            ConverterUnit("Curie", "Ci", 3.7e10)
        ), "Becquerel"),

        ConverterCategory("Radiation Dose", listOf(
            ConverterUnit("Gray", "Gy", 1.0),
            ConverterUnit("Sievert", "Sv", 1.0),
            ConverterUnit("Rad", "rad", 0.01),
            ConverterUnit("Rem", "rem", 0.01)
        ), "Gray"),

        ConverterCategory("Thermal Conductivity", listOf(
            ConverterUnit("Watt per Meter Kelvin", "W/(m·K)", 1.0),
            ConverterUnit("BTU per Hour Foot Fahrenheit", "BTU/(h·ft·°F)", 1.730735)
        ), "Watt per Meter Kelvin"),

        ConverterCategory("Specific Heat", listOf(
            ConverterUnit("Joule per Kilogram Kelvin", "J/(kg·K)", 1.0),
            ConverterUnit("Calorie per Gram Celsius", "cal/(g·°C)", 4184.0)
        ), "Joule per Kilogram Kelvin"),

        ConverterCategory("Heat Capacity", listOf(
            ConverterUnit("Joule per Kelvin", "J/K", 1.0),
            ConverterUnit("Calorie per Celsius", "cal/°C", 4.184)
        ), "Joule per Kelvin"),

        ConverterCategory("Thermal Expansion", listOf(
            ConverterUnit("Per Kelvin", "1/K", 1.0),
            ConverterUnit("Per Celsius", "1/°C", 1.0)
        ), "Per Kelvin"),

        ConverterCategory("Viscosity", listOf(
            ConverterUnit("Pascal Second", "Pa·s", 1.0),
            ConverterUnit("Poise", "P", 0.1),
            ConverterUnit("Centipoise", "cP", 0.001)
        ), "Pascal Second"),

        ConverterCategory("Surface Tension", listOf(
            ConverterUnit("Newton per Meter", "N/m", 1.0),
            ConverterUnit("Dyne per Centimeter", "dyn/cm", 0.001)
        ), "Newton per Meter"),

        ConverterCategory("Concentration", listOf(
            ConverterUnit("Molar", "M", 1.0),
            ConverterUnit("Molal", "m", 1.0),
            ConverterUnit("Percent", "%", 0.01),
            ConverterUnit("Parts Per Million", "ppm", 1e-6),
            ConverterUnit("Parts Per Billion", "ppb", 1e-9)
        ), "Molar"),

        ConverterCategory("Molar Mass", listOf(
            ConverterUnit("Gram per Mole", "g/mol", 0.001),
            ConverterUnit("Kilogram per Mole", "kg/mol", 1.0)
        ), "Kilogram per Mole"),

        ConverterCategory("Data Storage", listOf(
            ConverterUnit("Bit", "b", 1.0),
            ConverterUnit("Nibble", "nibble", 4.0),
            ConverterUnit("Byte", "B", 8.0),
            ConverterUnit("Kilobyte", "KB", 8000.0),
            ConverterUnit("Megabyte", "MB", 8e6),
            ConverterUnit("Gigabyte", "GB", 8e9),
            ConverterUnit("Terabyte", "TB", 8e12),
            ConverterUnit("Petabyte", "PB", 8e15),
            ConverterUnit("Exabyte", "EB", 8e18),
            ConverterUnit("Kibibyte", "KiB", 8192.0),
            ConverterUnit("Mebibyte", "MiB", 8.388608e6),
            ConverterUnit("Gibibyte", "GiB", 8.589934592e9),
            ConverterUnit("Tebibyte", "TiB", 8.796093022208e12)
        ), "Bit"),

        ConverterCategory("Data Transfer Rate", listOf(
            ConverterUnit("bps", "bps", 1.0),
            ConverterUnit("Kbps", "Kbps", 1000.0),
            ConverterUnit("Mbps", "Mbps", 1e6),
            ConverterUnit("Gbps", "Gbps", 1e9),
            ConverterUnit("Tbps", "Tbps", 1e12)
        ), "bps"),

        ConverterCategory("Number System", listOf(
            ConverterUnit("Binary", "BIN", 1.0),
            ConverterUnit("Octal", "OCT", 1.0),
            ConverterUnit("Decimal", "DEC", 1.0),
            ConverterUnit("Hexadecimal", "HEX", 1.0),
            ConverterUnit("ASCII", "ASCII", 1.0),
            ConverterUnit("Unicode", "UNI", 1.0),
            ConverterUnit("Base32", "B32", 1.0),
            ConverterUnit("Base64", "B64", 1.0),
            ConverterUnit("Roman Numerals", "ROM", 1.0)
        ), "Decimal", isSpecial = true)
    )

    fun convert(valueStr: String, fromUnit: ConverterUnit, toUnit: ConverterUnit, category: ConverterCategory): String {
        if (valueStr.isBlank()) return ""
        if (fromUnit == toUnit && !category.isSpecial) return valueStr

        try {
            if (category.name == "Number System") {
                return convertNumberSystem(valueStr, fromUnit.name, toUnit.name)
            }

            if (category.name == "Temperature") {
                val input = valueStr.toDoubleOrNull() ?: return "Error"
                val valueInKelvin = when (fromUnit.name) {
                    "Celsius" -> input + 273.15
                    "Fahrenheit" -> (input - 32.0) * 5.0 / 9.0 + 273.15
                    "Kelvin" -> input
                    "Rankine" -> input * 5.0 / 9.0
                    else -> input
                }
                val result = when (toUnit.name) {
                    "Celsius" -> valueInKelvin - 273.15
                    "Fahrenheit" -> (valueInKelvin - 273.15) * 9.0 / 5.0 + 32.0
                    "Kelvin" -> valueInKelvin
                    "Rankine" -> valueInKelvin * 9.0 / 5.0
                    else -> valueInKelvin
                }
                return formatDouble(result)
            }

            if (category.name == "Fuel Consumption") {
                val input = valueStr.toDoubleOrNull() ?: return "Error"
                if (input <= 0.0) return "Error: Must be > 0"
                // Standardize to L/100km
                val valueInL100 = when (fromUnit.name) {
                    "Kilometer per Liter" -> 100.0 / input
                    "Liter per 100 Kilometer" -> input
                    "Miles per Gallon (US)" -> 235.214583 / input
                    "Miles per Gallon (UK)" -> 282.4809363 / input
                    else -> input
                }
                val result = when (toUnit.name) {
                    "Kilometer per Liter" -> 100.0 / valueInL100
                    "Liter per 100 Kilometer" -> valueInL100
                    "Miles per Gallon (US)" -> 235.214583 / valueInL100
                    "Miles per Gallon (UK)" -> 282.4809363 / valueInL100
                    else -> valueInL100
                }
                return formatDouble(result)
            }

            // Standard physical conversion
            val inputDouble = valueStr.toDoubleOrNull() ?: return "Error"
            val baseValue = (inputDouble + fromUnit.offset) * fromUnit.factorToBase
            val finalValue = (baseValue / toUnit.factorToBase) - toUnit.offset
            return formatDouble(finalValue)
        } catch (e: Exception) {
            return "Error"
        }
    }

    private fun formatDouble(value: Double): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return "Infinity"
        if (abs(value) < 1e-9 || abs(value) > 1e9) {
            return String.format(Locale.US, "%.8e", value)
        }
        val bd = BigDecimal(value).setScale(12, RoundingMode.HALF_UP).stripTrailingZeros()
        return bd.toPlainString()
    }

    private fun convertNumberSystem(input: String, from: String, to: String): String {
        if (input.isBlank()) return ""
        val trimmed = input.trim()

        // 1. Convert "from" to standard Decimal value (BigInteger representation as a decimal string)
        val decimalString: String = try {
            when (from) {
                "Decimal" -> {
                    trimmed.toBigInteger(10).toString(10)
                }
                "Binary" -> {
                    trimmed.toBigInteger(2).toString(10)
                }
                "Octal" -> {
                    trimmed.toBigInteger(8).toString(10)
                }
                "Hexadecimal" -> {
                    val san = trimmed.lowercase().removePrefix("0x")
                    san.toBigInteger(16).toString(10)
                }
                "ASCII" -> {
                    if (trimmed.isEmpty()) "" else {
                        trimmed.map { it.code.toString() }.joinToString(" ")
                    }
                }
                "Unicode" -> {
                    trimmed.map { "U+" + String.format("%04X", it.code) }.joinToString(" ")
                }
                "Base32" -> {
                    base32Decode(trimmed)
                }
                "Base64" -> {
                    try {
                        val decodedBytes = android.util.Base64.decode(trimmed, android.util.Base64.DEFAULT)
                        String(decodedBytes, Charsets.UTF_8)
                    } catch (e: Exception) {
                        return "Invalid Base64"
                    }
                }
                "Roman Numerals" -> {
                    romanToDecimal(trimmed).toString()
                }
                else -> trimmed
            }
        } catch (e: Exception) {
            return "Invalid $from Input"
        }

        // 2. Convert from standard Decimal to "to"
        return try {
            when (to) {
                "Decimal" -> decimalString
                "Binary" -> decimalString.toBigInteger(10).toString(2)
                "Octal" -> decimalString.toBigInteger(10).toString(8)
                "Hexadecimal" -> decimalString.toBigInteger(10).toString(16).uppercase()
                "ASCII" -> {
                    // Try to treat decimal as ASCII value or string
                    decimalString.split(" ").mapNotNull {
                        it.toIntOrNull()?.toChar()?.toString()
                    }.joinToString("")
                }
                "Unicode" -> {
                    decimalString.split(" ").mapNotNull {
                        it.toIntOrNull()?.toChar()?.toString()
                    }.joinToString("")
                }
                "Base32" -> {
                    base32Encode(decimalString)
                }
                "Base64" -> {
                    android.util.Base64.encodeToString(decimalString.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
                }
                "Roman Numerals" -> {
                    val intValue = decimalString.toIntOrNull() ?: return "Value Too Large"
                    if (intValue <= 0 || intValue > 3999) "Out of Range (1-3999)" else decimalToRoman(intValue)
                }
                else -> decimalString
            }
        } catch (e: Exception) {
            "Conversion Error"
        }
    }

    // Base32 helpers
    private val BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
    private fun base32Encode(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        var i = 0
        var index = 0
        var digit: Int
        var currByte: Int
        var nextByte: Int
        val base32 = StringBuilder((bytes.size + 7) * 8 / 5)

        while (i < bytes.size) {
            currByte = if (bytes[i] >= 0) bytes[i].toInt() else bytes[i].toInt() + 256

            if (index > 3) {
                if (i + 1 < bytes.size) {
                    nextByte = if (bytes[i + 1] >= 0) bytes[i + 1].toInt() else bytes[i + 1].toInt() + 256
                } else {
                    nextByte = 0
                }
                digit = currByte and (0xFF ushr index)
                index = (index + 5) % 8
                digit = (digit ushr index) or (nextByte ushr (8 - index))
                i++
            } else {
                digit = (currByte ushr (8 - (index + 5))) and 0x1F
                index = (index + 5) % 8
                if (index == 0) i++
            }
            base32.append(BASE32_CHARS[digit])
        }
        return base32.toString()
    }

    private fun base32Decode(input: String): String {
        val trimmed = input.uppercase().replace("[^A-Z2-7]".toRegex(), "")
        val bytes = ByteArray(trimmed.length * 5 / 8)
        var i = 0
        var lookup: Int
        var offset = 0
        var buffer = 0
        for (c in trimmed) {
            lookup = BASE32_CHARS.indexOf(c)
            if (lookup == -1) continue
            buffer = (buffer shl 5) or lookup
            offset += 5
            if (offset >= 8) {
                if (i < bytes.size) {
                    bytes[i++] = (buffer ushr (offset - 8)).toByte()
                }
                offset -= 8
            }
        }
        return String(bytes, Charsets.UTF_8)
    }

    // Roman Numerals helper
    private fun romanToDecimal(roman: String): Int {
        val map = mapOf('I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100, 'D' to 500, 'M' to 1000)
        var sum = 0
        var prev = 0
        for (i in roman.uppercase().reversed()) {
            val curr = map[i] ?: 0
            if (curr < prev) {
                sum -= curr
            } else {
                sum += curr
            }
            prev = curr
        }
        return sum
    }

    private fun decimalToRoman(num: Int): String {
        val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val symbols = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        val roman = StringBuilder()
        var remaining = num
        for (i in values.indices) {
            while (remaining >= values[i]) {
                remaining -= values[i]
                roman.append(symbols[i])
            }
        }
        return roman.toString()
    }
}
