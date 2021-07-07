set title "Quantal Response Row Payoff against UR" 
set xlabel "lambda"
set ylabel "Expected Utility"
set key off
plot "util.dat" using 1:2 with linespoints ls 2