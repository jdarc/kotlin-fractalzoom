import java.awt.Color
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import kotlin.math.floor
import kotlin.math.ln

class Mandelbrot(private val width: Int, private val height: Int) {

    fun render(x1: Double, y1: Double, x2: Double, y2: Double, maxIterations: Int, destination: IntArray) {
        val h = (x2 - x1) / width
        val v = (y2 - y1) / height
        ForkJoinPool.commonPool().invokeAll(MutableList(height) { ny ->
            Executors.callable {
                val a = y1 + ny * v
                val mem = ny * width
                for (nx in 0 until width) {
                    var red = 0
                    var grn = 0
                    var blu = 0
                    val b = x1 + nx * h
                    for (j in -1..1) {
                        for (i in -1..1) {
                            val col = calculate(a + j * v * 0.25, b + i * h * 0.25, maxIterations)
                            red += 255 and col.shr(16)
                            grn += 255 and col.shr(8)
                            blu += 255 and col
                        }
                    }
                    destination[mem + nx] = OPAQUE or (red / 9 shl 16) or (grn / 9 shl 8) or (blu / 9)
                }
            }
        })
    }

    companion object {

        private const val OPAQUE = 255 shl 24

        private val overLn2 = 1.0 / ln(2.0)

        private val colors = makePalette()

        private fun makePalette(): Array<Color> {
            val markers = intArrayOf(
                0x0AFC84, 0x3264F0, 0xE63C14, 0xE6AA00, 0xAFAF0A,
                0x5A0032, 0xB45A78, 0xFF1428, 0x1E46C8, 0x0AFC84
            )
            val colors = Array<Color>(256) { Color.BLACK }
            var eR = markers[0].shr(0x10).and(255).toDouble()
            var eG = markers[0].shr(0x08).and(255).toDouble()
            var eB = markers[0].shr(0x00).and(255).toDouble()
            val step = 256.0 / (markers.size - 1)
            for (j in 1 until markers.size) {
                var sR = eR
                var sG = eG
                var sB = eB
                eR = markers[j].shr(0x10).and(255).toDouble()
                eG = markers[j].shr(0x08).and(255).toDouble()
                eB = markers[j].shr(0x00).and(255).toDouble()
                val dr = (eR - sR) / step
                val dg = (eG - sG) / step
                val db = (eB - sB) / step
                var i = 0
                while (i < step) {
                    val red = floor(sR).toInt().coerceIn(0, 255)
                    val grn = floor(sG).toInt().coerceIn(0, 255)
                    val blu = floor(sB).toInt().coerceIn(0, 255)
                    colors[floor((j - 1) * step + i).toInt()] = Color(red, grn, blu)
                    sR += dr
                    sG += dg
                    sB += db
                    ++i
                }
            }
            return colors
        }

        private fun calculate(a: Double, b: Double, iterations: Int): Int {
            var x = 0.0
            var y = 0.0
            var i = 0
            while (true) {
                val z = x * x
                val w = y * y
                if (++i >= iterations || z + w >= 4.0) break
                x = 2.0 * x * y + a
                y = w - z + b
            }
            return if (i < iterations) smoothColor(i, x, y) else 0
        }

        private fun smoothColor(i: Int, x: Double, y: Double): Int {
            val n = i + 1.0 - ln(ln(x * x + y * y) * overLn2) * overLn2
            val d = n - n.toInt()
            val fl = floor(n).toInt()
            val color1 = colors[255 and fl]
            val color2 = colors[255 and fl + 1]
            val red = (color1.red + (color2.red - color1.red) * d).toInt()
            val grn = (color1.green + (color2.green - color1.green) * d).toInt()
            val blu = (color1.blue + (color2.blue - color1.blue) * d).toInt()
            return OPAQUE or red.shl(16) or grn.shl(8) or blu
        }
    }
}
