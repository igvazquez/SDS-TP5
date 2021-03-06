import numpy as np
from numpy.lib.stride_tricks import sliding_window_view
import pandas as pd
import matplotlib.pyplot as plt

simulations = 50
dt = 0.0375

df = pd.read_csv('ejB.csv', sep=',')
dfs = []
for i in range(simulations):
    dfs.append(df[df['simulation'] == i])

min = min([len(x) for x in dfs])
dfs = [df[0:min].drop(['simulation'], axis=1) for df in dfs]

 # cada elemento de la lista tiene los tiempos en donde habia i particulas
times = []
for i in range(200):
    times.append(df[df['n_t'] == i])

mean = [np.mean(t['t']) for t in times]
std = [np.std(t['t']) for t in times]

np_array = np.array(list(map(lambda x: x.to_numpy(), dfs)))

t = 0
Q = []
T = []
ns = []
for i in range(np_array.shape[1]):
    a = np_array[:,i]
    ns.append(np.sum(a,0)/len(a))

ns = np.array(ns)
W = 200
window = sliding_window_view(ns[:,1], W)
ret = [ (a[-1] - a[0])/W/dt for a in window]

fig = plt.figure(figsize=(16, 10))
ax = fig.add_subplot(1, 1, 1)
ax.errorbar(mean, range(200) ,xerr=std, capsize=2)
ax.set_xlabel(r'$t$ (s)', size=20)
ax.set_ylabel(r'n(t)', size=20)
ax.grid(which="both")

fig = plt.figure(figsize=(16, 10))
ax = fig.add_subplot(1, 1, 1)
ax.plot(ns[:,0][:len(ns)-W+1], ret, 'o')
ax.set_xlabel(r'$t$ (s)', size=20)
ax.set_ylabel(r'Q(t)', size=20)
ax.grid(which="both")
plt.show()