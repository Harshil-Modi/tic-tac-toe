from tkinter import *

title = 'Tic Tac Toe'
root = Tk()
root.title(title)
root.iconbitmap('icon.ico')

CONST_WIN_BOT = 1
CONST_WIN_PLAYER = -1

btns_clicked = 0
board_size, check_adjacent_buttons = 3, 3
player, bot, player_turn = 'O', 'X', True


def checkLine(mark, is_row):
    global btns, board_size, check_adjacent_buttons
    for i in range(board_size):
        for j in range(board_size - check_adjacent_buttons + 1):
            count = 0
            matched_btns = list()
            for k in range(j, check_adjacent_buttons + j):
                if is_row:
                    if btns[i][k].cget('text') != mark:
                        break
                else:
                    if btns[k][i].cget('text') != mark:
                        break
                count += 1
                matched_btns.append((i, k) if is_row else (k, i))
            if count == check_adjacent_buttons:
                return True, matched_btns
    return False, None


def checkRows(mark):
    return checkLine(mark, True)


def checkCols(mark):
    return checkLine(mark, False)


def checkDiagonals(mark):
    global btns, board_size, check_adjacent_buttons

    # / diagonal
    for i in range(check_adjacent_buttons - 1, board_size):
        for j in range(board_size - check_adjacent_buttons + 1):
            count, k = 0, i
            matched_btns = list()
            for l in range(j, check_adjacent_buttons + j):
                if btns[l][k].cget('text') != mark:
                    break
                matched_btns.append((l, k))
                k -= 1
                count += 1
            if count == check_adjacent_buttons:
                return True, matched_btns

    # \ diagonal
    for i in range(board_size - check_adjacent_buttons, 0, -1):
        for j in range(board_size - check_adjacent_buttons + 1):
            count, l = 0, j
            matched_btns = list()
            for k in range(i, check_adjacent_buttons + i):
                if btns[k][l].cget('text') != mark:
                    break
                matched_btns.append((l, k))
                count += 1
                l += 1
            if count == check_adjacent_buttons:
                return True, matched_btns
    return False, None


def check_runner(mark):
    res_row, matched_btns = checkRows(mark)
    if res_row:
        return res_row, matched_btns

    res_col, matched_btns = checkCols(mark)
    if res_col:
        return res_col, matched_btns

    res_diagonal, matched_btns = checkDiagonals(mark)
    if res_diagonal:
        return res_diagonal, matched_btns
    else:
        return False, None


def check(is_computer_thinking=False):
    return_val, matched_btn = None, None

    res_bot = check_runner(bot)
    if res_bot[0]:
        return_val, matched_btn = CONST_WIN_BOT, res_bot[1]

    res_player = check_runner(player)
    if res_player[0]:
        return_val, matched_btn = CONST_WIN_PLAYER, res_player[1]

    if return_val and matched_btn and not is_computer_thinking:
        set_btns_state(DISABLED)
        set_btns_matched_bg(matched_btn)

    return return_val


def set_turn_text(str=None):
    global player_turn, lblTurn, player, bot
    lblTurn[
        'text'] = str if str else f"{ 'Player' if player_turn else 'Computer'}'s turn, put {player if player_turn else bot}"


def set_btn_text(i, j, text):
    global btns
    btns[i][j]["text"] = text


def set_btn_bg(i, j, bg=None):
    global btns
    btns[i][j].config(bg=bg if bg else 'red')


def set_btn_state(i, j, state):
    global btns
    btns[i][j].config(state=state)


def set_btns_matched_bg(matched_list):
    for i in matched_list:
        set_btn_bg(i[0], i[1])


def set_btns_state(state):
    for i in range(board_size):
        for j in range(board_size):
            set_btn_state(i, j, state)


def set_btn_vals(btn_row, btn_col, text, bg="SystemButtonFace", state=NORMAL):
    set_btn_text(btn_row, btn_col, text)
    set_btn_bg(btn_row, btn_col, bg)
    set_btn_state(btn_row, btn_col, state)


def are_all_btns_disabled():
    for i in range(board_size):
        for j in range(board_size):
            if btns[i][j].cget("state") != DISABLED:
                return False
    return True


def bot_turn():
    global board_size, btns, player_turn
    best_score, best_move = -800, (0, 0)
    for i in range(board_size):
        for j in range(board_size):
            if not btns[i][j].cget('text'):
                set_btn_vals(i, j, bot)
                score = minimax(0, False)
                set_btn_vals(i, j, '')
                if score > best_score:
                    best_score, best_move = score, (i, j)
    btn_click(best_move[0], best_move[1])


def minimax(depth, isMaximizing):
    global board_size, btns, player_turn

    res = check(True)
    if res:
        return res

    if (isMaximizing):
        best_score = -800
        for i in range(board_size):
            for j in range(board_size):
                if not btns[i][j].cget('text'):
                    set_btn_vals(i, j, bot)
                    score = minimax(depth + 1, False)
                    set_btn_vals(i, j, '')
                    if score > best_score:
                        best_score = score
    else:
        best_score = 800
        for i in range(board_size):
            for j in range(board_size):
                if not btns[i][j].cget('text'):
                    set_btn_vals(i, j, player)
                    score = minimax(depth + 1, True)
                    set_btn_vals(i, j, '')
                    if score < best_score:
                        best_score = score
    return best_score


def btn_click(i, j):
    global player_turn, btns, btns_clicked
    btns_clicked += 1
    text = player if player_turn else bot
    player_turn = not player_turn
    if btns[i][j].cget("state") == NORMAL:
        set_btn_vals(i, j, text=text, state=DISABLED)
        if btns_clicked >= ((check_adjacent_buttons * 2) - 1):
            res = check()
            if res == None and are_all_btns_disabled():
                set_turn_text("Draw")
            elif res == CONST_WIN_PLAYER:
                set_turn_text("Player won!")
            elif res == CONST_WIN_BOT:
                set_turn_text("Computer won!")
            else:
                set_turn_text()
        else:
            set_turn_text()

    if not player_turn:
        bot_turn()


def reset():
    global root, btns, player_turn, lblTurn
    player_turn = True
    btns = list()
    lblTurn = Label(root, font=("Comic Sans MS", 12), height=3)
    set_turn_text()
    lblTurn.grid(row=board_size, column=0)
    for i in range(board_size):
        btns.append(list())
        for j in range(board_size):
            btn = Button(
                root,
                font=("Comic Sans MS", 30),
                height=1,
                width=8,
                bg="SystemButtonFace",
                disabledforeground="black",
                command=lambda x=i, y=j: btn_click(x, y),
            )
            btns[i].append(btn)
            btns[i][j].grid(row=i, column=j)


my_menu = Menu(root)
root.config(menu=my_menu)
options_menu = Menu(my_menu, tearoff=False)
my_menu.add_cascade(label="Options", menu=options_menu)
options_menu.add_command(label="Reset Game", command=reset)
options_menu.add_command(label="Exit", command=exit)

reset()
root.mainloop()
