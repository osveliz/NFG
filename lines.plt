set terminal png
set output "nem.png"
set title "Payoff against Nemesis" 
set xlabel "Payoff Uncertainty"
set ylabel "Average Expected Payoff"
plot for [COL=2:8] 'nem.dat' using 1:COL with linespoints title columnheader
