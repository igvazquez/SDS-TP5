import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('ejB.csv', sep=',')
dfs = []
for i in range(5):
    dfs.append(df[df['simulation'] == i])

min = min([len(x) for x in dfs])
dfs = [df[0:min].drop(['simulation'], axis=1).reset_index() for df in dfs]

for df in dfs:
    plt.plot(df['t'], df['n_t'])
    
plt.show()