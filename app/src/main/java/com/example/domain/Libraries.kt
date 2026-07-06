package com.example.domain

data class ScientificConstant(
    val name: String,
    val symbol: String,
    val value: String,
    val unit: String,
    val description: String,
    val category: String // "Mathematical", "Physical", "Chemical", "Astronomical", "Engineering"
)

data class FormulaItem(
    val title: String,
    val formula: String,
    val variables: String,
    val units: String,
    val explanation: String,
    val category: String // "Mathematics", "Physics", "Chemistry", "Geometry", "Trigonometry", "Statistics", "Engineering"
)

object Libraries {
    val constants = listOf(
        // Mathematical Constants
        ScientificConstant("Pi", "π", "3.141592653589793", "dimensionless", "The ratio of a circle's circumference to its diameter.", "Mathematical"),
        ScientificConstant("Euler's Number", "e", "2.718281828459045", "dimensionless", "The base of natural logarithms.", "Mathematical"),
        ScientificConstant("Golden Ratio", "φ", "1.618033988749895", "dimensionless", "The ratio of two quantities where their ratio is the same as the ratio of their sum to the larger of the two.", "Mathematical"),
        ScientificConstant("Euler-Mascheroni Constant", "γ", "0.577215664901532", "dimensionless", "The limiting difference between the harmonic series and the natural logarithm.", "Mathematical"),
        ScientificConstant("Catalan's Constant", "G", "0.915965594177219", "dimensionless", "Appears in combinatorial mathematics and low-dimensional topology.", "Mathematical"),
        ScientificConstant("Apery's Constant", "ζ(3)", "1.202056903159594", "dimensionless", "The value of the Riemann zeta function at 3.", "Mathematical"),

        // Physical Constants
        ScientificConstant("Speed of Light", "c", "299792458", "m/s", "The universal physical constant in a vacuum.", "Physical"),
        ScientificConstant("Planck Constant", "h", "6.62607015e-34", "J·s", "The quantum of electromagnetic action.", "Physical"),
        ScientificConstant("Reduced Planck Constant", "ħ", "1.054571817e-34", "J·s", "Planck constant divided by 2pi.", "Physical"),
        ScientificConstant("Gravitational Constant", "G", "6.6743e-11", "m³/(kg·s²)", "Key constant in Newton's law of universal gravitation.", "Physical"),
        ScientificConstant("Standard Gravity", "g", "9.80665", "m/s²", "Nominal acceleration of gravity on Earth's surface.", "Physical"),
        ScientificConstant("Elementary Charge", "e", "1.602176634e-19", "C", "The electrical charge carried by a single proton.", "Physical"),
        ScientificConstant("Electron Mass", "m_e", "9.1093837e-31", "kg", "The rest mass of an electron.", "Physical"),
        ScientificConstant("Proton Mass", "m_p", "1.67262192e-27", "kg", "The rest mass of a proton.", "Physical"),
        ScientificConstant("Neutron Mass", "m_n", "1.67492749e-27", "kg", "The rest mass of a neutron.", "Physical"),

        // Chemical Constants
        ScientificConstant("Avogadro Constant", "N_A", "6.02214076e23", "1/mol", "Number of constituent particles per mole.", "Chemical"),
        ScientificConstant("Universal Gas Constant", "R", "8.314462618", "J/(mol·K)", "The constant in the Ideal Gas Law.", "Chemical"),
        ScientificConstant("Boltzmann Constant", "k_B", "1.380649e-23", "J/K", "Relates key kinetic energy of gas particles to temperature.", "Chemical"),
        ScientificConstant("Faraday Constant", "F", "96485.33212", "C/mol", "The magnitude of electric charge per mole of electrons.", "Chemical"),
        ScientificConstant("Rydberg Constant", "R_∞", "10973731.56816", "1/m", "Relates to the electromagnetic spectra of elements.", "Chemical"),

        // Astronomical Constants
        ScientificConstant("Astronomical Unit", "au", "1.495978707e11", "m", "The average distance between the Earth and the Sun.", "Astronomical"),
        ScientificConstant("Light Year", "ly", "9.4607304725808e15", "m", "The distance that light travels in a vacuum in one year.", "Astronomical"),
        ScientificConstant("Parsec", "pc", "3.085677581e16", "m", "Distance corresponding to an annual parallax of one arcsecond.", "Astronomical"),
        ScientificConstant("Solar Mass", "M_☉", "1.98847e30", "kg", "Standard unit of mass in astronomy used to indicate the mass of stars.", "Astronomical"),
        ScientificConstant("Solar Radius", "R_☉", "6.957e8", "m", "Unit of distance used to express the size of stars.", "Astronomical"),
        ScientificConstant("Earth Mass", "M_⊕", "5.9722e24", "kg", "Standard astronomical unit of mass equal to Earth's mass.", "Astronomical"),
        ScientificConstant("Earth Radius", "R_⊕", "6371000", "m", "Average radius of the Earth.", "Astronomical"),
        ScientificConstant("Lunar Distance", "LD", "384400000", "m", "Average distance from the center of Earth to the center of Moon.", "Astronomical"),

        // Engineering Constants
        ScientificConstant("Density of Water (at 4°C)", "ρ_water", "1000", "kg/m³", "Reference density for specific gravity.", "Engineering"),
        ScientificConstant("Atmospheric Pressure", "P_atm", "101325", "Pa", "Standard sea-level atmospheric pressure.", "Engineering"),
        ScientificConstant("Stefan-Boltzmann Constant", "σ", "5.670374419e-8", "W/(m²·K⁴)", "Constant in blackbody radiation law.", "Engineering"),
        ScientificConstant("Vacuum Permittivity", "ε_0", "8.8541878128e-12", "F/m", "Physical constant representing capability of vacuum to permit electric field lines.", "Engineering"),
        ScientificConstant("Vacuum Permeability", "μ_0", "1.25663706212e-6", "H/m", "Magnetic constant in a vacuum.", "Engineering")
    )

    val formulas = listOf(
        // Mathematics
        FormulaItem("Quadratic Formula", "x = (-b ± √(b² - 4ac)) / (2a)", "a, b, c = coefficients of ax² + bx + c = 0", "dimensionless", "Solves for roots of a second-degree polynomial.", "Mathematics"),
        FormulaItem("Euler's Identity", "e^(i·π) + 1 = 0", "e = natural base, i = imaginary unit, π = Pi", "dimensionless", "Bridges trigonometry, algebra, and calculus in one elegant equation.", "Mathematics"),
        FormulaItem("Logarithm Identity", "log_b(xy) = log_b(x) + log_b(y)", "b = base, x, y = arguments", "dimensionless", "Converts multiplications into simpler additions.", "Mathematics"),
        FormulaItem("Binomial Theorem", "(x+y)ⁿ = Σ (n choose k) xⁿ⁻ᵏ yᵏ", "x, y = variables, n = integer exponent", "dimensionless", "Expands powers of a sum of terms.", "Mathematics"),

        // Physics
        FormulaItem("Einstein's Mass-Energy", "E = m·c²", "E = Energy, m = Mass, c = Speed of light", "Energy: Joules (J), Mass: kg", "Relates mass directly to energy equivalence.", "Physics"),
        FormulaItem("Newton's Second Law", "F = m·a", "F = Force, m = Mass, a = Acceleration", "Force: Newtons (N), Acceleration: m/s²", "Describes how force causes objects of mass to accelerate.", "Physics"),
        FormulaItem("Ohm's Law", "V = I·R", "V = Voltage, I = Current, R = Resistance", "V: Volts (V), I: Amperes (A), R: Ohms (Ω)", "Relates electrical potential difference, current, and resistance.", "Physics"),
        FormulaItem("Universal Gravitation", "F = G · (m₁·m₂) / r²", "F = Force, m₁, m₂ = Masses, r = Distance", "Force: Newtons (N), Distance: m", "Describes attractive force acting between any two bodies of mass.", "Physics"),

        // Chemistry
        FormulaItem("Ideal Gas Law", "P·V = n·R·T", "P=Pressure, V=Volume, n=Moles, T=Temperature, R=Gas constant", "P: Pascals (Pa), V: m³, T: Kelvin (K)", "Equation of state of a hypothetical ideal gas.", "Chemistry"),
        FormulaItem("pH Definition", "pH = -log₁₀[H⁺]", "[H⁺] = concentration of hydrogen ions", "dimensionless", "Measures acidity or basicity of an aqueous solution.", "Chemistry"),
        FormulaItem("Molarity", "M = n / V", "n = moles of solute, V = volume of solution", "M: moles/L (M)", "Defines concentration of a chemical solution.", "Chemistry"),

        // Geometry
        FormulaItem("Area of Circle", "A = π·r²", "A = Area, r = Radius", "Area: m², Radius: m", "Calculates total two-dimensional space enclosed by a circle.", "Geometry"),
        FormulaItem("Pythagorean Theorem", "a² + b² = c²", "a, b = perpendicular legs, c = hypotenuse", "Length: m", "Relates sides of a right triangle.", "Geometry"),
        FormulaItem("Sphere Volume", "V = (4/3)·π·r³", "V = Volume, r = Radius", "Volume: m³", "Calculates the three-dimensional space inside a perfect sphere.", "Geometry"),

        // Trigonometry
        FormulaItem("Trigonometric Identity", "sin²(θ) + cos²(θ) = 1", "θ = angle", "dimensionless", "The fundamental Pythagorean trigonometric identity.", "Trigonometry"),
        FormulaItem("Law of Sines", "a/sin(A) = b/sin(B) = c/sin(C)", "a, b, c = side lengths, A, B, C = opposite angles", "dimensionless", "Relates the sides and angles of any general triangle.", "Trigonometry"),
        FormulaItem("Law of Cosines", "c² = a² + b² - 2·a·b·cos(C)", "a, b, c = sides, C = angle opposite side c", "dimensionless", "Generalizes Pythagorean theorem to all triangles.", "Trigonometry"),

        // Statistics
        FormulaItem("Mean (Average)", "μ = (Σ x_i) / N", "x_i = values, N = total count", "dimensionless", "Computes the central value of a dataset.", "Statistics"),
        FormulaItem("Standard Deviation", "σ = √[ Σ(x_i - μ)² / N ]", "x_i = values, μ = mean, N = count", "dimensionless", "Measures the dispersion or spread of data points.", "Statistics"),

        // Engineering
        FormulaItem("Young's Modulus", "E = σ / ε", "E = Modulus, σ = Stress, ε = Strain", "E: Pascals (Pa), Stress: Pa, Strain: dimensionless", "Measures tensile stiffness of an elastic solid material.", "Engineering"),
        FormulaItem("Reynolds Number", "Re = (ρ·v·L) / μ", "ρ=density, v=velocity, L=length, μ=dynamic viscosity", "dimensionless", "Determines whether fluid flow is laminar or turbulent.", "Engineering")
    )
}
