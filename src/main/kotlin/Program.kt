import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JFrame
import javax.swing.SwingUtilities

object Program {
    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater {
            val frame = JFrame("Fractal Zoom")
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            frame.background = Color.BLACK
            frame.layout = BorderLayout()
            frame.contentPane.add(FractalPanel(), BorderLayout.CENTER)
            frame.pack()
            frame.setLocationRelativeTo(null)
            frame.isResizable = false
            frame.isVisible = true
        }
    }
}
