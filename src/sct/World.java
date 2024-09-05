package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import javax.swing.*;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
//
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.io.IOException;
import java.awt.AWTException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.swing.filechooser.FileSystemView;
import java.awt.Graphics2D;

public class World extends JPanel{
	ArrayList<Bot> objects;
	int size = 25;
	Timer timer;
	int delay = 10;
	Random rand = new Random();
	int[] world_scale = {100, 100};
	int[][] Map = new int[world_scale[0]][world_scale[1]];//0 - none, 1 - bot, 2 - organics
	Color gray = new Color(100, 100, 100);
	Color green = new Color(0, 255, 0);
	Color red = new Color(255, 0, 0);
	Color black = new Color(0, 0, 0);
	Color white = new Color(255, 255, 255);
	int steps = 0;
	int draw_type = 0;
	int b_count = 0;
	int obj_count = 0;
	int org_count = 0;
	String txt;
	String txt2;
	int mouse = 0;
	int W = 1920;
	int H = 1080;
	JButton stop_button = new JButton("Stop");
	boolean pause = false;
	boolean render = true;
	Bot selection = null;
	int[] botpos = new int [2];
	int[][] for_set_operators;
	int[][][] for_set_conditions;
	int[][][] for_set_commands;
	JButton save_button = new JButton("Save");
	JButton show_brain_button = new JButton("Show brain");
	JButton render_button = new JButton("Render: on");
	JButton record_button = new JButton("Record: off");
	JTextField for_save = new JTextField();
	JTextField for_load = new JTextField();
	boolean sh_brain = false;
	boolean rec = false;
	public World() {
		setLayout(null);
		timer = new Timer(delay, new BotListener());
		objects = new ArrayList<Bot>();
		setBackground(gray);
		addMouseListener(new BotListener());
		addMouseMotionListener(new BotListener());
		//
		stop_button.addActionListener(new start_stop());
		stop_button.setBounds(W - 300, 125, 250, 35);
        add(stop_button);
        //
        JButton predators_button = new JButton("Predators");
        predators_button.addActionListener(new dr1());
		predators_button.setBounds(W - 300, 190, 125, 20);
        add(predators_button);
        //
        JButton energy_button = new JButton("Energy");
        energy_button.addActionListener(new dr3());
		energy_button.setBounds(W - 170, 190, 125, 20);
        add(energy_button);
        //
        JButton minerals_button = new JButton("Minerals");
		minerals_button.setBounds(W - 300, 215, 125, 20);
		minerals_button.addActionListener(new dr4());
        add(minerals_button);
        //
        JButton age_button = new JButton("Age");
        age_button.addActionListener(new dr5());
		age_button.setBounds(W - 170, 215, 125, 20);
        add(age_button);
        //
        JButton color_button = new JButton("Color");
        color_button.addActionListener(new dr2());
		color_button.setBounds(W - 300, 240, 125, 20);
        add(color_button);
        //
        JButton brain_layer_button = new JButton("Brain layer");
        brain_layer_button.addActionListener(new dr7());
        brain_layer_button.setBounds(W - 170, 265, 125, 20);
        add(brain_layer_button);
        //
        JButton recomb_button = new JButton("Recombination");
        recomb_button.addActionListener(new dr8());
        recomb_button.setBounds(W - 300, 265, 125, 20);
        add(recomb_button);
        //
        JButton memory_button = new JButton("Memory");
        memory_button.addActionListener(new dr6());
		memory_button.setBounds(W - 170, 240, 125, 20);
        add(memory_button);
        //
        JButton select_button = new JButton("Select");
        select_button.addActionListener(new select());
		select_button.setBounds(W - 300, 485, 95, 20);
        add(select_button);
        //
        JButton set_button = new JButton("Set");
        set_button.addActionListener(new set());
        set_button.setBounds(W - 200, 485, 95, 20);
        add(set_button);
        //
        JButton remove_button = new JButton("Remove");
        remove_button.addActionListener(new remove());
        remove_button.setBounds(W - 100, 485, 95, 20);
        add(remove_button);
        //
        save_button.addActionListener(new save_bot());
        save_button.setBounds(W - 300, 445, 125, 20);
        save_button.setEnabled(false);
        add(save_button);
        //
        show_brain_button.addActionListener(new shbr());
        show_brain_button.setBounds(W - 170, 445, 125, 20);
        show_brain_button.setEnabled(false);
        add(show_brain_button);
        //
        for_save.setBounds(W - 300, 420, 250, 20);
        add(for_save);
        //
        for_load.setBounds(W - 300, 545, 250, 20);
        add(for_load);
        //
        JButton load_bot_button = new JButton("Load bot");
        load_bot_button.addActionListener(new load_bot());
        load_bot_button.setBounds(W - 300, 570, 125, 20);
        add(load_bot_button);
        //
        JButton load_world_button = new JButton("Load world");
        //load_world_button.addActionListener(new remove());
        load_world_button.setBounds(W - 170, 570, 125, 20);
        add(load_world_button);
        //
        JButton new_population_button = new JButton("New population");
        new_population_button.addActionListener(new nwp());
        new_population_button.setBounds(W - 300, 610, 125, 20);
        add(new_population_button);
        //
        render_button.addActionListener(new rndr());
        render_button.setBounds(W - 300, 635, 125, 20);
        add(render_button);
        //
        record_button.addActionListener(new rcrd());
        record_button.setBounds(W - 170, 635, 125, 20);
        add(record_button);
        //
        JButton kill_button = new JButton("Kill all");
        kill_button.addActionListener(new kill_all());
        kill_button.setBounds(W - 170, 610, 125, 20);
        add(kill_button);
        //
		timer.start();
	}
	public void paintComponent(Graphics canvas) {
		super.paintComponent(canvas);
		//for (int x = 0; x < 162; x++) {
		//	for (int y = 0; y < 108; y++) {
		//		if (Map[x][y] == 1) {
		//			canvas.setColor(green);
		//			canvas.fillRect(x * 10, y * 10, 10, 10);
		//		}else if (Map[x][y] == 2){
		//			canvas.setColor(red);
		//			canvas.fillRect(x * 10, y * 10, 10, 10);
		//		}
		//	}
		//}
		canvas.setColor(white);
		canvas.fillRect(0, 0, world_scale[0] * 10, world_scale[1] * 10);
		if (render) {
			for(Bot b: objects) {
				b.Draw(canvas, draw_type);
			}
		}
		canvas.setColor(black);
		canvas.setFont(new Font("arial", Font.BOLD, 18));
		canvas.drawString("Main: ", W - 300, 20);
		canvas.drawString("version 1.3", W - 300, 40);
		canvas.drawString("steps: " + String.valueOf(steps), W - 300, 60);
		canvas.drawString("objects: " + String.valueOf(obj_count) + ", bots: " + String.valueOf(b_count), W - 300, 80);
		if (draw_type == 0) {
			txt = "predators view";
		}else if (draw_type == 1) {
			txt = "color view";
		}else if (draw_type == 2) {
			txt = "energy view";
		}else if (draw_type == 3) {
			txt = "minerals view";
		}else if (draw_type == 4){
			txt = "age view";
		}else if (draw_type == 5) {
			txt = "memory view";
		}else if (draw_type == 6) {
			txt = "brain layer view";
		}else if (draw_type == 7) {
			txt = "recombination view";
		}
		canvas.drawString("render type: " + txt, W - 300, 100);
		if (mouse == 0) {
			txt2 = "select";
		}else if (mouse == 1) {
			txt2 = "set";
		}else {
			txt2 = "remove";
		}
		canvas.drawString("mouse function: " + txt2, W - 300, 120);
		canvas.drawString("Render types:", W - 300, 180);
		canvas.drawString("Selection:", W - 300, 300);
		canvas.drawString("enter name:", W - 300, 415);
		canvas.drawString("Mouse functions:", W - 300, 480);
		canvas.drawString("Load:", W - 300, 520);
		canvas.drawString("enter name:", W - 300, 540);
		canvas.drawString("Controls:", W - 300, 605);
		if (selection != null) {
			canvas.drawString("energy: " + String.valueOf(selection.energy) + ", minerals: " + String.valueOf(selection.minerals), W - 300, 320);
			canvas.drawString("age: " + String.valueOf(selection.age), W - 300, 340);
			canvas.drawString("position: " + "[" + String.valueOf(selection.xpos) + ", " + String.valueOf(selection.ypos) + "]", W - 300, 360);
			canvas.drawString("color: " + "(" + String.valueOf(selection.color.getRed()) + ", " + String.valueOf(selection.color.getGreen()) + ", " + String.valueOf(selection.color.getBlue()) + ")", W - 300, 380);
			canvas.drawString("memory: " + String.valueOf(selection.memory), W - 300, 400);
			canvas.setColor(new Color(90, 90, 90, 90));
			canvas.fillRect(0, 0, W - 300, 1080);
			canvas.setColor(new Color(255, 0, 0));
			canvas.fillRect(selection.xpos * 10, selection.ypos * 10, 10, 10);
		}else {
			canvas.drawString("none", W - 300, 320);
		}
		if (sh_brain) {
			canvas.setColor(new Color(90, 90, 90));
			canvas.fillRect(0, 0, 360, 360);
			canvas.setColor(new Color(128, 128, 128));
			for (int x = 0; x < 8; x++) {
				for (int y = 0; y < 8; y++) {
					canvas.setColor(new Color(128, 128, 128));
					canvas.fillRect(x * 45, y * 45, 40, 40);
					canvas.setColor(new Color(0, 0, 0));
					canvas.drawString(String.valueOf(selection.commands[x + y * 8]), x * 45 + 20, y * 45 + 20);
				}
			}
		}
		if (rec && steps % 25 == 0) {
			try {
				BufferedImage buff = new BufferedImage(world_scale[0] * 10, world_scale[1] * 10, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2d = buff.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				for(Bot b: objects) {
					b.Draw(g2d, 0);
				}
				g2d.dispose();
				//
				BufferedImage buff2 = new BufferedImage(world_scale[0] * 10, world_scale[1] * 10, BufferedImage.TYPE_INT_RGB);
				g2d = buff2.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				for(Bot b: objects) {
					b.Draw(g2d, 2);
				}
				g2d.dispose();
				//
				BufferedImage buff3 = new BufferedImage(world_scale[0] * 10, world_scale[1] * 10, BufferedImage.TYPE_INT_RGB);
				g2d = buff3.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				for(Bot b: objects) {
					b.Draw(g2d, 1);
				}
				g2d.dispose();
				//
				BufferedImage buff4 = new BufferedImage(world_scale[0] * 10, world_scale[1] * 10, BufferedImage.TYPE_INT_RGB);
				g2d = buff4.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				for(Bot b: objects) {
					b.Draw(g2d, 5);
				}
				g2d.dispose();
				//
				BufferedImage buff5 = new BufferedImage(world_scale[0] * 10, world_scale[1] * 10, BufferedImage.TYPE_INT_RGB);
				g2d = buff5.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				for(Bot b: objects) {
					b.Draw(g2d, 6);
				}
				g2d.dispose();
				//
				BufferedImage buff6 = new BufferedImage(world_scale[0] * 10, world_scale[1] * 10, BufferedImage.TYPE_INT_RGB);
				g2d = buff6.createGraphics();
				g2d.setColor(Color.WHITE);
				g2d.fillRect(0, 0, 1920, 1080);
				for(Bot b: objects) {
					b.Draw(g2d, 7);
				}
				g2d.dispose();
				//
				ImageIO.write(buff, "png", new File("record/predators/screen" + String.valueOf(steps / 25)+ ".png"));
				ImageIO.write(buff2, "png", new File("record/energy/screen" + String.valueOf(steps / 25)+ ".png"));
				ImageIO.write(buff3, "png", new File("record/color/screen" + String.valueOf(steps / 25)+ ".png"));
				ImageIO.write(buff4, "png", new File("record/memory/screen" + String.valueOf(steps / 25)+ ".png"));
				ImageIO.write(buff5, "png", new File("record/brain_layer/screen" + String.valueOf(steps / 25)+ ".png"));
				ImageIO.write(buff6, "png", new File("record/recombination/screen" + String.valueOf(steps / 25)+ ".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void newPopulation() {
		steps = 0;
		objects = new ArrayList<Bot>();
		Map = new int[world_scale[0]][world_scale[1]];//0 - none, 1 - bot, 2 - organics
		for (int i = 0; i < 200; i++) {
			while(true){
				int x = rand.nextInt(world_scale[0]);
				int y = rand.nextInt(world_scale[1]);
				if (Map[x][y] == 0) {
					objects.add(new Bot(
						x,
						y,
						new Color(rand.nextInt(256),rand.nextInt(256), rand.nextInt(256)),
						1000,
						Map,
						objects
					));
					Map[x][y] = 1;
					break;
				}
			}
		}
		repaint();
	}
	private class BotListener extends MouseAdapter implements ActionListener{
		public void mousePressed(MouseEvent e) {
			if (e.getX() < world_scale[0] * 10 && e.getY() < world_scale[1] * 10) {
				botpos[0] = e.getX() / 10;
				botpos[1] = e.getY() / 10;
				if (mouse == 0) {//select
					if (Map[botpos[0]][botpos[1]] == 1) {
						for(Bot b: objects) {
							if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
								selection = b;
								save_button.setEnabled(true);
								show_brain_button.setEnabled(true);
							}
						}
					}else {
						selection = null;
						save_button.setEnabled(false);
						show_brain_button.setEnabled(false);
						sh_brain = false;
					}
				}else if (mouse == 1) {//set
					if (Map[botpos[0]][botpos[1]] == 0) {
						if (for_set_operators != null && for_set_conditions != null && for_set_commands != null) {
							Bot new_bot = new Bot(botpos[0], botpos[1], new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)), 1000, Map, objects);
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 2; j++) {
									new_bot.operators[i][j] = for_set_operators[i][j];
								}
							}
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 3; j++) {
									for (int k = 0; k < 9; k++) {
										new_bot.conditions[i][j][k] = for_set_conditions[i][j][k];
									}
								}
							}
							for (int i = 0; i < 8; i++) {
								for (int j = 0; j < 4; j++) {
									for (int k = 0; k < 2; k++) {
										new_bot.commands[i][j][k] = for_set_commands[i][j][k];
									}
								}
							}
							objects.add(new_bot);
							Map[botpos[0]][botpos[1]] = 1;
						}
					}
				}else {//remove
					if (Map[botpos[0]][botpos[1]] != 0) {
						for(Bot b: objects) {
							if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
								b.energy = 0;
								b.killed = 1;
								Map[botpos[0]][botpos[1]] = 0;
							}
						}
					}
				}
			}
		}
		public void mouseDragged(MouseEvent e) {
			if (e.getX() < world_scale[0] * 10 && e.getY() < world_scale[1] * 10) {
				botpos[0] = e.getX() / 10;
				botpos[1] = e.getY() / 10;
				if (mouse == 1) {//set
					if (Map[botpos[0]][botpos[1]] == 0) {
						if (for_set_operators != null && for_set_conditions != null && for_set_commands != null) {
							Bot new_bot = new Bot(botpos[0], botpos[1], new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)), 1000, Map, objects);
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 2; j++) {
									new_bot.operators[i][j] = for_set_operators[i][j];
								}
							}
							for (int i = 0; i < 7; i++) {
								for (int j = 0; j < 3; j++) {
									for (int k = 0; k < 9; k++) {
										new_bot.conditions[i][j][k] = for_set_conditions[i][j][k];
									}
								}
							}
							for (int i = 0; i < 8; i++) {
								for (int j = 0; j < 4; j++) {
									for (int k = 0; k < 2; k++) {
										new_bot.commands[i][j][k] = for_set_commands[i][j][k];
									}
								}
							}
							objects.add(new_bot);
							Map[botpos[0]][botpos[1]] = 1;
						}
					}
				}else if (mouse == 2) {//remove
					if (Map[botpos[0]][botpos[1]] != 0) {
						for(Bot b: objects) {
							if (b.xpos == botpos[0] && b.ypos == botpos[1]) {
								b.energy = 0;
								b.killed = 1;
								Map[botpos[0]][botpos[1]] = 0;
							}
						}
					}
				}
			}
		}
		public void actionPerformed(ActionEvent e) {
			if (!pause) {
				steps++;
				b_count = 0;
				obj_count = 0;
				org_count = 0;
				ListIterator<Bot> bot_iterator = objects.listIterator();
				while (bot_iterator.hasNext()) {
					Bot next_bot = bot_iterator.next();
					next_bot.Update(bot_iterator);
					if (selection != null) {
						if (next_bot.xpos == selection.xpos && next_bot.ypos == selection.ypos) {
							if (next_bot != selection) {
								selection = null;
								save_button.setEnabled(false);
								show_brain_button.setEnabled(false);
								sh_brain = false;
							}
						}
					}
					obj_count++;
					if (next_bot.state != 0) {
						org_count++;
					}else {
						b_count++;
					}
				}
				if (selection != null) {
					if (selection.killed == 1 || Map[selection.xpos][selection.ypos] != 1 || selection.state != 0){
						selection = null;
						save_button.setEnabled(false);
						show_brain_button.setEnabled(false);
						sh_brain = false;
					}
				}
			}
			ListIterator<Bot> iterator = objects.listIterator();
			while (iterator.hasNext()) {
				Bot next_bot = iterator.next();
				if (next_bot.killed == 1) {
					iterator.remove();
				}
			}
			repaint();
			
		}
		
	}
	private class dr1 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 0;
		}
	}
	private class dr2 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 1;
		}
	}
	private class dr3 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 2;
		}
	}
	private class dr4 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 3;
		}
	}
	private class dr5 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 4;
		}
	}
	private class dr6 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 5;
		}
	}
	private class dr7 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 6;
		}
	}
	private class dr8 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			draw_type = 7;
		}
	}
	private class start_stop implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			pause = !pause;
			if (pause) {
				stop_button.setText("Start");
			}else {
				stop_button.setText("Stop");
			}
		}
	}
	private class select implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 0;
		}
	}
	private class set implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 1;
		}
	}
	private class remove implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			mouse = 2;
		}
	}
	private class nwp implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			newPopulation();
		}
	}
	private class rndr implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			render = !render;
			if (render) {
				render_button.setText("Render: on");
			}else {
				render_button.setText("Render: off");
			}
		}
	}
	private class rcrd implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			rec = !rec;
			if (rec) {
				record_button.setText("Record: on");
			}else {
				record_button.setText("Record: off");
			}
		}
	}
	private class shbr implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			sh_brain = !sh_brain;
			if (pause == false) {
				pause = true;
			}else if (sh_brain == false) {
				pause = false;
			}
		}
	}
	private class kill_all implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			steps = 0;
			objects = new ArrayList<Bot>();
			Map = new int[162][108];//0 - none, 1 - bot, 2 - organics
		}
	}
	private class save_bot implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String txt = "";
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 2; j++) {
					txt += String.valueOf(selection.operators[i][j]) + " ";
				}
				txt += ":";
			}
			txt += ";";
			//
			for (int i = 0; i < 7; i++) {
				for (int j = 0; j < 3; j++) {
					for (int k = 0; k < 9; k++) {
						txt += String.valueOf(selection.conditions[i][j][k]) + " ";
					}
					txt += ":";
				}
				txt += "/";
			}
			txt += ";";
			//
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 4; j++) {
					for (int k = 0; k < 2; k++) {
						txt += String.valueOf(selection.commands[i][j][k]) + " ";
					}
					txt += ":";
				}
				txt += "/";
			}
			txt += ";";
			//
			try {
	            FileWriter fileWriter = new FileWriter("saved objects/" + for_save.getText() + ".dat");
	            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	 
	            bufferedWriter.write(txt);
	 
	            bufferedWriter.close();
	        } catch (IOException ex) {
	            System.out.println("Ошибка при записи в файл");
	            ex.printStackTrace();
	        }
		}
	}
	private class load_bot implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			try {
				for_set_operators = new int[7][2];
				for_set_conditions = new int[7][3][9];
				for_set_commands = new int[8][4][2];
				
	            FileReader fileReader = new FileReader("saved objects/" + for_load.getText() + ".dat");
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	 
	            String line = bufferedReader.readLine();
	 
	            bufferedReader.close();
	            
	            String[] sec = line.split(";");
	            
	            String[] sec0_layers = sec[0].split(":");
	            for (int i = 0; i < 7; i++) {
	            	String[] sec0_layer = sec0_layers[i].split(" ");
					for (int j = 0; j < 2; j++) {
						for_set_operators[i][j] = Integer.parseInt(sec0_layer[j]);
					}
	            }
	            String[] sec1_layers = sec[1].split("/");
	            for (int i = 0; i < 7; i++) {
	            	String[] sec1_layer = sec1_layers[i].split(":");
					for (int j = 0; j < 3; j++) {
						String[] sec1_symbols = sec1_layer[j].split(" ");
						for (int k = 0; k < 9; k++) {
							for_set_conditions[i][j][k] = Integer.parseInt(sec1_symbols[k]);
						}
					}
	            }
	            String[] sec2_layers = sec[2].split("/");
	            for (int i = 0; i < 8; i++) {
	            	String[] sec2_layer = sec2_layers[i].split(":");
					for (int j = 0; j < 4; j++) {
						String[] sec2_symbols = sec2_layer[j].split(" ");
						for (int k = 0; k < 2; k++) {
							for_set_commands[i][j][k] = Integer.parseInt(sec2_symbols[k]);
						}
					}
	            }
	        } catch (IOException ex) {
	            System.out.println("Ошибка при чтении файла");
	            ex.printStackTrace();
	        }
		}
	}
}
