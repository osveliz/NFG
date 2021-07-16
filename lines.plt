#set datafile separator "\t"
set title "Payoff against Nemesis" 
set xlabel "Payoff Uncertainty"
set ylabel "Average Expected Payoff"
plot for [COL=2:8] 'nem.dat' using 1:COL with linespoints title columnheader
#plot 'nem.dat' using 1:2 with linespoints title columnhead, \
    'nem.dat' using 1:3 with linespoints title columnhead, \
    'nem.dat' using 1:4 with linespoints title columnhead, \
    'nem.dat' using 1:5 with linespoints title columnhead, \
    'nem.dat' using 1:6 with linespoints title columnhead, \
    'nem.dat' using 1:7 with linespoints title columnhead