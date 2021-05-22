import pandas as pd

nfa_states = {}


def buildNFA(states):
    for stateRules in states:
        rules = stateRules.split(' ')

        if not rules[0] in nfa_states:
            nfa_states[rules[0]] = {}

            if not rules[1] in nfa_states[rules[0]]:
                nfa_states[rules[0]][rules[1]] = ''
                nfa_states[rules[0]][rules[1]] += rules[2]

    return nfa_states


def buildDFA(nfa_states):
    statements = []

    for state in nfa_states:
        statements.append(state)

        for path in nfa_states[state]:
            if len(nfa_states[state][path]) > 1:
                if not nfa_states[state][path] in statements:
                    statements.append(nfa_states[state][path])
                else:
                    if not nfa_states[state][path][0] in statements:
                        statements.append(nfa_states[state][path][0])

    paths = []

    for state in nfa_states:
        for path in nfa_states[state]:

            if not path in paths:
                paths.append(path)

    for x in statements:

        if not x in nfa_states:
            newstate = list(x)

            for y in paths:
                temp = []

                for z in newstate:
                    if y in nfa_states[z]:
                        temp.append(nfa_states[z][y])

                if not x in nfa_states:
                    nfa_states[x] = {}

                nfa_states[x][y] = ''.join(set(''.join(temp)))
                statements.append(''.join(set(''.join(temp))))

    return nfa_states


states = [
    '0 a 0',
    '0 a 1',
    '1 a 2',
    '1 b 1',
    '2 a 3',
    '3 a 1',
]


print("Printing NFA to DFA transition table :- ")
nfa = buildNFA(states)
dfa = buildDFA(nfa_states)
print(dfa)
dfa_table = pd.DataFrame(dfa)
print(dfa_table.transpose())