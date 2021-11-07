import numpy as np
from numpy.lib.stride_tricks import sliding_window_view
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib

matplotlib.rcParams.update({'errorbar.capsize': 4})

def get_simulation_list(big_df, simulations):
    dfs = []
    for i in range(simulations):
        dfs.append(big_df[big_df['simulation'] == i])

    _min = min([len(x) for x in dfs])
    dfs = [df[0:_min].drop(['simulation'], axis=1) for df in dfs]
    return dfs


def plot_caudal(caudal, t):
    fig = plt.figure(figsize=(16, 10))
    ax = fig.add_subplot(1, 1, 1)
    ax.plot(caudal, t, 'o')
    ax.set_xlabel(r'$t$ (s)', size=20)
    ax.set_ylabel(r'Q(t)', size=20)
    ax.grid(which="both")
    plt.show()


def get_time_stats(df):
    # cada elemento de la lista tiene los tiempos en donde habia i particulas
    times = []
    for i in range(200):
        times.append(df[df['n_t'] == i])

    mean = [np.mean(t['t']) for t in times]
    std = [np.std(t['t']) for t in times]
    return mean, std


def process(dfs):
    ns = []
    np_array = np.array(list(map(lambda x: x.to_numpy(), dfs)))

    for i in range(np_array.shape[1]):
        a = np_array[:, i]
        ns.append(np.sum(a, 0) / len(a))

    ns = np.array(ns)
    W = 200
    window = sliding_window_view(ns[:, 2], W)
    ret = [(a[-1] - a[0]) / W / dt for a in window]
    return ret, ns[:, 1][:len(ns)-W+1]


def print_Q(Q):
    # si defino mi estacionario entre 10s y 30s
    inf_lim = int(10 / dt)
    sup_lim = int(30 / dt)
    stationary = Q[inf_lim:sup_lim]
    mean_q = np.mean(stationary)
    std_q = np.std(stationary)

    # print("MaxQ: " + str(np.max(Q)))
    # print("MinQ: " + str(np.min(Q)))
    # print("Mean: " + str(np.mean(Q)))
    print('Mean stationary: ' + str(mean_q))
    return mean_q, std_q


simulations = 30
dt = 0.0375

main_df = pd.read_csv('ejC.csv', sep=',')

df_n200_d1_2 = main_df[main_df['d'] == 1.2]
df_n260_d1_8 = main_df[main_df['d'] == 1.8]
df_n320_d2_4 = main_df[main_df['d'] == 2.4]
df_n380_d3_0 = main_df[main_df['d'] == 3.0]


dfs_n200_d1_2 = get_simulation_list(df_n200_d1_2, simulations)
dfs_n260_d1_8 = get_simulation_list(df_n260_d1_8, simulations)
dfs_n320_d2_4 = get_simulation_list(df_n320_d2_4, simulations)
dfs_n380_d3_0 = get_simulation_list(df_n380_d3_0, simulations)

caudal_n200_d1_2, times_n200_d1_2 = process(dfs_n200_d1_2)
caudal_n260_d1_8, times_n260_d1_8 = process(dfs_n260_d1_8)
caudal_n320_d2_4, times_n320_d2_4 = process(dfs_n320_d2_4)
caudal_n380_d3_0, times_n380_d3_0 = process(dfs_n380_d3_0)


fig = plt.figure(figsize=(16, 10))
ax = fig.add_subplot(1, 1, 1)
ax.set_xlabel(r'$t$ (s)', size=20)
ax.set_ylabel(r'Caudal (1/s)', size=20)
ax.grid(which="both")
ax.tick_params(axis='both', which='major', labelsize=18)
ax.plot(times_n200_d1_2, caudal_n200_d1_2, 'o', label='d = 1.2')
ax.plot(times_n260_d1_8, caudal_n260_d1_8, 'o', label='d = 1.8')
ax.plot(times_n320_d2_4, caudal_n320_d2_4, 'o', label='d = 2.4')
ax.plot(times_n380_d3_0, caudal_n380_d3_0, 'o', label='d = 3.0')
ax.legend(prop=dict(size=18))

min = 0
max = 0
for i in range(len(times_n200_d1_2)):
    if times_n200_d1_2[i] <= 10:
        min = i
    if times_n200_d1_2[i] <= 30:
        max = i

ds = [1.2, 1.8, 2.4, 3.0]
mean_Q = []
mean_Q.append(np.mean(caudal_n200_d1_2[min:max]))
mean_Q.append(np.mean(caudal_n260_d1_8[min:max]))
mean_Q.append(np.mean(caudal_n320_d2_4[min:max]))
mean_Q.append(np.mean(caudal_n380_d3_0[min:max]))

stddev_Q = []
stddev_Q.append(np.std(caudal_n200_d1_2[min:max]))
stddev_Q.append(np.std(caudal_n260_d1_8[min:max]))
stddev_Q.append(np.std(caudal_n320_d2_4[min:max]))
stddev_Q.append(np.std(caudal_n380_d3_0[min:max]))

# Ajuste por Ley de Beverloo
ds = np.array(ds)
b = np.linspace(0, 3, 50000)
error = np.sum((b.reshape((b.size,1)) * ds**1.5 - mean_Q)**2, axis=1) / ds.size

minEcm = np.min(error)
minB = b[np.where(error == minEcm)]

fig = plt.figure(figsize=(16, 10))
ax = fig.add_subplot(1, 1, 1)
ax.set_xlabel(r'$B$ ($s^{-1}m^{-3/2}$)', size=20)
ax.set_ylabel(r'ECM ($s^{-2}$)', size=20)
ax.grid(which="both")
ax.tick_params(axis='both', which='major', labelsize=18)
ax.scatter(minB,minEcm, color='r', marker='x',s=500)
ax.plot(b, error)

fig = plt.figure(figsize=(16, 10))
ax = fig.add_subplot(1, 1, 1)
ax.set_xlabel(r'$d$ (m)', size=20)
ax.set_ylabel(r'$<Q_d>$: Caudal medio (1/s)', size=20)
ax.grid(which="both")
ax.set_xticks(ds)
ax.tick_params(axis='both', which='major', labelsize=18)
ax.errorbar(x=ds, y=mean_Q, yerr=stddev_Q, label=r'$<Q_d>$')
ax.plot(ds, minB*ds**1.5, label=r'$Bd^{3/2}$')
ax.legend(prop=dict(size=18))

plt.show()

