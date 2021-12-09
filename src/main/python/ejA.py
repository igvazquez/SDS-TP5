import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('ejB.csv', sep=',')
dfs = []
for i in range(5):
    dfs.append(df[df['simulation'] == i])

min = min([len(x) for x in dfs])
dfs = [df[0:min].drop(['simulation'], axis=1).reset_index() for df in dfs]

fig = plt.figure(figsize=(16, 10))
ax = fig.add_subplot(1, 1, 1)
ax.set_xlabel(r'$t$ (s)', size=20)
ax.set_ylabel(r'Descarga: n(t)', size=20)
ax.grid(which="both")
ax.tick_params(axis='both', which='major', labelsize=18)

for df in dfs:
    ax.plot(df['t'], df['n_t'])
    
plt.show()