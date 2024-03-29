set terminal png
set output "tourney.png"
set style fill solid 1.00 border lt -1
set key outside center bottom horizontal
set style histogram clustered gap 1 title textcolor lt -1

set style data histograms
set title "Payoff against Benchmarks" 
set xlabel "agent"
set ylabel "average payoff"
#set yrange [0:100]
set xtics rotate by 315
#plot "chart.dat" using 2: xtic(1) with histogram
plot for [COL=2:6] 'chart0.dat' using COL:xticlabels(1) title columnheader