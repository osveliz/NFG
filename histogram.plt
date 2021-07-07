#set terminal pngcairo  transparent enhanced font "arial,10" fontscale 1.0 size 600, 400 
#set output 'histograms.2.png'
#set boxwidth 0.9 absolute
set style fill solid 1.00 border lt -1
#set key fixed right top vertical Right noreverse noenhanced autotitle nobox
set key outside center bottom horizontal
set style histogram clustered gap 1 title textcolor lt -1
#set datafile missing '-'
set style data histograms
set title "Quantal Response Row Strategies against UR" 
set xlabel "lambda"
set ylabel "probability per action"
set yrange [ 0.0 :  ] noreverse writeback
#plot 'test.dat' using 0:xtic(1) ti col, '' u 2 ti col, '' u 3 ti col, '' u 4 ti col
plot for [COL=2:4] 'test.dat' using COL:xticlabels(1) title columnheader