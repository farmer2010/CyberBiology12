package sct;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.ListIterator;

public class Bot{
	ArrayList<Bot> objects;
	Random rand = new Random();
	private int x;
	private int y;
	public int xpos;
	public int ypos;
	public Color color;
	public int energy;
	public int minerals;
	public int killed = 0;
	public int[][] map;
	public int[][] operators = new int[7][2];
	public int[][][] conditions = new int[7][3][9];
	public int[][][] commands = new int[8][4][2];
	private int[] indexes = new int[8];
	public int memory = 0;
	public int age = 1000;
	public int state = 0;//бот или органика
	public int state2 = 1;//что ставить в массив с миром
	private int rotate = rand.nextInt(8);
	private int[][] movelist = {
		{0, -1},
		{1, -1},
		{1, 0},
		{1, 1},
		{0, 1},
		{-1, 1},
		{-1, 0},
		{-1, -1}
	};
	private int[] minerals_list = {
		1,
		2,
		3
	};
	private int[] photo_list = {
		10,
		8,
		6,
		5,
		4,
		3
	};
	private int[] world_scale = {100, 100};
	private int c_red = 0;
	private int c_green = 0;
	private int c_blue = 0;
	private int sector_len = world_scale[1] / 8;
	public int stop = 0;
	public int recomb_time = 0;
	public Bot(int new_xpos, int new_ypos, Color new_color, int new_energy, int[][] new_map, ArrayList<Bot> new_objects) {
		xpos = new_xpos;
		ypos = new_ypos;
		x = new_xpos * 10;
		y = new_ypos * 10;
		color = new_color;
		energy = new_energy;
		minerals = 0;
		objects = new_objects;
		map = new_map;
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 2; j++) {
				operators[i][j] = rand.nextInt(64);
			}
		}
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 9; k++) {
					conditions[i][j][k] = rand.nextInt(64);
				}
			}
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 2; k++) {
					commands[i][j][k] = rand.nextInt(64);
				}
			}
		}
	}
	public void Draw(Graphics canvas, int draw_type) {
		if (state == 0) {//рисуем бота
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x, y, 10, 10);
			if (draw_type == 0) {//режим отрисовки хищников
				int r = 0;
				int g = 0;
				int b = 0;
				if (c_red + c_green + c_blue == 0) {
					r = 128;
					g = 128;
					b = 128;
				}else {
					r = (int)((c_red * 1.0) / (c_red + c_green + c_blue) * 255.0);
					g = (int)((c_green * 1.0) / (c_red + c_green + c_blue) * 255.0);
					b = (int)((c_blue * 1.0) / (c_red + c_green + c_blue) * 255.0);
				}
				canvas.setColor(new Color(r, g, b));
			}else if (draw_type == 1) {//цвета
				canvas.setColor(color);
			}else if (draw_type == 2) {//энергии
				int g = 255 - (int)(energy / 1000.0 * 255.0);
				if (g > 255) {
					g = 255;
				}else if (g < 0) {
					g = 0;
				}
				canvas.setColor(new Color(255, g, 0));
			}else if (draw_type == 3) {//минералов
				int rg = 255 - (int)(minerals / 1000.0 * 255.0);
				if (rg > 255) {
					rg = 255;
				}else if (rg < 0) {
					rg = 0;
				}
				canvas.setColor(new Color(rg, rg, 255));
			}else if (draw_type == 4) {//возраста
				canvas.setColor(new Color((int)(age / 1000.0 * 255.0), (int)(age / 1000.0 * 255.0), (int)(age / 1000.0 * 255.0)));
			}else if (draw_type == 5) {//памяти
				try {
					canvas.setColor(new Color(0, 255 - memory * 4, memory * 4));
				}catch (java.lang.IllegalArgumentException ex){
					System.out.println(memory);
				}
			}else if (draw_type == 6) {//слоя мозга
				int u = stop * 32;
				if (u > 255) {
					u = 255;
				}
				canvas.setColor(new Color(0, 255 - u, u));
			}else if (draw_type == 7) {//рекомбинации
				canvas.setColor(new Color(255 / 9 * recomb_time, 255 / 9 * recomb_time, 255 - 255 / 9 * recomb_time));
			}
			canvas.fillRect(x + 1, y + 1, 8, 8);
		}else {//рисуем органику
			canvas.setColor(new Color(0, 0, 0));
			canvas.fillRect(x + 1, y + 1, 8, 8);
			canvas.setColor(new Color(128, 128, 128));
			canvas.fillRect(x + 2, y + 2, 6, 6);
		}
	}
	public int Update(ListIterator<Bot> iterator) {
		if (killed == 0) {
			if (state == 0) {//бот
				if (memory > 63) {
					memory = 63;
				}
				int sector = bot_in_sector();
				energy--;
				age--;
				if (sector <= 7 & sector >= 5) {
					minerals += minerals_list[sector - 5];
				}
				update_commands(iterator);
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = 0;
					return(0);
				}else if (energy > 1000) {
					energy = 1000;
				}
				if (energy >= 800) {//автоматическое деление
					multiply(rotate, iterator);
				}
				if (age <= 0) {
					state = 1;
					state2 = 2;
					map[xpos][ypos] = 2;
					return(0);
				}
				if (minerals > 1000) {
					minerals = 1000;
				}
				if (recomb_time > 0) {
					recomb_time--;
				}
			}else if (state == 1) {//падающая органика
				move(4);
				int[] pos = get_rotate_position(4);
				if (pos[1] > 0 & pos[1] < world_scale[1]) {
					if (map[pos[0]][pos[1]] != 0) {
						state = 2;
					}
				}
			}else {//стоящая органика
				//
			}
		}
		return(0);
	}
	public void update_commands(ListIterator<Bot> iterator) {//мозг
		for (int i = 0; i < 8; i++) {
			if (i < 7) {
				if (condition2(i)) {
					command(i, iterator);
					stop = i;
					break;
				}
			}else {
				command(i, iterator);
				stop = 8;
			}
		}
	}
	public boolean condition(int index, int index2) {
		int[] cond = conditions[index][index2];
		int[] cmd = {cond[0], cond[1], cond[2], cond[3]};
		int num1 = number(cmd);
		int[] cmd2 = {cond[5], cond[6], cond[7], cond[8]};
		int num2 = number(cmd2);
		boolean ret = false;
		if (cond[4] % 6 == 0) {//==
			ret = num1 == num2;
		}else if (cond[4] % 6 == 1) {//!=
			ret = num1 != num2;
		}else if (cond[4] % 6 == 2) {//>
			ret = num1 > num2;
		}else if (cond[4] % 6 == 3) {//<
			ret = num1 < num2;
		}else if (cond[4] % 6 == 4) {//>=
			ret = num1 >= num2;
		}else if (cond[4] % 6 == 5) {//<=
			ret = num1 <= num2;
		}
		return(ret);
	}
	public int number(int[] cmd) {
		int ret = 0;
		int num1 = number2(cmd[1]);
		int num2 = number2(cmd[2]);
		if (cmd[0] % 2 == 0) {//использовать ли математику?
			if (cmd[3] % 4 == 0) {//сложение
				ret = border(num1 + num2, 63, 0);
			}else if (cmd[3] % 4 == 1) {//вычитание
				ret = border(num1 - num2, 63, 0);
			}else if (cmd[3] % 4 == 2) {//умножение
				ret = border(num1 * num2, 63, 0);
			}else if (cmd[3] % 4 == 3) {//деление
				if (num2 != 0) {
					ret = border(num1 / num2, 63, 0);
				}else {
					ret = 0;
				}
			}
		}else {
			ret = num1;
		}
		return(ret);
	}
	public int number2(int num) {
		int ret = 0;
		if (num > 31) {
			if (num % 8 == 0) {//энергию в число
				ret = (int)(energy / 1000.0 * 63);
			}else if (num % 8 == 1) {//минералы в число
				ret = (int)(minerals / 1000.0 * 63);
			}else if (num % 8 == 2) {//возраст в число
				ret = (int)(age / 1000.0 * 63);
			}else if (num % 8 == 3) {//память в число
				ret = memory;
			}else if (num % 8 == 4) {//направление в число
				ret = rotate * 8;
			}else if (num % 8 == 5) {//xpos в число
				ret = (int)(xpos / (world_scale[0] * 1.0) * 63);
			}else if (num % 8 == 6) {//ypos в число
				ret = (int)(ypos / (world_scale[1] * 1.0) * 63);
			}else if (num % 8 == 7) {//зрение в число
				ret = see(rotate) * 15;
			}
		}else {
			ret = num * 2;
		}
		return(ret);
	}
	public boolean condition2(int index) {
		boolean ret = condition(index, 0);
		for (int i = 1; i < 3; i++) {
			if (operators[index][i - 1] % 6 == 0) {//or
				ret = ret || condition(index, i);
			}else if (operators[index][i - 1] % 6 == 1) {//and
				ret = ret && condition(index, i);
			}else if (operators[index][i - 1] % 6 == 2) {//xor
				ret = (ret || condition(index, i)) && !(ret && condition(index, i));
			}else if (operators[index][i - 1] % 6 == 3){//!or
				ret = !(ret || condition(index, i));
			}else if (operators[index][i - 1] % 6 == 4){//!and
				ret = !(ret && condition(index, i));
			}else {//!xor
				ret = !((ret || condition(index, i)) && !(ret && condition(index, i)));
			}
		}
		return(ret);
	}
	public void command(int index, ListIterator<Bot> iterator) {
		for (int i = 0; i < 4; i++) {
			int[] command = commands[index][indexes[index]];
			if (command[0] % 32 == 0 || command[0] % 32 == 25) {//фотосинтез
				int sector = bot_in_sector();
				if (sector <= 5) {
					energy += photo_list[sector];
					c_green += 1;
				}
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 1 || command[0] % 32 == 26) {//минералы в энергию
				if (minerals > 0) {
					c_blue++;
				}
				energy += minerals * 4;
				minerals = 0;
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 2 || command[0] % 32 == 27) {//походить абсолютно
				int sens = move(rotate);
				if (sens == 1) {
					energy -= 1;
				}
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 3 || command[0] % 32 == 28) {//походить относительно
				int sens = move(command[1] % 8);
				if (sens == 1) {
					energy -= 1;
				}
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 4 || command[0] % 32 == 29) {//атаковать абсолютно
				attack(rotate);
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 5 || command[0] % 32 == 30) {//атаковать относительно
				attack(command[1] % 8);
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 6 || command[0] % 32 == 31) {//повернуться
				rotate += command[1] % 8;
				rotate %= 8;
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 7) {//сменить направление
				rotate = command[1] % 8;
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 8) {//отдать часть ресурсов абсолютно
				give(rotate);
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 9) {//отдать часть ресурсов относительно
				give(command[1] % 8);
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 10) {//равномерное распределение ресурсов абсолютно
				give2(rotate);
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 11) {//равномерное распределение ресурсов относительно
				give2(command[1] % 8);
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 12) {//поделиться абсолютно
				multiply(rotate, iterator);
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 13) {//поделиться относительно
				multiply(command[1] % 8, iterator);
				indexes[index]++;
				indexes[index] %= 4;
				break;
			}else if (command[0] % 32 == 14) {//установить направление в случайное
				rotate = rand.nextInt(8);
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 15) {//записать в память число
				memory = command[1];
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 16) {//записать в память случайное число
				memory = rand.nextInt(64);
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 17) {//прибавить к памяти число
				memory += command[1];
				if (memory > 63) {
					memory = 63;
				}
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 18) {//записать энергию в память
				int m = (int)(energy / 1000.0 * 63);
				if (m > 63) {
					m = 63;
				}
				memory = m;
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 19) {//записать минералы в память
				int m = (int)(minerals / 1000.0 * 63);
				if (m > 63) {
					m = 63;
				}
				memory = m;
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 20) {//записать возраст в память
				memory = (int)(age / 1000.0 * 63);
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 21) {//записать направление в память
				memory = rotate * 8;
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 22) {//записать зрение в память
				memory = see(rotate) * 15;
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 23) {//рекомбинация относительно
				recombination(command[1] % 8);
				indexes[index]++;
				indexes[index] %= 4;
			}else if (command[0] % 32 == 24) {//рекомбинация абсолютно
				recombination(rotate);
				indexes[index]++;
				indexes[index] %= 4;
			}
		}
	}
	public void recombination(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 && pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 1) {
				Bot b = find(pos);
				if (b != null) {
					recomb_time = 9;
					int red = (color.getRed() + b.color.getRed()) / 2;
					int green = (color.getGreen() + b.color.getGreen()) / 2;
					int blue = (color.getBlue() + b.color.getBlue()) / 2;
					color = new Color(red, green, blue);
					b.color = new Color(red, green, blue);
					//
					int[][] new_operators = new int[7][2];
					int[][][] new_conditions = new int[7][3][9];
					int[][][] new_commands = new int[8][4][2];
					//
					for (int i = 0; i < 7; i++) {
						if (rand.nextInt(2) == 0) {
							for (int j = 0; j < 2; j++) {
								new_operators[i][j] = operators[i][j];
							}
						}else {
							for (int j = 0; j < 2; j++) {
								new_operators[i][j] = b.operators[i][j];
							}
						}
					}
					for (int i = 0; i < 7; i++) {
						if (rand.nextInt(2) == 0) {
							for (int j = 0; j < 3; j++) {
								for (int k = 0; k < 9; k++) {
									new_conditions[i][j][k] = conditions[i][j][k];
								}
							}
						}else {
							for (int j = 0; j < 3; j++) {
								for (int k = 0; k < 9; k++) {
									new_conditions[i][j][k] = b.conditions[i][j][k];
								}
							}
						}
					}
					for (int i = 0; i < 8; i++) {
						if (rand.nextInt(2) == 0) {
							for (int j = 0; j < 4; j++) {
								for (int k = 0; k < 2; k++) {
									new_commands[i][j][k] = commands[i][j][k];
								}
							}
						}else {
							for (int j = 0; j < 4; j++) {
								for (int k = 0; k < 2; k++) {
									new_commands[i][j][k] = b.commands[i][j][k];
								}
							}
						}
					}
					//
					for (int i = 0; i < 7; i++) {
						for (int j = 0; j < 2; j++) {
							operators[i][j] = new_operators[i][j];
							b.operators[i][j] = new_operators[i][j];
						}
					}
					for (int i = 0; i < 7; i++) {
						for (int j = 0; j < 3; j++) {
							for (int k = 0; k < 9; k++) {
								conditions[i][j][k] = new_conditions[i][j][k];
								b.conditions[i][j][k] = new_conditions[i][j][k];
							}
						}
					}
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 4; j++) {
							for (int k = 0; k < 2; k++) {
								commands[i][j][k] = new_commands[i][j][k];
								b.commands[i][j][k] = new_commands[i][j][k];
							}
						}
					}
				}
			}
		}
	}
	public int see(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 && pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 0) {
				return(0);//если ничего
			}else if (map[pos[0]][pos[1]] == 1) {
				Bot b = find(pos);
				if (b != null) {
					if (is_relative(color, b.color)) {
						return(3);//если родственник
					}else {
						return(2);//если враг
					}
				}else {
					return(0);//если ничего
				}
			}else if (map[pos[0]][pos[1]] == 2) {
				return(4);//если органика
			}
		}else {
			return(1);//если граница
		}
		return(0);//если ошибка
	}
	public void give(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 1) {
				Bot relative = find(pos);
				if (relative.killed == 0) {
					relative.energy += energy / 4;
					relative.minerals += minerals / 4;
					energy -= energy / 4;
					minerals -= minerals / 4;
				}
			}
		}
	}
	public void give2(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 1) {
				Bot relative = find(pos);
				if (relative.killed == 0) {
					int enr = relative.energy + energy;
					int mnr = relative.minerals + minerals;
					relative.energy = enr / 2;
					relative.minerals = mnr / 2;
					energy = enr / 2;
					minerals = mnr / 2;
				}
			}
		}
	}
	public void attack(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] != 0) {
				Bot victim = find(pos);
				if (victim != null) {
					victim.killed = 1;
					energy += victim.energy;
					map[pos[0]][pos[1]] = 0;
					c_red++;
				}
			}
		}
	}
	public Bot find(int[] pos) {//только если есть сосед
		for (Bot b: objects) {
			if (b.killed == 0 & b.xpos == pos[0] & b.ypos == pos[1]) {
				return(b);
			}
		}
		return(null);
	}
	public boolean is_relative(Color color1, Color color2) {
		boolean is_red = (color1.getRed() - 20 < color2.getRed()) && (color1.getRed() + 20 > color2.getRed());
		boolean is_green = (color1.getGreen() - 20 < color2.getGreen()) && (color1.getGreen() + 20 > color2.getGreen());
		boolean is_blue = (color1.getBlue() - 20 < color2.getBlue()) && (color1.getBlue() + 20 > color2.getBlue());
		return(is_red && is_green && is_blue);
	}
	public int[] get_rotate_position(int rot){
		int[] pos = new int[2];
		pos[0] = (xpos + movelist[rot][0]) % world_scale[0];
		pos[1] = ypos + movelist[rot][1];
		if (pos[0] < 0) {
			pos[0] = world_scale[0] - 1;
		}else if(pos[0] >= world_scale[0]) {
			pos[0] = 0;
		}
		return(pos);
	}
	public int move(int rot) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 0) {
				map[xpos][ypos] = 0;
				xpos = pos[0];
				ypos = pos[1];
				x = xpos * 10;
				y = ypos * 10;
				map[xpos][ypos] = state2;
				return(1);
			}
		}
		return(0);
	}
	public void multiply(int rot, ListIterator<Bot> iterator) {
		int[] pos = get_rotate_position(rot);
		if (pos[1] > 0 & pos[1] < world_scale[1]) {
			if (map[pos[0]][pos[1]] == 0) {
				energy -= 150;
				if (energy <= 0) {
					killed = 1;
					map[xpos][ypos] = 0;
				}else {
					map[pos[0]][pos[1]] = 1; 
					Color new_color = color;
					if (rand.nextInt(500) == 0) {
						new_color = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
					}else {
						new_color = new Color(border(color.getRed() + rand.nextInt(-12, 13), 255, 0), border(color.getGreen() + rand.nextInt(-12, 13), 255, 0), border(color.getBlue() + rand.nextInt(-12, 13), 255, 0));
					}
					//
					int[][] new_operators = new int[7][2];
					int[][][] new_conditions = new int[7][3][9];
					int[][][] new_commands = new int[8][4][2];
					for (int i = 0; i < 7; i++) {
						for (int j = 0; j < 2; j++) {
							new_operators[i][j] = operators[i][j];
						}
					}
					for (int i = 0; i < 7; i++) {
						for (int j = 0; j < 3; j++) {
							for (int k = 0; k < 9; k++) {
								new_conditions[i][j][k] = conditions[i][j][k];
							}
						}
					}
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 4; j++) {
							for (int k = 0; k < 2; k++) {
								new_commands[i][j][k] = commands[i][j][k];
							}
						}
					}
					//
					if (rand.nextInt(4) == 0) {//мутация
						new_operators[rand.nextInt(7)][rand.nextInt(2)] = rand.nextInt(64);
						new_conditions[rand.nextInt(7)][rand.nextInt(3)][rand.nextInt(9)] = rand.nextInt(64);
						new_commands[rand.nextInt(8)][rand.nextInt(4)][rand.nextInt(2)] = rand.nextInt(64);
					}
					//
					Bot new_bot = new Bot(pos[0], pos[1], new_color, energy / 2, map, objects);
					new_bot.minerals = minerals / 2;
					energy /= 2;
					minerals /= 2;
					new_bot.operators = new_operators;
					new_bot.conditions = new_conditions;
					new_bot.commands = new_commands;
					iterator.add(new_bot);
				}
			}
		}
	}
	public int bot_in_sector() {
		int sec = ypos / sector_len;
		if (sec > 7) {
			sec = 10;
		}
		return(sec);
	}
	public int border(int number, int border1, int border2) {
		if (number > border1) {
			number = border1;
		}else if (number < border2) {
			number = border2;
		}
		return(number);
	}
	public int max(int number1, int number2) {//максимальное из двух чисел
		if (number1 > number2) {
			return(number1);
		}else if (number2 > number1) {
			return(number2);
		}else {
			return(number1);
		}
	}
}
