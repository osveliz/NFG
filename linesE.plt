#set datafile separator "\t"
set title "Payoff against ENE" 
set xlabel "Payoff Uncertainty"
set ylabel "Average Expected Payoff"
plot for [COL=2:8] 'equilib.dat' using 1:COL with linespoints title columnheader
