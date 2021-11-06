import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

simulations = 50
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

dt = 0.15
t = 0
Q = []
T = []
ns = []
for i in range(np_array.shape[1]):
    a = np_array[:,i]
    ns.append(np.sum(a,0)/len(a))

for i in range(len(ns) - 1):
    q = (ns[i+1][1] - ns[i][1])/dt
    print("q: " + str(q))
    Q.append(q)
    T.append(ns[i][0])

print("MaxQ: " + str(np.max(Q)))
print("MinQ: " + str(np.min(Q)))
print("Mean: " + str(np.mean(Q)))

fig = plt.figure(figsize=(16, 10))
ax = fig.add_subplot(1, 1, 1)
ax.errorbar(mean, range(200) ,xerr=std, capsize=2)
ax.set_xlabel(r'$t$ (s)', size=20)
ax.set_ylabel(r'n(t)', size=20)
ax.grid(which="both")

fig = plt.figure(figsize=(16, 10))
ax = fig.add_subplot(1, 1, 1)
ax.plot(T, Q)
ax.set_xlabel(r'$t$ (s)', size=20)
ax.set_ylabel(r'Q(t)', size=20)
ax.grid(which="both")
plt.show()