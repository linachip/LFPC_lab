import pandas as pd
import re

input_file = ['var8.txt']
eps = '@'


def parsing(grammar, start_symbol, parsing_table):
    flag = 0
    user_input = grammar + "$"

    stack = ["$", start_symbol]

    length = len(user_input)
    index = 0

    while len(stack) > 0:

        top = stack[len(stack) - 1]
        print("Stack: ", top)
        current_input = user_input[index]
        print("Input: ", current_input)

        if top == current_input:
            stack.pop()
            index = index + 1
        else:

            key = top, current_input
            print(key)

            if key not in parsing_table:
                flag = 1
                break

            value = parsing_table[key]
            if value != eps:
                value = value[::-1]
                value = list(value)

                stack.pop()

                for element in value:
                    stack.append(element)
            else:
                stack.pop()

    print(" ")
    if flag == 0:
        print("Given string " + string + " is accepted by the grammar!")
    else:
        print("Given string " + string + " is rejected by the grammar!")


def first(start, productions):
    c = start[0]
    ans = set()
    if c.isupper():
        for st in productions[c]:
            if st == eps:
                if len(start) != 1:
                    ans = ans.union(first(start[1:], productions))
                else:
                    ans = ans.union(eps)
            else:
                f = first(st, productions)
                ans = ans.union(x for x in f)
    else:
        ans = ans.union(c)
    return ans


def follow(start, productions, ans):
    if len(start) != 1:
        return {}

    for key in productions:
        for value in productions[key]:
            f = value.find(start)
            if f != -1:
                if f == (len(value) - 1):
                    if key != start:
                        if key in ans:
                            temp = ans[key]
                        else:
                            ans = follow(key, productions, ans)
                            temp = ans[key]

                        ans[start] = ans[start].union(temp)
                else:
                    first_of_next = first(value[f + 1:], productions)
                    if eps in first_of_next:
                        if key != start:
                            if key in ans:
                                temp = ans[key]
                            else:
                                ans = follow(key, productions, ans)
                                temp = ans[key]

                            ans[start] = ans[start].union(temp)
                            ans[start] = ans[start].union(first_of_next) - {eps}
                    else:
                        ans[start] = ans[start].union(first_of_next)
    return ans


def ll1parse(follow, productions):
    print("\n")
    print("Parsing Table")

    parse_table = {}

    for key in productions:
        for value in productions[key]:
            if value != eps:
                for element in first(value, productions):
                    parse_table[key, element] = value
            else:
                for element in follow[key]:
                    parse_table[key, element] = value

    for key, val in parse_table.items():
        print(key, "->", val)

    matrix_table = {}

    for pair in parse_table:
        matrix_table[pair[1]] = {}

    for pair in parse_table:
        matrix_table[pair[1]][pair[0]] = parse_table[pair]

    print("\n")
    print("Predictive Matrix")
    m_table = pd.DataFrame(matrix_table)
    m_table = m_table.fillna("-")
    print(m_table)
    print("")

    return parse_table


if __name__ == "__main__":
    print("Input")
    input_file = open("var8.txt")
    contents = input_file.read()
    print(contents)
    input_file.close()
    print("")
    productions = dict()
    grammar = open("var8.txt", "r")
    first_dict = dict()
    follow_dict = dict()
    flag = 1
    start = ""
    for line in grammar:
        line = line[0:-1]
        l = re.split("[->|\|]", line)
        del (l[1])
        lhs = l[0]
        rhs = set(l[1:]) - {''}
        if flag:
            flag = 0
            start = lhs
        productions[lhs] = rhs

    print('First')
    for lhs in productions:
        first_dict[lhs] = first(lhs, productions)

    for f in first_dict:
        print(str(f) + " -> " + str(first_dict[f]))
    print('')

    print('Follow')
    for lhs in productions:
        follow_dict[lhs] = set()

    follow_dict[start] = follow_dict[start].union('$')

    for lhs in productions:
        follow_dict = follow(lhs, productions, follow_dict)

    for lhs in productions:
        follow_dict = follow(lhs, productions, follow_dict)

    for f in follow_dict:
        print(str(f) + " -> " + str(follow_dict[f]))

    ll1_parsing_table = ll1parse(follow_dict, productions)
    print('Parsing Expression')
    print(" ")
    string = 'aaaacadeebbb'
    parsing(string, start, ll1_parsing_table)

    # string = 'aaaacadb'
    # parsing(string, start, ll1_parsing_table)
