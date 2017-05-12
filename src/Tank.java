import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by nishu on 5/9/2017.
 */
public class Tank {
    private boolean fired = false;
    private int prevTurretPt = 1;
    private final double G = -98.1;
    private Graphics g;
    private Graphics2D g2;
    private DrawingPanel panel;
    private int xPos;
    private int yPos;
    private BufferedImage bomb = ImageIO.read(new File("bomb.png"));
    private Point[] turretPts = {new Point(163, 475), new Point(158, 465), new Point(162, 465), new Point(160, 451),
    new Point(158, 447), new Point(158, 447), new Point(156, 436), new Point(154, 431), new Point(150, 428),
    new Point(145, 424), new Point(143, 419), new Point(138, 415), new Point(133, 412)};
    public Tank(int xPos, int yPos) throws IOException {
        this.panel = new DrawingPanel(1000, 800);
        this.g = panel.getGraphics();
        g2 = (Graphics2D)g;
        this.xPos = xPos;
        this.yPos = yPos;
        g.drawImage(ImageIO.read(new File("tanksprite1.png")), xPos, yPos, null);
        Color brown = new Color(139, 69, 19);
        g.setColor(brown);
        g.fillRect(0, 780, 1000, 20);
        g.drawImage(ImageIO.read(new File("Powerbar\\Powerbar1.png")), turretPts[0].x, turretPts[0].y, null);
        fire(250, 40);
        panel.addMouseListener(new MouseInputAdapter() {
            public void mousePressed(MouseEvent e) {
                fired = true;
                Point p1 = new Point(xPos+144, yPos);
                Point p2 = new Point(e.getX(), e.getY());
                double vX = Math.abs(p1.x-p2.x);
                double vY = Math.abs(p1.y-p2.y);
                if (vX > 350) {
                    vX = 350;
                }
                if (vY > 350) {
                    vY = 350;
                }

                fire(vX, vY);
                panel.sleep(3000);
                fired = false;
            }

            public void mouseMoved(MouseEvent e) {
                if (!fired) {
                    Point p1 = new Point(e.getX(), e.getY());
                    Point p2 = new Point(155, 670);
                    double xDist = (p1.x - p2.x);
                    double yDist = (p2.y - p1.y);

                    double theta = Math.toDegrees(Math.atan2(yDist, xDist));
                    theta = 5 * (Math.round(theta / 5));

                    double turretAngle = theta;
                    if (turretAngle > 60) {
                        turretAngle = 60;
                    } else if (turretAngle < 0) {
                        turretAngle = 0;
                    }

                    int imgIndex = ((int) turretAngle / 5) + 1;
                    g.setColor(Color.white);
                    g.fillRect(xPos, yPos, 170, 70);
                    try {
                        g.drawImage(ImageIO.read(new File("TankSprite" + imgIndex + ".png")), xPos, yPos, null);
                    } catch (IOException ex) {
                        System.out.println("TankSprite" + imgIndex);
                    }
                    Point turretPt = turretPts[imgIndex - 1];
                    p1 = new Point(e.getX(), e.getY());
                    double power = new Point(155, 670).distance(p1);

                    if (power > 350) {
                        power = 350;
                    }
                    power = 25 * Math.round(power / 25);
                    int pwrIndex = ((int) power / 25) + 1;
                    g2.fillRect(turretPts[prevTurretPt].x - 10, turretPts[prevTurretPt].y - 55, 370, 340);

                    try {
                        BufferedImage bar = ImageIO.read(new File("Powerbar\\Powerbar" + pwrIndex + ".png"));
                        double deg = Math.toRadians((10 - imgIndex) * (5));
                        AffineTransform at = g2.getTransform();
                        at.translate(turretPts[imgIndex - 1].x, turretPts[imgIndex - 1].y);
                        at.rotate(deg, 0, 262);
                        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                        BufferedImage rotated = ato.createCompatibleDestImage(bar, bar.getColorModel());
                        g2.drawImage(bar, at, null);
                    } catch (IOException ex) {
                        System.out.println("Powerbar" + pwrIndex);
                    }
                    prevTurretPt = (imgIndex - 1);
                }
            }
        });
    }

    private void fire(double vX, double vY) {
        System.out.println("Firing with x velocity " + vX + " and y velocity " + vY);
        g.setColor(Color.white);
        g.fillRect(133, 412, 300, 350);
        double deltaT = 0.01;
        double x = xPos+144;
        double y = yPos;
        double oldX = x;
        double oldY = y;
        Rectangle ground = new Rectangle(0, 780, 1000, 20);
        Rectangle ball = new Rectangle((int)xPos, (int)yPos, 32, 32);
        while (!ball.intersects(ground)) {
            g.drawImage(bomb, (int)x, (int)y, null);
            ball = new Rectangle((int)x, (int)y, 32, 32);
            if (ball.intersects(ground)) {
                break;
            }
            vY += (G*deltaT);
            x += (vX*deltaT);
            y -= (vY*deltaT);
            panel.sleep(10);
            g.setColor(Color.white);
            g.fillRect((int)oldX, (int)oldY, 32, 32);
            oldX = x;
            oldY = y;
        }
    }
}
