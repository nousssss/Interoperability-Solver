package org.processmining.plugins.petrinet.configurable.ui.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.SwingConstants;

import org.deckfour.uitopia.ui.components.ImageButton;

/**
 * Define various helpers such as images and buttons.
 * 
 * @author dfahland
 *
 */
public class UIUtils {
	
	public static BufferedImage checkMark;
	public static BufferedImage plusSign;
	public static BufferedImage crossSign;
	public static BufferedImage nextSign;

	static {
		checkMark = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
		Graphics g = checkMark.getGraphics();
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setColor(new Color(190, 190, 190));
		g2d.setStroke(new BasicStroke(4));
		g2d.drawLine(5,15,10,20);
		g2d.drawLine(10,20,20,10);
		g2d.dispose();
		
		plusSign = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
		g = plusSign.getGraphics();
		g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setColor(new Color(190, 190, 190));
		g2d.setStroke(new BasicStroke(5));
		g2d.drawLine(13,7,13,19);
		g2d.drawLine(7,13,19,13);
		g2d.dispose();
		
		crossSign = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
		g = crossSign.getGraphics();
		g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setColor(new Color(190, 190, 190));
		g2d.setStroke(new BasicStroke(3));
		g2d.drawLine(10,10,17,17);
		g2d.drawLine(10,17,17,10);
		g2d.dispose();
		
		nextSign = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
		g = nextSign.getGraphics();
		g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setColor(new Color(190, 190, 190));
		g2d.setStroke(new BasicStroke(3));
		Polygon p = new Polygon(new int[] { 7, 19, 7}, new int[] { 7, 13, 19}, 3);
		g2d.fillPolygon(p);
		//g2d.drawPolygon();
		g2d.dispose();
	}
	
	public static ImageButton createCheckMarkButton()
	{
		ImageButton button = new ImageButton(UIUtils.checkMark, new Color(0, 80, 0), new Color(0, 120, 0), 0);
		button.setMinimumSize(new Dimension(30, 30));
		button.setPreferredSize(new Dimension(30, 30));
		button.setMaximumSize(new Dimension(30, 30));
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.CENTER);
		return button;
	}
	
	public static ImageButton createPlusButton()
	{
		ImageButton button = new ImageButton(UIUtils.plusSign, Color.DARK_GRAY, new Color(0, 80, 0), 0);
		button.setMinimumSize(new Dimension(30, 30));
		button.setPreferredSize(new Dimension(30, 30));
		button.setMaximumSize(new Dimension(30, 30));
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.CENTER);
		return button;
	}
	
	public static ImageButton createCrossSignButton()
	{
		ImageButton button = new ImageButton(UIUtils.crossSign, Color.DARK_GRAY, new Color(80, 0, 0), 0);
		button.setMinimumSize(new Dimension(30, 30));
		button.setPreferredSize(new Dimension(30, 30));
		button.setMaximumSize(new Dimension(30, 30));
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.CENTER);
		return button;
	}
	
	public static ImageButton createNextSignButton()
	{
		ImageButton button = new ImageButton(UIUtils.nextSign, new Color(0, 80, 0), new Color(0, 120, 0), 0);
		button.setMinimumSize(new Dimension(30, 30));
		button.setPreferredSize(new Dimension(30, 30));
		button.setMaximumSize(new Dimension(30, 30));
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.CENTER);
		return button;
	}
}
