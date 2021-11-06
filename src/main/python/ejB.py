import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('ejB.csv', sep=',')
dfs = []
for i in range(30):
    dfs.append(df[df['simulation'] == i])

min = min([len(x) for x in dfs])
dfs = [df[0:min].drop(['simulation'], axis=1) for df in dfs]

times = []
for i in range(200):
    times.append(df[df['n_t'] == i])

mean = [np.mean(t['t']) for t in times]
std = [np.std(t['t']) for t in times]

# np_array = np.array(list(map(lambda x: x.to_numpy(), dfs)))

# dt = 0.15
# t = 0
# Q = []
# T = []
# run = np_array[0]
# for i in range(len(run) - 1):
#     t += dt
#     q = (run[i+1][1] - run[i][1])/t
#     Q.append(q)
#     T.append(t)

fig = plt.figure(figsize=(16, 10))
ax = fig.add_subplot(1, 1, 1)
ax.errorbar(mean, range(200) ,xerr=std, capsize=2)
ax.set_xlabel(r'$t$ (s)', size=20)
ax.set_ylabel(r'n(t)', size=20)
ax.grid(which="both")

# plt.plot(T, Q)
plt.show()