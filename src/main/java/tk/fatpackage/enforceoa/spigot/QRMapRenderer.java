package tk.fatpackage.enforceoa.spigot;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;

public class QRMapRenderer extends MapRenderer {

    private Image image;
    private boolean firstRender = true;

    public QRMapRenderer(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix;
        try {
            matrix = writer.encode(text, BarcodeFormat.QR_CODE, 128, 128);
        } catch (WriterException e) {
            e.printStackTrace();
            return;
        }
        image = MatrixToImageWriter.toBufferedImage(matrix);
    }


    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        if (image != null && firstRender) {
            mapCanvas.drawImage(0, 0, image);
            firstRender = false;
        }
    }
}
