import numpy as np
from numpy.lib.stride_tricks import sliding_window_view
import pandas as pd
import matplotlib.pyplot as plt


def get_simulation_list(big_df, simulations):
    dfs = []
    for i in range(simulations):
        dfs.append(big_df[big_df['simulation'] == i])

    _min = min([len(x) for x in dfs])
    dfs = [df[0:_min].drop(['simulation'], axis=1) for df in dfs]
    return dfs


def plot_caudal(time_mean, time_std, caudal, t):
    fig = plt.figure(figsize=(16, 10))
    ax = fig.add_subplot(1, 1, 1)
    ax.errorbar(time_mean, range(200) ,xerr=time_std, capsize=2)
    ax.set_xlabel(r'$t$ (s)', size=20)
    ax.set_ylabel(r'n(t)', size=20)
    ax.grid(which="both")

    fig = plt.figure(figsize=(16, 10))
    ax = fig.add_subplot(1, 1, 1)
    # ax.plot(T, Q)
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
    t = 0
    Q = []
    T = []
    ns = []
    np_array = np.array(list(map(lambda x: x.to_numpy(), dfs)))

    for i in range(np_array.shape[1]):
        a = np_array[:, i]
        ns.append(np.sum(a, 0) / len(a))

    ns = np.array(ns)
    W = 200
    window = sliding_window_view(ns[:, 1], W)
    ret = [(a[-1] - a[0]) / W / dt for a in window]
    for i in range(len(ns) - W):
        q = (ns[i + W][1] - ns[i][1]) / W
        # print("q: " + str(q))
        Q.append(q)
        T.append(ns[i][0])
    return Q, T, ret, ns[:, 0][:len(ns)-W+1]


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


simulations = 50
dt = 0.0375

df_n200_d1_2 = pd.read_csv('/home/abossi/Desktop/clases/SS/SDS-TP5/ejc_N=200_d=1.2.csv', sep=',')
df_n260_d1_8 = pd.read_csv('/home/abossi/Desktop/clases/SS/SDS-TP5/ejc_N=260_d=1.8.csv', sep=',')
df_n320_d2_4 = pd.read_csv('/home/abossi/Desktop/clases/SS/SDS-TP5/ejc_N=320_d=2.4.csv', sep=',')
df_n380_d3_0 = pd.read_csv('/home/abossi/Desktop/clases/SS/SDS-TP5/ejc_N=380_d=3.0.csv', sep=',')


dfs_n200_d1_2 = get_simulation_list(df_n200_d1_2, simulations)
dfs_n260_d1_8 = get_simulation_list(df_n260_d1_8, simulations)
dfs_n320_d2_4 = get_simulation_list(df_n320_d2_4, simulations)
dfs_n380_d3_0 = get_simulation_list(df_n380_d3_0, simulations)

mean_n200_d1_2, std_n200_d1_2 = get_time_stats(df_n200_d1_2)
mean_n260_d1_8, std_n260_d1_8 = get_time_stats(df_n260_d1_8)
mean_n320_d2_4, std_n320_d2_4 = get_time_stats(df_n320_d2_4)
mean_n380_d3_0, std_n380_d3_0 = get_time_stats(df_n380_d3_0)

Q_n200_d1_2, T_n200_d1_2, caudal_n200_d1_2, times_n200_d1_2 = process(dfs_n200_d1_2)
Q_n260_d1_8, T_n260_d1_8, caudal_n260_d1_8, times_n260_d1_8 = process(dfs_n260_d1_8)
Q_n320_d2_4, T_n320_d2_4, caudal_n320_d2_4, times_n320_d2_4 = process(dfs_n320_d2_4)
Q_n380_d3_0, T_n380_d3_0, caudal_n380_d3_0, times_n380_d3_0 = process(dfs_n380_d3_0)

# plot_caudal(mean_n200_d1_2, std_n200_d1_2, times_n200_d1_2, caudal_n200_d1_2)
# plot_caudal(mean_n260_d1_8, std_n260_d1_8, times_n260_d1_8, caudal_n260_d1_8)
# plot_caudal(mean_n320_d2_4, std_n320_d2_4, times_n320_d2_4, caudal_n320_d2_4)
# plot_caudal(mean_n380_d3_0, std_n380_d3_0, times_n380_d3_0, caudal_n380_d3_0)


ds = [1.2, 1.8, 2.4, 3.0]
mean_qs = []
std_qs = []

stat_mean_n200_d1_2, stat_std_n200_d1_2 = print_Q(Q_n200_d1_2)
stat_mean_n260_d1_8, stat_std_n260_d1_8 = print_Q(Q_n260_d1_8)
stat_mean_n320_d2_4, stat_std_n320_d2_4 = print_Q(Q_n320_d2_4)
stat_mean_n380_d3_0, stat_std_n380_d3_0 = print_Q(Q_n380_d3_0)
mean_qs.append(stat_mean_n200_d1_2)
mean_qs.append(stat_mean_n260_d1_8)
mean_qs.append(stat_mean_n320_d2_4)
mean_qs.append(stat_mean_n380_d3_0)
std_qs.append(stat_std_n200_d1_2)
std_qs.append(stat_std_n260_d1_8)
std_qs.append(stat_std_n320_d2_4)
std_qs.append(stat_std_n380_d3_0)

plt.errorbar(x=ds, y=mean_qs, yerr=std_qs)
plt.show()

