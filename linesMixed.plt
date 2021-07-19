#set datafile separator "\t"
set terminal png
set output "mixed.png"
set title "Interpolation Strategy"
set xlabel "ENE:Nemesis Ratio"
set ylabel "Average Expected Payoff"
set xtics ("1-0" 1, ".9-.1" 2, ".8-.2" 3, ".7-.3" 4, ".6-.4" 5, ".5-.5" 6, ".4-.6" 7, ".3-.7" 8, ".2-.8" 9, ".1-.9" 10, "0-1" 11)
plot for [COL=2:12] 'mixed.dat' using 1:COL with linespoints title columnheader
