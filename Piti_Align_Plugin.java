import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.NonBlockingGenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * ImageJ Plugin that aligns the different images in the satck, accoding to the user input
 */
public class Piti_Align_Plugin implements PlugInFilter, MouseListener {
    private ImagePlus ip;
    private ImageCanvas canvas;
    private List<Piti_Align_Plugin.Coord> coordList;

    public Piti_Align_Plugin() {
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        int x = mouseEvent.getX();
        int y = mouseEvent.getY();
        Piti_Align_Plugin.Coord coord = new Piti_Align_Plugin.Coord(this.canvas.offScreenX(x), this.canvas.offScreenY(y));
        this.coordList.add(coord);
        this.ip.setSlice(this.ip.getSlice() + 1);
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public int setup(String s, ImagePlus imagePlus) {
        this.ip = imagePlus;
        this.coordList = new ArrayList(this.ip.getStackSize());
        return ij.plugin.filter.PluginFilter.DOES_ALL;
    }

    public void run(ImageProcessor imageProcessor) {
        ImageWindow win = this.ip.getWindow();
        this.canvas = win.getCanvas();
        this.canvas.addMouseListener(this);
        NonBlockingGenericDialog gd = new NonBlockingGenericDialog("Piti Plugin");
        gd.addMessage("Click on OK when finished");
        gd.showDialog();
        if(gd.wasCanceled()) {
            this.canvas.removeMouseListener(this);
        } else {
            String msg = "";

            for(int i = 0; i < this.coordList.size(); ++i) {
                msg = msg + (i + 1) + ": " + this.coordList.get(i) + "\n";
            }

            this.performAlignment();
            this.canvas.removeMouseListener(this);
        }
    }

    private void performAlignment() {
        Piti_Align_Plugin.Coord reference = (Piti_Align_Plugin.Coord)this.coordList.get(0);

        for(int i = 1; i < this.ip.getStackSize(); ++i) {
            this.ip.setSlice(i + 1);
            ImageProcessor img = this.ip.getProcessor();
            if(i < this.coordList.size()) {
                Piti_Align_Plugin.Coord coord = (Piti_Align_Plugin.Coord)this.coordList.get(i);
                int x = reference.getX() - coord.getX();
                int y = reference.getY() - coord.getY();
                img.translate((double)x, (double)y);
            }
        }

    }

    private class Coord {
        private final int x;
        private final int y;

        private Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public String toString() {
            return "Coord{x=" + this.x + ", y=" + this.y + '}';
        }
    }
}

