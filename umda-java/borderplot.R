raw1 = read.table(file = "umda_binval_currlevel_power0.6_gamma0.2.txt", header = FALSE)

pdf(file = "umda_bval_currLevel_mu_0_6.pdf", width = 8.0, height = 8.0)
par(mfrow=c(1,1))
mar.default = c(5, 4, 4, 2) + 0.1
par(mar=mar.default+c(0,1,0,0))

# current level
plot(raw1$V2~ raw1$V1, tck=0.03, ylim=c(0, 1000), main = "\\mu= n^0.5, \\lambda = 5*\\mu",
     xlab = "Iterations", ylab="Number of Bits",cex.lab=2, cex.main=2, cex.axis=1.5, col="grey")
# number of bits at upper border
lines(raw1$V3, col="red") 
# number of bits at lower border
lines(raw1$V4, col="blue")
# number of bits inbetween
lines(raw1$V5, col="green")

legend(50, 1000, legend=c("Current Level", "Bits at Up-Border", "Bits at Low-Border", "Bits inbetween"),
       col=c("grey", "red", "blue", "green"), lty=1, lwd=4, cex=1.0)

# 
# # current level
# plot(raw1$V2~ raw1$V1, tck=0.03, ylim=c(0, 500),main = "Bits after the current level only",
#      xlab = "Iterations", ylab="Number of Bits",cex.lab=2, cex.main=2, cex.axis=1.5, col="grey")
# # number of bits at upper border
# lines(raw1$V6, col="red") 
# # number of bits at lower border
# lines(raw1$V7, col="blue")
# # number of bits inbetween
# lines(raw1$V8, col="green")
# 
# legend(12000, 300, legend=c("Current Level", "Bits at Up-Border", "Bits at Low-Border", "Bits inbetween"),
#        col=c("grey", "red", "blue", "green"), lty=1, lwd = 4, cex=1.5)

par(mfrow =c(1,1))
dev.off()