#set datafile separator "\t"
set title "Payoff against Uniform Random" 
set xlabel "Payoff Uncertainty"
set ylabel "Average Expected Payoff"
plot for [COL=2:8] 'uniform.dat' using 1:COL with linespoints title columnheader
