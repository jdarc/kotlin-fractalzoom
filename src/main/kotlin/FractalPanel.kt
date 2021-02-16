import java.awt.*
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import javax.swing.JPanel

class FractalPanel : JPanel(true) {
    private val image: BufferedImage

    override fun paintComponent(g: Graphics) = (g as Graphics2D).drawImage(image, null, 0, 0)

    init {
        size = Dimension(960, 600)
        preferredSize = size
        layout = null
        ignoreRepaint = true

        image = BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB_PRE)
        val pixels = (image.raster.dataBuffer as DataBufferInt).data
        val mandelbrot = Mandelbrot(image.width, image.height)

        Toolkit.getDefaultToolkit().systemEventQueue.push(object : EventQueue() {
            private var minX = -2.25
            private var minY = -0.95
            private var maxX = 0.75
            private var maxY = 0.95
            private var iterations = 32

            override fun dispatchEvent(event: AWTEvent) {
                super.dispatchEvent(event)
                if (peekEvent() == null) {
                    zoom()
                    generate()
                    repaint()
                }
            }

            private fun zoom() {
                minX += (-0.743643887037151 - minX) / 20.0
                minY += (0.131825904205330 - minY) / 20.0
                maxX += (-0.743643887037151 - maxX) / 20.0
                maxY += (0.131825904205330 - maxY) / 20.0
                iterations += 8
            }

            private fun generate() = mandelbrot.render(minX, minY, maxX, maxY, iterations, pixels)
        })
    }
}
