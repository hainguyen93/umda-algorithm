#setEPS()
#postscript
pdf(file="umda_bval_runtime.pdf", width = 16.0, height = 6)
par(mfrow=c(1,1))
mar.default = c(5, 4, 4, 2) + 0.1
par(mar=mar.default+c(0,1,0,0))

#small population size
raw.small = read.table(file = "umda_binval_mulog_gamma0.2.txt", header= FALSE)

for (n in seq(100, 1000, by=100)){
  print(nrow(raw.small[raw.small$V1==n, ]))
}

m = matrix(0L, nrow=10, ncol=100, dimnames = list(seq(100,1000,by=100),1:100))
print(m)

r = 1
for (n in seq(100, 1000, by=100)){
  nres = raw.small[raw.small$V1==n,]
  rows = nrow(nres)
  c = 1
  for (cols in 1:rows){
    m[r,c] = nres[cols,3]
    c = c + 1
  }
  r = r + 1
}

is.na(m) <- m==0
print(m)
print(nrow(m))
for (n in 1:nrow(m)){
  m[n,is.na(m[n,])] = mean(m[n,], na.rm = TRUE)
}
print(m)

#data = matrix(data.vector, nrow = 10, ncol = 100, byrow = TRUE, dimnames = list(seq(100,1000,by=100),1:100))
rm = rowMeans(m, na.rm = TRUE, dims = 1)
print(rm)
x = seq(100, 1000, by=100)
y = rm


boxplot(m/(x^1.5) ~ x, 
        xlab = "Problem Instance Size n",
        ylab = expression(T/n^1.5), main="UMDA on BinVal. Settings: \\mu=5*\\log(n), \\lambda=\\mu/0.2")

#abline(h=1.65, col="blue")
grid(NA, 5, lwd = 2) 

#plot(x,log(y)/log(x), pch=16,tck=0.03, main = "Small population size", xlab = "Length of bitstring",
#      ylab = "Average runtime", cex.lab=2, cex.main=2, cex.axis=1.5, xlim=c(0,550), ylim=c(1.9,2.2))

#abline(h=2.0, col="red")
# ds = data.frame(x=x, y=y)
# str(ds)
# m = nls(y~a*x^power, data=ds, start = list(a=.5, power=1.5), trace = T)
# a = round(coef(m)[1],3)
# power = round(coef(m)[2],3)
# lines(x, predict(m, list(x=x)), lty=1, col="red")
# text(100,600000,expression(4.304*n^1.802), pos = 4, cex = 2)
# grid(nx=NULL, ny = NULL, col = "gray48", lty = "dotted",
#      lwd = 2, equilogs = TRUE)

# medium population size
# raw.small = read.table(file = "umda-los-medium", header= FALSE)
# data.vector = c()
# for (n in seq(100, 1000, by=100)){
#   for (row in 1:1000) {
#     if (raw.small[row,1]==n){
#       data.vector = c(data.vector, raw.small[row,3])
#     }
#   }
# }
# data = matrix(data.vector, nrow = 10, ncol = 100, byrow = TRUE, dimnames = list(seq(100,1000,by=100),1:100))
# rm = rowMeans(data, na.rm = FALSE,dims = 1)
# x = seq(100, 1000, by=100)
# y = rm
# 
# boxplot(log(data)/log(x)~ x)
# plot(x,y, pch=16,tck=0.03, main = "Medium population size", xlab = "Length of bitstring",
#      ylab = "Average runtime", cex.lab=2, cex.main=2, cex.axis=1.5)
# ds = data.frame(x=x, y=y)
# str(ds)
# m = nls(y~a*x^power, data=ds, start = list(a=.5, power=1.5), trace = T)
# a = round(coef(m)[1],3)
# power = round(coef(m)[2],3)
# lines(x, predict(m, list(x=x)), lty=1, col="red")
# text(100,600000,expression(1.468*n^1.946), pos = 4, cex = 2)
# grid(nx=NULL, ny = NULL, col = "gray48", lty = "dotted",
#      lwd = 2, equilogs = TRUE)

# large population size
# raw.small = read.table(file = "umda-los-large", header= FALSE)
# data.vector = c()
# for (n in seq(100, 1000, by=100)){
#   for (row in 1:1000) {
#     if (raw.small[row,1]==n){
#       data.vector = c(data.vector, raw.small[row,3])
#     }
#   }
# }
# data = matrix(data.vector, nrow = 10, ncol = 100, byrow = TRUE, dimnames = list(seq(100,1000,by=100),1:100))
# rm = rowMeans(data, na.rm = FALSE,dims = 1)
# x = seq(100, 1000, by=100)
# y = rm
# 
# boxplot(log(data)/log(x) ~ x)
# plot(x,y, pch=16,tck=0.03, main = "Large population size", xlab = "Length of bitstring",
#      ylab = "Average runtime", cex.lab=2, cex.main=2, cex.axis=1.5)
# ds = data.frame(x=x, y=y)
# str(ds)
# m = nls(y~a*x^power, data=ds, start = list(a=.5, power=1.5), trace = T)
# a = round(coef(m)[1],3)
# power = round(coef(m)[2],3)
# lines(x, predict(m, list(x=x)), lty=1, col="red")
# text(100,1500000,expression(6.360*n^1.874), pos = 4, cex = 2)
# grid(nx=NULL, ny = NULL, col = "gray48", lty = "dotted",
#      lwd = 2, equilogs = TRUE)

par(mfrow =c(1,1))
dev.off()
