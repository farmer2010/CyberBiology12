from random import randint as rand
import pygame
pygame.init()

screen = pygame.display.set_mode((1920, 1080))
keep_going = 1

def render_text(text, pos, color=(0, 0, 0), size=24, centerx=False, centery=False):#отрисовка текста на экране
    font = pygame.font.SysFont(None, size)
    text_img = font.render(text, True, color)
    text_rect = text_img.get_rect()
    if centerx:
        text_rect.centerx = pos[0]
    else:
        text_rect.x = pos[0]
    if centery:
        text_rect.centery = pos[1]
    else:
        text_rect.y = pos[1]
    screen.blit(text_img, text_rect)

file = open("bot.dat")#имя вводить сюда
text = file.readline()
file.close()

operators = [[0 for j in range(2)] for i in range(7)]
conditions = [[[0 for k in range(9)] for j in range(3)] for i in range(7)]
commands = [[[0 for k in range(2)] for j in range(4)] for i in range(8)]

genom_view = ["" for i in range(16)]
genom_view[14] = "иначе:"

sec = text.split(";")

sec0_layers = sec[0].split(":")
for i in range(7):
    sec0_layer = sec0_layers[i].split(" ")
    for j in range(2):
        operators[i][j] = int(sec0_layer[j])

sec1_layers = sec[1].split("/")
for i in range(7):
    sec1_layer = sec1_layers[i].split(":")
    for j in range(3):
        sec1_symbols = sec1_layer[j].split(" ")
        for k in range(9):
            conditions[i][j][k] = int(sec1_symbols[k])

sec2_layers = sec[2].split("/")
for i in range(8):
    sec2_layer = sec2_layers[i].split(":")
    for j in range(4):
        sec2_symbols = sec2_layer[j].split(" ")
        for k in range(2):
            commands[i][j][k] = int(sec2_symbols[k])

def number_to_text(numbers):
    ret = ""
    number1 = number_to_text2(numbers[1])
    number2 = number_to_text2(numbers[2])
    if numbers[0] % 2 == 0:
        if numbers[3] % 4 == 0:#сложение
            math = " + "
        elif numbers[3] % 4 == 1:#вычитание
            math = " - "
        elif numbers[3] % 4 == 2:#умножение
            math = " * "
        elif numbers[3] % 4 == 3:#деление
            math = " / "
        ret = number1 + math + number2
    else:
        ret = number1
    return(ret)

def number_to_text2(number):
    ret = ""
    if number > 31:
        if number % 8 == 0:#энергия
            ret = "энергия"
        elif number % 8 == 1:#минералы
            ret = "минералы"
        elif number % 8 == 2:#возраст
            ret = "возраст"
        elif number % 8 == 3:#память
            ret = "память"
        elif number % 8 == 4:#направление
            ret = "направление"
        elif number % 8 == 5:#позиция x
            ret = "позиция x"
        elif number % 8 == 6:#позиция y
            ret = "позиция y"
        elif number % 8 == 7:#зрение
            ret = "зрение"
    else:
        ret = str(number * 2)
    return(ret)

def condition_to_text(cond):
    num1 = number_to_text([cond[0], cond[1], cond[2], cond[3]])
    num2 = number_to_text([cond[5], cond[6], cond[7], cond[8]])
    c = ""
    if cond[4] % 6 == 0:#равно
        c = " == "
    elif cond[4] % 6 == 1:#не равно
        c = " != "
    elif cond[4] % 6 == 2:#больше
        c = " > "
    elif cond[4] % 6 == 3:#меньше
        c = " < "
    elif cond[4] % 6 == 4:#больше - равно
        c = " >= "
    elif cond[4] % 6 == 5:#меньше - равно
        c = " <= "
    return("(" + num1 + c + num2 + ")")

def layer_to_text(layer_condition, layer_operators):
    text = "если "
    for i in range(3):
        text += condition_to_text(layer_condition[i])
        if i < 2:
            if layer_operators[i] % 6 == 0:#or
                text += " || "
            elif layer_operators[i] % 6 == 1:#and
                text += " && "
            elif layer_operators[i] % 6 == 2:#xor
                text += " ^ "
            elif layer_operators[i] % 6 == 3:#not or
                text += " !|| "
            elif layer_operators[i] % 6 == 4:#not and
                text += " !&& "
            elif layer_operators[i] % 6 == 5:#not xor
                text += " !^ "
    text += ", то:"
    return(text)

def command_to_text(cmd):
    ret = ""
    if cmd[0] % 32 == 0 or cmd[0] % 32 == 25:
        ret = "фотосинтез"
    elif cmd[0] % 32 == 1 or cmd[0] % 32 == 26:
        ret = "преобразовать минералы в энергию"
    elif cmd[0] % 32 == 2 or cmd[0] % 32 == 27:
        ret = "походить абсолютно"
    elif cmd[0] % 32 == 3 or cmd[0] % 32 == 28:
        ret = "походить в направлении "
        ret += str(cmd[1] % 8)
    elif cmd[0] % 32 == 4 or cmd[0] % 32 == 29:
        ret = "атаковать абсолютно"
    elif cmd[0] % 32 == 5 or cmd[0] % 32 == 30:
        ret = "атаковать в направлении "
        ret += str(cmd[1] % 8)
    elif cmd[0] % 32 == 6 or cmd[0] % 32 == 31:
        ret = "повернуться на "
        ret += str(cmd[1] % 8)
    elif cmd[0] % 32 == 7:
        ret = "сменить направление на "
        ret += str(cmd[1] % 8)
    elif cmd[0] % 32 == 8:
        ret = "отдать часть ресурсов абсолютно"
    elif cmd[0] % 32 == 9:
        ret = "отдать часть ресурсов в направлении "
        ret += str(cmd[1] % 8)
    elif cmd[0] % 32 == 10:
        ret = "равномерное распределение ресурсов абсолютно"
    elif cmd[0] % 32 == 11:
        ret = "равномерное распределение ресурсов в направлении "
        ret += str(cmd[1] % 8)
    elif cmd[0] % 32 == 12:
        ret = "поделиться абсолютно"
    elif cmd[0] % 32 == 13:
        ret = "поделиться в направлении "
        ret += str(cmd[1] % 8)
    elif cmd[0] % 32 == 14:
        ret = "установить направление в случайное"
    elif cmd[0] % 32 == 15:
        ret = "записать в память "
        ret += str(cmd[1])
    elif cmd[0] % 32 == 16:
        ret = "записать в память случайное число"
    elif cmd[0] % 32 == 17:
        ret = "прибавить к памяти "
        ret += str(cmd[1])
    elif cmd[0] % 32 == 18:
        ret = "записать энергию в память"
    elif cmd[0] % 32 == 19:
        ret = "записать минералы в память"
    elif cmd[0] % 32 == 20:
        ret = "записать возраст в память"
    elif cmd[0] % 32 == 21:
        ret = "записать направление в память"
    elif cmd[0] % 32 == 22:
        ret = "записать зрение в память"
    elif cmd[0] % 32 == 23:
        ret = "рекомбинация в направлении "
        ret += str(cmd[1] % 8)
    elif cmd[0] % 32 == 24:
        ret = "рекомбинация абсолютно"
    return(ret)

def layer_command_to_text(layer_command):
    ret = ""
    for i in range(4):
        ret += command_to_text(layer_command[i])
        ret += "; "
    return(ret)

for i in range(7):
    genom_view[i * 2] = layer_to_text(conditions[i], operators[i])

for i in range(8):
    genom_view[i * 2 + 1] = layer_command_to_text(commands[i])

def draw():
    screen.fill((255, 255, 255))
    for i in range(16):
        render_text(genom_view[i], (0, 20 * i + (i // 2) * 20))

while keep_going:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            keep_going = 0
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_ESCAPE:
                keep_going = 0
    draw()
    pygame.display.update()
pygame.quit()
