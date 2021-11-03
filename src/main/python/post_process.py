import os
import pandas as pd
import matplotlib.pyplot as plt
from numpy import float64


def plot_n_runs(df_list, n):
    for i in range(n):
        plot_dataframe(df_list[i])


def plot_average(df_list):
    sum = df_list[0]['escaped_particles']
    for i in range(1, len(df_list)):
        sum += df_list[i]['escaped_particles']
    average = sum/len(df_list)
    avg = {'time': df_list[0]['time'], 'avg': average}
    avg_df = pd.DataFrame(avg).sort_values(by=['time'])
    print(avg_df)


def plot_dataframe(df):
    plt.figure()
    plt.plot(df['time'], df['escaped_particles'])
    plt.xlabel("t [s]")
    plt.ylabel("Descarga")
    plt.show()

    plt.figure()


basePath = '/home/abossi/Desktop/clases/SS/SDS-TP5/Data/'
#list the files
file_list = os.listdir(basePath)
#read them into pandas
sort_by = 'time'  # para el a es time, para el b es particles_in_room
df_list = [pd.read_csv(basePath + file, sep=';').sort_values(by=[sort_by]) for file in file_list]

# punto a
plot_n_runs(df_list, 1)
# punto b
plot_average(df_list)
